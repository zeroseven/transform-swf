/*
 * DefineShape.java
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.SWF;
import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.DefineTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.coder.SWFFactory;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.exception.IllegalArgumentRangeException;
import com.flagstone.transform.fillstyle.FillStyle;
import com.flagstone.transform.linestyle.LineStyle;

/**
 * DefineShape defines a shape to be displayed.
 *
 * <p>
 * The shape defines a path containing a mix of straight and curved edges and
 * pen move actions. A path need not be contiguous. When the shape is drawn the
 * ShapeStyle object selects the line and fill styles, from the respective
 * array, to be used. ShapeStyle objects can be defined in the shape at any time
 * to change the styles being used. The fill style used can either be a solid
 * colour, a bitmap image or a gradient. The line style specifies the colour and
 * thickness of the line drawn around the shape outline.
 * </p>
 *
 * <p>
 * For both line and fill styles the selected style may be undefined, allowing
 * the shape to be drawn without an outline or left unfilled.
 * </p>
 *
 * @see DefineShape2
 * @see DefineShape3
 */
//TODO(class)
public final class DefineShape implements DefineTag {

    /** Format string used in toString() method. */
    private static final String FORMAT = "DefineShape: { identifier=%d;"
            + " bounds=%s; fillStyles=%s; lineStyles=%s; shape=%s }";

    /** The unique identifier for this object. */
    private int identifier;
    /** The bounding box that encloses the shape. */
    private Bounds bounds;
    /** The list of fill styles for the shape. */
    private List<FillStyle> fillStyles;
    /** The list of line styles for the shape. */
    private List<LineStyle> lineStyles;
    /** The shape. */
    private Shape shape;

    /** The length of the object, minus the header, when it is encoded. */
    private transient int length;
    /** The number of bits to encode indices into the fill style array. */
    private transient int fillBits;
    /** The number of bits to encode indices into the line style array. */
    private transient int lineBits;

    /**
     * Creates and initialises a DefineShape object using values encoded
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
     * @throws IOException
     *             if an error occurs while decoding the data.
     */
    public DefineShape(final SWFDecoder coder, final Context context)
            throws IOException {
        length = coder.readUnsignedShort() & Coder.LENGTH_FIELD;
        if (length == Coder.IS_EXTENDED) {
            length = coder.readInt();
        }
        coder.mark();
        identifier = coder.readUnsignedShort();
        bounds = new Bounds(coder);

        fillStyles = new ArrayList<FillStyle>();
        lineStyles = new ArrayList<LineStyle>();

        context.put(Context.TYPE, MovieTypes.DEFINE_SHAPE);

        final int fillStyleCount = coder.readByte();

        final SWFFactory<FillStyle> decoder = context.getRegistry()
                .getFillStyleDecoder();

        for (int i = 0; i < fillStyleCount; i++) {
            fillStyles.add(decoder.getObject(coder, context));
        }

        final int lineStyleCount = coder.readByte();

        for (int i = 0; i < lineStyleCount; i++) {
            lineStyles.add(new LineStyle(coder, context));
        }

        if (context.getRegistry().getShapeDecoder() == null) {
            shape = new Shape();
            shape.add(new ShapeData(new byte[length - coder.bytesRead()]));
        } else {
            shape = new Shape(coder, context);
        }

        context.remove(Context.TYPE);
        coder.unmark(length);
    }

