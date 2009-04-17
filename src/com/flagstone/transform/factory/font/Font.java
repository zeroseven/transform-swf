package com.flagstone.transform.factory.font;

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.DataFormatException;

import com.flagstone.transform.coder.BigEndianDecoder;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.factory.shape.Canvas;
import com.flagstone.transform.movie.Movie;
import com.flagstone.transform.movie.MovieTag;
import com.flagstone.transform.movie.datatype.Bounds;
import com.flagstone.transform.movie.datatype.CoordTransform;
import com.flagstone.transform.movie.font.DefineFont;
import com.flagstone.transform.movie.font.DefineFont2;
import com.flagstone.transform.movie.font.FontInfo;
import com.flagstone.transform.movie.font.FontInfo2;
import com.flagstone.transform.movie.font.Kerning;
import com.flagstone.transform.movie.shape.Shape;
import com.flagstone.transform.movie.shape.ShapeRecord;
import com.flagstone.transform.movie.font.CharacterEncoding;

/**
 * <p>Font is used to add embedded fonts to a movie.</p> 
 * 
 * <p>Flash supports two types of font definition: embedded fonts where the Flash 
 * file contains the glyphs that are drawn to represents the text characters and  
 * device fonts where the font is provided by the Flash Player showing the movie.
 * Embedded fonts are preferred since the movie will always look the same 
 * regardless of where it is played - if a Flash Player does not contain a 
 * device font it will substitute it with another.</p>
 * 
 * <p>Device fonts can be added to a movie by simply creating a DefineFont or 
 * DefineFont2 object which contain the name of the font. An embedded font 
 * must contain all the information to draw and layout the glyphs representing
 * the text to be displayed. The Font class hides all this detail and makes it 
 * easy to add embedded fonts to a movie.<p>
 * 
 * <p>The Font class can be used to create embedded fonts in three ways:</p>
 * 
 * <ol>
 * <li>Using TrueType or OpenType font definition stored in a file.</li>
 * <li>Using an existing font definition from a flash file.</li>
 * <li>Using a given Java AWT font as a template.</li>
 * </ol>
 * 
 * <P>For OpenType or TrueType fonts, files with the extensions ".otf" or ".ttf" 
 * may be used. Files containing collections of fonts ".otc" are not currently 
 * supported.</p>
 * 
 * <p>Using an existing Flash font definition is the most interesting. Fonts 
 * can initially be created using AWT Font objects or TrueType files and all the
 * visible characters included. If the generated Flash definition is saved to 
 * a file it can easily and quickly be loaded. Indeed the overhead of parsing 
 * an AWT or TrueType font is significant (sometimes several seconds) so creating
 * libraries of "pre-parsed" flash fonts is the preferred way of use fonts.</p>  
 */
@SuppressWarnings("unused")
public final class Font
{
    protected static final int OS_2 = 0x4F532F32;
    protected static final int HEAD = 0x68656164;
    protected static final int HHEA = 0x68686561;
    protected static final int MAXP = 0x6D617870;
    protected static final int LOCA = 0x6C6F6361;
    protected static final int CMAP = 0x636D6170;
    protected static final int HMTX = 0x686D7478;
    protected static final int NAME = 0x6E616D65;
    protected static final int GLYF = 0x676C7966;

    protected static final int ITLF_SHORT = 0;
    protected static final int ITLF_LONG  = 1;
    
    protected static final int WEIGHT_THIN       = 100;
    protected static final int WEIGHT_EXTRALIGHT = 200;
    protected static final int WEIGHT_LIGHT      = 300;
    protected static final int WEIGHT_NORMAL     = 400;
    protected static final int WEIGHT_MEDIUM     = 500;
    protected static final int WEIGHT_SEMIBOLD   = 600;
    protected static final int WEIGHT_BOLD       = 700;
    protected static final int WEIGHT_EXTRABOLD  = 800;
    protected static final int WEIGHT_BLACK      = 900;
    
    protected static final int ON_CURVE    = 0x01;
    protected static final int X_SHORT     = 0x02;
    protected static final int Y_SHORT     = 0x04;
    protected static final int REPEAT_FLAG = 0x08;
    protected static final int X_SAME      = 0x10;
    protected static final int Y_SAME      = 0x20;
    protected static final int X_POSITIVE  = 0x10;
    protected static final int Y_POSITIVE  = 0x20;
    
    protected static final int ARGS_ARE_WORDS = 0x01;
    protected static final int ARGS_ARE_XY    = 0x02;
    protected static final int HAVE_SCALE     = 0x08;
    protected static final int HAVE_XYSCALE   = 0x40;
    protected static final int HAVE_2X2       = 0x80;
    protected static final int HAS__MORE      = 0x10;
    
    /**
     * Create a set of Fonts using the font definitions stored in a Flash file.
     * 
     * @param file the path to the file containing the font definitions.
     * @return an array of Font object, one for each font family, weight and 
     * style defined in the file.
     * 
     * @throws FileNotFoundException is the file cannot be found or opened.
     * @throws IOException if there is an error reading the file.
     * @throws CoderException if the file cannot be decoded.
     * @throws DataFormatException if the file is not in the Flash file format.
     */
    public static Font[] fontsFromFile(File file) throws FileNotFoundException, 
    	CoderException, IOException, DataFormatException
    {
    	Movie movie = new Movie();
    	movie.decodeFromFile(file);
    	
    	List<Font>list = new ArrayList<Font>();
    	
        Font font;

        for (MovieTag obj :  movie.getObjects())
        {
         	if (obj instanceof DefineFont2) {
        		font = new Font();
        		font.decode((DefineFont2)obj);
        		list.add(font);
        	}
        }
        return list.toArray(new Font[] {});
    }

    /**
     * Create a Font object from a Flash file containing a definition with the 
     * specified font family, weight and style.
     * 
     * This method can be used to load a single font from a library file.
     * 
     * @param file the path to the file containing the font.
     * @param name the name of the font family.
     * @param bold whether the font weight is bold (true) or normal (false).
     * @param italic whether the font style is italic (true) or normal (false).
     * @return the Font containing all the glyphs and layout information for the
     * specified font or null if the file does not contain a matching font.
	 *
     * @throws FileNotFoundException is the file cannot be found or opened.
     * @throws IOException if there is an error reading the file.
     * @throws CoderException if the file cannot be decoded.
     * @throws DataFormatException if the file is not in the Flash file format.
     */
    public static Font fontFromFile(File file, String name, boolean bold, boolean italic) 
    	throws FileNotFoundException, CoderException, IOException, DataFormatException
	{
    	Movie movie = new Movie();
    	movie.decodeFromFile(file);

    	Font font = null;
        
        for (MovieTag obj : movie.getObjects())
        {
        	if (obj instanceof DefineFont2) 
        	{
        		DefineFont2 fontObj = (DefineFont2)obj;
        		
        		if (fontObj.getName().equals(name) && fontObj.isBold() == bold && fontObj.isItalic() == italic) {
        			font = new Font(); 
        			font.decode(fontObj);
        			break;
        		}
         	}
        }
        return font;
    }

    protected String name;
    protected boolean bold;
    protected boolean italic;

