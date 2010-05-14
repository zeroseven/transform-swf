/*
 * GradientBevelFilter.java
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

import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.Constants;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.datatype.Color;
import com.flagstone.transform.fillstyle.Gradient;

/** TODO(class). */
public final class GradientBevelFilter implements Filter {

    /** TODO(class). */
    public static final class Builder {
        private final transient List<Gradient>gradients;
        private transient int blurX;
        private transient int blurY;
        private transient int angle;
        private transient int distance;
        private transient int strength;
        private transient int mode;
        private transient int passes;


        public Builder() {
            gradients = new ArrayList<Gradient>();
        }


        public Builder addGradient(final Gradient gradient) {
            gradients.add(gradient);
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


        public GradientBevelFilter build() {
            return new GradientBevelFilter(this);
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

    private static final String FORMAT = "GradientBevelFilter: { "
            + "gradients=%s; blurX=%f; blurY=%f; "
            + "angle=%f; disance=%f, strength=%f; mode=%s; passes=%d}";

    private final transient List<Gradient> gradients;
    private final transient int blurX;
    private final transient int blurY;
    private final transient int angle;
    private final transient int distance;
    private final transient int strength;
    private final transient int mode;
    private final transient int passes;

    public GradientBevelFilter(final Builder builder) {
        gradients = builder.gradients;
        blurX = builder.blurX;
        blurY = builder.blurY;
        angle = builder.angle;
        distance = builder.distance;
        strength =  builder.strength;
        mode =  builder.mode;
        passes = builder.passes;
    }

    /**
     * Creates and initialises a GradientBevelFilter object using values encoded
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
    public GradientBevelFilter(final SWFDecoder coder, final Context context)
            throws CoderException {
        coder.adjustPointer(8);
        final int count = coder.readByte();
        gradients = new ArrayList<Gradient>(count);
        Color color;
        int ratio;

        int colors = coder.getPointer();
        int ratios = coder.getPointer() + (count << 5);

        for (int i = 0; i < count; i++) {
            coder.setPointer(colors);
            color = new Color(coder, context);
            colors = coder.getPointer();

            coder.setPointer(ratios);
            ratio = coder.readByte();
            ratios = coder.getPointer();

            gradients.add(new Gradient(ratio, color));
        }

        blurX = coder.readSI32();
        blurY = coder.readSI32();
        angle = coder.readSI32();
        distance = coder.readSI32();
        strength = coder.readSI16();

        final int value = coder.readByte();

        passes = value & 0x0F;
        mode = (value & 0x0D) >>> 4;
    }


    public List<Gradient> getGradients() {
        return gradients;
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
        case 0x0030:
            value = FilterMode.TOP;
            break;
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
        return String.format(FORMAT, gradients.toString(),
                getBlurX(), getBlurY(),
                getAngle(), getDistance(), getStrength(), mode, passes);
    }

    @Override
    public boolean equals(final Object object) {
        boolean result;
        GradientBevelFilter filter;

        if (object == null) {
            result = false;
        } else if (object == this) {
            result = true;
        } else if (object instanceof GradientBevelFilter) {
            filter = (GradientBevelFilter) object;
            result = gradients.equals(filter.gradients)
                    && (blurX == filter.blurX) && (blurY == filter.blurY)
                    && (angle == filter.angle) && (distance == filter.distance)
                    && (strength == filter.strength) && (mode == filter.mode)
                    && (passes == filter.passes);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return (((((((gradients.hashCode() * Constants.PRIME
                + blurX) * Constants.PRIME
                + blurY) * Constants.PRIME
                + angle) * Constants.PRIME)
                + distance) * Constants.PRIME
                + strength) * Constants.PRIME
                + mode) * Constants.PRIME
                + passes;
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        return 21 + 5 * gradients.size();
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        coder.writeByte(FilterTypes.GRADIENT_BEVEL);
        coder.writeByte(gradients.size());

        for (final Gradient gradient : gradients) {
            gradient.getColor().encode(coder, context);
        }

        for (final Gradient gradient : gradients) {
            coder.writeByte(gradient.getRatio());
        }

        coder.writeI32(blurX);
        coder.writeI32(blurY);
        coder.writeI32(angle);
        coder.writeI32(distance);
        coder.writeI16(strength);
        coder.writeByte(mode | passes);
    }
}
