/*
 * TextTable.java
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

package com.flagstone.transform.util.text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.datatype.Color;
import com.flagstone.transform.datatype.CoordTransform;
import com.flagstone.transform.font.DefineFont2;
import com.flagstone.transform.text.DefineText2;
import com.flagstone.transform.text.GlyphIndex;
import com.flagstone.transform.text.TextSpan;

/** TODO(class). */
public final class TextTable {

    private final transient int size;
    private final transient int ascent;
    private final transient int descent;
    private final transient int identifier;

    private final transient Map<Character, GlyphIndex> characters;

    public TextTable(final DefineFont2 font, final int fontSize) {

        identifier = font.getIdentifier();
        size = fontSize;
        characters = new LinkedHashMap<Character, GlyphIndex>();

        final List<Integer> codes = font.getCodes();
        final List<Integer> advances = font.getAdvances();
        final float scale = fontSize / 1024.0f;
        final int count = codes.size();

        ascent = (int) (font.getAscent() * scale);
        descent = (int) (font.getDescent() * scale);

        for (int i = 0; i < count; i++) {
            characters.put((char) codes.get(i).intValue(), new GlyphIndex(i,
                    (int) (advances.get(i) * scale)));
        }
    }

    /**
     * Create a bound box that encloses the line of text when rendered using the
     * specified font and size.
     *
     * @param text
     *            the string to be displayed.
     *
     * @return the bounding box that completely encloses the text.
     */
    public Bounds boundsForText(final String text) {
        int total = 0;
        for (int i = 0; i < text.length(); i++) {
            total += characters.get(text.charAt(i)).getAdvance();
        }
        return new Bounds(0, -ascent, total, descent);
    }

    /**
     * Create a bound box that encloses the line of text when rendered using the
     * specified font and size and including a margin.
     *
     * @param text
     *            the string to be displayed.
     * @param padding
     *            the padding applied so the box is larger than the space
     *            occupied by the glyphs.
     *
     * @return the bounding box that completely encloses the text.
     */
    public Bounds boundsForText(final String text, final int padding) {
        int total = 0;
        for (int i = 0; i < text.length(); i++) {
            total += characters.get(text.charAt(i)).getAdvance();
        }
        return new Bounds(padding, -ascent - padding,
                total + padding, descent + padding);
    }

    /**
     * Create an array of characters that can be added to a text span.
     *
     * @param text
     *            the string to be displayed.
     *
     * @return a TextSpan object that can be added to a DefineText or
     *         DefineText2 object.
     */
    public List<GlyphIndex> charactersForText(final String text) {
        final List<GlyphIndex> list = new ArrayList<GlyphIndex>(text.length());
        for (int i = 0; i < text.length(); i++) {
            list.add(characters.get(text.charAt(i)));
        }
        return list;
    }

    /**
     * Create a span of text that can be added to a static text field.
     *
     * @param text
     *            the string to be displayed.
     *
     * @param color
     *            the colour used to display the text.
     *
     * @param xCoord
     *            the x-coordinate for the origin of the text span.
     *
     * @param yCoord
     *            the y-coordinate for the origin of the text span.
     *
     * @return a TextSpan object that can be added to a DefineText or
     *         DefineText2 object.
     */
    public TextSpan defineSpan(final String text, final Color color,
            final int xCoord, final int yCoord) {
         return new TextSpan(identifier, size, color, xCoord, yCoord,
                charactersForText(text));
    }

    /**
     * Create a definition for a static text field that displays a single line
     * of text in the specified font.
     *
     * @param uid
     *            the unique identifier that will be used to reference the text
     *            field in a flash file.
     *
     * @param text
     *            the string to be displayed.
     *
     * @param color
     *            the colour used to display the text.
     *
     * @return a DefineText2 object that can be added to a Flash file.
     */
    public DefineText2 defineText(final int uid, final String text,
            final Color color) {
        final CoordTransform transform = CoordTransform.translate(0, 0);
        final ArrayList<TextSpan> spans = new ArrayList<TextSpan>();
        spans.add(defineSpan(text, color, 0, 0));
        return new DefineText2(uid, boundsForText(text), transform, spans);
    }

    /**
     * Create a definition for a static text field that displays a block of text
     * in the specified font.
     *
     * @param uid
     *            the unique identifier that will be used to reference the text
     *            field in a flash file.
     *
     * @param lines
     *            the array of strings to be displayed.
     *
     * @param color
     *            the colour used to display the text.
     *
     * @param lineSpacing
     *            the spearation between successive lines of text.
     *
     * @return a DefineText2 object that can be added to a Flash file.
     */
    public DefineText2 defineTextBlock(final int uid, final List<String> lines,
            final Color color, final int lineSpacing) {
        final CoordTransform transform = CoordTransform.translate(0, 0);
        int xMin = 0;
        int yMin = 0;
        int xMax = 0;
        int yMax = 0;

        final int xOffset = 0;
        int yOffset = ascent;

        final ArrayList<TextSpan> spans = new ArrayList<TextSpan>();
        String text;

        int lineNumber = 0;

        for (final Iterator<String> i = lines.iterator(); i.hasNext();
        yOffset += lineSpacing, lineNumber++) {
            text = i.next();

            spans.add(new TextSpan(identifier, size, color, xOffset, yOffset,
                    charactersForText(text)));

            final Bounds bounds = boundsForText(text);

            if (lineNumber == 0) {
                yMin = bounds.getMinY();
                yMax = bounds.getMaxY();
            } else {
                yMax += lineSpacing;
            }

            if (lineNumber == lines.size() - 1) {
                yMax += bounds.getHeight();
            }

            xMin = (xMin < bounds.getMinX()) ? xMin : bounds.getMinX();
            xMax = (xMax > bounds.getMaxX()) ? xMax : bounds.getMaxX();
        }

        return new DefineText2(uid, new Bounds(xMin, yMin, xMax, yMax),
                transform, spans);
    }
}