    protected CharacterEncoding encoding;

    protected float ascent;
    protected float descent;
    protected float leading;

    protected int[] charToGlyph;
    protected int[] glyphToChar;

    protected Glyph[] glyphTable;

    protected int glyphCount;
    protected int missingGlyph;
    protected char maxChar;
    
    protected List<Kerning> kernings = new ArrayList<Kerning>();
    
    private int scale;
    private int metrics;
    private int glyphOffset;

    /**
     * Initialise this object with the information from a flash font definition.
     * 
     * @param glyphs a DefineFont object which contains the definition of the 
     * glyphs.
     * 
     * @param info a FontInfo object that contains information on the font name,
     * weight, style and character codes.
     */
    public void decode(DefineFont glyphs, FontInfo info)
    {
    	name = info.getName();
    	bold = info.isBold();
    	italic = info.isItalic();

    	encoding = info.getEncoding();   	
    	
    	if (encoding == CharacterEncoding.ANSI) {
    		encoding = CharacterEncoding.UCS2;
    	}

    	//TODO ascent = info.getAscent();
    	//TODO descent = info.getDescent();
    	//TODO leading = info.getLeading();
    	
    	missingGlyph = 0;

    	glyphCount = glyphs.getShapes().size();
    	glyphTable = new Glyph[glyphCount];
        glyphToChar = new int[glyphCount];
        
        maxChar = 0;
        charToGlyph = new int[0];

    	if (glyphCount > 0)
    	{
        	int glyphIndex = 0;

        	for (Iterator<Shape> i=glyphs.getShapes().iterator(); i.hasNext(); glyphIndex++) {
        		glyphTable[glyphIndex] = new Glyph(i.next());
        	}

        	glyphIndex = 0;

        	for (Integer code : info.getCodes())
        	{
        		maxChar = (char) (code > maxChar ? code : maxChar);
        	}

        	charToGlyph = new int[maxChar+1];

           	for (Integer code : info.getCodes())
        	{
        		charToGlyph[code] = glyphIndex;
        		glyphToChar[glyphIndex] = code;
        	}

/* TODO
            if (glyphs.getAdvances() != null)
        	{
            	glyphIndex = 0;  	
            	
            	for (Iterator<Integer> i = font.getAdvances().iterator(); i.hasNext(); glyphIndex++)
            		glyphTable[glyphIndex].setAdvance(i.next());
        	}
        	
        	if (font.getBounds() != null)
        	{
            	glyphIndex = 0;
            	
                	for (Iterator<Bounds> i = font.getBounds().iterator(); i.hasNext(); glyphIndex++)
                		glyphTable[glyphIndex].setBounds(i.next());
        	}
*/    	
        }
    }

    /**
     * Initialise this object with the information from a flash font definition.
     * 
     * @param glyphs a DefineFont object which contains the definition of the 
     * glyphs.
     * 
     * @param info a FontInfo2 object that contains information on the font name,
     * weight, style and character codes.
     */
    public void decode(DefineFont glyphs, FontInfo2 info)
    {
    	name = info.getName();
    	bold = info.isBold();
    	italic = info.isItalic();

    	encoding = info.getEncoding();   	
    	
    	if (encoding == CharacterEncoding.ANSI) {
    		encoding = CharacterEncoding.UCS2;
    	}

    	//TODO ascent = info.getAscent();
    	//TODO descent = info.getDescent();
    	//TODO leading = info.getLeading();
    	
    	missingGlyph = 0;

    	glyphCount = glyphs.getShapes().size();
    	glyphTable = new Glyph[glyphCount];
        glyphToChar = new int[glyphCount];
        
        maxChar = 0;
        charToGlyph = new int[0];

    	if (glyphCount > 0)
    	{
        	int glyphIndex = 0;

        	for (Iterator<Shape> i=glyphs.getShapes().iterator(); i.hasNext(); glyphIndex++) {
        		glyphTable[glyphIndex] = new Glyph(i.next());
        	}

         	glyphIndex = 0;

        	for (Integer code : info.getCodes())
        	{
        		maxChar = (char) (code > maxChar ? code : maxChar);
        	}

        	charToGlyph = new int[maxChar+1];

        	int code;
        	
        	for (Iterator<Integer> i=info.getCodes().iterator(); i.hasNext(); glyphIndex++)
        	{
        		code = i.next();

        		charToGlyph[code] = glyphIndex;
        		glyphToChar[glyphIndex] = code;
        	}

/* TODO
            if (glyphs.getAdvances() != null)
        	{
            	glyphIndex = 0;  	
            	
            	for (Iterator<Integer> i = font.getAdvances().iterator(); i.hasNext(); glyphIndex++)
            		glyphTable[glyphIndex].setAdvance(i.next());
        	}
        	
        	if (font.getBounds() != null)
        	{
            	glyphIndex = 0;
            	
                	for (Iterator<Bounds> i = font.getBounds().iterator(); i.hasNext(); glyphIndex++)
                		glyphTable[glyphIndex].setBounds(i.next());
        	}
*/    	
        }
    }

    /**
     * Initialise this object with the information from a flash font definition.
     * 
     * @param font a DefineFont2 object that contains information on the font name,
     * weight, style and character codes as well as the glyph definitions.
     */
    public void decode(DefineFont2 font)
    {
    	name = font.getName();
    	bold = font.isBold();
    	italic = font.isItalic();

    	encoding = font.getEncoding();   	
    	
    	if (encoding == CharacterEncoding.ANSI) {
    		encoding = CharacterEncoding.UCS2;
    	}

    	ascent = font.getAscent();
    	descent = font.getDescent();
    	leading = font.getLeading();
    	
    	missingGlyph = 0;

    	glyphCount = font.getShapes().size();
    	glyphTable = new Glyph[glyphCount];
        glyphToChar = new int[glyphCount];
        
        maxChar = 0;
        charToGlyph = new int[0];

    	if (glyphCount > 0)
    	{
        	int glyphIndex = 0;

        	for (Iterator<Shape> i=font.getShapes().iterator(); i.hasNext(); glyphIndex++) {
        		glyphTable[glyphIndex] = new Glyph(i.next());
        	}

        	glyphIndex = 0;

        	for (Integer code : font.getCodes())
        	{
        		maxChar = (char) (code > maxChar ? code : maxChar);
        	}

            charToGlyph = new int[maxChar+1];

            int code;
            	
        	for (Iterator<Integer> i=font.getCodes().iterator(); i.hasNext(); glyphIndex++)
        	{
        		code = i.next();

        		charToGlyph[code] = glyphIndex;
        		glyphToChar[glyphIndex] = code;
        	}

            if (font.getAdvances() != null)
        	{
            	glyphIndex = 0;  	
            	
            	for (Iterator<Integer> i = font.getAdvances().iterator(); i.hasNext(); glyphIndex++) {
            		glyphTable[glyphIndex].setAdvance(i.next());
            	}
        	}
        	
        	if (font.getBounds() != null)
        	{
            	glyphIndex = 0;
            	
                	for (Iterator<Bounds> i = font.getBounds().iterator(); i.hasNext(); glyphIndex++) {
                		glyphTable[glyphIndex].setBounds(i.next());
                	}
        	}
    	}
    }
    
