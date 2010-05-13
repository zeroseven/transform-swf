/*
 * BevelFilter.java
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

import com.flagstone.transform.Constants;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.datatype.Color;

/** TODO(class). */
public final class BevelFilter implements Filter {

    /** TODO(class). */
    public static final class Builder {
        private transient Color shadow;
        private transient Color highlight;
        private transient int blurX;
        private transient int blurY;
        private transient int angle;
        private transient int distance;
        private transient int strength;
        private transient int mode;
        private transient int passes;


       public Builder setShadow(final Color color) {
            shadow = color;
            return this;
        }


        public Builder setHighlight(final Color color) {
            highlight = color;
            return this;
        }


        public Builder setBlur(final float xAmount, final float yAmount) {
            blurX = (int) (xAmount * 65536.0f);
            blurY = (int) (yAmount * 65536.0f);
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
            angle = (int) (anAngle * 65536.0f);
            return this;
        }


        public Builder setDistance(final float dist) {
            distance = (int) (dist * 65536.0f);
            return this;
        }


        public Builder setStrength(final float weight) {
            strength = (int) (weight * 256.0f);
            return this;
        }


        public Builder setPasses(final int count) {
            passes = count;
            return this;
        }


        public BevelFilter build() {
            return new BevelFilter(this);
        }
    }

    private static final String FORMAT = "BevelFilter: {"
            + " shadow=%s; highlight=%s; blurX=%f; blurY=%f"
            + " angle=%f; disance=%f, strength=%f; mode=%s; passes=%d}";

    private final transient Color shadow;
    private final transient Color highlight;
    private final transient int blurX;
    private final transient int blurY;
    private final transient int angle;
    private final transient int distance;
    private final transient int strength;
    private final transient int mode;
    private final transient int passes;

    public BevelFilter(final Builder builder) {
        shadow = builder.shadow;
        highlight = builder.highlight;
        blurX = builder.blurX;
        blurY = builder.blurY;
        angle =  builder.angle;
        distance =  builder.distance;
        strength =  builder.strength;
        mode =  builder.mode;
        passes = builder.passes;
    }

    /**
     * Creates and initialises a BevelFilter object using values encoded
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
    public BevelFilter(final SWFDecoder coder, final Context context)
            throws CoderException {
        coder.adjustPointer(8);
        shadow = new Color(coder, context);
        highlight = new Color(coder, context);
        blurX = coder.readWord(4, true);
        blurY = coder.readWord(4, true);
        angle = coder.readWord(4, true);
        distance = coder.readWord(4, true);
        strength = coder.readWord(2, true);

        final int value = coder.readByte();

        passes = value & 0x0F;
        mode = (value & 0x0D) >>> 4;
    }


    public Color getShadow() {
        return shadow;
    }


    public Color getHightlight() {
        return highlight;
    }


    public float getBlurX() {
        return blurX / 65536.0f;
    }


    public float getBlurY() {
        return blurY / 65536.0f;
    }


    public float getAngle() {
        return angle / 65536.0f;
    }


    public float getDistance() {
        return distance / 65536.0f;
    }


    public float getStrength() {
        return strength / 256.0f;
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

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, shadow.toString(), highlight.toString(),
                getAngle(), getDistance(), getStrength(),
                getBlurX(), getBlurY(), mode, passes);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object object) {
        boolean result;
        BevelFilter filter;

        if (object == null) {
            result = false;
        } else if (object == this) {
            result = true;
        } else if (object instanceof BevelFilter) {
            filter = (BevelFilter) object;
            result = shadow.equals(filter.shadow)
                    && highlight.equals(filter.highlight)
                    && (blurX == filter.blurX) && (blurY == filter.blurY)
                    && (angle == filter.angle) && (distance == filter.distance)
                    && (strength == filter.strength) && (mode == filter.mode)
                    && (passes == filter.passes);
        } else {
            result = false;
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return ((((((((shadow.hashCode() * Constants.PRIME)
                + highlight.hashCode()) * Constants.PRIME
                + blurX) * Constants.PRIME
                + blurY) * Constants.PRIME
                + angle) * Constants.PRIME
                + distance) * Constants.PRIME
                + strength) * Constants.PRIME
                + mode) * Constants.PRIME
                + passes;
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        return 28;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        coder.writeByte(FilterTypes.BEVEL);
        shadow.encode(coder, context);
        highlight.encode(coder, context);
        coder.writeWord(blurX, 4);
        coder.writeWord(blurY, 4);
        coder.writeWord(angle, 4);
        coder.writeWord(distance, 4);
        coder.writeWord(strength, 2);
        coder.writeByte(mode | passes);
    }
}
