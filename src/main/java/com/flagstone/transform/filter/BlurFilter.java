/*
 * BlurFilter.java
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
import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.exception.IllegalArgumentRangeException;

/** TODO(class). */
public final class BlurFilter implements Filter {

    /** Scaling factor for 16.16 fixed point values. */
    private static final float SCALE_16 = 65536.0f;
    /** Maximum number of passes to blur an object. */
    private static final int MAX_BLUR_COUNT = 31;

    /** Format string used in toString() method. */
    private static final String FORMAT = "BlurFilter: { blurX=%f; blurY=%f;"
            + " passes=%d }";

    /** The horizontal blur amount. */
    private final transient int blurX;
    /** The vertical blur amount. */
    private final transient int blurY;
    /** The number of blur passes. */
    private final transient int passes;

    /**
     * Creates and initialises a BlueFilter object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws IOException
     *             if an error occurs while decoding the data.
     */
    public BlurFilter(final SWFDecoder coder)
            throws IOException {
        blurX = coder.readInt();
        blurY = coder.readInt();
        passes = (coder.readByte() & Coder.UNSIGNED_BYTE_MASK) >>> 3;
    }


    public BlurFilter(final float xBlur, final float yBlur, final int count) {
        blurX = (int) (xBlur * SCALE_16);
        blurY = (int) (yBlur * SCALE_16);

        if ((count < 0) || (count > MAX_BLUR_COUNT)) {
            throw new IllegalArgumentRangeException(0, MAX_BLUR_COUNT, count);
        }
        passes = count;
    }


    public float getBlurX() {
        return blurX / SCALE_16;
    }


    public float getBlurY() {
        return blurY / SCALE_16;
    }


    public int getPasses() {
        return passes;
    }

    @Override
    public String toString() {
        return String.format(FORMAT, getBlurX(), getBlurY(), passes);
    }

    @Override
    public boolean equals(final Object object) {
        boolean result;
        BlurFilter filter;

        if (object == null) {
            result = false;
        } else if (object == this) {
            result = true;
        } else if (object instanceof BlurFilter) {
            filter = (BlurFilter) object;
            result = (blurX == filter.blurX) && (blurY == filter.blurY)
                    && (passes == filter.passes);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return ((blurX * Constants.PRIME) + blurY) * Constants.PRIME + passes;
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final Context context) {
        // CHECKSTYLE:OFF
        return 10;
        //CHECKSTYLE:ON
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws IOException {
        coder.writeByte(FilterTypes.BLUR);
        coder.writeI32(blurX);
        coder.writeI32(blurY);
        coder.writeByte(passes << 3);

    }
}
