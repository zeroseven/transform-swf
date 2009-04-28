package com.flagstone.transform.filter;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.Filter;
import com.flagstone.transform.coder.FilterTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.datatype.Color;

public final class ConvolutionFilter implements Filter {

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

    public ConvolutionFilter(final ConvolutionFilter object) {
        divisor = object.divisor;
        bias = object.bias;
        matrix = object.matrix;
        color = object.color;
        clamp = object.clamp;
        alpha = object.alpha;
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

    public ConvolutionFilter copy() {
        return new ConvolutionFilter(this);
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
        return (((((matrix.hashCode() * 31) + Float.floatToIntBits(divisor)) * 31 + Float
                .floatToIntBits(bias)) * 31 + color.hashCode()) * 31 + Boolean
                .valueOf(clamp).hashCode())
                * 31 + Boolean.valueOf(alpha).hashCode();
    }

    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        rows = matrix.length;
        cols = matrix[0].length;

        return 16 + rows * cols * 4;
    }

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