    /**
     * Creates a DefineShape object.
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
    public DefineShape(final int uid, final Bounds aBounds,
            final List<FillStyle> fillStyleArray,
            final List<LineStyle> lineStyleArray, final Shape aShape) {
        setIdentifier(uid);
        setBounds(aBounds);
        setFillStyles(fillStyleArray);
        setLineStyles(lineStyleArray);
        setShape(aShape);
    }

    /**
     * Creates and initialises a DefineShape object using the values copied
     * from another DefineShape object.
     *
     * @param object
     *            a DefineShape object from which the values will be
     *            copied.
     */
    public DefineShape(final DefineShape object) {
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

    /** {@inheritDoc} */
    public int getIdentifier() {
        return identifier;
    }

    /** {@inheritDoc} */
    public void setIdentifier(final int uid) {
        if ((uid < SWF.MIN_IDENTIFIER) || (uid > SWF.MAX_IDENTIFIER)) {
            throw new IllegalArgumentRangeException(
                    SWF.MIN_IDENTIFIER, SWF.MAX_IDENTIFIER, uid);
        }
        identifier = uid;
    }

    /**
     * Get the width of the shape in twips.
     *
     * @return the width of the shape.
     */
    public int getWidth() {
        return bounds.getWidth();
    }

    /**
     * Get the height of the shape in twips.
     *
     * @return the height of the shape.
     */
    public int getHeight() {
        return bounds.getHeight();
    }

    /**
     * Add a LineStyle to the array of line styles.
     *
     * @param style
     *            and LineStyle object. Must not be null.
     *
     * @return this object.
     */
    public DefineShape add(final LineStyle style) {
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
     *
     * @return this object.
     */
    public DefineShape add(final FillStyle style) {
        if (style == null) {
            throw new IllegalArgumentException();
        }
        fillStyles.add(style);
        return this;
    }

    /**
     * Get the bounding rectangle for the shape.
     *
     * @return the Bounds that encloses the shape.
     */
    public Bounds getBounds() {
        return bounds;
    }

    /**
     * Get the array fill styles.
     *
     * @return the list of fill styles used in the shape.
     */
    public List<FillStyle> getFillStyles() {
        return fillStyles;
    }

    /**
     * Get the array line styles.
     *
     * @return the list of line styles used in the shape.
     */
    public List<LineStyle> getLineStyles() {
        return lineStyles;
    }

    /**
     * Get the shape.
     *
     * @return the shape.
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

    /** {@inheritDoc} */
    public DefineShape copy() {
        return new DefineShape(this);
    }

    @Override
    public String toString() {
        return String.format(FORMAT, identifier, bounds, fillStyles,
                lineStyles, shape);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final Context context) {
        fillBits = SWFEncoder.unsignedSize(fillStyles.size());
        lineBits = SWFEncoder.unsignedSize(lineStyles.size());

        if (context.contains(Context.POSTSCRIPT)) {
            if (fillBits == 0) {
                fillBits = 1;
            }

            if (lineBits == 0) {
                lineBits = 1;
            }
        }

        length = 2 + bounds.prepareToEncode(context);
        length += 1;

        for (final FillStyle style : fillStyles) {
            length += style.prepareToEncode(context);
        }

        length += 1;

        for (final LineStyle style : lineStyles) {
            length += style.prepareToEncode(context);
        }

        context.put(Context.FILL_SIZE, fillBits);
        context.put(Context.LINE_SIZE, lineBits);

        length += shape.prepareToEncode(context);

        context.put(Context.FILL_SIZE, 0);
        context.put(Context.LINE_SIZE, 0);

        return (length > SWFEncoder.STD_LIMIT ? SWFEncoder.EXT_LENGTH
                : SWFEncoder.STD_LENGTH) + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws IOException {

        coder.writeHeader(MovieTypes.DEFINE_SHAPE, length);
        coder.mark();
        coder.writeI16(identifier);
        bounds.encode(coder, context);

        coder.writeWord(fillStyles.size(), 1);

        for (final FillStyle style : fillStyles) {
            style.encode(coder, context);
        }

        coder.writeWord(lineStyles.size(), 1);

        for (final LineStyle style : lineStyles) {
            style.encode(coder, context);
        }

        context.put(Context.FILL_SIZE, fillBits);
        context.put(Context.LINE_SIZE, lineBits);

        shape.encode(coder, context);

        context.put(Context.FILL_SIZE, 0);
        context.put(Context.LINE_SIZE, 0);
        coder.unmark(length);
    }
}
