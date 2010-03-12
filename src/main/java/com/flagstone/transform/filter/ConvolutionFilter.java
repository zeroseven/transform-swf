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

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.datatype.Color;
import com.flagstone.transform.filter.BevelFilter.Builder;

/** TODO(class). */
public final class ConvolutionFilter implements Filter {

    /** TODO(class). */
    public static class Builder {
        private float[][] matrix;
        private float divisor;
        private float bias;
        private Color color;
        private boolean clamp;
        private boolean alpha;
        
        private int rows;
        private int cols;
        
        /** TODO(method). */
        public Builder matrix(float[][] matrix) {
            rows = matrix.length;
            cols = matrix[0].length;
            this.matrix = new float[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    this.matrix[i][j] = matrix[i][j];
                }
            }
            return this;
        }
        
        /** TODO(method). */
        public Builder divisor(float value) {
            divisor = value;
            return this;
        }
        
        /** TODO(method). */
        public Builder bias(float value) {
            bias = value;
            return this;
        }
        
        /** TODO(method). */
        public Builder color(Color color) {
            this.color = color;
            return this;
        }
        
        /** TODO(method). */
        public Builder clamp(boolean clamp) {
            this.clamp = clamp;
            return this;
        }
        
        /** TODO(method). */
        public Builder alpha(boolean alpha) {
            this.alpha = alpha;
            return this;
        }
        
        /** TODO(method). */
        public ConvolutionFilter build() {
            return new ConvolutionFilter(this);
        }
    }

    private static final String FORMAT = "ConvolutionFilter: { matrix=%s; "
            + "divisor=%d; bias=%d; color=%s; clamp=%s; alpha=%s }";

    private final float[][] matrix;
    private final float divisor;
    private final float bias;
    private final Color color;
    private final boolean clamp;
    private final boolean alpha;

    private transient int rows;
    private transient int cols;
    
    private ConvolutionFilter(Builder builder) {
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
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public ConvolutionFilter(final SWFDecoder coder, final Context context)
            throws CoderException {
        coder.readByte();
        cols = coder.readByte();
        rows = coder.readByte();
        divisor = coder.readFloat();
        bias = coder.readFloat();
        matrix = new float[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = coder.readFloat();
            }
        }
        color = new Color(coder, context);
        coder.adjustPointer(6);
        clamp = coder.readBits(1, false) != 0;
        alpha = coder.readBits(1, false) != 0;
    }

    /** TODO(method). */
    public float getDivisor() {
        return divisor;
    }

    /** TODO(method). */
    public float getBias() {
        return divisor;
    }

    /** TODO(method). */
    public float[][] getMatrix() {
        return matrix.clone();
    }

    /** TODO(method). */
    public Color getColor() {
        return color;
    }

    /** TODO(method). */
    public boolean isClamp() {
        return clamp;
    }

    /** TODO(method). */
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
        return ((((matrix.hashCode() * 31
                + Float.floatToIntBits(divisor)) * 31
                + Float.floatToIntBits(bias)) * 31
                + color.hashCode()) * 31
                + Boolean.valueOf(clamp).hashCode())* 31
                + Boolean.valueOf(alpha).hashCode();
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        rows = matrix.length;
        cols = matrix[0].length;

        return 16 + rows * cols * 4;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        coder.writeByte(FilterTypes.CONVOLUTION);
        coder.writeByte(cols);
        coder.writeByte(rows);
        coder.writeFloat(divisor);
        coder.writeFloat(bias);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                coder.writeFloat(matrix[i][j]);
            }
        }
        color.encode(coder, context);
        coder.writeBits(0, 6);
        coder.writeBits(clamp ? 1 : 0, 1);
        coder.writeBits(alpha ? 1 : 0, 1);
    }
}