    /**
     * Initialise this object with the font information decoded from a Java 
     * AWT Font object.
     * 
     * @param aFont the AWT Font object to be decoded.
     */
    public void decode(java.awt.Font aFont)
    {
        FontRenderContext fontContext = new FontRenderContext(new AffineTransform(), true, true);
        java.awt.Font font = aFont.deriveFont(1.0f);

        name = font.getName();
        encoding = CharacterEncoding.UCS2;

        Rectangle2D transform = transformToEMSquare(font, fontContext);

        double scaleY = 1024.0;
        double scaleX = scaleY;
        
        double translateX = 0;
        double translateY = 0;

        /*
         The new font scaled to the EM Square must be derived using the size as well 
         as the transform used for the glyphs otherwise the advance values are not 
         scaled accordingly.
         */
        AffineTransform affine = AffineTransform.getTranslateInstance(translateX, translateY);
 
        font = font.deriveFont(affine);
        font = font.deriveFont((float)scaleX);

        missingGlyph = font.getMissingGlyphCode();

        bold = font.isBold();
        italic = font.isItalic();

        int count = font.getNumGlyphs();
        int index = 0;
        int code = 0;
        char character;
    
        glyphTable = new Glyph[count];
        charToGlyph = new int[65536];
        glyphToChar = new int[count];
        
        // create the glyph for the characters that cannot be displayed
        
        GlyphVector glyphVector = font.createGlyphVector(fontContext, new int[] { missingGlyph });       
        java.awt.Shape outline = glyphVector.getGlyphOutline(0);
        int advance = (int) (glyphVector.getGlyphMetrics(0).getAdvance());
		glyphTable[index] = new Glyph(convertShape(outline), new Bounds(0, 0, 0, 0), advance);
		charToGlyph[code] = index;
		glyphToChar[index] = code;
   
		index = 1;
		
        /*
         * Run through all the unicode character codes looking for a corresponding glyph.
         */
        while ((index < count) && (code < 65536))
        {
            character = font.canDisplay(code) ? (char)code : (char)missingGlyph;
    
            glyphVector = font.createGlyphVector(fontContext, new char[] { character });
         
            outline = glyphVector.getGlyphOutline(0);
            advance = (int) (glyphVector.getGlyphMetrics(0).getAdvance());

			glyphTable[index] = new Glyph(convertShape(outline), new Bounds(0, 0, 0, 0), advance);
			charToGlyph[character] = index;
			glyphToChar[index] = character;

			if (!font.hasUniformLineMetrics())
			{
				LineMetrics lineMetrics = font.getLineMetrics(new char[] { character }, 0, 1, fontContext);

				ascent = Math.max(lineMetrics.getAscent(), ascent);
				descent = Math.max(lineMetrics.getDescent(), descent);
				leading = Math.max(lineMetrics.getLeading(), leading);
			}

            if (character != missingGlyph) {
            	index++;
            }
            	
            code++;
        }
    }
     
    /**
     * Initialise this object with the font information decoded from a TrueType 
     * or OpenType font stored in a file.
     * 
     * @param path the path to the file containing the font.
     * 
     * @throws FileNotFoundException is the file cannot be found or opened.
     * @throws IOException if there is an error reading the file.
     * @throws DataFormatException if the there is an error decoding the font.
     */
    public void decode(String path) throws FileNotFoundException, IOException, DataFormatException
    {
    	decode(loadFile(new File(path)));
    }
    
    /**
     * Initialise this object with the font information decoded from a TrueType 
     * or OpenType font stored in a file.
     * 
     * @param file the File containing the abstract path to the file containing the font.
     * 
     * @throws FileNotFoundException is the file cannot be found or opened.
     * @throws IOException if there is an error reading the file.
     * @throws DataFormatException if the there is an error decoding the font.
     */
    public void decode(File file) throws CoderException, FileNotFoundException, IOException, DataFormatException
    {
    	decode(loadFile(file));
    }
    
    private void decode(byte[] bytes) throws CoderException
    {
        BigEndianDecoder coder = new BigEndianDecoder(bytes);
        
        /* float version = */ coder.readBits(32, true);

        int tableCount = coder.readWord(2, false);
        /* int searchRange = */ coder.readWord(2, false);
        /* int entrySelector = */ coder.readWord(2, false);
        /* int rangeShift = */ coder.readWord(2, false);
        
        int os_2Offset = 0;
        int headOffset = 0;
        int hheaOffset = 0;
        int maxpOffset = 0;
        int locaOffset = 0;
        int cmapOffset = 0;
        int glyfOffset = 0;
        int hmtxOffset = 0;
        int nameOffset = 0;
        
        int os_2Length = 0;
        int headLength = 0;
        int hheaLength = 0;
        int maxpLength = 0;
        int locaLength = 0;
        int cmapLength = 0;
        int hmtxLength = 0;
        int nameLength = 0;
        int glyfLength = 0;

        int chunkType;
        int checksum;
        int offset;
        int length;
        
        for (int i=0; i<tableCount; i++) 
        {
            chunkType = coder.readWord(4, false);
            checksum = coder.readWord(4, false);
            offset = coder.readWord(4, false) << 3;
            length = coder.readWord(4, false);
            
            /* 
             * Chunks are encoded in ascending alphabetical order so
             * the location of the tables is mapped before they are 
             * decoded since the glyphs come before the loca or maxp
             * table which identify how many glyphs are encoded.
             */
            switch (chunkType)
            {
                case OS_2: os_2Offset = offset; os_2Length = length; break;
                case CMAP: cmapOffset = offset; cmapLength = length; break;
                case GLYF: glyfOffset = offset; glyfLength = length; break;
                case HEAD: headOffset = offset; headLength = length; break;
                case HHEA: hheaOffset = offset; hheaLength = length; break;
                case HMTX: hmtxOffset = offset; hmtxLength = length; break;
                case LOCA: locaOffset = offset; locaLength = length; break;
                case MAXP: maxpOffset = offset; maxpLength = length; break;
                case NAME: nameOffset = offset; nameLength = length; break;
                default: break;
            }
        }
        
        int bytesRead;

        if (maxpOffset != 0) { 
        	coder.setPointer(maxpOffset); 
        	decodeMAXP(coder); 
        	bytesRead = (coder.getPointer() - maxpOffset) >> 3; 
        }
        if (os_2Offset != 0) 
        { 
        	coder.setPointer(os_2Offset); 
        	decodeOS2(coder); 
        	bytesRead = (coder.getPointer() - os_2Offset) >> 3; 
        }
        if (headOffset != 0) 
        { 
        	coder.setPointer(headOffset); 
        	decodeHEAD(coder); 
        	bytesRead = (coder.getPointer() - headOffset) >> 3; 
        }
        if (hheaOffset != 0) 
        { 
        	coder.setPointer(hheaOffset);
        	decodeHHEA(coder); 
        	bytesRead = (coder.getPointer() - hheaOffset) >> 3; 
        }
        if (nameOffset != 0) 
        { 
        	coder.setPointer(nameOffset); 
        	decodeNAME(coder); 
        	bytesRead = (coder.getPointer() - nameOffset) >> 3; 
        }
        
        glyphTable = new Glyph[glyphCount];
        charToGlyph = new int[65536];
        glyphToChar = new int[glyphCount];
        
        // Decode glyphs first so objects will be created.
        if (locaOffset != 0) 
        { 
        	coder.setPointer(locaOffset); 
        	decodeGlyphs(coder, glyfOffset); 
        	bytesRead = (coder.getPointer() - locaOffset) >> 3; 
        }
        if (hmtxOffset != 0) 
        { 
        	coder.setPointer(hmtxOffset); 
        	decodeHMTX(coder); 
        	bytesRead = (coder.getPointer() - hmtxOffset) >> 3; 
        }
        if (cmapOffset != 0) 
        { 
        	coder.setPointer(cmapOffset); 
        	decodeCMAP(coder); 
        	bytesRead = (coder.getPointer() - cmapOffset) >> 3;
        }
    }

