package com.flagstone.transform.util.font;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.datatype.CharacterEncoding;
import com.flagstone.transform.font.DefineFont2;
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
public final class Font {
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

    private final List<Kerning> kernings = new ArrayList<Kerning>();

    private int scale;
    private int metrics;
    private int glyphOffset;

    /**
     * Returns name of the font face which contains the name of the font
     * followed by "Bold" for bold fonts and "Italic" for fonts with an italic
     * style.
     */
    public String getFace() {
        return name + (bold ? " Bold" : "") + (italic ? " Italic" : "");
    }

    /**
     * Returns the (family) name of the font.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns true if the font weight is bold or false if it is normal.
     */
    public boolean isBold() {
        return bold;
    }

    /**
     * Returns true if the font style is italic or false if it is normal.
     */
    public boolean isItalic() {
        return italic;
    }

    /**
     * Returns the code for the font style, as defined in the AWT Font class,
     * either Font.PLAIN or a combination of Font.BOLD and Font.ITALIC.
     */
    public int getStyle() {
        int style = java.awt.Font.PLAIN;

        if (bold && !italic) {
            style = java.awt.Font.BOLD;
        } else if (bold && italic) {
            style = java.awt.Font.BOLD + java.awt.Font.ITALIC;
        } else if (!bold && italic) {
            style = java.awt.Font.ITALIC;
        }

        return style;
    }

    /**
     * Returns the encoding scheme used for the character codes, either UCS2,
     * ANSI or SJIS.
     */
    public CharacterEncoding getEncoding() {
        return encoding;
    }

    /**
     * Returns the ascent (maximum distance) above the baseline for the font.
     */
    public float getAscent() {
        return ascent;
    }

    /**
     * Returns the descent (maximum distance) below the baseline for the font.
     */
    public float getDescent() {
        return descent;
    }

    /**
     * Returns the amount of space between lines.
     */
    public float getLeading() {
        return leading;
    }

    /**
     * Returns the number of glyphs defined in the font.
     */
    public int numberOfGlyphs() {
        return glyphCount;
    }

    /**
     * Returns the highest character code defined in the font.
     */
    public char highestChar() {
        return maxChar;
    }

    /** TODO(method). */
    public int getMissingGlyph() {
        return missingGlyph;
    }

    /**
     * Create and return a DefineFont2 object that contains information to
     * display a set of characters.
     *
     * @param identifier
     *            the unique identifier that will be used to reference the font
     *            definition in the flash file.
     *
     * @param characters
     *            the set of characters that the font must contain glyphs and
     *            layout information for,
     *
     * @return a font definition that contains information for all the glyphs in
     *         the set of characters.
     */
    public DefineFont2 defineFont(final int identifier,
            final Set<Character> characters) {
        final List<Character> list = new ArrayList<Character>(characters);
        Collections.sort(list);

        DefineFont2 fontDefinition = null;
        final int count = list.size();

        final ArrayList<Shape> glyphsArray = new ArrayList<Shape>(count);
        final ArrayList<Integer> codesArray = new ArrayList<Integer>(count);
        final ArrayList<Integer> advancesArray = new ArrayList<Integer>(count);
        final ArrayList<Bounds> boundsArray = new ArrayList<Bounds>(count);

        for (final Character character : list) {
            final Glyph glyph = glyphTable[charToGlyph[character]];

            glyphsArray.add(glyph.getShape());
            codesArray.add((int) character);
            advancesArray.add(glyph.getAdvance());

            if (glyph.getBounds() != null) {
                boundsArray.add(glyph.getBounds());
            }
        }

        fontDefinition = new DefineFont2(identifier, name);

        fontDefinition.setEncoding(encoding);
        fontDefinition.setItalic(italic);
        fontDefinition.setBold(bold);
        fontDefinition.setAscent((int) ascent);
        fontDefinition.setDescent((int) descent);
        fontDefinition.setLeading((int) leading);
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
     * @param aString
     *            the string to be displayed.
     *
     * @return the index of the character that cannot be displayed or -1 if all
     *         characters have corresponding glyphs.
     */
    public int canDisplay(final String aString) {
        int firstMissingChar = -1;

        for (int i = 0; i < aString.length(); i++) {
            if (!canDisplay(aString.charAt(i))) {
                firstMissingChar = i;
                break;
            }
        }
        return firstMissingChar;
    }

    /**
     * Tests whether a character can be displayed by the font or whether the
     * "missing" character glyph (usually an empty box) will be displayed
     * instead.
     *
     * @param character
     *            the character to be displayed.
     *
     * @return true if the font contains a glyph for character or false if there
     *         is no corresponding glyph and the missing character glyph will be
     *         displayed.
     */
    public boolean canDisplay(final char character) {
        boolean canDisplay;

        if ((character < charToGlyph.length)
                && ((character == ' ') || (charToGlyph[character] != 0))) {
            canDisplay = true;
        } else {
            canDisplay = false;
        }

        return canDisplay;
    }

    /**
     * Returns the glyph for the specified character.
     *
     * @param character
     *            the character.
     *
     * @return the Glyph object which contains the layout information.
     */
    public int glyphForCharacter(final char character) {
        return charToGlyph[character];
    }

    /**
     * Returns the characters code for the glyph at a specified index in the
     * font. This method is used for extracting the strings displayed by static
     * text (DefineText, DefineText2) fields.
     *
     * @param index
     *            the index of the font.
     *
     * @return the character code for the glyph.
     */
    public char characterForGlyph(final int index) {
        return (char) glyphToChar[index];
    }

    /**
     * Returns the default advance for the font as defined in the EM Square -
     * conceptually a font with a point size of 1024. The number returned needs
     * to be scaled to the correct size in order to calculate the advance to the
     * next character.
     *
     * @param character
     *            the character code.
     * @return the advance in twips to the next character.
     */
    public int advanceForCharacter(final char character) {
        return glyphTable[charToGlyph[character]].getAdvance();
    }

    /** TODO(method). */
    public int fontKey() {
        int value = 17;

        value = value * 31 + name.toLowerCase(Locale.getDefault()).hashCode();
        value = value * 31 + (bold ? 1 : 0);
        value = value * 31 + (italic ? 1 : 0);

        return value;
    }
}
