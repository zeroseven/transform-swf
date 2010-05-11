/*
 * DefineShape2.java
 * Transform
 *
 * Copyright (c) 2001-2010 Flagstone Software Ltd. All rights reserved.
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

package com.flagstone.transform.shape;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.DefineTag;
import com.flagstone.transform.coder.Encoder;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.coder.SWFFactory;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.exception.IllegalArgumentRangeException;
import com.flagstone.transform.fillstyle.FillStyle;
import com.flagstone.transform.linestyle.LineStyle;

/**
 * DefineShape2 defines a shape to be displayed with an extended set of fill
 * styles. It extends the functionality of the DefineShape class by allowing
 * more than 255 fill or line styles to be specified.
 *
 * <p>
 * The shape defines a path containing a mix of straight and curved edges and
 * pen move actions. A path need not be contiguous. When the shape is drawn the
 * ShapeStyle object selects the line and fill styles, from the respective
 * array, to be used. ShapeStyle objects can be defined in the shape at any time
 * to change the styles being used. The fill style used can either be a solid
 * colour, a bitmap image or a gradient. The line style specifies the colour and
 * thickness of the line drawn around the shape outline. For both line and fill
 * styles the selected style may be undefined, allowing the shape to be drawn
 * without an outline or left unfilled.
 * </p>
 *
 * @see DefineShape
 * @see DefineShape3
 */
//TODO(class)
public final class DefineShape2 implements DefineTag {

    private static final String FORMAT = "DefineShape2: { identifier=%d;"
            + " bounds=%s; fillStyles=%s; lineStyles=%s; shape=%s }";

    private int identifier;
    private Bounds bounds;
    private List<FillStyle> fillStyles;
    private List<LineStyle> lineStyles;
    private Shape shape;

    private transient int length;
    private transient int fillBits;
    private transient int lineBits;

    /**
     * Creates and initialises a DefineShape2 object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @param context
     *            a Context object used to manage the decoders for different
     *            type of object and to pass information on how objects are
     *            decoded.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public DefineShape2(final SWFDecoder coder, final Context context)
            throws CoderException {
        final int start = coder.getPointer();
        length = coder.readWord(2, false) & 0x3F;

        if (length == 0x3F) {
            length = coder.readWord(4, false);
        }
        final int end = coder.getPointer() + (length << 3);

        identifier = coder.readWord(2, false);
        bounds = new Bounds(coder);

        fillStyles = new ArrayList<FillStyle>();
        lineStyles = new ArrayList<LineStyle>();
        final Map<Integer, Integer> vars = context.getVariables();
        vars.put(Context.ARRAY_EXTENDED, 1);
        vars.put(Context.TYPE, MovieTypes.DEFINE_SHAPE_2);

        int fillStyleCount = coder.readByte();

        if (fillStyleCount == 0xFF) {
            fillStyleCount = coder.readWord(2, false);
        }

        final SWFFactory<FillStyle> decoder = context.getRegistry()
                .getFillStyleDecoder();
        FillStyle fill;
        int type;

        for (int i = 0; i < fillStyleCount; i++) {
            type = coder.scanByte();
            fill = decoder.getObject(coder, context);

            if (fill == null) {
                throw new CoderException(String.valueOf(type), start >>> 3, 0,
                        0, "Unsupported FillStyle");
            }
            fillStyles.add(fill);
        }

        int lineStyleCount = coder.readByte();

        if (lineStyleCount == 0xFF) {
            lineStyleCount = coder.readWord(2, false);
        }

        for (int i = 0; i < lineStyleCount; i++) {
            lineStyles.add(new LineStyle(coder, context));
        }
        shape = new Shape(coder, context);

        vars.remove(Context.ARRAY_EXTENDED);
        vars.remove(Context.TYPE);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }

    /**
     * Creates a DefineShape2 object.
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
    public DefineShape2(final int uid, final Bounds aBounds,
            final List<FillStyle> fillStyleArray,
            final List<LineStyle> lineStyleArray, final Shape aShape) {
        setIdentifier(uid);
        setBounds(aBounds);
        setFillStyles(fillStyleArray);
        setLineStyles(lineStyleArray);
        setShape(aShape);
    }

    /**
     * Creates and initialises a DefineShape2 object using the values copied
     * from another DefineShape2 object.
     *
     * @param object
     *            a DefineShape2 object from which the values will be
     *            copied.
     */
    public DefineShape2(final DefineShape2 object) {
        identifier = object.identifier;
        bounds = object.bounds;
        fillStyles = new ArrayList<FillStyle>(object.fillStyles.size());
        for (final FillStyle style : object.fillStyles) {
            fillStyles.add(style.copy());
        }
        lineStyles = new ArrayList<LineStyle>(object.lineStyles.size());
        for (final LineStyle style : object.lineStyles) {
            lineStyles.add(style.copy());
        }
        shape = object.shape.copy();
    }