    /**
     * Returns name of the font face which contains the name of the font followed
     * by "Bold" for bold fonts and "Italic" for fonts with an italic style. 
     */
    public String getFace() 
    {
		return name+(bold ? " Bold" : "")+(italic ? " Italic" : "");
	}

    /**
     * Returns the (family) name of the font.
     */
    public String getName() 
    {
		return name;
	}

    /**
     * Returns true if the font weight is bold or false if it is normal.
     */
	public boolean isBold()
	{
		return bold;
	}

    /**
     * Returns true if the font style is italic or false if it is normal.
     */
	public boolean isItalic()
	{
		return italic;
	}
	
    /**
     * Returns the code for the font style, as defined in the AWT Font class,
     * either Font.PLAIN or a combination of Font.BOLD and Font.ITALIC.
     */
	public int getStyle()
	{
		int style = java.awt.Font.PLAIN;
		
		if (bold && !italic) {
			style = java.awt.Font.BOLD;
		}
		else if (bold && italic) {
			style = java.awt.Font.BOLD + java.awt.Font.ITALIC;	
		}
		else if (!bold && italic) {
			style = java.awt.Font.ITALIC;	
		}
		
		return style;
	}

	/**
	 * Returns the encoding scheme used for the character codes, either 
	 * UCS2, ANSI or SJIS.
	 */
    public CharacterEncoding getEncoding()
	{
		return encoding;
	}
    
    /**
     * Returns the ascent (maximum distance) above the baseline for the font.
     */
	public float getAscent()
	{
		return ascent;
	}

    /**
     * Returns the descent (maximum distance) below the baseline for the font.
     */
	public float getDescent()
	{
		return descent;
	}

	/**
	 * Returns the amount of space between lines.
	 */
	public float getLeading()
	{
		return leading;
	}

	/**
	 * Returns the number of glyphs defined in the font.
	 */
	public int numberOfGlyphs()
	{
		return glyphCount;
	}

	/**
	 * Returns the highest character code defined in the font.
	 */
	public char highestChar()
	{
		return maxChar;
	}

	public int getMissingGlyph()
	{
		return missingGlyph;
	}

	/**
	 * Create and return a DefineFont2 object that contains information to 
	 * display a set of characters.
	 * 
	 * @param identifier the unique identifier that will be used to reference 
	 * the font definition in the flash file.
	 * 
	 * @param characters the set of characters that the font must contain 
	 * glyphs and layout information for,
	 * 
	 * @return a font definition that contains information for all the glyphs 
	 * in the set of characters.
	 */
	public DefineFont2 defineFont(int identifier, Set<Character>characters)
	{
		List<Character>list = new ArrayList<Character>(characters);
		Collections.sort(list);
		
		char[] array = new char[list.size()];
		int index = 0;

		for (Character character : list) {
			array[index++] = character;
		}
		
		return defineFont(identifier, array);
	}
	
	private DefineFont2 defineFont(int identifier, char[] characters)
	{
        DefineFont2 fontDefinition = null;

        int count = characters.length;

        ArrayList<Shape> glyphsArray = new ArrayList<Shape>(count);
        ArrayList<Integer> codesArray = new ArrayList<Integer>(count);
        ArrayList<Integer> advancesArray = new ArrayList<Integer>(count);
        ArrayList<Bounds> boundsArray = new ArrayList<Bounds>(count);

        for (int i=0; i<characters.length; i++)
        {
            Glyph glyph = glyphTable[charToGlyph[characters[i]]];

            glyphsArray.add(glyph.getShape());
            codesArray.add((int)characters[i]);
            advancesArray.add(glyph.getAdvance());
            
            if (glyph.getBounds() != null) {
            	boundsArray.add(glyph.getBounds());
            }
        }

        fontDefinition = new DefineFont2(identifier, name);

        fontDefinition.setEncoding(encoding);
        fontDefinition.setItalic(italic);
        fontDefinition.setBold(bold);
        fontDefinition.setAscent((int)ascent);
        fontDefinition.setDescent((int)descent);
        fontDefinition.setLeading((int)leading);
        fontDefinition.setShapes(glyphsArray);
        fontDefinition.setCodes(codesArray);
        fontDefinition.setAdvances(advancesArray);
        fontDefinition.setBounds(boundsArray);
        fontDefinition.setKernings(kernings);

        return fontDefinition;
	}

	/**
	 * Tests whether the font can display all the characters in a string.
	 * 
	 * @param aString the string to be displayed.
	 * 
	 * @return the index of the character that cannot be displayed or -1 if all
	 * characters have corresponding glyphs.
	 */
	public int canDisplay(String aString)
	{
		int firstMissingChar = -1;

		for (int i=0; i<aString.length(); i++)
		{
			if (!canDisplay(aString.charAt(i))) {
				firstMissingChar = i;
				break;
			}
		}
		return firstMissingChar;
	}

	/**
	 * Tests whether a character can be displayed by the font or whether the 
	 * "missing" character glyph (usually an empty box) will be displayed instead.
	 *  
	 * @param character the character to be displayed.
	 * 
	 * @return true if the font contains a glyph for character or false if there 
	 * is no corresponding glyph and the missing character glyph will be displayed.
	 */
	public boolean canDisplay(char character)
	{
		boolean canDisplay;

		if (character < charToGlyph.length && (character == ' ' || charToGlyph[character] != 0)) {
			canDisplay = true;
		}
		else {
			canDisplay = false;
		}

		return canDisplay;
	}

	/**
	 * Returns the glyph for the specified character.
	 * 
	 * @param character the character.
	 * 
	 * @return the Glyph object which contains the layout information.
	 */
	public int glyphForCharacter(char character)
	{
		return charToGlyph[character];
	}

	/**
	 * Returns the characters code for the glyph at a specified index in the 
	 * font. This method is used for extracting the strings displayed by static 
	 * text (DefineText, DefineText2) fields.
	 * 
	 * @param index the index of the font.
	 * 
	 * @return the character code for the glyph.
	 */
	public char characterForGlyph(int index)
	{
		return (char)glyphToChar[index];
	}

