/*
 * DefineShape3.java
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

package com.flagstone.transform.shape;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.DefineTag;
import com.flagstone.transform.coder.Encoder;
import com.flagstone.transform.coder.FillStyle;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.coder.SWFFactory;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.linestyle.LineStyle2;

/**
 * DefineShape3 defines a transparent shape to be displayed. It extends the
 * DefineShape2 class by encoding the alpha channel in any Color objects
 * included in the line and fill styles.
 * 
 * @see DefineShape2
 */
public final class DefineShape4 implements DefineTag {
    private static final String FORMAT = "DefineShape4: { identifier=%d; shapeBounds=%s; edgeBounds=%s; fillStyles=%s; lineStyles=%s; shape=%s }";

    private int identifier;
    private Bounds shapeBounds;
    private Bounds edgeBounds;
    private List<FillStyle> fillStyles;
    private List<LineStyle2> lineStyles;
    private Shape shape;

    private transient int length;
    private transient int fillBits;
    private transient int lineBits;
    private transient boolean scaling;

    // TODO(doc)
    public DefineShape4(final SWFDecoder coder, final Context context)
            throws CoderException {
        final int start = coder.getPointer();
        length = coder.readWord(2, false) & 0x3F;

        if (length == 0x3F) {
            length = coder.readWord(4, false);
        }
        final int end = coder.getPointer() + (length << 3);

        identifier = coder.readWord(2, false);
        final Map<Integer, Integer> vars = context.getVariables();
        vars.put(Context.TRANSPARENT, 1);

        shapeBounds = new Bounds(coder);
        edgeBounds = new Bounds(coder);

        coder.readByte(); // scaling hints

        int fillStyleCount = coder.readByte();

        if (fillStyleCount == 0xFF) {
            fillStyleCount = coder.readWord(2, false);
        }
        fillStyles = new ArrayList<FillStyle>();
        lineStyles = new ArrayList<LineStyle2>();

        final SWFFactory<FillStyle> decoder = context.getRegistry()
                .getFillStyleDecoder();
        FillStyle fill;
        int type;

        for (int i = 0; i < fillStyleCount; i++) {
            type = coder.scanByte();
            fill = decoder.getObject(coder, context);

            if (fill == null) {
                throw new CoderException(String.valueOf(type), start >>> 3, 0,
                        0, Strings.INVALID_FILLSTYLE);
            }

            fillStyles.add(fill);
        }

        int lineStyleCount = coder.readByte();

        if (lineStyleCount == 0xFF) {
            lineStyleCount = coder.readWord(2, false);
        }

        for (int i = 0; i < lineStyleCount; i++) {
            lineStyles.add(new LineStyle2(coder, context));
        }

        vars.put(Context.ARRAY_EXTENDED, 1);

        shape = new Shape(coder, context);

        vars.remove(Context.TRANSPARENT);
        vars.remove(Context.ARRAY_EXTENDED);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }

    /**
     * Creates a DefineShape3 object.
     * 
     * @param uid
     *            the unique identifier for the shape in the range 1..65535.
     * @param aBounds
     *            the bounding rectangle for the shape. Must not be null.
     * @param fillStyleArray
     *            the array of fill styles used in the shape. Must not be null.
     * @param lineStyleArray
     *            the array of line styles used in the shape. Must not be null.
     * @param aShape
     *            the shape to be drawn. Must not be null.
     */
    public DefineShape4(final int uid, final Bounds aBounds,
            final List<FillStyle> fillStyleArray,
            final List<LineStyle2> lineStyleArray, final Shape aShape) {
        setIdentifier(uid);
        setShapeBounds(aBounds);
        setFillStyles(fillStyleArray);
        setLineStyles(lineStyleArray);
        setShape(aShape);
    }

