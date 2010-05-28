/*
 * LineStyle2.java
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

package com.flagstone.transform.linestyle;


import java.io.IOException;

import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.Copyable;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncodeable;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.coder.SWFFactory;
import com.flagstone.transform.datatype.Color;
import com.flagstone.transform.exception.IllegalArgumentRangeException;
import com.flagstone.transform.fillstyle.FillStyle;

/** TODO(class). */
public final class LineStyle2 implements SWFEncodeable, Copyable<LineStyle2> {

    /** Format string used in toString() method. */
    private static final String FORMAT = "LineStyle2: { width=%d; color=%s;"
            + " fillStyle=%s; startCap=%s; endCap=%s; joinStyle=%s;"
            + " scaledHorizontally=%b; scaledVertically=%b;"
            + " pixelAligned=%b; lineClosed=%b; miterLimit=%d }";

    private int width;
    private Color color;

    private int startCap;
    private int endCap;
    private int joinStyle;
    private FillStyle fillStyle;

    private boolean horizontal;
    private boolean vertical;
    private boolean pixelAligned;
    private boolean lineClosed;

    private int miterLimit;

    private transient boolean hasFillStyle;
    private transient boolean hasMiter;

    /**
     * Creates and initialises a LineStyle2 object using values encoded
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
    public LineStyle2(final SWFDecoder coder, final Context context)
            throws IOException {

        width = coder.readUnsignedShort();

        int bits = coder.readByte();
        if ((bits & Coder.BIT6) != 0) {
            startCap = 1;
        } else if ((bits & Coder.BIT7) != 0) {
            startCap = 2;
        } else {
            startCap = 0;
        }

        if ((bits & Coder.BIT4) != 0) {
            joinStyle = 1;
            hasMiter = false;
        } else if ((bits & Coder.BIT5) != 0) {
            joinStyle = 2;
            hasMiter = true;
        } else {
            joinStyle = 0;
            hasMiter = false;
        }

        hasFillStyle = (bits & Coder.BIT3) != 0;
        horizontal = (bits & Coder.BIT2) == 0;
        vertical = (bits & Coder.BIT1) == 0;
        pixelAligned = (bits & Coder.BIT0) != 0;

        bits = coder.readByte();
        lineClosed = (bits & Coder.BIT2) == 0;
        endCap = bits & 0x03;

        if (hasMiter) {
            coder.readUnsignedShort();
        }

        if (hasFillStyle) {
            final SWFFactory<FillStyle> decoder = context.getRegistry()
                    .getFillStyleDecoder();
            fillStyle = decoder.getObject(coder, context);
        } else {
            color = new Color(coder, context);
        }
    }


    public LineStyle2(final int lineWidth, final Color lineColor) {
        super();

        setWidth(lineWidth);
        setColor(lineColor);

        vertical = true;
        vertical = true;
        lineClosed = true;
    }


    public LineStyle2(final int lineWidth, final FillStyle style) {
        super();

        setWidth(lineWidth);
        setFillStyle(style);

        vertical = true;
        vertical = true;
        lineClosed = true;
    }

     /**
     * Creates and initialises a LineStyle2 object using the values copied
     * from another LineStyle2 object.
     *
     * @param object
     *            a LineStyle2 object from which the values will be
     *            copied.
     */
    public LineStyle2(final LineStyle2 object) {
        width = object.width;
        color = object.color;

        if (object.fillStyle != null) {
            fillStyle = object.fillStyle.copy();
        }

        startCap = object.startCap;
        endCap = object.endCap;
        joinStyle = object.joinStyle;

        horizontal = object.horizontal;
        vertical = object.vertical;
        pixelAligned = object.pixelAligned;
        lineClosed = object.lineClosed;
        miterLimit = object.miterLimit;
    }

    /**
     * Get the width of the line.
     *
     * @return the stroke width.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the width of the line.
     *
     * @param thickness
     *            the width of the line. Must be in the range 0..65535.
     */
    public void setWidth(final int thickness) {
        if ((thickness < 0) || (thickness > 65535)) {
            throw new IllegalArgumentRangeException(0, 65535, thickness);
        }
        width = thickness;
    }

    /**
     * Get the colour of the line.
     *
     * @return the line colour.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the colour of the line.
     *
     * @param aColor
     *            the colour of the line. Must be not be null.
     */
    public void setColor(final Color aColor) {
        if (aColor == null) {
            throw new IllegalArgumentException();
        }
        color = aColor;
    }


