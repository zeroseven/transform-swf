package com.flagstone.transform.util.font;

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.font.CharacterEncoding;
import com.flagstone.transform.font.Kerning;
import com.flagstone.transform.shape.Shape;
import com.flagstone.transform.util.shape.Canvas;

/** TODO(class). */
public final class AWTDecoder {

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

    /** TODO(method). */
    public void read(final java.awt.Font font)
        throws IOException, DataFormatException {
        decode(font);
    }

    /** TODO(method). */
    public Font[] getFont() {
        final Font[] font = null;
        // TODO(implement)
        return font;
    }

    private void decode(final java.awt.Font aFont) {

        final FontRenderContext fontContext = new FontRenderContext(
                new AffineTransform(), true, true);
        java.awt.Font font = aFont.deriveFont(1.0f);

        name = font.getName();
        encoding = CharacterEncoding.UCS2;

        // TODO(code) still needed ? final Rectangle2D transform =
        // transformToEMSquare(font, fontContext);

        final double scaleY = 1024.0;
        final double scaleX = scaleY;
        final double translateX = 0;
        final double translateY = 0;

        /*
         * The new font scaled to the EM Square must be derived using the size
         * as well as the transform used for the glyphs otherwise the advance
         * values are not scaled accordingly.
         */
        final AffineTransform affine = AffineTransform.getTranslateInstance(
                translateX, translateY);

        font = font.deriveFont(affine);
        font = font.deriveFont((float) scaleX);

        missingGlyph = font.getMissingGlyphCode();

        bold = font.isBold();
        italic = font.isItalic();

        final int count = font.getNumGlyphs();
        int index = 0;
        int code = 0;
        char character;

        glyphTable = new Glyph[count];
        charToGlyph = new int[65536];
        glyphToChar = new int[count];

        // create the glyph for the characters that cannot be displayed

        GlyphVector glyphVector = font.createGlyphVector(fontContext,
                new int[] {missingGlyph});
        java.awt.Shape outline = glyphVector.getGlyphOutline(0);
        int advance = (int) (glyphVector.getGlyphMetrics(0).getAdvance());
        glyphTable[index] = new Glyph(convertShape(outline), new Bounds(0, 0,
                0, 0), advance);
        charToGlyph[code] = index;
        glyphToChar[index] = code;

        index = 1;

        /*
         * Run through all the unicode character codes looking for a
         * corresponding glyph.
         */
        while ((index < count) && (code < 65536)) {
            character = font.canDisplay(code) ? (char) code
                    : (char) missingGlyph;

            glyphVector = font.createGlyphVector(fontContext,
                    new char[] {character});

            outline = glyphVector.getGlyphOutline(0);
            advance = (int) (glyphVector.getGlyphMetrics(0).getAdvance());

            glyphTable[index] = new Glyph(convertShape(outline), new Bounds(0,
                    0, 0, 0), advance);
            charToGlyph[character] = index;
            glyphToChar[index] = character;

            if (!font.hasUniformLineMetrics()) {
                final LineMetrics lineMetrics = font.getLineMetrics(
                        new char[] {character}, 0, 1, fontContext);

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

    private Rectangle2D transformToEMSquare(final java.awt.Font font,
            final FontRenderContext fontContext) {
        final int numGlyphs = font.getNumGlyphs();
        int characterCode = 0;
        int glyphIndex = 0;

        double xCoord = 0.0;
        double yCoord = 0.0;
        double width = 0.0;
        double height = 0.0;

        /*
         * Scan through all the glyphs looking for glyphs that will fall outside
         * the left or bottom side of the EM Square once the glyph has been
         * scaled.
         */
        while ((glyphIndex < numGlyphs) && (characterCode < 65536)) {
            final char currentChar = (char) characterCode;

            if (font.canDisplay(currentChar)) {
                final GlyphVector glyphVector = font.createGlyphVector(
                        fontContext, new char[] {currentChar});
                final Rectangle2D bounds = glyphVector.getGlyphOutline(0)
                        .getBounds2D();

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

    private Shape convertShape(final java.awt.Shape glyph) {
        final PathIterator pathIter = glyph.getPathIterator(null);
        final Canvas path = new Canvas(false);

        final double[] coords = new double[6];

        while (!pathIter.isDone()) {
            final int segmentType = pathIter.currentSegment(coords);

            final int point1 = (int) (coords[0]);
            final int point2 = (int) (coords[1]);
            final int point3 = (int) (coords[2]);
            final int point4 = (int) (coords[3]);
            final int point5 = (int) (coords[4]);
            final int point6 = (int) (coords[5]);

            switch (segmentType) {
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
}