    // TODO(doc)
    public DefineShape4(final DefineShape4 object) {
        identifier = object.identifier;
        shapeBounds = object.shapeBounds;
        edgeBounds = object.edgeBounds;
        fillStyles = new ArrayList<FillStyle>(object.fillStyles.size());
        for (final FillStyle style : object.fillStyles) {
            fillStyles.add(style.copy());
        }
        lineStyles = new ArrayList<LineStyle2>(object.lineStyles.size());
        for (final LineStyle2 style : object.lineStyles) {
            lineStyles.add(style.copy());
        }
        shape = object.shape.copy();
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final int uid) {
        if ((uid < 0) || (uid > 65535)) {
            throw new IllegalArgumentException(Strings.IDENTIFIER_RANGE);
        }
        identifier = uid;
    }

    /**
     * Returns the bounding rectangle for the shape.
     */
    public Bounds getShapeBounds() {
        return shapeBounds;
    }

    /**
     * Sets the bounding rectangle that encloses the shape.
     * 
     * @param aBounds
     *            set the bounding rectangle for the shape. Must not be null.
     */
    public void setShapeBounds(final Bounds aBounds) {
        if (aBounds == null) {
            throw new IllegalArgumentException(Strings.OBJECT_IS_NULL);
        }
        shapeBounds = aBounds;
    }

    /**
     * Returns the bounding rectangle for the shape.
     */
    public Bounds getEdgeBounds() {
        return edgeBounds;
    }

    /**
     * Sets the bounding rectangle that encloses the shape.
     * 
     * @param aBounds
     *            set the bounding rectangle for the shape. Must not be null.
     */
    public void setEdgeBounds(final Bounds aBounds) {
        if (aBounds == null) {
            throw new IllegalArgumentException(Strings.OBJECT_IS_NULL);
        }
        edgeBounds = aBounds;
    }

    /**
     * Returns the width of the shape in twips.
     */
    public int getWidth() {
        return shapeBounds.getWidth();
    }

    /**
     * Returns the height of the shape in twips.
     */
    public int getHeight() {
        return shapeBounds.getHeight();
    }

    /**
     * Add a LineStyle to the array of line styles.
     * 
     * @param style
     *            and LineStyle object. Must not be null.
     */
    public DefineShape4 add(final LineStyle2 style) {
        if (style == null) {
            throw new IllegalArgumentException(Strings.OBJECT_IS_NULL);
        }
        lineStyles.add(style);
        return this;
    }

    /**
     * Add the fill style to the array of fill styles.
     * 
     * @param style
     *            and FillStyle object. Must not be null.
     */
    public DefineShape4 add(final FillStyle style) {
        if (style == null) {
            throw new IllegalArgumentException(Strings.OBJECT_IS_NULL);
        }
        fillStyles.add(style);
        return this;
    }

    /**
     * Returns the array fill styles.
     */
    public List<FillStyle> getFillStyles() {
        return fillStyles;
    }

    /**
     * Returns the array line styles.
     */
    public List<LineStyle2> getLineStyles() {
        return lineStyles;
    }

    /**
     * Returns the shape.
     */
    public Shape getShape() {
        return shape;
    }

    /**
     * Sets the array fill styles that will be used to draw the shape.
     * 
     * @param anArray
     *            set the fill styles for the shape. Must not be null.
     */
    public void setFillStyles(final List<FillStyle> anArray) {
        if (anArray == null) {
            throw new IllegalArgumentException(Strings.ARRAY_IS_NULL);
        }
        fillStyles = anArray;
    }

    /**
     * Sets the array of styles that will be used to draw the outline of the
     * shape.
     * 
     * @param anArray
     *            set the line styles for the shape. Must not be null.
     */
    public void setLineStyles(final List<LineStyle2> anArray) {
        if (anArray == null) {
            throw new IllegalArgumentException(Strings.ARRAY_IS_NULL);
        }
        lineStyles = anArray;
    }