	/**
	 * Returns the default advance for the font as defined in the EM Square -
	 * conceptually a font with a point size of 1024. The number returned needs 
	 * to be scaled to the correct size in order to calculate the advance to
	 * the next character.
	 * 
	 * @param character the character code.
	 * @return the advance in twips to the next character.
	 */
	public int advanceForCharacter(char character)
	{
		return glyphTable[charToGlyph[character]].getAdvance();
	}
	
	public int fontKey()
	{
    	int value = 17;
    	
    	value = value*31 + name.toLowerCase().hashCode();
    	value = value*31 + (bold ? 1 : 0);
    	value = value*31 + (italic ? 1 : 0);

    	return value;
	}
    
    private Rectangle2D transformToEMSquare(java.awt.Font font, FontRenderContext fontContext)
    {
        int numGlyphs = font.getNumGlyphs();
        int characterCode = 0;
        int glyphIndex = 0;

        double xCoord = 0.0;
        double yCoord = 0.0;
        double width = 0.0;
        double height = 0.0;

        /*
         * Scan through all the glyphs looking for glyphs that will fall outside 
         * the left or bottom side of the EM Square once the glyph has been scaled.
         */
        while ((glyphIndex < numGlyphs) && (characterCode < 65536)) 
        {
            char currentChar = (char) characterCode;

            if (font.canDisplay(currentChar)) 
            {
                GlyphVector glyphVector = font.createGlyphVector(fontContext,
                    new char[] { currentChar });
                Rectangle2D bounds = glyphVector.getGlyphOutline(0).getBounds2D();
    
                xCoord = Math.min(bounds.getX(), xCoord);
                yCoord = Math.min(bounds.getY(), yCoord);
    
                width = Math.max(bounds.getWidth(), width);
                height = Math.max(bounds.getHeight(), height);
    
                glyphIndex++;
            }
            characterCode++;
        }
        return new Rectangle2D.Double(xCoord, yCoord, width, height);
    }

    private Shape convertShape(java.awt.Shape glyph)
    {
        PathIterator pathIter = glyph.getPathIterator(null);
        Canvas path = new Canvas(false);
        
        double[] coords = new double[6];

        while (!pathIter.isDone())
        {
            int segmentType = pathIter.currentSegment(coords);

            int point1 = (int) (coords[0]);
            int point2 = (int) (coords[1]);
            int point3 = (int) (coords[2]);
            int point4 = (int) (coords[3]);
            int point5 = (int) (coords[4]);
            int point6 = (int) (coords[5]);

            switch (segmentType)
            {
                case PathIterator.SEG_MOVETO:
                    path.close();
                    path.moveForFont(point1, point2);
                    break;
                case PathIterator.SEG_LINETO:
                    path.line(point1, point2);
                    break;
                case PathIterator.SEG_QUADTO:
                    path.curve(point1, point2, point3, point4);
                    break;
                case PathIterator.SEG_CUBICTO:
                    path.curve(point1, point2, point3, point4, point5, point6);
                    break;
                case PathIterator.SEG_CLOSE:
                    path.close();
                    break;
                default:
                	break;
            }
            pathIter.next();
        }
        return path.getShape();
    }

    private void decodeHEAD(BigEndianDecoder coder)
    {
        byte[] date = new byte[8];
    
        coder.readBits(32, true); // table version fixed 16 
        coder.readBits(32, true); // font version fixed 16
        coder.readWord(4, false); // checksum adjustment
        coder.readWord(4, false); // magic number
        coder.readBits(1, false); // baseline at y=0
        coder.readBits(1, false); // side bearing at x=0;
        coder.readBits(1, false); // instructions depend on point size
        coder.readBits(1, false); // force ppem to integer values
        coder.readBits(1, false); // instructions may alter advance
        coder.readBits(11, false);
        scale = coder.readWord(2, false) / 1024;  // units per em

        if (scale == 0) {
            scale = 1;
        }

        coder.readBytes(date); // number of seconds since midnight, Jan 01 1904
        coder.readBytes(date); // number of seconds since midnight, Jan 01 1904
    
        coder.readWord(2, true); // xMin for all glyph bounding boxes
        coder.readWord(2, true); // yMin for all glyph bounding boxes
        coder.readWord(2, true); // xMax for all glyph bounding boxes
        coder.readWord(2, true); // yMax for all glyph bounding boxes
    
        /*
         * Next two byte define font appearance on Macs, values are 
         * specified in the OS/2 table 
         */ 
        bold = coder.readBits(1, false) != 0;
        italic = coder.readBits(1, false) != 0;
        coder.readBits(14, false); // 

        coder.readWord(2, false);// smallest readable size in pixels
        coder.readWord(2, true); // font direction hint
        glyphOffset = coder.readWord(2, true); 
        coder.readWord(2, true); // glyph data format
    }
    
    private void decodeHHEA(BigEndianDecoder coder)
    {
        coder.readBits(32, true); // table version, fixed 16
    
        ascent = coder.readWord(2, true);
        descent = coder.readWord(2, true);
        leading = coder.readWord(2, true);
        
        coder.readWord(2, false); // maximum advance in the htmx table
        coder.readWord(2, true); // minimum left side bearing in the htmx table
        coder.readWord(2, true); // minimum right side bearing in the htmx table
        coder.readWord(2, true); // maximum extent
        coder.readWord(2, true); // caret slope rise 
        coder.readWord(2, true); // caret slope run
        coder.readWord(2, true); // caret offset
    
        coder.readWord(2, false); // reserved
        coder.readWord(2, false); // reserved
        coder.readWord(2, false); // reserved
        coder.readWord(2, false); // reserved
    
        coder.readWord(2, true); // metric data format 
        
        metrics = coder.readWord(2, false);    
    }

    private void decodeOS2(BigEndianDecoder coder)
    {
        byte[] panose = new byte[10];
        int[] unicodeRange = new int[4];
        byte[] vendor = new byte[4];

        int version = coder.readWord(2, false); // version
        coder.readWord(2, true); // average character width
        
        int weight = coder.readWord(2, false);
        
        if (weight == WEIGHT_BOLD) {
        	bold = true;
        }

        coder.readWord(2, false); // width class
        coder.readWord(2, false); // embedding licence
        
        coder.readWord(2, true); // subscript x size
        coder.readWord(2, true); // subscript y size
        coder.readWord(2, true); // subscript x offset
        coder.readWord(2, true); // subscript y offset
        coder.readWord(2, true); // superscript x size
        coder.readWord(2, true); // superscript y size
        coder.readWord(2, true); // superscript x offset
        coder.readWord(2, true); // superscript y offset
        coder.readWord(2, true); // width of strikeout stroke
        coder.readWord(2, true); // strikeout stroke position
        coder.readWord(2, true); // font family class
    
        coder.readBytes(panose);
        
        for (int i=0; i<4; i++) {
            unicodeRange[i] = coder.readWord(4, false);
        }
            
        coder.readBytes(vendor); // font vendor identification
        
        italic = coder.readBits(1, false) != 0;
        coder.readBits(4, false);
        bold = coder.readBits(1, false) != 0;
        coder.readBits(10, false);
        
        coder.readWord(2, false); // first unicode character code
        coder.readWord(2, false); // last unicode character code
        
        ascent = coder.readWord(2, false);
        descent = coder.readWord(2, false);
        leading = coder.readWord(2, false);
    
        coder.readWord(2, false); // ascent in Windows
        coder.readWord(2, false); // descent in Windows
        
        if (version > 0)
        {
            coder.readWord(4, false); // code page range 
            coder.readWord(4, false); // code page range

            if (version > 1)
            {
                coder.readWord(2, true); // height
                coder.readWord(2, true); // Capitals height
                missingGlyph = coder.readWord(2, false);
                coder.readWord(2, false); // break character
                coder.readWord(2, false); // maximum context
            }
        }
    }

