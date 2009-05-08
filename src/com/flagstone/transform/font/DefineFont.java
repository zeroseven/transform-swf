/*
 * DefineFont.java
 * Transform
 *
 * Copyright (c) 2001-2009 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.flagstone.transform.font;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.DefineTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.shape.Shape;
import com.flagstone.transform.shape.ShapeData;

/**
 * DefineFont defines the glyphs that are drawn when text characters are
 * rendered in a particular font.
 *
 * <p>
 * A complete definition of a font is created using the DefineFont object for
 * the glyphs along with an FontInfo or FontInfo2 object which contains the name
 * of the font, whether the font face is bold or italics and a table that maps
 * character codes to the glyphs that is drawn to represent the character.
 * </p>
 *
 * <p>
 * When defining a font only the glyphs used from a particular font are
 * included. Unused glyphs can be omitted greatly reducing the amount of
 * information that is encoded.
 * </p>
 *
 * @see FontInfo
 * @see FontInfo2
 */
//TODO(class)
public final class DefineFont implements DefineTag {
    private static final String FORMAT = "DefineFont: { identifier=%d; shapes=%s }";

    private int identifier;
    private List<Shape> shapes;

    private transient int length;

    // TODO(optimise)
    /**
     * Creates and initialises a DefineFont object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public DefineFont(final SWFDecoder coder) throws CoderException {
        final int start = coder.getPointer();
        length = coder.readWord(2, false) & 0x3F;

        if (length == 0x3F) {
            length = coder.readWord(4, false);
        }
        final int end = coder.getPointer() + (length << 3);

        identifier = coder.readWord(2, false);
        shapes = new ArrayList<Shape>();

        final int offsetStart = coder.getPointer();
        final int shapeCount = coder.readWord(2, false) / 2;

        coder.setPointer(offsetStart);

        final int[] offset = new int[shapeCount + 1];

        for (int i = 0; i < shapeCount; i++) {
            offset[i] = coder.readWord(2, false);
        }

        offset[shapeCount] = length - 2;

        Shape shape;

        for (int i = 0; i < shapeCount; i++) {
            coder.setPointer(offsetStart + (offset[i] << 3));

            shape = new Shape();
            shape.add(new ShapeData(coder.readBytes(new byte[offset[i + 1]
                    - offset[i]])));
            shapes.add(shape);
        }

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }

    /**
     * Creates a DefineFont object setting the unique identifier for the object
     * and the array of glyphs used to render the characters used from the font.
     *
     * @param uid
     *            the unique identifier for this object.
     * @param anArray
     *            an array of Shape objects that define the outlines for each
     *            glyph in the font.
     */
    public DefineFont(final int uid, final List<Shape> anArray) {
        setIdentifier(uid);
        setShapes(anArray);
    }

    /**
     * Creates and initialises a DefineFont object using the values copied
     * from another DefineFont object.
     *
     * @param object
     *            a DefineFont object from which the values will be
     *            copied.
     */
    public DefineFont(final DefineFont object) {
        identifier = object.identifier;
        shapes = new ArrayList<Shape>(object.shapes.size());
        for (final Shape shape : object.shapes) {
            shapes.add(shape.copy());
        }
    }

    /** TODO(method). */
    public int getIdentifier() {
        return identifier;
    }

    /** TODO(method). */
    public void setIdentifier(final int uid) {
        if ((uid < 0) || (uid > 65535)) {
            throw new IllegalArgumentException(Strings.IDENTIFIER_RANGE);
        }
        identifier = uid;
    }

    /**
     * Add a shape to the array of shapes that represent the glyphs for the
     * font.
     *
     * @param obj
     *            a shape which must not be null.
     */
    public DefineFont add(final Shape obj) {
        if (obj == null) {
            throw new IllegalArgumentException(Strings.OBJECT_IS_NULL);
        }
        shapes.add(obj);
        return this;
    }

    /**
     * Returns the array of shapes that define the outline for each glyph.
     */
    public List<Shape> getShapes() {
        return shapes;
    }

    /**
     * Sets the array of shapes that describe each glyph.
     *
     * @param anArray
     *            an array of Shape objects that define the outlines for each
     *            glyph in the font. Must not be null.
     */
    public void setShapes(final List<Shape> anArray) {
        if (anArray == null) {
            throw new IllegalArgumentException(Strings.ARRAY_IS_NULL);
        }
        shapes = anArray;
    }

    /** TODO(method). */
    public DefineFont copy() {
        return new DefineFont(this);
    }

    @Override
    public String toString() {
        return String.format(FORMAT, identifier, shapes);
    }

    // TODO(optimise)
    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        length = 2;

        final Map<Integer, Integer> vars = context.getVariables();
        vars.put(Context.FILL_SIZE, 1);
        vars.put(Context.LINE_SIZE, vars.containsKey(Context.POSTSCRIPT) ? 1
                : 0);

        length += shapes.size() * 2;

        for (final Shape shape : shapes) {
            length += shape.prepareToEncode(coder, context);
        }

        vars.put(Context.FILL_SIZE, 0);
        vars.put(Context.LINE_SIZE, 0);

        return (length > 62 ? 6 : 2) + length;
    }

    // TODO(optimise)
    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        final int start = coder.getPointer();

        if (length > 62) {
            coder.writeWord((MovieTypes.DEFINE_FONT << 6) | 0x3F, 2);
            coder.writeWord(length, 4);
        } else {
            coder.writeWord((MovieTypes.DEFINE_FONT << 6) | length, 2);
        }
        final int end = coder.getPointer() + (length << 3);
        coder.writeWord(identifier, 2);

        final Map<Integer, Integer> vars = context.getVariables();
        vars.put(Context.FILL_SIZE, 1);
        vars.put(Context.LINE_SIZE, vars.containsKey(Context.POSTSCRIPT) ? 1
                : 0);

        int currentLocation;
        int offset;

        final int tableStart = coder.getPointer();

        for (int i = 0; i < shapes.size(); i++) {
            coder.writeWord(0, 2);
        }

        int tableEntry = tableStart;

        for (final Shape shape : shapes) {
            currentLocation = coder.getPointer();
            offset = (coder.getPointer() - tableStart) >> 3;

            coder.setPointer(tableEntry);
            coder.writeWord(offset, 2);
            coder.setPointer(currentLocation);

            shape.encode(coder, context);
            tableEntry += 16;
        }

        vars.put(Context.FILL_SIZE, 0);
        vars.put(Context.LINE_SIZE, 0);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }
}
