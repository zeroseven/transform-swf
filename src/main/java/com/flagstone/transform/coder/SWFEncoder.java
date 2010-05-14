/*
 * SWFEncoder.java
 * Transform
 *
 * Copyright (c) 2001-2010 Flagstone Software Ltd. All rights reserved.
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

package com.flagstone.transform.coder;

/**
 * SWFEncoder extends LittleEndianEncoder by adding a context used to pass
 * information between classes during encoding.
 */
//TODO(class)
public final class SWFEncoder extends Encoder {

    /**
     * The maximum length in bytes of an encoded object before the length must
     * be encoded using a 32-bit integer.
     */
    public static final int STD_LIMIT = 62;
    /**
     * Number of bytes occupied by the header when the size of the encoded
     * object is 62 bytes or less.
     */
    public static final int STD_LENGTH = 2;
    /**
     * Number of bytes occupied by the header when the size of the encoded
     * object is greater than 62 bytes.
     */
    public static final int EXT_LENGTH = 6;

    /**
     * Length, in bytes, of type and length fields of an encoded action.
     */
    public static final int ACTION_HEADER = 3;
    /**
     * The number of bits used to encode the length field when the length is
     * less than the maximum length of 62.
     */
    private static final int LENGTH_FIELD_SIZE = 6;
    /**
     * Values used to indicate that the length of an object has been encoded
     * as a 32-bit integer following the header for the MovieTag.
     */
    private static final int IS_EXTENDED = 0x3F;

    /**
     * Creates an SWFEncoder with the buffer used to encode data set to the
     * specified size.
     *
     * @param size
     *            the number of bytes in the internal buffer.
     */
    public SWFEncoder(final int size) {
        super(size);
    }

    /**
     * Calculate minimum number of bytes a 32-bit unsigned integer can be
     * encoded in.
     *
     * @param value
     *            an integer containing the value to be written.
     * @return the number of bytes required to encode the integer.
     */
    public static int sizeVariableU32(final int value) {

        int val = value;
        int size = 1;

        while (val > 127) {
            size += 1;
            val = val >>> 7;
        }
        return size;
    }

    /**
     * Write a 16-bit integer.
     *
     * @param value
     *            an integer containing the value to be written.
     */
    public void writeI16(final int value) {
        data[index++] = (byte) value;
        data[index++] = (byte) (value >>> ALIGN_BYTE_1);
    }

    /**
     * Write a 32-bit integer.
     *
     * @param value
     *            an integer containing the value to be written.
     */
    public void writeI32(final int value) {
        data[index++] = (byte) value;
        data[index++] = (byte) (value >>> ALIGN_BYTE_1);
        data[index++] = (byte) (value >>> ALIGN_BYTE_2);
        data[index++] = (byte) (value >>> ALIGN_BYTE_3);
    }

    /**
     * Write a word.
     *
     * @param value
     *            an integer containing the value to be written.
     *
     * @param numberOfBytes
     *            the (least significant) number of bytes from the value that
     *            will be written.
     */
    public void writeWord(final int value, final int numberOfBytes) {
        for (int i = 0; i < numberOfBytes; i++) {
            data[index++] = (byte) (value >>> (i << 3));
        }
    }

    /**
     * Write a 32-bit unsigned integer, encoded in a variable number of bytes.
     *
     * @param value
     *            an integer containing the value to be written.
     */
    public void writeVariableU32(final int value) {
        //CHECKSTYLE:OFF
        int val = value;

        if (val > 127) {
            data[index++] = (byte) ((val & 0x007F) | 0x0080);
            val = val >>> 7;

            if (val > 127) {
                data[index++] = (byte) ((val & 0x007F) | 0x0080);
                val = val >>> 7;

                if (val > 127) {
                    data[index++] = (byte) ((val & 0x007F) | 0x0080);
                    val = val >>> 7;

                    if (val > 127) {
                        data[index++] = (byte) ((val & 0x007F) | 0x0080);
                        val = val >>> 7;

                        data[index++] = (byte) (val & 0x007F);
                    } else {
                        data[index++] = (byte) (val & 0x007F);
                    }
                } else {
                    data[index++] = (byte) (val & 0x007F);
                }
            } else {
                data[index++] = (byte) (val & 0x007F);
            }
        } else {
            data[index++] = (byte) (value & 0x007F);
        }
        //CHECKSTYLE:ON
    }

    /**
     * Write a single-precision floating point number.
     *
     * @param value
     *            the value to be written.
     */
    public void writeHalf(final float value) {
        //CHECKSTYLE:OFF
        final int intValue = Float.floatToIntBits(value);
        final int sign = (intValue >> 16) & 0x00008000;
        final int exponent = ((intValue >> 23) & UNSIGNED_BYTE_MASK)
                        - (127 - 15);
        int mantissa = intValue & 0x007fffff;

        if (exponent <= 0) {
            if (exponent < -10) {
                writeI16(0);
            } else {
                mantissa = (mantissa | 0x00800000) >> (1 - exponent);
                writeI16((sign | (mantissa >> 13)));
            }
        } else if (exponent == 0xff - (127 - 15)) {
            if (mantissa == 0) { // Inf
                writeI16(sign | 0x7c00);
            } else { // NAN
                mantissa >>= 13;
                writeI16((sign | 0x7c00 | mantissa
                        | ((mantissa == 0) ? 1 : 0)));
            }
        } else {
            if (exponent > 30) { // Overflow
                writeI16((sign | 0x7c00));
            } else {
                writeI16((sign | (exponent << 10) | (mantissa >> 13)));
            }
        }
        //CHECKSTYLE:ON
    }

    /**
     * Write a single-precision floating point number.
     *
     * @param value
     *            the value to be written.
     */
    public void writeFloat(final float value) {
        writeI32(Float.floatToIntBits(value));
    }

    /**
     * Write a double-precision floating point number.
     *
     * @param value
     *            the value to be written.
     */
    public void writeDouble(final double value) {
        final long longValue = Double.doubleToLongBits(value);

        writeI32((int) (longValue >> ALIGN_WORD));
        writeI32((int) longValue);
    }

    /**
     * Write the header fields containing type and length of the object.
     *
     * Normally objects are encoded with a 2-byte header however if the length
     * is greater than 62 then the size of the header is extended to 6 bytes
     * and length is written as a 32-bit integer.
     *
     * @param type used to identify the object when decoding.
     * @param length the length in bytes of the encoded object.
     */
    public void writeHeader(final int type, final int length) {
        if (length > STD_LIMIT) {
            writeI16((type << LENGTH_FIELD_SIZE) | IS_EXTENDED);
            writeI32(length);
        } else {
            writeI16((type << LENGTH_FIELD_SIZE) | length);
        }
    }
}
