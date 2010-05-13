/*
 * ColorMatrixFilter.java
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

import java.util.Arrays;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

/** TODO(class). */
public final class ColorMatrixFilter implements Filter {

    private static final String ROW_FORMAT = "[%f, %f, %f, %f, %f]";
    private static final String FORMAT = "ColorMatrix: { matrix=["
            + ROW_FORMAT + ROW_FORMAT + ROW_FORMAT + ROW_FORMAT
            + "]}";

    private final transient float[] matrix;

    /**
     * Creates and initialises a ColorMatrix object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public ColorMatrixFilter(final SWFDecoder coder) throws CoderException {
        coder.readByte();
        matrix = new float[20];
        for (int i = 0; i < 20; i++) {
            matrix[i] = coder.readFloat();
        }
    }


    public ColorMatrixFilter(final float[] aMatrix) {
        if ((aMatrix == null) || (aMatrix.length != 20)) {
            throw new IllegalArgumentException("Value not set");
        }
        matrix = Arrays.copyOf(aMatrix, aMatrix.length);
    }


    public float[] getMatrix() {
        return Arrays.copyOf(matrix, matrix.length);
    }

    @Override
    public String toString() {
        return String.format(FORMAT,
                matrix[0], matrix[1], matrix[2], matrix[3], matrix[4],
                matrix[5], matrix[6], matrix[7], matrix[8], matrix[9],
                matrix[10], matrix[11], matrix[12], matrix[13], matrix[14],
                matrix[15], matrix[16], matrix[17], matrix[18], matrix[19]
        );
    }

    @Override
    public boolean equals(final Object object) {
        boolean result;
        ColorMatrixFilter filter;

        if (object == null) {
            result = false;
        } else if (object == this) {
            result = true;
        } else if (object instanceof ColorMatrixFilter) {
            filter = (ColorMatrixFilter) object;
            result = Arrays.equals(matrix, filter.matrix);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(matrix);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        return 81;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        coder.writeByte(FilterTypes.COLOR_MATRIX);
        for (final float value : matrix) {
            coder.writeFloat(value);
        }
    }
}
