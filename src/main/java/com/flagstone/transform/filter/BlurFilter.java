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


import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.exception.IllegalArgumentRangeException;

/** TODO(class). */
public final class BlurFilter implements Filter {

    private static final String FORMAT = "BlurFilter: { blurX=%f; blurY=%f;"
            + " passes=%d }";

    private final int blurX;
    private final int blurY;
    private final int passes;

    /**
     * Creates and initialises a BlueFilter object using values encoded
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
    public BlurFilter(final SWFDecoder coder, final Context context)
            throws CoderException {
        coder.readByte();
        blurX = coder.readWord(4, true);
        blurY = coder.readWord(4, true);
        passes = (coder.readByte() & 0x00FF) >>> 3;
    }

    /** TODO(method). */
    public BlurFilter(final float blurX, final float blurY, final int passes) {
        this.blurX = (int) (blurX * 65536);
        this.blurY = (int) (blurY * 65536);

        if ((passes < 0) || (passes > 31)) {
            throw new IllegalArgumentRangeException(0, 31, passes);
        }
        this.passes = passes;
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
        return ((blurX * 31) + blurY) * 31 + passes;
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        return 10;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        coder.writeByte(FilterTypes.BLUR);
        coder.writeWord(blurX, 4);
        coder.writeWord(blurY, 4);
        coder.writeByte(passes << 3);

    }
}
