/*
 * DefineMorphShape.java
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

import com.flagstone.transform.DefineTag;
import com.flagstone.transform.SWF;

import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.coder.SWFFactory;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.exception.IllegalArgumentRangeException;
import com.flagstone.transform.fillstyle.FillStyle;
import com.flagstone.transform.linestyle.MorphLineStyle;

/**
 * DefineMorphShape defines a shape that will morph from one form into another.
 *
 * <p>
 * Only the start and end shapes are defined the Flash Player will perform the
 * interpolation that transforms the shape at each staging in the morphing
 * process.
 * </p>
 *
 * <p>
 * Morphing can be applied to any shape, however there are a few restrictions:
 * </p>
 *
 * <ul>
 * <li>The start and end shapes must have the same number of edges (Line and
 * Curve objects).</li>
 * <li>The fill style (Solid, Bitmap or Gradient) must be the same in the start
 * and end shape.</li>
 * <li>If a bitmap fill style is used then the same image must be used in the
 * start and end shapes.</li>
 * <li>If a gradient fill style is used then the gradient must contain the same
 * number of points in the start and end shape.</li>
 * <li>Start and end shape must contain the same set of ShapeStyle objects.</li>
 * </ul>
 *
 * <p>
 * To perform the morphing of a shape the shape is placed in the display list
 * using a PlaceObject2 object. The ratio attribute in the PlaceObject2 object
 * defines the progress of the morphing process. The ratio ranges between 0 and
 * 65535 where 0 represents the start of the morphing process and 65535, the
 * end.
 * </p>
 *
 * <p>
 * The edges in the shapes may change their type when a shape is morphed.
 * Straight edges can become curves and vice versa.
 * </p>
 *
 */
//TODO(class)
public final class DefineMorphShape implements DefineTag {

    /**
     * Reserved length for style counts indicated that the number of line
     * or fill styles is encoded in the next 16-bit word.
     */
    private static final int EXTENDED = 255;

    /** Format string used in toString() method. */
    private static final String FORMAT = "DefineMorphShape: { identifier=%d;"
            + " startBounds=%s; endBounds=%s; fillStyles=%s; lineStyles=%s;"
            + " startShape=%s; endShape=%s }";

    /** The unique identifier for this object. */
    private int identifier;
    /** The bounding box for the shape at the start of the morphing process. */
    private Bounds startBounds;
    /** The bounding box for the shape at the end of the morphing process. */
    private Bounds endBounds;

    /** The list of fill styles for the shape. */
    private List<FillStyle> fillStyles;
    /** The list of line styles for the shape. */
    private List<MorphLineStyle> lineStyles;
    /** The shape at the start of the morphing process. */
    private Shape startShape;
    /** The shape at the end of the morphing process. */
    private Shape endShape;

    /** The length of the object, minus the header, when it is encoded. */
    private transient int length;
    /** The number of bits to encode indices into the fill style array. */
    private transient int fillBits;
    /** The number of bits to encode indices into the line style array. */
    private transient int lineBits;
    private transient int offset;