    private void decodeNAME(BigEndianDecoder coder)
    {
    	int stringTableBase = coder.getPointer() >>> 3;
    	
        int format = coder.readWord(2, false);
        int names = coder.readWord(2, false);
        int stringTable = coder.readWord(2, false) + stringTableBase;
        
        for (int i=0; i<names; i++) 
        {
            int platformId = coder.readWord(2, false);
            int encodingId = coder.readWord(2, false);
            int languageId = coder.readWord(2, false);
            int nameId = coder.readWord(2, false);
            
            int stringLength = coder.readWord(2, false);
            int stringOffset = coder.readWord(2, false);
            
            int current = coder.getPointer();
            
            coder.setPointer((stringTable+stringOffset) << 3);
            byte[] bytes = new byte[stringLength];
            coder.readBytes(bytes);
            
            String nameEncoding = "UTF-8";
            
            if (platformId == 0) // Unicode
            {
                nameEncoding = "UTF-16";
            }
            else if (platformId == 1) // Macintosh
            {
                if (encodingId == 0 && languageId == 0) {
                    nameEncoding = "ISO8859-1";
                }
            }
            else if (platformId == 3) // Microsoft
            {
                switch (encodingId)
                {
                    case 1: nameEncoding = "UTF-16"; break;
                    case 2: nameEncoding = "SJIS"; break;
                    case 4: nameEncoding = "Big5"; break;
                    default: nameEncoding = "UTF-8"; break;
                }
            }
            
            try
            {
                if (nameId == 1) {
                    name = new String(bytes, nameEncoding);
                }
            }
            catch (UnsupportedEncodingException e)
            {
                name = new String(bytes);
            } 
            coder.setPointer(current);
        }
    }

    private void decodeMAXP(BigEndianDecoder coder)
    {
        float version = coder.readBits(32, true)/65536.0f;
        glyphCount = coder.readWord(2, false);
        
        if (version == 1.0)
        {
            coder.readWord(2, false); // maximum number of points in a simple glyph
            coder.readWord(2, false); // maximum number of contours in a simple glyph
            coder.readWord(2, false); // maximum number of points in a composite glyph
            coder.readWord(2, false); // maximum number of contours in a composite glyph
            coder.readWord(2, false); // maximum number of zones
            coder.readWord(2, false); // maximum number of point in Z0
            coder.readWord(2, false); // number of storage area locations
            coder.readWord(2, false); // maximum number of FDEFs
            coder.readWord(2, false); // maximum number of IDEFs
            coder.readWord(2, false); // maximum stack depth
            coder.readWord(2, false); // maximum byte count for glyph instructions
            coder.readWord(2, false); // maximum number of components for composite glyphs
            coder.readWord(2, false); // maximum level of recursion
        }
    }
 
    private void decodeHMTX(BigEndianDecoder coder)
    {
        int index = 0;
        
        for (index=0; index<metrics; index++) 
        {
            glyphTable[index].setAdvance((coder.readWord(2, false) / scale));
            coder.readWord(2, true); // left side bearing
        }
        
        int advance = glyphTable[index-1].getAdvance();

        while (index<glyphCount) {
            glyphTable[index++].setAdvance(advance);
        }

        while (index<glyphCount) {
            coder.readWord(2, true);
            index++;
        }
    }
    
    private void decodeCMAP(BigEndianDecoder coder)
    {
        int tableStart = coder.getPointer();
        
        int version = coder.readWord(2, false);
        int numberOfTables = coder.readWord(2, false);
        
        int platformId = 0;
        int encodingId = 0;
        int offset = 0;
        int current = 0;
        
        int format = 0;
        int length = 0;
        int language = 0;
        
        int segmentCount = 0;
        int[] startCount = null;
        int[] endCount = null;
        int[] delta = null;
        int[] range = null;
        int[] rangeAdr = null;
        
        int tableCount = 0;
        int index = 0;
        
        for (tableCount=0; tableCount<numberOfTables; tableCount++)
        {
            platformId = coder.readWord(2, false);
            encodingId = coder.readWord(2, false);
            offset = coder.readWord(4, false) << 3;
            current = coder.getPointer();
            
            if (platformId == 0) // Unicode
            {
                encoding = CharacterEncoding.UCS2;
            }
            else if (platformId == 1) // Macintosh
            {
            	if (encodingId == 1) {
            		encoding = CharacterEncoding.SJIS;	
            	}
            	else {
            		encoding = CharacterEncoding.ANSI;
            	}
            }
            else if (platformId == 3) // Microsoft
            {
            	if (encodingId == 1) {
            		encoding = CharacterEncoding.UCS2;
            	}
            	else if (encodingId == 2) {
            		encoding = CharacterEncoding.SJIS;
            	}
            	else {
            		encoding = CharacterEncoding.ANSI;
            	}
            }

            coder.setPointer(tableStart+offset);
            
            format = coder.readWord(2, false);
            length = coder.readWord(2, false);
            language = coder.readWord(2, false);
            
            switch (format) 
            {
                case 0: 
                    for (index=0; index<256; index++) {
                        charToGlyph[index] = coder.readByte();
                        glyphToChar[charToGlyph[index]] = index;
                    }
                    break;
                case 4:
                    segmentCount = coder.readWord(2, false) / 2;
    
                    coder.readWord(2, false); // search range
                    coder.readWord(2, false); // entry selector
                    coder.readWord(2, false); // range shift

                    startCount = new int[segmentCount];
                    endCount = new int[segmentCount];
                    delta = new int[segmentCount];
                    range = new int[segmentCount];
                    rangeAdr = new int[segmentCount];
                    
                    for (index=0; index<segmentCount; index++) {
                        endCount[index] = coder.readWord(2, false);
                    }

                    coder.readWord(2, false); // reserved padding
                
                    for (index=0; index<segmentCount; index++) {
                        startCount[index] = coder.readWord(2, false);
                    }
                
                    for (index=0; index<segmentCount; index++) {
                        delta[index] = coder.readWord(2, true);
                    }
                
                    for (index=0; index<segmentCount; index++)
                    {
                        rangeAdr[index] = coder.getPointer() >> 3;
                        range[index] = coder.readWord(2, true);
                    }

                    int glyphIndex = 0;
                    int location = 0;
                            
                    for (index=0; index<segmentCount; index++)
                    {
                        for (int code=startCount[index]; code<=endCount[index]; code++)
                        {
                            if (range[index] == 0)
                            {
                                glyphIndex = (delta[index] + code) % 65536;
                            }
                            else {
                                location = rangeAdr[index] + range[index] + ((code - startCount[index]) << 1);
                                coder.setPointer(location << 3);
                                glyphIndex = coder.readWord(2, false);
                                
                                if (glyphIndex != 0) {
                                    glyphIndex = (glyphIndex + delta[index]) % 65536;
                                }
                            }

                            charToGlyph[code] = glyphIndex;
                            glyphToChar[glyphIndex] = code;
                        }
                    }
                    break;
                case 2:
                case 6: 
                    break;
                default: 
                    break;
            }
            coder.setPointer(current);
        }
        encoding = CharacterEncoding.SJIS;
    }
    
