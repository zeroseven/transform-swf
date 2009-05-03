package com.flagstone.transform.util.font;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.DataFormatException;

import com.flagstone.transform.Movie;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.font.CharacterEncoding;
import com.flagstone.transform.font.DefineFont;
import com.flagstone.transform.font.DefineFont2;
import com.flagstone.transform.font.FontInfo;
import com.flagstone.transform.font.FontInfo2;
import com.flagstone.transform.font.Kerning;
import com.flagstone.transform.shape.Shape;

/**
 * <p>
 * Font is used to add embedded fonts to a movie.
 * </p>
 *
 * <p>
 * Flash supports two types of font definition: embedded fonts where the Flash
 * file contains the glyphs that are drawn to represents the text characters and
 * device fonts where the font is provided by the Flash Player showing the
 * movie. Embedded fonts are preferred since the movie will always look the same
 * regardless of where it is played - if a Flash Player does not contain a
 * device font it will substitute it with another.
 * </p>
 *
 * <p>
 * Device fonts can be added to a movie by simply creating a DefineFont or
 * DefineFont2 object which contain the name of the font. An embedded font must
 * contain all the information to draw and layout the glyphs representing the
 * text to be displayed. The Font class hides all this detail and makes it easy
 * to add embedded fonts to a movie.
 * <p>
 *
 * <p>
 * The Font class can be used to create embedded fonts in three ways:
 * </p>
 *
 * <ol>
 * <li>Using TrueType or OpenType font definition stored in a file.</li>
 * <li>Using an existing font definition from a flash file.</li>
 * <li>Using a given Java AWT font as a template.</li>
 * </ol>
 *
 * <P>
 * For OpenType or TrueType fonts, files with the extensions ".otf" or ".ttf"
 * may be used. Files containing collections of fonts ".otc" are not currently
 * supported.
 * </p>
 *
 * <p>
 * Using an existing Flash font definition is the most interesting. Fonts can
 * initially be created using AWT Font objects or TrueType files and all the
 * visible characters included. If the generated Flash definition is saved to a
 * file it can easily and quickly be loaded. Indeed the overhead of parsing an
 * AWT or TrueType font is significant (sometimes several seconds) so creating
 * libraries of "pre-parsed" flash fonts is the preferred way of use fonts.
 * </p>
 */
//TODO(class)
public final class SWFFontDecoder implements FontProvider, FontDecoder {

    private transient String name;
    private transient boolean bold;
    private transient boolean italic;

    private transient CharacterEncoding encoding;

    private transient float ascent;
    private transient float descent;
    private transient float leading;

    private transient int[] charToGlyph;
    private transient int[] glyphToChar;

    private transient Glyph[] glyphTable;

    private transient int glyphCount;
    private transient int missingGlyph;
    private transient char maxChar;

    private final transient List<Kerning> kernings = new ArrayList<Kerning>();

    private transient int scale;
    private transient int metrics;
    private transient int glyphOffset;

    /** TODO(method). */
    public FontDecoder newDecoder() {
        return new TTFDecoder();
    }

    /** TODO(method). */
    public void read(final File file) throws IOException, DataFormatException {
        final FileInputStream stream = new FileInputStream(file);
        try {
            decode(stream);
        } finally {
            stream.close();
        }
    }

    /** TODO(method). */
    public void read(final URL url) throws IOException, DataFormatException {
        final URLConnection connection = url.openConnection();

        if (connection.getContentLength() < 0) {
            throw new FileNotFoundException(url.getFile());
        }

        final InputStream stream = connection.getInputStream();

        try {
            decode(stream);
        } finally {
            stream.close();
        }
    }

    /** TODO(method). */
    public Font[] getFonts() {
        final Font[] fonts = null;
        // TODO(implement)
        return fonts;
    }

    private void decode(final InputStream stream) throws IOException, DataFormatException {
        final Movie movie = new Movie();
        movie.decodeFromStream(stream);

        final List<Font> list = new ArrayList<Font>();

        SWFFontDecoder decoder;

        for (final MovieTag obj : movie.getObjects()) {
            if (obj instanceof DefineFont2) {
                decoder = new SWFFontDecoder();
                decoder.decode((DefineFont2) obj);
            }
        }
    }

