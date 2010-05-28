/*
 * DropShadowFilter.java
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

package com.flagstone.transform.filter;

import java.io.IOException;

import com.flagstone.transform.Constants;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.datatype.Color;

/** TODO(class). */
public final class DropShadowFilter implements Filter {

    /** TODO(class). */
    public static final class Builder {
        /** The shadow colour. */
        private transient Color shadow;
        /** The horizontal blur amount. */
        private transient int blurX;
        /** The vertical blur amount. */
        private transient int blurY;
        /** Angle of shadow in radians. */
        private transient int angle;
        /** The distance of the drop shadow. */
        private transient int distance;
        /** The strength of the drop shadow. */
        private transient int strength;
        /** Compositing mode. */
        private transient int mode;
        /** The number of blur passes. */
        private transient int passes;


       public Builder setShadow(final Color color) {
            shadow = color;
            return this;
        }


        public Builder setBlur(final float xAmount, final float yAmount) {
            blurX = (int) (xAmount * SCALE_16);
            blurY = (int) (yAmount * SCALE_16);
            return this;
        }


        public Builder setMode(final FilterMode filterMode) {
            switch (filterMode) {
            case TOP:
                 mode = 0x0030;
                break;
            case KNOCKOUT:
                mode = 0x0060;
                break;
            case INNER:
                mode = 0x00A0;
                break;
            default:
                throw new IllegalArgumentException();
            }
            return this;
        }


        public Builder setAngle(final float anAngle) {
            angle = (int) (anAngle * SCALE_16);
            return this;
        }


        public Builder setDistance(final float dist) {
            distance = (int) (dist * SCALE_16);
            return this;
        }


        public Builder setStrength(final float weight) {
            strength = (int) (weight * SCALE_8);
            return this;
        }


        public Builder setPasses(final int count) {
            passes = count;
            return this;
        }


        public DropShadowFilter build() {
            return new DropShadowFilter(this);
        }
    }

    /**
     * Factor used to scale floating-point so they can be encoded as 16.16
     * fixed point values..
     */
    private static final float SCALE_16 = 65536.0f;
    /**
     * Factor used to scale floating-point so they can be encoded as 8.8
     * fixed point values..
     */
    private static final float SCALE_8 = 256.0f;

    /** Format string used in toString() method. */
    private static final String FORMAT = "DropShadowFilter: {"
        + " shadow=%s; blurX=%f; blurY=%f"
        + " angle=%f; disance=%f, strength=%f; mode=%s; passes=%d}";

    /** The shadow colour. */
    private final transient Color shadow;
    /** The horizontal blur amount. */
    private final transient int blurX;
    /** The vertical blur amount. */
    private final transient int blurY;
    /** Angle of shadow in radians. */
    private final transient int angle;
    /** The distance of the drop shadow. */
    private final transient int distance;
    /** The strength of the drop shadow. */
    private final transient int strength;
    /** Compositing mode. */
    private final transient int mode;
    /** The number of blur passes. */
    private final transient int passes;

    public DropShadowFilter(final Builder builder) {
        shadow = builder.shadow;
        blurX = builder.blurX;
        blurY = builder.blurY;
        angle =  builder.angle;
        distance =  builder.distance;
        strength =  builder.strength;
        mode =  builder.mode;
        passes = builder.passes;
    }

    /**
     * Creates and initialises a DropShadowFilter object using values encoded
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
    public DropShadowFilter(final SWFDecoder coder, final Context context)
            throws IOException {
        shadow = new Color(coder, context);
        blurX = coder.readInt();
        blurY = coder.readInt();
        angle = coder.readInt();
        distance = coder.readInt();
        strength = coder.readSignedShort();

        final int value = coder.readByte();

        passes = value & 0x0F;
        mode = (value & 0x0D) >>> 4;
    }


    public Color getShadow() {
        return shadow;
    }


    public float getBlurX() {
        return blurX / SCALE_16;
    }


    public float getBlurY() {
        return blurY / SCALE_16;
    }


    public float getAngle() {
        return angle / SCALE_16;
    }


    public float getDistance() {
        return distance / SCALE_16;
    }


    public float getStrength() {
        return strength / SCALE_8;
    }


    public FilterMode getMode() {
        FilterMode value;
        switch (mode) {
        case 0x0060:
            value = FilterMode.KNOCKOUT;
            break;
        case 0x00A0:
            value = FilterMode.INNER;
            break;
        default:
            throw new IllegalStateException();
        }
        return value;
    }


    public int getPasses() {
        return passes;
    }

    @Override
    public String toString() {
        return String.format(FORMAT, shadow.toString(),
                getAngle(), getDistance(), getStrength(),
                getBlurX(), getBlurY(), mode, passes);
    }

    @Override
    public boolean equals(final Object object) {
        boolean result;
        DropShadowFilter filter;

        if (object == null) {
            result = false;
        } else if (object == this) {
            result = true;
        } else if (object instanceof DropShadowFilter) {
            filter = (DropShadowFilter) object;
            result = shadow.equals(filter.shadow) && (blurX == filter.blurX)
                    && (blurY == filter.blurY) && (angle == filter.angle)
                    && (distance == filter.distance)
                    && (strength == filter.strength) && (mode == filter.mode)
                    && (passes == filter.passes);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return ((((((shadow.hashCode() * Constants.PRIME
                + blurX) * Constants.PRIME
                + blurY) * Constants.PRIME
                + angle * Constants.PRIME)
                + distance) * Constants.PRIME
                + strength) * Constants.PRIME
                + mode) * Constants.PRIME
                + passes;
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final Context context) {
        return 24;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws IOException {
        coder.writeByte(FilterTypes.DROP_SHADOW);
        shadow.encode(coder, context);
        coder.writeI32(blurX);
        coder.writeI32(blurY);
        coder.writeI32(angle);
        coder.writeI32(distance);
        coder.writeI16(strength);
        coder.writeByte(mode | passes);
    }
}