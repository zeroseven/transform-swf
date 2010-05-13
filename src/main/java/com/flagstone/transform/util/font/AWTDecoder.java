/*
 * AWTDecoder.java
 * Transform
 *
 * Copyright (c) 2009-2010 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.flagstone.transform.util.font;

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.font.CharacterFormat;
import com.flagstone.transform.shape.Shape;
import com.flagstone.transform.util.shape.Canvas;

/** TODO(class). */
public final class AWTDecoder {

    private final transient List<Font>fonts;

    public AWTDecoder() {
        fonts = new ArrayList<Font>();
    }


    public void read(final java.awt.Font font)
        throws IOException, DataFormatException {
        decode(font);
    }


    public List<Font> getFonts() {
        return fonts;
    }

    private void decode(final java.awt.Font aFont) {

        final FontRenderContext fontContext = new FontRenderContext(
                new AffineTransform(), true, true);
        java.awt.Font awtFont = aFont.deriveFont(1.0f);

        final Font font = new Font();

        font.setFace(new FontFace(awtFont.getName(),
                awtFont.isBold(), awtFont.isItalic()));
        font.setEncoding(CharacterFormat.UCS2);

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

        awtFont = awtFont.deriveFont(affine);
        awtFont = awtFont.deriveFont((float) scaleX);

        final int missingGlyph = awtFont.getMissingGlyphCode();
        final int count = awtFont.getNumGlyphs();

        font.setMissingGlyph(missingGlyph);
        font.setNumberOfGlyphs(count);
        font.setHighestChar((char) 65536);

        int index = 0;
        int code = 0;
        char character;

        // create the glyph for the characters that cannot be displayed

        GlyphVector glyphVector = awtFont.createGlyphVector(fontContext,
                new int[] {missingGlyph});
        java.awt.Shape outline = glyphVector.getGlyphOutline(0);
        int advance = (int) (glyphVector.getGlyphMetrics(0).getAdvance());

        font.addGlyph((char) missingGlyph, new Glyph(convertShape(outline),
                new Bounds(0, 0, 0, 0), advance));

        index = 1;

        float ascent = 0.0f;
        float descent = 0.0f;
        float leading = 0.0f;

        /*
         * Run through all the unicode character codes looking for a
         * corresponding glyph.
         */
        while ((index < count) && (code < 65536)) {
            if (awtFont.canDisplay(code)) {
                character = (char) code;
            } else {
                character = (char) missingGlyph;
            }

            glyphVector = awtFont.createGlyphVector(fontContext,
                    new char[] {character});

            outline = glyphVector.getGlyphOutline(0);
            advance = (int) (glyphVector.getGlyphMetrics(0).getAdvance());

            font.addGlyph(character,  new Glyph(convertShape(outline),
                    new Bounds(0, 0, 0, 0), advance));

            if (!awtFont.hasUniformLineMetrics()) {
                final LineMetrics lineMetrics = awtFont.getLineMetrics(
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
        font.setAscent(ascent);
        font.setDescent(descent);
        font.setLeading(leading);

        fonts.add(font);
    }

//    private Rectangle2D transformToEMSquare(final java.awt.Font font,
//            final FontRenderContext fontContext) {
//        final int numGlyphs = font.getNumGlyphs();
//        int characterCode = 0;
//        int glyphIndex = 0;
//
//        double xCoord = 0.0;
//        double yCoord = 0.0;
//        double width = 0.0;
//        double height = 0.0;
//
//        /*
//         * Scan through all the glyphs looking for glyphs that will fall
//         * outside the left or bottom side of the EM Square once the glyph
//         * has been scaled.
//         */
//        while ((glyphIndex < numGlyphs) && (characterCode < 65536)) {
//            final char currentChar = (char) characterCode;
//
//            if (font.canDisplay(currentChar)) {
//                final GlyphVector glyphVector = font.createGlyphVector(
//                        fontContext, new char[] {currentChar});
//                final Rectangle2D bounds = glyphVector.getGlyphOutline(0)
//                        .getBounds2D();
//
//                xCoord = Math.min(bounds.getX(), xCoord);
//                yCoord = Math.min(bounds.getY(), yCoord);
//
//                width = Math.max(bounds.getWidth(), width);
//                height = Math.max(bounds.getHeight(), height);
//
//                glyphIndex++;
//            }
//            characterCode++;
//        }
//        return new Rectangle2D.Double(xCoord, yCoord, width, height);
//    }

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
