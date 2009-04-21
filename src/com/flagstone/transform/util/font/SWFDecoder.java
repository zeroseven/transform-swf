package com.flagstone.transform.util.font;

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.DataFormatException;

import com.flagstone.transform.Bounds;
import com.flagstone.transform.CoordTransform;
import com.flagstone.transform.Movie;
import com.flagstone.transform.MovieTag;
import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.BigEndianDecoder;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.font.CharacterEncoding;
import com.flagstone.transform.font.DefineFont;
import com.flagstone.transform.font.DefineFont2;
import com.flagstone.transform.font.FontInfo;
import com.flagstone.transform.font.FontInfo2;
import com.flagstone.transform.font.Kerning;
import com.flagstone.transform.shape.Shape;
import com.flagstone.transform.shape.ShapeRecord;
import com.flagstone.transform.util.image.ImageInfo;
import com.flagstone.transform.util.shape.Canvas;

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
public final class SWFDecoder implements FontProvider, FontDecoder
{
    private String name;
    private boolean bold;
    private boolean italic;

    private CharacterEncoding encoding;

    private float ascent;
    private float descent;
    private float leading;

    private int[] charToGlyph;
    private int[] glyphToChar;

    private Glyph[] glyphTable;

    private int glyphCount;
    private int missingGlyph;
    private char maxChar;
    
    private List<Kerning> kernings = new ArrayList<Kerning>();
    
    private int scale;
    private int metrics;
    private int glyphOffset;

    public FontDecoder newDecoder() {
    	return new TTFDecoder();
    }
   
    public void read(String path) throws FileNotFoundException, IOException, DataFormatException {
    	decode(loadFile(new File(path)));
    }
    
    public void read(File file) throws FileNotFoundException, IOException, DataFormatException {
    	decode(loadFile(file));
    }
      
    public void read(URL url) throws FileNotFoundException, IOException, DataFormatException
    {
	    URLConnection connection = url.openConnection();

	    int fileSize = connection.getContentLength();
            
	    if (fileSize<0) {
              throw new FileNotFoundException(url.getFile());
	    }
	    
	    byte[] bytes = new byte[fileSize];

	    InputStream stream = url.openStream();
	    BufferedInputStream buffer = new BufferedInputStream(stream);

	    buffer.read(bytes);
	    buffer.close();

    	ImageInfo info = new ImageInfo();
    	info.setInput(new ByteArrayInputStream(bytes));
    	info.setDetermineImageNumber(true);
    	
    	if (!info.check())  {
    		throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT);
    	}
    	
		decode(bytes);
    }

    public Font[] getFonts() {
    	Font[] fonts = null; 
    	
    	return fonts;
    }

    private void decode(byte[] bytes) throws FileNotFoundException, CoderException, IOException, DataFormatException
	{
		Movie movie = new Movie();
		movie.decodeFromData(bytes);
		
		List<Font>list = new ArrayList<Font>();
		
	    SWFDecoder decoder;
	
	    for (MovieTag obj :  movie.getObjects())
	    {
	     	if (obj instanceof DefineFont2) {
	     		decoder = new SWFDecoder();
	     		decoder.decode((DefineFont2)obj);
	     	}
	    }
	}
    
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
    
	private byte[] loadFile(final File file)
			throws FileNotFoundException, IOException {
		byte[] data = new byte[(int) file.length()];

		FileInputStream stream = null; //TODO(code) fix

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