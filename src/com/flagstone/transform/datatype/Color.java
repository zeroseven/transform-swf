/*
 * Color.java
 * Transform
 *
 * Copyright (c) 2001-2009 Flagstone Software Ltd. All rights reserved.
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
package com.flagstone.transform.datatype;

import com.flagstone.transform.Constants;
import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncodeable;
import com.flagstone.transform.coder.SWFEncoder;

/**
 * Color is used to represent 32-bit colours in the RGB colour space with 8 bits
 * per channel and an optional alpha channel.
 *
 * <p>
 * Whether a colour contains transparency information is determined by the
 * object that contains the colour. For example the colours in a DefineShape or
 * DefineShape2 do not use the alpha channel while those in DefineShape3 do. The
 * Context object, passed to each Color object, when it is encoded or decoded
 * signals whether the alpha channel should be included.
 * </p>
 */
public final class Color implements SWFEncodeable {

    /**
     * The number of channels in an opaque Color object. Only used within the
     * framework or when adding a new class.
     */
    public static final int RGB = 3;
    /**
     * The number of channels in a transparent Color object. Only used within
     * the framework or when adding a new class.
     */
    public static final int RGBA = 4;

    private static final String FORMAT = "Color: {"
            + " red=%d; green=%d; blue=%d; alpha=%d }";

    /** The minimum value that can be assigned to a colour channel. */
    public static final int MIN_LEVEL = 0;
    /** The maximum value that can be assigned to a colour channel. */
    public static final int MAX_LEVEL = 255;

    private final transient int red;
    private final transient int green;
    private final transient int blue;
    private final transient int alpha;

    /**
     * Creates and initialises a Color object using values encoded
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
    public Color(final SWFDecoder coder, final Context context)
            throws CoderException {
        red = coder.readByte();
        green = coder.readByte();
        blue = coder.readByte();

        if (context.getVariables().containsKey(Context.TRANSPARENT)) {
            alpha = coder.readByte();
        } else {
            alpha = MAX_LEVEL;
        }
    }

    /**
     * Creates an opaque colour object using an integer to represent the values
     * for the red, green and blue colour channels.
     *
     * @param rgb
     *            the integer value of the colour channels. The value is a
     *            24-bit integer with the value for the red channel in the most
     *            significant byte and blue in the least significant.
     */
    public Color(final int rgb) {
        red = (rgb >>> Coder.SELECT_BYTE_2) & Coder.MASK_BYTE_0;
        green = (rgb >>> Coder.SELECT_BYTE_1) & Coder.MASK_BYTE_0;
        blue = rgb & Coder.MASK_BYTE_0;
        alpha = MAX_LEVEL;
    }

    /**
     * Creates a transparent colour object using two integers, the first to
     * represents the values for the red, green and blue colour channels and the
     * second the value for the transparency.
     *
     * @param rgb
     *            the integer value of the colour channels. The value is a
     *            24-bit integer with the value for the red channel in the most
     *            significant byte and blue in the least significant.
     * @param alpha
     *            the level of transparency, in the range 0..255 where 0 is
     *            completely transparent and 255 is completely opaque.
     */
    public Color(final int rgb, final int alpha) {
        red = (rgb >>> Coder.SELECT_BYTE_2) & Coder.MASK_BYTE_0;
        green = (rgb >>> Coder.SELECT_BYTE_1) & Coder.MASK_BYTE_0;
        blue = rgb & Coder.MASK_BYTE_0;
        this.alpha = alpha;
    }

    /**
     * Creates a Color object containing red, green and blue channels. The alpha
     * channel defaults to the value 255 - defining an opaque colour.
     *
     * @param red
     *            value for the red channel, in the range 0..255.
     * @param green
     *            value for the green channel, in the range 0..255.
     * @param blue
     *            value for the blue channel, in the range 0..255.
     */
    public Color(final int red, final int green, final int blue) {
        this.red = checkLevel(red);
        this.green = checkLevel(green);
        this.blue = checkLevel(blue);
        alpha = MAX_LEVEL;
    }

    /**
     * Creates a transparent Color object containing red, green, blue and alpha
     * channels.
     *
     * @param red
     *            value for the red channel, in the range 0..255.
     * @param green
     *            value for the green channel, in the range 0..255.
     * @param blue
     *            value for the blue channel, in the range 0..255.
     * @param alpha
     *            value for the alpha channel, in the range 0..255.
     */
    public Color(final int red, final int green, final int blue, final int alpha) {
        this.red = checkLevel(red);
        this.green = checkLevel(green);
        this.blue = checkLevel(blue);
        this.alpha = checkLevel(alpha);
    }

    private int checkLevel(final int level) {
        if ((level < MIN_LEVEL) || (level > MAX_LEVEL)) {
            throw new IllegalArgumentException(Strings.COLOR_RANGE);
        }
        return level;
    }

    /**
     * Returns the value for the red colour channel.
     */
    public int getRed() {
        return red;
    }

    /**
     * Returns the value for the green colour channel.
     */
    public int getGreen() {
        return green;
    }

    /**
     * Returns the value for the blue colour channel.
     */
    public int getBlue() {
        return blue;
    }

    /**
     * Returns the value for the alpha colour channel.
     */
    public int getAlpha() {
        return alpha;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, red, green, blue, alpha);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object object) {
        boolean result;
        Color color;

        if (object == null) {
            result = false;
        } else if (object == this) {
            result = true;
        } else if (object instanceof Color) {
            color = (Color) object;
            result = (red == color.red) && (green == color.green)
                    && (blue == color.blue) && (alpha == color.alpha);
        } else {
            result = false;
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return ((red * Constants.PRIME + green) *  Constants.PRIME + blue)
            *  Constants.PRIME + alpha;
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        return (context.getVariables().containsKey(Context.TRANSPARENT))
            ? RGBA : RGB;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        coder.writeByte(red);
        coder.writeByte(green);
        coder.writeByte(blue);

        if (context.getVariables().containsKey(Context.TRANSPARENT)) {
            coder.writeByte(alpha);
        }
    }
}