    /**
     * Creates and initialises a DefineMorphShape object using values encoded
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
    // TODO(optimise)
    public DefineMorphShape(final SWFDecoder coder, final Context context)
            throws IOException {
        length = coder.readUnsignedShort() & SWFDecoder.LENGTH_FIELD;
        if (length == SWFDecoder.IS_EXTENDED) {
            length = coder.readInt();
        }
        coder.mark();
        coder.mark();
        context.put(Context.TRANSPARENT, 1);
        context.put(Context.ARRAY_EXTENDED, 1);
        context.put(Context.TYPE, MovieTypes.DEFINE_MORPH_SHAPE);

        identifier = coder.readUnsignedShort();

        startBounds = new Bounds(coder);
        endBounds = new Bounds(coder);
        fillStyles = new ArrayList<FillStyle>();
        lineStyles = new ArrayList<MorphLineStyle>();

        final int offsetToEnd = coder.readInt();
        coder.mark();

        int fillStyleCount = coder.readByte();

        if (context.contains(Context.ARRAY_EXTENDED)
                && (fillStyleCount == EXTENDED)) {
            fillStyleCount = coder.readUnsignedShort();
        }

        final SWFFactory<FillStyle> decoder = context.getRegistry()
                .getMorphFillStyleDecoder();

        for (int i = 0; i < fillStyleCount; i++) {
            fillStyles.add(decoder.getObject(coder, context));
        }

        int lineStyleCount = coder.readByte();

        if (context.contains(Context.ARRAY_EXTENDED)
                && (lineStyleCount == EXTENDED)) {
            lineStyleCount = coder.readUnsignedShort();
        }

        for (int i = 0; i < lineStyleCount; i++) {
            lineStyles.add(new MorphLineStyle(coder, context));
        }

        if (context.getRegistry().getShapeDecoder() == null) {
            int size = coder.bytesRead() - offsetToEnd;
            coder.unmark();

            startShape = new Shape();
            startShape.add(new ShapeData(new byte[size]));

            size = length - coder.bytesRead();
            coder.unmark();

            endShape = new Shape();
            endShape.add(new ShapeData(new byte[size]));
        } else {
            startShape = new Shape(coder, context);
            endShape = new Shape(coder, context);
            coder.unmark();
            coder.unmark();
        }

        context.remove(Context.TRANSPARENT);
        context.put(Context.ARRAY_EXTENDED, 1);
        context.remove(Context.TYPE);
        // known bug - empty objects may be added to Flash file.
        if (length - coder.bytesRead() != 33) {
            coder.unmark(length);
        }
    }

    /**
     * Creates a DefineMorphShape object.
     *
     * @param uid
     *            an unique identifier for this object. Must be in the range
     *            1..65535.
     * @param initialBounds
     *            the bounding rectangle enclosing the start shape. Must not be
     *            null.
     * @param finalBounds
     *            the bounding rectangle enclosing the end shape. Must not be
     *            null.
     * @param fills
     *            an array of MorphSolidFill, MorphBitmapFill and
     *            MorphGradientFill objects. Must not be null.
     * @param lines
     *            an array of MorphLineStyle objects. Must not be null.
     * @param initialShape
     *            the shape at the start of the morphing process. Must not be
     *            null.
     * @param finalShape
     *            the shape at the end of the morphing process. Must not be
     *            null.
     */
    public DefineMorphShape(final int uid, final Bounds initialBounds,
            final Bounds finalBounds, final List<FillStyle> fills,
            final List<MorphLineStyle> lines, final Shape initialShape,
            final Shape finalShape) {
        setIdentifier(uid);
        setStartBounds(initialBounds);
        setEndBounds(finalBounds);
        setFillStyles(fills);
        setLineStyles(lines);
        setStartShape(initialShape);
        setEndShape(finalShape);
    }