    /**
     * Initialise this object with the information from a flash font definition.
     *
     * @param glyphs
     *            a DefineFont object which contains the definition of the
     *            glyphs.
     *
     * @param info
     *            a FontInfo object that contains information on the font name,
     *            weight, style and character codes.
     */
    public void decode(final DefineFont glyphs, final FontInfo info) {
        name = info.getName();
        bold = info.isBold();
        italic = info.isItalic();

        encoding = info.getEncoding();

        if (encoding == CharacterEncoding.ANSI) {
            encoding = CharacterEncoding.UCS2;
        }

        ascent = 0;
        descent = 0;
        leading = 0;

        missingGlyph = 0;

        glyphCount = glyphs.getShapes().size();
        glyphTable = new Glyph[glyphCount];
        glyphToChar = new int[glyphCount];

        maxChar = 0;
        charToGlyph = new int[0];

        if (glyphCount > 0) {
            int glyphIndex = 0;

            for (final Iterator<Shape> i = glyphs.getShapes().iterator(); i
                    .hasNext(); glyphIndex++) {
                glyphTable[glyphIndex] = new Glyph(i.next());
            }

            glyphIndex = 0;

            for (final Integer code : info.getCodes()) {
                maxChar = (char) (code > maxChar ? code : maxChar);
            }

            charToGlyph = new int[maxChar + 1];

            for (final Integer code : info.getCodes()) {
                charToGlyph[code] = glyphIndex;
                glyphToChar[glyphIndex] = code;
            }

            /*
             * TODO(code) if (glyphs.getAdvances() != null) { glyphIndex = 0;
             *
             * for (Iterator<Integer> i = font.getAdvances().iterator();
             * i.hasNext(); glyphIndex++)
             * glyphTable[glyphIndex].setAdvance(i.next()); }
             *
             * if (font.getBounds() != null) { glyphIndex = 0;
             *
             * for (Iterator<Bounds> i = font.getBounds().iterator();
             * i.hasNext(); glyphIndex++)
             * glyphTable[glyphIndex].setBounds(i.next()); }
             */
        }
    }

    /**
     * Initialise this object with the information from a flash font definition.
     *
     * @param glyphs
     *            a DefineFont object which contains the definition of the
     *            glyphs.
     *
     * @param info
     *            a FontInfo2 object that contains information on the font name,
     *            weight, style and character codes.
     */
    public void decode(final DefineFont glyphs, final FontInfo2 info) {
        name = info.getName();
        bold = info.isBold();
        italic = info.isItalic();

        encoding = info.getEncoding();

        if (encoding == CharacterEncoding.ANSI) {
            encoding = CharacterEncoding.UCS2;
        }

        // TODO(code) ascent = info.getAscent();
        // TODO(code) descent = info.getDescent();
        // TODO(code) leading = info.getLeading();

        missingGlyph = 0;

        glyphCount = glyphs.getShapes().size();
        glyphTable = new Glyph[glyphCount];
        glyphToChar = new int[glyphCount];

        maxChar = 0;
        charToGlyph = new int[0];

        if (glyphCount > 0) {
            int glyphIndex = 0;

            for (final Iterator<Shape> i = glyphs.getShapes().iterator(); i
                    .hasNext(); glyphIndex++) {
                glyphTable[glyphIndex] = new Glyph(i.next());
            }

            glyphIndex = 0;

            for (final Integer code : info.getCodes()) {
                maxChar = (char) (code > maxChar ? code : maxChar);
            }

            charToGlyph = new int[maxChar + 1];

            int code;

            for (final Iterator<Integer> i = info.getCodes().iterator(); i
                    .hasNext(); glyphIndex++) {
                code = i.next();

                charToGlyph[code] = glyphIndex;
                glyphToChar[glyphIndex] = code;
            }

            /*
             * TODO(code) if (glyphs.getAdvances() != null) { glyphIndex = 0;
             *
             * for (Iterator<Integer> i = font.getAdvances().iterator();
             * i.hasNext(); glyphIndex++)
             * glyphTable[glyphIndex].setAdvance(i.next()); }
             *
             * if (font.getBounds() != null) { glyphIndex = 0;
             *
             * for (Iterator<Bounds> i = font.getBounds().iterator();
             * i.hasNext(); glyphIndex++)
             * glyphTable[glyphIndex].setBounds(i.next()); }
             */
        }
    }

    /**
     * Initialise this object with the information from a flash font definition.
     *
     * @param font
     *            a DefineFont2 object that contains information on the font
     *            name, weight, style and character codes as well as the glyph
     *            definitions.
     */
    public void decode(final DefineFont2 font) {
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

        if (glyphCount > 0) {
            int glyphIndex = 0;

            for (final Iterator<Shape> i = font.getShapes().iterator(); i
                    .hasNext(); glyphIndex++) {
                glyphTable[glyphIndex] = new Glyph(i.next());
            }

            glyphIndex = 0;

            for (final Integer code : font.getCodes()) {
                maxChar = (char) (code > maxChar ? code : maxChar);
            }

            charToGlyph = new int[maxChar + 1];

            int code;

            for (final Iterator<Integer> i = font.getCodes().iterator(); i
                    .hasNext(); glyphIndex++) {
                code = i.next();

                charToGlyph[code] = glyphIndex;
                glyphToChar[glyphIndex] = code;
            }

            if (font.getAdvances() != null) {
                glyphIndex = 0;

                for (final Iterator<Integer> i = font.getAdvances().iterator(); i
                        .hasNext(); glyphIndex++) {
                    glyphTable[glyphIndex].setAdvance(i.next());
                }
            }

            if (font.getBounds() != null) {
                glyphIndex = 0;

                for (final Iterator<Bounds> i = font.getBounds().iterator(); i
                        .hasNext(); glyphIndex++) {
                    glyphTable[glyphIndex].setBounds(i.next());
                }
            }
        }
    }
}
