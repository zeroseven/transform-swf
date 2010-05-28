/*
 * SWFDecoder.java
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

import java.io.IOException;
import java.io.InputStream;


/**
 * SWFDecoder extends LittleEndianDecoder by adding a context used to pass
 * information between classes during decoding and a factory class for
 * generating instances of objects.
 */
//TODO(class)
public final class LittleDecoder extends Decoder {

    /**
     * Bit mask for extracting the length field from the header word.
     */
    private static final int LENGTH_FIELD = 0x3F;
    /**
     * Reserved length indicating length is encoded in next 32-bit word.
     */
    private static final int IS_EXTENDED = 63;

    public static final int BIT7 = 0x0080;
    public static final int BIT6 = 0x0040;
    public static final int BIT5 = 0x0020;
    public static final int BIT4 = 0x0010;
    public static final int BIT3 = 0x0008;
    public static final int BIT2 = 0x0004;
    public static final int BIT1 = 0x0002;
    public static final int BIT0 = 0x0001;

    public static final int NIB1 = 0x000C;
    public static final int NIB0 = 0x0003;

    private transient int type;
    private transient int length;
    private transient int bits;

    private transient InputStream stream;

    /**
     * Creates a SWFDecoder object initialised with the data to be decoded.
     *
     * @param data
     *            an array of bytes to be decoded.
     */
    public LittleDecoder(final byte[] data) {
        super(data);
    }

    public LittleDecoder(final InputStream streamIn) {
        super(new byte[100]);
        stream = streamIn;
    }

    public int prefetchByte() {
        bits = data[index++];
        return bits;
    }

    public boolean getBool(final int mask) {
        return (bits & mask) != 0;
    }

    public int getBit(final int mask) {
        return bits & mask;
    }

    /**
     * Read an unsigned 16-bit integer.
     *
     * @return the value read.
     */
    public int readUI16() {
        int value = data[index++] & UNSIGNED_BYTE_MASK;
        value |= (data[index++] & UNSIGNED_BYTE_MASK) << ALIGN_BYTE_1;
        return value;
    }

    /**
     * Read an unsigned 16-bit integer.
     *
     * @return the value read.
     */
    public int readSI16() {
        int value = data[index++] & UNSIGNED_BYTE_MASK;
        value |= data[index++] << ALIGN_BYTE_1;
        return value;
    }

    /**
     * Read an unsigned 32-bit integer.
     *
     * @return the value read.
     */
    public int readUI32() {
        int value = data[index++] & UNSIGNED_BYTE_MASK;
        value |= (data[index++] & UNSIGNED_BYTE_MASK) << ALIGN_BYTE_1;
        value |= (data[index++] & UNSIGNED_BYTE_MASK) << ALIGN_BYTE_2;
        value |= (data[index++] & UNSIGNED_BYTE_MASK) << ALIGN_BYTE_3;
        return value;
    }

    /**
     * Read an unsigned 32-bit integer.
     *
     * @return the value read.
     */
    public int readSI32() {
        int value = data[index++] & UNSIGNED_BYTE_MASK;
        value |= (data[index++] & UNSIGNED_BYTE_MASK) << ALIGN_BYTE_1;
        value |= (data[index++] & UNSIGNED_BYTE_MASK) << ALIGN_BYTE_2;
        value |= data[index++] << ALIGN_BYTE_3;
        return value;
    }

    /**
     * Read a word.
     *
     * @param numberOfBytes
     *            the number of bytes read in the range 1..4.
     *
     * @param signed
     *            indicates whether the value read is signed (true) or unsigned
     *            (false).
     *
     * @return the value read.
     */
    public int readWord(final int numberOfBytes, final boolean signed) {
        int value = 0;

        for (int i = 0; i < numberOfBytes; i++) {
            value += (data[index++] & UNSIGNED_BYTE_MASK) << (i << 3);
        }

        if (signed) {
            value <<= 32 - (numberOfBytes << 3);
            value >>= 32 - (numberOfBytes << 3);
        }

        return value;
    }

