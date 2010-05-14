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


/**
 * SWFDecoder extends LittleEndianDecoder by adding a context used to pass
 * information between classes during decoding and a factory class for
 * generating instances of objects.
 */
//TODO(class)
public final class SWFDecoder extends Decoder {

    /**
     * Bit mask for extracting the length field from the header word.
     */
    private static final int LENGTH_FIELD = 0x3F;
    /**
     * Reserved length indicating length is encoded in next 32-bit word.
     */
    private static final int IS_EXTENDED = 63;

    /**
     * Creates a SWFDecoder object initialised with the data to be decoded.
     *
     * @param data
     *            an array of bytes to be decoded.
     */
    public SWFDecoder(final byte[] data) {
        super(data);
    }

    /**
     * Read an unsigned short integer without changing the internal pointer.
     */
    public int scanUnsignedShort() {
        return ((data[index + 1] & UNSIGNED_BYTE_MASK) << ALIGN_BYTE_1)
            + (data[index] & UNSIGNED_BYTE_MASK);
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

    /**
     * Gets the length of the encoded object from the header fields.
     *
     * @return the length of the encoded object in bytes.
     */
    public int readHeader() {
        int length = readUI16() & LENGTH_FIELD;
        if (length == IS_EXTENDED) {
            length = readUI32();
        }
        return length;
    }

    /**
     * Searches the internal buffer for a word and advances the pointer to the
     * location where the word was found, returning true to signal a successful
     * search. If word cannot be found then the method returns false and the
     * position of the internal pointer is not changed.
     *
     * @param value
     *            an integer containing the word to search for.
     *
     * @param numberOfBytes
     *            least significant number of bytes from the value to search
     *            for.
     *
     * @param step
     *            the number of bytes to step between searches.
     *
     * @return true if the pattern was found, false otherwise.
     */
    public boolean findWord(final int value, final int numberOfBytes,
            final int step) {
        boolean found;

        final int mark = getPointer();

        while (index + numberOfBytes <= data.length) {

            if (readWord(numberOfBytes, false) == value) {
                index -= numberOfBytes;
                break;
            }
            index = index - numberOfBytes + step;
        }

        if (index + numberOfBytes > data.length) {
            found = false;
            setPointer(mark);
        } else {
            found = true;
        }

        return found;
    }
}