    private void decodeGlyphs(BigEndianDecoder coder, int glyfOffset) throws CoderException
    {
        int numberOfContours = 0;
        int glyphStart = 0;
        int start = coder.getPointer();
        int end = 0;
        int[] offsets = new int[glyphCount];
        
        if (glyphOffset == ITLF_SHORT) {
            offsets[0] = glyfOffset + (coder.readWord(2, false)*2 << 3);
        }
        else {
            offsets[0] = glyfOffset + (coder.readWord(4, false) << 3);
        }

        for (int i=1; i<glyphCount; i++)
        {
            if (glyphOffset == ITLF_SHORT) {
                offsets[i] = glyfOffset + (coder.readWord(2, false)*2 << 3);
            }
            else {
                offsets[i] = glyfOffset + (coder.readWord(4, false) << 3);
            }
            
            if (offsets[i] == offsets[i-1]) {
                offsets[i-1] = 0;
            }
        }
        
        end = coder.getPointer();
            
        for (int i=0; i<glyphCount; i++)
        {
            if (offsets[i] == 0)
            {
                glyphTable[i] = new Glyph(new Shape(new ArrayList<ShapeRecord>()), new Bounds(0, 0, 0, 0), 0);
            }
            else
            {
                coder.setPointer(offsets[i]);
                
                numberOfContours = coder.readWord(2, true);
                
                if (numberOfContours >= 0) {
                    decodeSimpleGlyph(coder, i, numberOfContours);
                }
            }
        }
        
        coder.setPointer(start);

        for (int i=0; i<glyphCount; i++)
        {
            if (offsets[i] != 0)
            {
                coder.setPointer(offsets[i]);
            
                if (coder.readWord(2, true) == -1) {
                    decodeCompositeGlyph(coder, i);
                }
            }
        }
        coder.setPointer(end);
    }

    private void decodeSimpleGlyph(BigEndianDecoder coder, int glyphIndex, int numberOfContours)
    {
        int xMin = coder.readWord(2, true) / scale;
        int yMin = coder.readWord(2, true) / scale;
        int xMax = coder.readWord(2, true) / scale;
        int yMax = coder.readWord(2, true) / scale;
        
        int[] endPtsOfContours = new int[numberOfContours];

        for (int i=0; i<numberOfContours; i++) {
            endPtsOfContours[i] = coder.readWord(2, false);
        }

        int instructionCount = coder.readWord(2, false);   
        int[] instructions = new int[instructionCount];
            
        for (int i=0; i<instructionCount; i++) {
            instructions[i] = coder.readByte();
        }
                
        int numberOfPoints = (numberOfContours == 0) ? 0 : endPtsOfContours[endPtsOfContours.length-1]+1;
        
        int[] flags = new int[numberOfPoints];
        int[] xCoordinates = new int[numberOfPoints];
        int[] yCoordinates = new int[numberOfPoints];
        boolean[] onCurve = new boolean[numberOfPoints];
        
        int repeatCount = 0;
        int repeatFlag  = 0;
            
        for (int i=0; i<numberOfPoints; i++) 
        {
            if (repeatCount > 0) 
            {
                flags[i] = repeatFlag;
                repeatCount--;
            } 
            else 
            {
                flags[i] = coder.readByte();
                    
                if ((flags[i] & REPEAT_FLAG) > 0) 
                {
                    repeatCount = coder.readByte();
                    repeatFlag = flags[i];
                }
            }
            onCurve[i] = (flags[i] & ON_CURVE) > 0;
        }

        int last = 0;

        for (int i=0; i<numberOfPoints; i++) 
        {        
            if ((flags[i] & X_SHORT) > 0) 
            {
                if ((flags[i] & X_POSITIVE) > 0) {
                    xCoordinates[i] = last + coder.readByte();
                    last = xCoordinates[i];
                }
                else {
                    xCoordinates[i] = last - coder.readByte();
                    last = xCoordinates[i];
                }
            } 
            else 
            {
                if ((flags[i] & X_SAME) > 0) {
                    xCoordinates[i] = last;
                }
                else {
                    xCoordinates[i] = last + coder.readWord(2, true);
                    last = xCoordinates[i];
                }
            }
        }

        last = 0;

        for (int i = 0; i < numberOfPoints; i++) 
        {
            if ((flags[i] & Y_SHORT) > 0)
            {
                if ((flags[i] & Y_POSITIVE) > 0) {
                    yCoordinates[i] = last + coder.readByte();
                    last = yCoordinates[i];
                }
                else {
                    yCoordinates[i] = last - coder.readByte();
                    last = yCoordinates[i];
                }
            } 
            else 
            {
                if ((flags[i] & Y_SAME) > 0) {
                    yCoordinates[i] = last;
                }
                else {
                    yCoordinates[i] = last + coder.readWord(2, true);
                    last = yCoordinates[i];
                }
            }
        }
            
        /*
         * Convert the coordinates into a shape
         */
        Canvas path = new Canvas(false);
        
        boolean contourStart = true;
        boolean offPoint = false;
        
        int contour = 0;
        
        int xCoord = 0;
        int yCoord = 0;
        
        int prevX = 0;
        int prevY = 0;
        
        int initX = 0;
        int initY = 0;
        
        for (int i=0; i<numberOfPoints; i++)
        {
            xCoord = xCoordinates[i] / scale;
            yCoord = yCoordinates[i] / scale;
            
            if (onCurve[i])
            {
                if (contourStart)
                {
                    path.moveForFont(xCoord, -yCoord);
                    contourStart = false;
                    initX = xCoord;
                    initY = yCoord;
                }
                else if (offPoint)
                {
                    path.curve(prevX, -prevY, xCoord, -yCoord);
                    offPoint = false;
                }
                else
                {
                    path.line(xCoord, -yCoord);
                }
            }
            else
            {
                if (offPoint) {
                    path.curve(prevX, -prevY, (xCoord+prevX)/2, -(yCoord+prevY)/2);
                }

                prevX = xCoord;
                prevY = yCoord;
                offPoint = true;
            }
            
            if (i == endPtsOfContours[contour])
            {
                if (offPoint)
                {
                    path.curve(xCoord, -yCoord, initX, -initY);
                }
                else
                {
                    path.close();
                } 
                contourStart = true;
                offPoint = false;
                prevX = 0;
                prevY = 0;
                contour++;
            }
        }
        
        glyphTable[glyphIndex] = new Glyph(path.getShape(),new Bounds(xMin, -yMax, xMax, -yMin), 0);

        //glyphTable[glyphIndex].xCoordinates = xCoordinates;
        //glyphTable[glyphIndex].yCoordinates = yCoordinates;
        //glyphTable[glyphIndex].onCurve = onCurve;
        //glyphTable[glyphIndex].endPoints = endPtsOfContours;
    }
 