    /**
     * Read a 32-bit unsigned integer, encoded using a variable number of bytes.
     *
     * @return the value read.
     */
    public int readVariableU32() {

        int value = data[index++] & UNSIGNED_BYTE_MASK;

        final int mask = 0xFFFFFFFF;
        int test = 0x00000080;
        int step = 7;

        while ((value & test) != 0) {
            value = ((data[index++] & UNSIGNED_BYTE_MASK) << step)
                + (value & mask >>> (32 - step));
            test <<= 7;
            step += 7;
        }
//        if ((value & 0x00000080) != 0) {
//            value = ((data[index++] & 0x000000FF) << 7)
//        + (value & 0x0000007f);
//
//            if ((value & 0x00004000) != 0) {
//                value = ((data[index++] & 0x000000FF) << 14)
//                        + (value & 0x00003fff);
//
//                if ((value & 0x00200000) != 0) {
//                    value = ((data[index++] & 0x000000FF) << 21)
//                            + (value & 0x001fffff);
//
//                    if ((value & 0x10000000) != 0) {
//                        value = ((data[index++] & 0x000000FF) << 28)
//                                + (value & 0x0fffffff);
//                    }
//                }
//            }
//        }
        return value;
    }

    /**
     * Read a single-precision floating point number.
     *
     * @return the value.
     */
    public float readHalf() {
        final int bits = readWord(2, false);
        final int sign = (bits >> 15) & 0x00000001;
        int exp = (bits >> 10) & 0x0000001f;
        int mantissa = bits & 0x000003ff;
        float value;

        if (exp == 0) {
            if (mantissa == 0) { // Plus or minus zero
                value = Float.intBitsToFloat(sign << 31);
            } else { // Denormalized number -- renormalize it
                while ((mantissa & 0x00000400) == 0) {
                    mantissa <<= 1;
                    exp -=  1;
                }
                exp += 1;
                exp = exp + (127 - 15);
                mantissa &= ~0x00000400;
                mantissa = mantissa << 13;
                value = Float.intBitsToFloat((sign << 31)
                        | (exp << 23) | mantissa);
            }
        } else if (exp == 31) {
            if (mantissa == 0) { // Inf
                value = Float.intBitsToFloat((sign << 31) | 0x7f800000);
            } else { // NaN
                value = Float.intBitsToFloat((sign << 31)
                        | 0x7f800000 | (mantissa << 13));
            }
        } else {
            exp = exp + (127 - 15);
            mantissa = mantissa << 13;
            value = Float.intBitsToFloat((sign << 31)
                    | (exp << 23) | mantissa);
        }
        return value;
    }

    /**
     * Read a single-precision floating point number.
     *
     * @return the value.
     */
    public float readFloat() {
        return Float.intBitsToFloat(readWord(4, false));
    }

    /**
     * Read a double-precision floating point number.
     *
     * @return the value.
     */
    public double readDouble() {
        long longValue = (long) readWord(4, false) << 32;
        longValue |= readWord(4, false) & 0x00000000FFFFFFFFL;

        return Double.longBitsToDouble(longValue);
    }

    public int nextHeader() throws IOException {
        int value = stream.read();
        value |= stream.read() << 8;

        type = value >> 6;
        length = value & LENGTH_FIELD;

        if (length == IS_EXTENDED) {
            length = stream.read();
            length |= stream.read() << 8;
            length |= stream.read() << 16;
            length |= stream.read() << 24;
        }
        return type;
    }

    public void fetchToDecode() throws IOException {
        index = 0;
        offset = 0;
        pointer = 0;

        if (type == MovieTypes.DEFINE_MOVIE_CLIP) {
            length = 4;
        }

        if (data.length < length) {
            data = new byte[length];
        }

        fetchDirect(data, length);
    }

    public void fetchDirect(final byte[] bytes, final int len)
                throws IOException {
        int bytesRead = 0;

        do {
            bytesRead = stream.read(bytes, bytesRead, len);
        } while (bytesRead != -1 && bytesRead < len);
    }

    /**
     * Gets the type of the encoded object from the header fields.
     *
     * @return the value identifying the object when it is encoded.
     */
    public int readType() {
        return type;
    }

    /**
     * Gets the length of the encoded object from the header fields.
     *
     * @return the length of the encoded object in bytes.
     */
    public int readLength() {
        return length;
    }
}