    /** TODO(method). */
    public int getIdentifier() {
        return identifier;
    }

    /** TODO(method). */
    public void setIdentifier(final int uid) {
        if ((uid < 1) || (uid > 65535)) {
             throw new IllegalArgumentRangeException(1, 65536, uid);
        }
        identifier = uid;
    }

    /**
     * Returns the width of the shape in twips.
     */
    public int getWidth() {
        return bounds.getWidth();
    }

    /**
     * Returns the height of the shape in twips.
     */
    public int getHeight() {
        return bounds.getHeight();
    }

    /**
     * Add a LineStyle to the array of line styles.
     *
     * @param style
     *            and LineStyle object. Must not be null.
     */
    public DefineShape2 add(final LineStyle style) {
        if (style == null) {
            throw new IllegalArgumentException();
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
    public DefineShape2 add(final FillStyle style) {
        if (style == null) {
            throw new IllegalArgumentException();
        }
        fillStyles.add(style);
        return this;
    }

    /**
     * Returns the bounding rectangle for the shape.
     */
    public Bounds getBounds() {
        return bounds;
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
    public List<LineStyle> getLineStyles() {
        return lineStyles;
    }

    /**
     * Returns the shape.
     */
    public Shape getShape() {
        return shape;
    }

    /**
     * Sets the bounding rectangle that encloses the shape.
     *
     * @param aBounds
     *            set the bounding rectangle for the shape. Must not be null.
     */
    public void setBounds(final Bounds aBounds) {
        if (aBounds == null) {
            throw new IllegalArgumentException();
        }
        bounds = aBounds;
    }

    /**
     * Sets the array fill styles that will be used to draw the shape.
     *
     * @param anArray
     *            set the fill styles for the shape. Must not be null.
     */
    public void setFillStyles(final List<FillStyle> anArray) {
        if (anArray == null) {
            throw new IllegalArgumentException();
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
    public void setLineStyles(final List<LineStyle> anArray) {
        if (anArray == null) {
            throw new IllegalArgumentException();
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
            throw new IllegalArgumentException();
        }
        shape = aShape;
    }

    /** TODO(method). */
    public DefineShape2 copy() {
        return new DefineShape2(this);
    }

    @Override
    public String toString() {
        return String.format(FORMAT, identifier, bounds, fillStyles,
                lineStyles, shape);
    }

    /** {@inheritDoc} */
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

        length = 2 + bounds.prepareToEncode(coder, context);
        length += (fillStyles.size() >= 255) ? 3 : 1;

        for (final FillStyle style : fillStyles) {
            length += style.prepareToEncode(coder, context);
        }

        length += (lineStyles.size() >= 255) ? 3 : 1;

        for (final LineStyle style : lineStyles) {
            length += style.prepareToEncode(coder, context);
        }

        vars.put(Context.ARRAY_EXTENDED, 1);
        vars.put(Context.FILL_SIZE, fillBits);
        vars.put(Context.LINE_SIZE, lineBits);

        length += shape.prepareToEncode(coder, context);

        vars.remove(Context.ARRAY_EXTENDED);
        vars.put(Context.FILL_SIZE, 0);
        vars.put(Context.LINE_SIZE, 0);

        return (length > 62 ? 6 : 2) + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        final int start = coder.getPointer();

        if (length >= 63) {
            coder.writeWord((MovieTypes.DEFINE_SHAPE_2 << 6) | 0x3F, 2);
            coder.writeWord(length, 4);
        } else {
            coder.writeWord((MovieTypes.DEFINE_SHAPE_2 << 6) | length, 2);
        }
        final int end = coder.getPointer() + (length << 3);

        coder.writeWord(identifier, 2);
        bounds.encode(coder, context);

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

        for (final LineStyle style : lineStyles) {
            style.encode(coder, context);
        }

        final Map<Integer, Integer> vars = context.getVariables();
        vars.put(Context.ARRAY_EXTENDED, 1);
        vars.put(Context.FILL_SIZE, fillBits);
        vars.put(Context.LINE_SIZE, lineBits);

        shape.encode(coder, context);

        vars.remove(Context.ARRAY_EXTENDED);
        vars.put(Context.FILL_SIZE, 0);
        vars.put(Context.LINE_SIZE, 0);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }
}