    private void decodeCompositeGlyph(BigEndianDecoder coder, int glyphIndex) throws CoderException
    {
        Shape shape = new Shape(new ArrayList<ShapeRecord>());
        CoordTransform transform = null;
                        
        int xMin = coder.readWord(2, true);
        int yMin = coder.readWord(2, true);
        int xMax = coder.readWord(2, true);
        int yMax = coder.readWord(2, true);
        
        Glyph points = null;
        
        int numberOfPoints = 0;
        
        int[] endPtsOfContours = null;
        int[] xCoordinates = null;
        int[] yCoordinates = null;
        boolean[] onCurve = null;
        
        int flags = 0;
        int sourceGlyph = 0;
        
        int xOffset = 0;
        int yOffset = 0;
        
        int sourceIndex = 0;
        int destIndex = 0;

        do {
            flags = coder.readWord(2, false);
            sourceGlyph = coder.readWord(2, false);
            
            if (sourceGlyph >= glyphTable.length || glyphTable[sourceGlyph] == null)
            {
                glyphTable[glyphIndex] = new Glyph(null, new Bounds(xMin, yMin, xMax, yMax), 0);            
                return;
            }
            
            points = glyphTable[sourceGlyph];
            //numberOfPoints = points.xCoordinates.length;
            
            //endPtsOfContours = new int[points.endPoints.length];
            
            /*
            for (int i=0; i<endPtsOfContours.length; i++)
                endPtsOfContours[i] = points.endPoints[i];
                
            xCoordinates = new int[numberOfPoints];
            
            for (int i=0; i<numberOfPoints; i++)
                xCoordinates[i] = points.xCoordinates[i];
                
            yCoordinates = new int[numberOfPoints];
            
            for (int i=0; i<numberOfPoints; i++)
                yCoordinates[i] = points.yCoordinates[i];
                
            onCurve = new boolean[numberOfPoints];
            
            for (int i=0; i<numberOfPoints; i++)
                onCurve[i] = points.onCurve[i];
            */
            if ((flags & ARGS_ARE_WORDS) == 0 && (flags & ARGS_ARE_XY) == 0)
            {
                destIndex = coder.readByte();
                sourceIndex = coder.readByte();
                
                //xCoordinates[destIndex] = glyphTable[sourceGlyph].xCoordinates[sourceIndex];
                //yCoordinates[destIndex] = glyphTable[sourceGlyph].yCoordinates[sourceIndex];
            }
            else if ((flags & ARGS_ARE_WORDS) == 0 && (flags & ARGS_ARE_XY) > 0)
            {
                xOffset = (coder.readByte() << 24) >> 24;
                yOffset = (coder.readByte() << 24) >> 24;
            }
            else if ((flags & ARGS_ARE_WORDS) > 0 && (flags & ARGS_ARE_XY) == 0)
            {
                destIndex = coder.readWord(2, false);
                sourceIndex = coder.readWord(2, false);
                
                //xCoordinates[destIndex] = glyphTable[sourceGlyph].xCoordinates[sourceIndex];
                //yCoordinates[destIndex] = glyphTable[sourceGlyph].yCoordinates[sourceIndex];
            }
            else
            {
                xOffset = coder.readWord(2, true);
                yOffset = coder.readWord(2, true);
            }
            
            
            if ((flags & HAVE_SCALE) > 0) 
            {
                float scaleXY = coder.readBits(16, true)/16384.0f;
                transform = new CoordTransform(scaleXY, scaleXY, 0, 0, xOffset, yOffset);
            }
            else if ((flags & HAVE_XYSCALE) > 0)
            {
                float scaleX = coder.readBits(16, true)/16384.0f;
                float scaleY = coder.readBits(16, true)/16384.0f;
                transform = new CoordTransform(scaleX, scaleY, 0, 0, xOffset, yOffset);
            }
            else if ((flags & HAVE_2X2) > 0) 
            {
                float scaleX = coder.readBits(16, true)/16384.0f;
                float scale01 = coder.readBits(16, true)/16384.0f;
                float scale10 = coder.readBits(16, true)/16384.0f;
                float scaleY = coder.readBits(16, true)/16384.0f;
                
                transform = new CoordTransform(scaleX, scaleY, scale01, scale10, xOffset, yOffset);
            }
            
            float[][] matrix = transform.getMatrix();
            float[][] result; 
            
            for (int i=0; i<numberOfPoints; i++)
            {
                result = CoordTransform.product(matrix, 
                		CoordTransform.translate(xCoordinates[i], yCoordinates[i]).getMatrix());
                
                xCoordinates[i] = (int)result[0][2];
                yCoordinates[i] = (int)result[1][2];
            }

            Canvas path = new Canvas(false);
        
            boolean contourStart = true;
            boolean offPoint = false;
        
            int contour = 0;
        
            int xCoord = 0;
            int yCoord = 0;
        
            int prevX = 0;
            int prevY = 0;
        
            int initX = 0;
            int initY = 0;
        
            for (int i=0; i<numberOfPoints; i++)
            {
                xCoord = xCoordinates[i] / scale;
                yCoord = yCoordinates[i] / scale;
            
                if (onCurve[i])
                {
                    if (contourStart)
                    {
                        path.moveForFont(xCoord, -yCoord);
                        contourStart = false;
                        initX = xCoord;
                        initY = yCoord;
                    }
                    else if (offPoint)
                    {
                        path.curve(prevX, -prevY, xCoord, -yCoord);
                        offPoint = false;
                    }
                    else
                    {
                        path.line(xCoord, -yCoord);
                    }
                }
                else
                {
                    if (offPoint) {
                        path.curve(prevX, -prevY, (xCoord+prevX)/2, -(yCoord+prevY)/2);
                    }

                    prevX = xCoord;
                    prevY = yCoord;
                    offPoint = true;
                }
            
                if (i == endPtsOfContours[contour])
                {
                    if (offPoint)
                    {
                        path.curve(xCoord, -yCoord, initX, -initY);
                    }
                    else
                    {
                        path.close();
                    } 
                    contourStart = true;
                    offPoint = false;
                    prevX = 0;
                    prevY = 0;
                    contour++;
                }
            }
            shape.getObjects().addAll(path.getShape().getObjects());
              
        } while ((flags & HAS__MORE) > 0);

        glyphTable[glyphIndex] = new Glyph(shape, new Bounds(xMin, yMin, xMax, yMax), 0);

        //glyphTable[glyphIndex].xCoordinates = xCoordinates;
        //glyphTable[glyphIndex].yCoordinates = yCoordinates;
        //glyphTable[glyphIndex].onCurve = onCurve;
        //glyphTable[glyphIndex].endPoints = endPtsOfContours;
    }
    
	private byte[] loadFile(final File file)
			throws FileNotFoundException, IOException {
		byte[] data = new byte[(int) file.length()];

		FileInputStream stream = null; // NOPMD

		try {
			stream = new FileInputStream(file);
			int bytesRead = stream.read(data);

			if (bytesRead != data.length) {
				throw new IOException(file.getAbsolutePath());
			}
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		return data;
	}
}