    public CapStyle getStartCap() {
        CapStyle style;
        if (startCap == 1) {
            style = CapStyle.NONE;
        } else if (startCap == 2) {
            style = CapStyle.SQUARE;
        } else {
            style = CapStyle.ROUND;
        }
        return style;
    }


    public void setStartCap(final CapStyle capStyle) {
        switch (capStyle) {
        case NONE:
            startCap = 1;
            break;
        case SQUARE:
            startCap = 2;
            break;
        default:
            startCap = 0;
            break;
        }
    }


    public CapStyle getEndCap() {
        CapStyle style;
        if (endCap == 1) {
            style = CapStyle.NONE;
        } else if (endCap == 2) {
            style = CapStyle.SQUARE;
        } else {
            style = CapStyle.ROUND;
        }
        return style;
    }


    public void setEndCap(final CapStyle capStyle) {
        switch (capStyle) {
        case NONE:
            endCap = 1;
            break;
        case SQUARE:
            endCap = 2;
            break;
        default:
            endCap = 0;
            break;
        }
    }


    public JoinStyle getJoinStyle() {
        JoinStyle style;
        if (endCap == 1) {
            style = JoinStyle.BEVEL;
        } else if (endCap == 2) {
            style = JoinStyle.MITER;
        } else {
            style = JoinStyle.ROUND;
        }
        return style;
    }


    public void setJoinStyle(final JoinStyle style) {
        switch (style) {
        case BEVEL:
            joinStyle = 1;
            break;
        case MITER:
            joinStyle = 2;
            break;
        default:
            joinStyle = 0;
            break;
        }
    }


    public boolean isHorizontal() {
        return horizontal;
    }


    public void setHorizontal(final boolean scaled) {
        horizontal = scaled;
    }


    public boolean isVertical() {
        return vertical;
    }


    public void setVertical(final boolean scaled) {
        vertical = scaled;
    }


    public boolean isPixelAligned() {
        return pixelAligned;
    }


    public void setPixelAligned(final boolean aligned) {
        pixelAligned = aligned;
    }


    public boolean isLineClosed() {
        return lineClosed;
    }


    public void setLineClosed(final boolean closed) {
        lineClosed = closed;
    }


    public int getMiterLimit() {
        return miterLimit;
    }


    public void setMiterLimit(final int limit) {
        if ((limit < 0) || (limit > 65535)) {
            throw new IllegalArgumentRangeException(0, 65535, limit);
        }
        miterLimit = limit;
    }


    public FillStyle getFillStyle() {
        return fillStyle;
    }


    public void setFillStyle(final FillStyle style) {
        fillStyle = style;
    }

    /** {@inheritDoc} */
    public LineStyle2 copy() {
        return new LineStyle2(this);
    }

    @Override
    public String toString() {
        return String.format(FORMAT, width, color, fillStyle, startCap, endCap,
                joinStyle, horizontal, vertical, pixelAligned,
                lineClosed, miterLimit);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final Context context) {

        hasFillStyle = fillStyle != null;
        hasMiter = joinStyle == 2;

        int length = 4;

        if (hasMiter) {
            length += 2;
        }

        if (hasFillStyle) {
            length += fillStyle.prepareToEncode(context);
        } else {
            length += 4;
        }

        if (horizontal || vertical) {
            context.getVariables().put(Context.SCALING_STROKE, 1);
        }

        return length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws IOException {
        coder.writeI16(width);

        int value = 0;

        if (startCap == 1) {
            value |= 0x000040;
        } else if (startCap == 2) {
            value |= 0x000080;
        }

        if (joinStyle == 1) {
            value |= 0x000010;
        } else if (joinStyle == 2) {
            value |= 0x000020;
        }

        value |= fillStyle == null ? 0 : 0x000008;
        value |= horizontal ? 0 : 0x000004;
        value |= vertical ? 0 : 0x000002;
        value |= pixelAligned ? 0x000001 : 0;

        coder.writeByte(value);

        value = lineClosed ? 0 : 0x00000004;
        value |= endCap;
        coder.writeByte(value);

        if (hasMiter) {
            coder.writeI16(miterLimit);
        }

        if (hasFillStyle) {
            fillStyle.encode(coder, context);
        } else {
            color.encode(coder, context);
        }
    }
}
