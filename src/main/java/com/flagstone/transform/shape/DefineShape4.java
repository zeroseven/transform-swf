/*
 * DefineShape4.java
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

package com.flagstone.transform.shape;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.DefineTag;
import com.flagstone.transform.SWF;
import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.coder.SWFFactory;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.exception.IllegalArgumentRangeException;
import com.flagstone.transform.fillstyle.FillStyle;
import com.flagstone.transform.linestyle.LineStyle2;

/**
 * DefineShape3 defines a transparent shape to be displayed. It extends the
 * DefineShape2 class by encoding the alpha channel in any Color objects
 * included in the line and fill styles.
 *
 * @see DefineShape2
 */
//TODO(class)
public final class DefineShape4 implements DefineTag {

    /**
     * Reserved length for style counts indicated that the number of line
     * or fill styles is encoded in the next 16-bit word.
     */
    private static final int EXTENDED = 255;

    /** Format string used in toString() method. */
    private static final String FORMAT = "DefineShape4: { identifier=%d;"
            + " shapeBounds=%s; edgeBounds=%s; fillStyles=%s; lineStyles=%s;"
            + " shape=%s }";

    /** The unique identifier for this object. */
    private int identifier;
    private Bounds shapeBounds;
    private Bounds edgeBounds;
    /** The list of fill styles for the shape. */
    private List<FillStyle> fillStyles;
    /** The list of line styles for the shape. */
    private List<LineStyle2> lineStyles;
    /** The shape. */
    private Shape shape;

    /** The length of the object, minus the header, when it is encoded. */
    private transient int length;
    /** The number of bits to encode indices into the fill style array. */
    private transient int fillBits;
    /** The number of bits to encode indices into the line style array. */
    private transient int lineBits;
    /** The length of the object, minus the header, when it is encoded. */
    private transient boolean scaling;