    /**
     * Creates and initialises a DefineMorphShape object using the values copied
     * from another DefineMorphShape object.
     *
     * @param object
     *            a DefineMorphShape object from which the values will be
     *            copied.
     */
    public DefineMorphShape(final DefineMorphShape object) {
        identifier = object.identifier;
        startBounds = object.startBounds;
        endBounds = object.endBounds;
        fillStyles = new ArrayList<FillStyle>(object.fillStyles.size());
        for (final FillStyle style : object.fillStyles) {
            fillStyles.add(style.copy());
        }
        lineStyles = new ArrayList<MorphLineStyle>(object.lineStyles.size());
        for (final MorphLineStyle style : object.lineStyles) {
            lineStyles.add(style.copy());
        }
        startShape = object.startShape.copy();
        endShape = object.endShape.copy();
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
     * Get the width of the shape at the start of the morphing process.
     *
     * @return the width of the starting shape.
     */
    public int getWidth() {
        return startBounds.getWidth();
    }

    /**
     * Get the height of the shape at the start of the morphing process.
     *
     * @return the height of the starting shape.
     */
    public int getHeight() {
        return startBounds.getHeight();
    }

    /**
     * Add a LineStyle object to the array of line styles.
     *
     * @param aLineStyle
     *            and LineStyle object. Must not be null.
     *
     * @return this object.
     */
    public DefineMorphShape add(final MorphLineStyle aLineStyle) {
        lineStyles.add(aLineStyle);
        return this;
    }

    /**
     * Add the fill style object to the array of fill styles.
     *
     * @param aFillStyle
     *            an FillStyle object. Must not be null.
     *
     * @return this object.
     */
    public DefineMorphShape add(final FillStyle aFillStyle) {
        fillStyles.add(aFillStyle);
        return this;
    }

    /**
     * Get the Bounds object that defines the bounding rectangle enclosing
     * the start shape.
     *
     * @return the bounding box for the starting shape.
     */
    public Bounds getStartBounds() {
        return startBounds;
    }

    /**
     * Get the Bounds object that defines the bounding rectangle enclosing
     * the end shape.
     *
     * @return the bounding box for the final shape.
     */
    public Bounds getEndBounds() {
        return endBounds;
    }

    /**
     * Get the array of fill styles (MorphSolidFill, MorphBitmapFill and
     * MorphGradientFill objects) for the shapes.
     *
     * @return the list of fill styles used in the shape.
     */
    public List<FillStyle> getFillStyles() {
        return fillStyles;
    }

    /**
     * Get the array of line styles (MorphLineStyle objects) for the shapes.
     *
     * @return the list of line styles used in the shape.
     */
    public List<MorphLineStyle> getLineStyles() {
        return lineStyles;
    }

    /**
     * Get shape displayed at the start of the morphing process.
     *
     * @return the starting shape.
     */
    public Shape getStartShape() {
        return startShape;
    }

    /**
     * Get shape displayed at the end of the morphing process.
     *
     * @return the final shape.
     */
    public Shape getEndShape() {
        return endShape;
    }

    /**
     * Sets the starting bounds of the shape.
     *
     * @param aBounds
     *            the bounding rectangle enclosing the start shape. Must not be
     *            null.
     */
    public void setStartBounds(final Bounds aBounds) {
        if (aBounds == null) {
            throw new IllegalArgumentException();
        }
        startBounds = aBounds;
    }

    /**
     * Sets the ending bounds of the shape.
     *
     * @param aBounds
     *            the bounding rectangle enclosing the end shape. Must not be
     *            null.
     */
    public void setEndBounds(final Bounds aBounds) {
        if (aBounds == null) {
            throw new IllegalArgumentException();
        }
        endBounds = aBounds;
    }

    /**
     * Sets the array of morph fill styles.
     *
     * @param anArray
     *            an array of MorphSolidFill, MorphBitmapFill and
     *            MorphGradientFill objects. Must not be null.
     */
    public void setFillStyles(final List<FillStyle> anArray) {
        if (anArray == null) {
            throw new IllegalArgumentException();
        }
        fillStyles = anArray;
    }

    /**
     * Sets the array of morph line styles.
     *
     * @param anArray
     *            an array of MorphLineStyle objects. Must not be null.
     */
    public void setLineStyles(final List<MorphLineStyle> anArray) {
        if (anArray == null) {
            throw new IllegalArgumentException();
        }
        lineStyles = anArray;
    }

    /**
     * Sets the shape that will be displayed at the start of the morphing
     * process.
     *
     * @param aShape
     *            the shape at the start of the morphing process. Must not be
     *            null.
     */
    public void setStartShape(final Shape aShape) {
        if (aShape == null) {
            throw new IllegalArgumentException();
        }
        startShape = aShape;
    }

    /**
     * Sets the shape that will be displayed at the end of the morphing process.
     *
     * @param aShape
     *            the shape at the end of the morphing process. Must not be
     *            null.
     */
    public void setEndShape(final Shape aShape) {
        if (aShape == null) {
            throw new IllegalArgumentException();
        }
        endShape = aShape;
    }

    /** {@inheritDoc} */
    public DefineMorphShape copy() {
        return new DefineMorphShape(this);
    }

    @Override
    public String toString() {
        return String.format(FORMAT, identifier, startBounds, endBounds,
                fillStyles, lineStyles, startShape, endShape);
    }

    // TODO(optimise)
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

        context.put(Context.TRANSPARENT, 1);

        length = 2 + startBounds.prepareToEncode(context);
        length += endBounds.prepareToEncode(context);
        offset = length;
        length += 4;

        length += (fillStyles.size() >= EXTENDED) ? 3 : 1;

        for (final FillStyle style : fillStyles) {
            length += style.prepareToEncode(context);
        }

        length += (lineStyles.size() >= EXTENDED) ? 3 : 1;

        for (final MorphLineStyle style : lineStyles) {
            length += style.prepareToEncode(context);
        }

        context.put(Context.ARRAY_EXTENDED, 1);
        context.put(Context.FILL_SIZE, fillBits);
        context.put(Context.LINE_SIZE, lineBits);

        length += startShape.prepareToEncode(context);
        offset = length - offset;
        // Number of Fill and Line bits is zero for end shape.
        context.put(Context.FILL_SIZE, 0);
        context.put(Context.LINE_SIZE, 0);

        length += endShape.prepareToEncode(context);

        context.remove(Context.ARRAY_EXTENDED);
        context.remove(Context.TRANSPARENT);

        return (length > SWFEncoder.STD_LIMIT ? SWFEncoder.EXT_LENGTH
                : SWFEncoder.STD_LENGTH) + length;
    }

    // TODO(optimise)
    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws IOException {

        coder.writeHeader(MovieTypes.DEFINE_MORPH_SHAPE, length);
        coder.mark();
        coder.writeShort(identifier);
        context.put(Context.TRANSPARENT, 1);

        startBounds.encode(coder, context);
        endBounds.encode(coder, context);
        coder.writeInt(offset);

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

        for (final MorphLineStyle style : lineStyles) {
            style.encode(coder, context);
        }

        context.put(Context.ARRAY_EXTENDED, 1);
        context.put(Context.FILL_SIZE, fillBits);
        context.put(Context.LINE_SIZE, lineBits);

        startShape.encode(coder, context);

        // Number of Fill and Line bits is zero for end shape.
        context.put(Context.FILL_SIZE, 0);
        context.put(Context.LINE_SIZE, 0);

        endShape.encode(coder, context);

        context.remove(Context.ARRAY_EXTENDED);
        context.remove(Context.TRANSPARENT);
        coder.unmark(length);
    }
}
