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

        /** TODO(method). */
        public Builder() {
            gradients = new ArrayList<Gradient>();
        }
        
        /** TODO(method). */
        public Builder addGradient(final Gradient gradient) {
            gradients.add(gradient);
            return this;
        }
                
        /** TODO(method). */
        public Builder setBlur(final float xAmount, final float yAmount) {
            blurX = (int) (xAmount * 65536.0f);
            blurY = (int) (yAmount * 65536.0f);
            return this;
        }
        
        /** TODO(method). */
        public Builder setMode(final FilterMode mode) {
            switch (mode) {
            case TOP:
                 this.mode = 0x0030;
                break;
            case KNOCKOUT:
                this.mode = 0x0060;
                break;
            case INNER:
                this.mode = 0x00A0;
                break;
            default:
                throw new IllegalArgumentException();
            }
            return this;
        }

        /** TODO(method). */
        public Builder setAngle(final float angle) {
            this.angle = (int) (angle * 65536.0f);
            return this;
        }
        
        /** TODO(method). */
        public Builder setDistance(final float distance) {
            this.distance = (int) (distance * 65536.0f);
            return this;
        }
        
        /** TODO(method). */
        public Builder setStrength(final float strength) {
            this.strength = (int) (strength * 256.0f);
            return this;
        }
        
        /** TODO(method). */
        public Builder setPasses(final int count) {
            passes = count;
            return this;
        }
        
        /** TODO(method). */
        public GradientBevelFilter build() {
            return new GradientBevelFilter(this);
        }
    }

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

        blurX = coder.readWord(4, true);
        blurY = coder.readWord(4, true);
        angle = coder.readWord(4, true);
        distance = coder.readWord(4, true);
        strength = coder.readWord(2, true);

        final int value = coder.readByte();

        passes = value & 0x0F;
        mode = (value & 0x0D) >>> 4;
    }

    /** TODO(method). */
    public List<Gradient> getGradients() {
        return gradients;
    }

    /** TODO(method). */
    public float getBlurX() {
        return blurX / 65536.0f;
    }

    /** TODO(method). */
    public float getBlurY() {
        return blurY / 65536.0f;
    }

    /** TODO(method). */
    public float getAngle() {
        return angle / 65536.0f;
    }

    /** TODO(method). */
    public float getDistance() {
        return distance / 65536.0f;
    }

    /** TODO(method). */
    public float getStrength() {
        return strength / 256.0f;
    }
    
    /** TODO(method). */
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

    /** TODO(method). */
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
        return (((((((gradients.hashCode() * 31) + blurX) * 31 + blurY) * 31
            + angle * 31) + distance) * 31 + strength) * 31 + mode)
            * 31 + passes;
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

        coder.writeWord(blurX, 4);
        coder.writeWord(blurY, 4);
        coder.writeWord(angle, 4);
        coder.writeWord(distance, 4);
        coder.writeWord(strength, 2);
        coder.writeByte(mode | passes);
    }
}