    /**
     * Sets the shape.
     * 
     * @param aShape
     *            set the shape to be drawn. Must not be null.
     */
    public void setShape(final Shape aShape) {
        if (aShape == null) {
            throw new IllegalArgumentException(Strings.OBJECT_IS_NULL);
        }
        shape = aShape;
    }

    public DefineShape4 copy() {
        return new DefineShape4(this);
    }

    @Override
    public String toString() {
        return String.format(FORMAT, identifier, shapeBounds, edgeBounds,
                fillStyles, lineStyles, shape);
    }

    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        fillBits = Encoder.unsignedSize(fillStyles.size());
        lineBits = Encoder.unsignedSize(lineStyles.size());

        final Map<Integer, Integer> vars = context.getVariables();
        if (vars.containsKey(Context.POSTSCRIPT)) {
            if (fillBits == 0) {
                fillBits = 1;
            }

            if (lineBits == 0) {
                lineBits = 1;
            }
        }

        vars.put(Context.TRANSPARENT, 1);

        length = 3;
        length += shapeBounds.prepareToEncode(coder, context);
        length += edgeBounds.prepareToEncode(coder, context);

        length += (fillStyles.size() >= 255) ? 3 : 1;

        for (final FillStyle style : fillStyles) {
            length += style.prepareToEncode(coder, context);
        }

        vars.remove(Context.SCALING_STROKE);

        length += (lineStyles.size() >= 255) ? 3 : 1;

        for (final LineStyle2 style : lineStyles) {
            length += style.prepareToEncode(coder, context);
        }

        scaling = vars.containsKey(Context.SCALING_STROKE);

        vars.put(Context.ARRAY_EXTENDED, 1);
        vars.put(Context.FILL_SIZE, fillBits);
        vars.put(Context.LINE_SIZE, lineBits);

        length += shape.prepareToEncode(coder, context);

        vars.remove(Context.ARRAY_EXTENDED);
        vars.put(Context.FILL_SIZE, 0);
        vars.put(Context.LINE_SIZE, 0);
        vars.remove(Context.TRANSPARENT);
        vars.remove(Context.SCALING_STROKE);

        return (length > 62 ? 6 : 2) + length;
    }

    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        final Map<Integer, Integer> vars = context.getVariables();
        vars.put(Context.TRANSPARENT, 1);

        final int start = coder.getPointer();

        if (length >= 63) {
            coder.writeWord((MovieTypes.DEFINE_SHAPE_4 << 6) | 0x3F, 2);
            coder.writeWord(length, 4);
        } else {
            coder.writeWord((MovieTypes.DEFINE_SHAPE_4 << 6) | length, 2);
        }
        final int end = coder.getPointer() + (length << 3);

        coder.writeWord(identifier, 2);

        shapeBounds.encode(coder, context);
        edgeBounds.encode(coder, context);

        coder.writeByte(scaling ? 1 : 2);

        if (fillStyles.size() >= 255) {
            coder.writeWord(0xFF, 1);
            coder.writeWord(fillStyles.size(), 2);
        } else {
            coder.writeWord(fillStyles.size(), 1);
        }

        for (final FillStyle style : fillStyles) {
            style.encode(coder, context);
        }

        if (lineStyles.size() >= 255) {
            coder.writeWord(0xFF, 1);
            coder.writeWord(lineStyles.size(), 2);
        } else {
            coder.writeWord(lineStyles.size(), 1);
        }

        for (final LineStyle2 style : lineStyles) {
            style.encode(coder, context);
        }

        vars.put(Context.ARRAY_EXTENDED, 1);
        vars.put(Context.FILL_SIZE, fillBits);
        vars.put(Context.LINE_SIZE, lineBits);

        shape.encode(coder, context);

        vars.remove(Context.ARRAY_EXTENDED);
        vars.put(Context.FILL_SIZE, 0);
        vars.put(Context.LINE_SIZE, 0);
        vars.remove(Context.TRANSPARENT);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }
}
