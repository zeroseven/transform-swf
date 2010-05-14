/*
 * DefineShape3.java
 * Transform
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

import com.flagstone.transform.SWF;
import com.flagstone.transform.coder.Coder;
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
 * DefineShape3 defines a transparent shape to be displayed. It extends the
 * DefineShape2 class by encoding the alpha channel in any Color objects
 * included in the line and fill styles.
 *
 * @see DefineShape2
 */
//TODO(class)
public final class DefineShape3 implements DefineTag {

    /**
     * Reserved length for style counts indicated that the number of line
     * or fill styles is encoded in the next 16-bit word.
     */
    private static final int EXTENDED = 255;

    private static final String FORMAT = "DefineShape3: { identifier=%d;"
            + " bounds=%s; fillStyles=%s; lineStyles=%s; shape=%s }";

    private Bounds bounds;
    private List<FillStyle> fillStyles;
    private List<LineStyle> lineStyles;
    private Shape shape;

    private transient int length;
    private transient int fillBits;
    private transient int lineBits;
    private int identifier;

    /**
     * Creates and initialises a DefineShape3 object using values encoded
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
    public DefineShape3(final SWFDecoder coder, final Context context)
            throws CoderException {
        final int start = coder.getPointer();
        length = coder.readHeader();
        final int end = coder.getPointer() + (length << Coder.BYTES_TO_BITS);

        identifier = coder.readUI16();
        final Map<Integer, Integer> vars = context.getVariables();
        vars.put(Context.TRANSPARENT, 1);
        vars.put(Context.TYPE, MovieTypes.DEFINE_SHAPE_3);

        bounds = new Bounds(coder);

        int fillStyleCount = coder.readByte();

        if (fillStyleCount == EXTENDED) {
            fillStyleCount = coder.readUI16();
        }

        fillStyles = new ArrayList<FillStyle>();
        lineStyles = new ArrayList<LineStyle>();

        final SWFFactory<FillStyle> decoder = context.getRegistry()
                .getFillStyleDecoder();
        for (int i = 0; i < fillStyleCount; i++) {
            fillStyles.add(decoder.getObject(coder, context));
        }

        int lineStyleCount = coder.readByte();

        if (lineStyleCount == EXTENDED) {
            lineStyleCount = coder.readUI16();
        }

        for (int i = 0; i < lineStyleCount; i++) {
            lineStyles.add(new LineStyle(coder, context));
        }

        vars.put(Context.ARRAY_EXTENDED, 1);

        shape = new Shape(coder, context);

        vars.remove(Context.TRANSPARENT);
        vars.remove(Context.ARRAY_EXTENDED);
        vars.remove(Context.TYPE);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(),
                    start >> Coder.BITS_TO_BYTES, length,
                    (coder.getPointer() - end) >> Coder.BITS_TO_BYTES);
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
    public DefineShape3(final int uid, final Bounds aBounds,
            final List<FillStyle> fillStyleArray,
            final List<LineStyle> lineStyleArray, final Shape aShape) {
        setIdentifier(uid);
        setBounds(aBounds);
        setFillStyles(fillStyleArray);
        setLineStyles(lineStyleArray);
        setShape(aShape);
    }

    /**
     * Creates and initialises a DefineShape3 object using the values copied
     * from another DefineShape3 object.
     *
     * @param object
     *            a DefineShape3 object from which the values will be
     *            copied.
     */
    public DefineShape3(final DefineShape3 object) {
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
    public DefineShape3 add(final LineStyle style) {
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
    public DefineShape3 add(final FillStyle style) {
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

    /** {@inheritDoc} */
    public DefineShape3 copy() {
        return new DefineShape3(this);
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

        vars.put(Context.TRANSPARENT, 1);

        length = 2 + bounds.prepareToEncode(coder, context);

        length += (fillStyles.size() >= EXTENDED) ? 3 : 1;

        for (final FillStyle style : fillStyles) {
            length += style.prepareToEncode(coder, context);
        }

        length += (lineStyles.size() >= EXTENDED) ? 3 : 1;

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
        vars.remove(Context.TRANSPARENT);

        return (length > SWFEncoder.STD_LIMIT ? SWFEncoder.EXT_LENGTH
                : SWFEncoder.STD_LENGTH) + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        final int start = coder.getPointer();
        coder.writeHeader(MovieTypes.DEFINE_SHAPE_3, length);
        final int end = coder.getPointer() + (length << Coder.BYTES_TO_BITS);

        coder.writeI16(identifier);
        final Map<Integer, Integer> vars = context.getVariables();
        vars.put(Context.TRANSPARENT, 1);

        bounds.encode(coder, context);

        if (fillStyles.size() >= EXTENDED) {
            coder.writeWord(EXTENDED, 1);
            coder.writeI16(fillStyles.size());
        } else {
            coder.writeWord(fillStyles.size(), 1);
        }

        for (final FillStyle style : fillStyles) {
            style.encode(coder, context);
        }

        if (lineStyles.size() >= 255) {
            coder.writeWord(EXTENDED, 1);
            coder.writeI16(lineStyles.size());
        } else {
            coder.writeWord(lineStyles.size(), 1);
        }

        for (final LineStyle style : lineStyles) {
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
            throw new CoderException(getClass().getName(),
                    start >> Coder.BITS_TO_BYTES, length,
                    (coder.getPointer() - end) >> Coder.BITS_TO_BYTES);
        }
    }
}
