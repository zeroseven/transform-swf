/*
 * ConvolutionFilter.java
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

import com.flagstone.transform.SWF;
import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.datatype.Color;

/** TODO(class). */
public final class ConvolutionFilter implements Filter {

    /** TODO(class). */
    public static final class Builder {
        private transient float[][] matrix;
        private transient float divisor;
        private transient float bias;
        private transient Color color;
        private transient boolean clamp;
        private transient boolean alpha;

        private transient int rows;
        private transient int cols;


        public Builder setMatrix(final float[][] aMatrix) {
            rows = aMatrix.length;
            cols = aMatrix[0].length;
            matrix = new float[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    matrix[i][j] = aMatrix[i][j];
                }
            }
            return this;
        }


        public Builder setDivisor(final float value) {
            divisor = value;
            return this;
        }


        public Builder setBias(final float value) {
            bias = value;
            return this;
        }


        public Builder setColor(final Color aColor) {
            color = aColor;
            return this;
        }


        public Builder setClamp(final boolean flag) {
            clamp = flag;
            return this;
        }


        public Builder setAlpha(final boolean level) {
            alpha = level;
            return this;
        }


        public ConvolutionFilter build() {
            return new ConvolutionFilter(this);
        }
    }

    /** Format string used in toString() method. */
    private static final String FORMAT = "ConvolutionFilter: { matrix=%s;"
            + " divisor=%d; bias=%d; color=%s; clamp=%s; alpha=%s }";

    private final transient float[][] matrix;
    private final transient float divisor;
    private final transient float bias;
    private final transient Color color;
    private final transient boolean clamp;
    private final transient boolean alpha;

    private transient int rows;
    private transient int cols;

    public ConvolutionFilter(final Builder builder) {
        matrix = builder.matrix;
        divisor = builder.divisor;
        bias = builder.bias;
        color = builder.color;
        clamp = builder.clamp;
        alpha = builder.alpha;
    }

    /**
     * Creates and initialises a ConvolutionFilter object using values encoded
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
    public ConvolutionFilter(final SWFDecoder coder, final Context context)
            throws IOException {
        cols = coder.readByte();
        rows = coder.readByte();
        divisor = Float.intBitsToFloat(coder.readInt());
        bias = Float.intBitsToFloat(coder.readInt());
        matrix = new float[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = Float.intBitsToFloat(coder.readInt());
            }
        }
        color = new Color(coder, context);
        final int bits = coder.readByte();
        clamp = (bits & Coder.BIT1) != 0;
        alpha = (bits & Coder.BIT0) != 0;
    }


    public float getDivisor() {
        return divisor;
    }


    public float getBias() {
        return divisor;
    }


    public float[][] getMatrix() {
        return matrix.clone();
    }


    public Color getColor() {
        return color;
    }


    public boolean isClamp() {
        return clamp;
    }


    public boolean isAlpha() {
        return alpha;
    }

    @Override
    public String toString() {
        return String
                .format(FORMAT, matrix, divisor, bias, color, clamp, alpha);
    }

    @Override
    public boolean equals(final Object object) {
        boolean result;
        ConvolutionFilter filter;

        if (object == null) {
            result = false;
        } else if (object == this) {
            result = true;
        } else if (object instanceof ConvolutionFilter) {
            filter = (ConvolutionFilter) object;
            result = matrix.equals(filter.matrix)
                    && (divisor == filter.divisor) && (bias == filter.bias)
                    && color.equals(filter.color) && (clamp == filter.clamp)
                    && (alpha == filter.alpha);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return ((((matrix.hashCode() * SWF.PRIME
                + Float.floatToIntBits(divisor)) * SWF.PRIME
                + Float.floatToIntBits(bias)) * SWF.PRIME
                + color.hashCode()) * SWF.PRIME
                + Boolean.valueOf(clamp).hashCode()) * SWF.PRIME
                + Boolean.valueOf(alpha).hashCode();
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final Context context) {
        rows = matrix.length;
        cols = matrix[0].length;
        // CHECKSTYLE IGNORE MagicNumberCheck FOR NEXT 1 LINES
        return 16 + rows * cols * 4;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws IOException {
        coder.writeByte(FilterTypes.CONVOLUTION);
        coder.writeByte(cols);
        coder.writeByte(rows);
        coder.writeInt(Float.floatToIntBits(divisor));
        coder.writeInt(Float.floatToIntBits(bias));
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                coder.writeInt(Float.floatToIntBits(matrix[i][j]));
            }
        }
        color.encode(coder, context);
        int bits = 0;
        bits |= clamp ? Coder.BIT1 : 0;
        bits |= alpha ? Coder.BIT0 : 0;
        coder.writeByte(bits);
    }
}