    /**
     * Creates and initialises a DefineShape4 object using values encoded
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
    public DefineShape4(final SWFDecoder coder, final Context context)
            throws IOException {
        length = coder.readUnsignedShort() & Coder.LENGTH_FIELD;
        if (length == Coder.IS_EXTENDED) {
            length = coder.readInt();
        }
        coder.mark();
        identifier = coder.readUnsignedShort();
        context.put(Context.TRANSPARENT, 1);
        context.put(Context.TYPE, MovieTypes.DEFINE_SHAPE_4);

        shapeBounds = new Bounds(coder);
        edgeBounds = new Bounds(coder);

        coder.readByte(); // scaling hints

        int fillStyleCount = coder.readByte();

        if (fillStyleCount == EXTENDED) {
            fillStyleCount = coder.readUnsignedShort();
        }
        fillStyles = new ArrayList<FillStyle>();
        lineStyles = new ArrayList<LineStyle2>();

        final SWFFactory<FillStyle> decoder = context.getRegistry()
                .getFillStyleDecoder();

        for (int i = 0; i < fillStyleCount; i++) {
             fillStyles.add(decoder.getObject(coder, context));
        }

        int lineStyleCount = coder.readByte();

        if (lineStyleCount == EXTENDED) {
            lineStyleCount = coder.readUnsignedShort();
        }

        for (int i = 0; i < lineStyleCount; i++) {
            lineStyles.add(new LineStyle2(coder, context));
        }

        context.put(Context.ARRAY_EXTENDED, 1);

        if (context.getRegistry().getShapeDecoder() == null) {
            shape = new Shape();
            shape.add(new ShapeData(new byte[length - coder.bytesRead()]));
        } else {
            shape = new Shape(coder, context);
        }

        context.remove(Context.TRANSPARENT);
        context.remove(Context.ARRAY_EXTENDED);
        context.remove(Context.TYPE);
        coder.unmark(length);
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

    /**
     * Creates and initialises a DefineShape4 object using the values copied
     * from another DefineShape4 object.
     *
     * @param object
     *            a DefineShape4 object from which the values will be
     *            copied.
     */
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
     * Get the bounding rectangle that completely enclosed the shape.
     *
     * @return the Bounds that encloses the shape
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
            throw new IllegalArgumentException();
        }
        shapeBounds = aBounds;
    }

    /**
     * Get the bounding rectangle that covers the outline of the shape.
     *
     * @return the Bounds that encloses the shape outline, excluding any
     * lines drawn.
     */
    public Bounds getEdgeBounds() {
        return edgeBounds;
    }

    /**
     * Sets the bounding rectangle that encloses the outline of the shape,
     * excluding the width of any lines drawn.
     *
     * @param aBounds
     *            set the bounding rectangle for the shape. Must not be null.
     */
    public void setEdgeBounds(final Bounds aBounds) {
        if (aBounds == null) {
            throw new IllegalArgumentException();
        }
        edgeBounds = aBounds;
    }

    /**
     * Get the width of the shape in twips.
     *
     * @return the width of the shape.
     */
    public int getWidth() {
        return shapeBounds.getWidth();
    }

    /**
     * Get the height of the shape in twips.
     *
     * @return the height of the shape.
     */
    public int getHeight() {
        return shapeBounds.getHeight();
    }

    /**
     * Add a LineStyle to the array of line styles.
     *
     * @param style
     *            and LineStyle object. Must not be null.
     *
     * @return this object.
     */
    public DefineShape4 add(final LineStyle2 style) {
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
    public DefineShape4 add(final FillStyle style) {
        if (style == null) {
            throw new IllegalArgumentException();
        }
        fillStyles.add(style);
        return this;
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
    public List<LineStyle2> getLineStyles() {
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
    public void setLineStyles(final List<LineStyle2> anArray) {
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
    public DefineShape4 copy() {
        return new DefineShape4(this);
    }

    @Override
    public String toString() {
        return String.format(FORMAT, identifier, shapeBounds, edgeBounds,
                fillStyles, lineStyles, shape);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final Context context) {
        fillBits = Coder.unsignedSize(fillStyles.size());
        lineBits = Coder.unsignedSize(lineStyles.size());

        if (context.contains(Context.POSTSCRIPT)) {
            if (fillBits == 0) {
                fillBits = 1;
            }

            if (lineBits == 0) {
                lineBits = 1;
            }
        }

        context.put(Context.TRANSPARENT, 1);

        length = 3;
        length += shapeBounds.prepareToEncode(context);
        length += edgeBounds.prepareToEncode(context);

        length += (fillStyles.size() >= EXTENDED) ? 3 : 1;

        for (final FillStyle style : fillStyles) {
            length += style.prepareToEncode(context);
        }

        context.remove(Context.SCALING_STROKE);

        length += (lineStyles.size() >= EXTENDED) ? 3 : 1;

        for (final LineStyle2 style : lineStyles) {
            length += style.prepareToEncode(context);
        }

        scaling = context.contains(Context.SCALING_STROKE);

        context.put(Context.ARRAY_EXTENDED, 1);
        context.put(Context.FILL_SIZE, fillBits);
        context.put(Context.LINE_SIZE, lineBits);

        length += shape.prepareToEncode(context);

        context.remove(Context.ARRAY_EXTENDED);
        context.put(Context.FILL_SIZE, 0);
        context.put(Context.LINE_SIZE, 0);
        context.remove(Context.TRANSPARENT);
        context.remove(Context.SCALING_STROKE);

        return (length > Coder.SHORT_HEADER_LIMIT ? Coder.LONG_HEADER
                : Coder.SHORT_HEADER) + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws IOException {
        context.put(Context.TRANSPARENT, 1);

        if (length > Coder.SHORT_HEADER_LIMIT) {
            coder.writeShort((MovieTypes.DEFINE_SHAPE_4
                    << Coder.LENGTH_FIELD_SIZE) | Coder.IS_EXTENDED);
            coder.writeInt(length);
        } else {
            coder.writeShort((MovieTypes.DEFINE_SHAPE_4
                    << Coder.LENGTH_FIELD_SIZE) | length);
        }
        coder.mark();
        coder.writeShort(identifier);

        shapeBounds.encode(coder, context);
        edgeBounds.encode(coder, context);

        coder.writeByte(scaling ? 1 : 2);

        if (fillStyles.size() >= EXTENDED) {
            coder.writeByte(EXTENDED);
            coder.writeShort(fillStyles.size());
        } else {
            coder.writeByte(fillStyles.size());
        }

        for (final FillStyle style : fillStyles) {
            style.encode(coder, context);
        }

        if (lineStyles.size() >= EXTENDED) {
            coder.writeByte(EXTENDED);
            coder.writeShort(lineStyles.size());
        } else {
            coder.writeByte(lineStyles.size());
        }

        for (final LineStyle2 style : lineStyles) {
            style.encode(coder, context);
        }

        context.put(Context.ARRAY_EXTENDED, 1);
        context.put(Context.FILL_SIZE, fillBits);
        context.put(Context.LINE_SIZE, lineBits);

        shape.encode(coder, context);

        context.remove(Context.ARRAY_EXTENDED);
        context.put(Context.FILL_SIZE, 0);
        context.put(Context.LINE_SIZE, 0);
        context.remove(Context.TRANSPARENT);
        coder.unmark(length);
    }
}
