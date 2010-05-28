/*
 * FLVDecoder.java
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

package com.flagstone.transform.coder;

/** TODO(class). */
public final class BigDecoder extends Decoder {

    /**
     * Create a FLVDecoder initialised with the specified data.
     * @param data the array of byes to decode.
     */
    public BigDecoder(final byte[] data) {
        super(data);
    }

    /**
     * Read an unsigned short integer without changing the internal pointer.
     *
     * @return an unsigned 16-bit value.
     */
    public int scanUnsignedShort() {
        return ((data[index] & 0x00FF) << Coder.BYTES_TO_BITS)
                + (data[index + 1] & Coder.UNSIGNED_BYTE_MASK);
    }

    /**
     * Read an unsigned 16-bit integer.
     *
     * @return the value read.
     */
    public int readUI16() {
        int value = (data[index++] & Coder.UNSIGNED_BYTE_MASK)
                << Coder.ALIGN_BYTE_1;
        value |= data[index++] & Coder.UNSIGNED_BYTE_MASK;
        return value;
    }

    /**
     * Read a signed 16-bit integer.
     *
     * @return the value read.
     */
    public int readSI16() {
        int value = data[index++] << Coder.ALIGN_BYTE_1;
        value |= data[index++] & Coder.UNSIGNED_BYTE_MASK;
        return value;
    }

    /**
     * Read an unsigned 32-bit integer.
     *
     * @return the value read.
     */
    public int readUI32() {
        int value = (data[index++] & Coder.UNSIGNED_BYTE_MASK)
                << Coder.ALIGN_BYTE_3;
        value |= (data[index++] & Coder.UNSIGNED_BYTE_MASK)
                << Coder.ALIGN_BYTE_2;
        value |= (data[index++] & Coder.UNSIGNED_BYTE_MASK)
                << Coder.ALIGN_BYTE_1;
        value |= data[index++] & Coder.UNSIGNED_BYTE_MASK;
        return value;
    }

    /**
     * Read a signed 32-bit integer.
     *
     * @return the value read.
     */
    public int readSI32() {
        int value = data[index++] << Coder.ALIGN_BYTE_3;
        value |= (data[index++] & Coder.UNSIGNED_BYTE_MASK)
                << Coder.ALIGN_BYTE_2;
        value |= (data[index++] & Coder.UNSIGNED_BYTE_MASK)
                << Coder.ALIGN_BYTE_1;
        value |= data[index++] & Coder.UNSIGNED_BYTE_MASK;
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
     * @return the decoded value.
     */
    public int readWord(final int numberOfBytes, final boolean signed) {
        int value = 0;

        for (int i = 0; i < numberOfBytes; i++) {
            value <<= 8;
            value += data[index++] & Coder.UNSIGNED_BYTE_MASK;
        }

        if (signed) {
            value <<= Coder.BITS_PER_INT
                        - (numberOfBytes << Coder.BYTES_TO_BITS);
            value >>= Coder.BITS_PER_INT
                        - (numberOfBytes << Coder.BYTES_TO_BITS);
        }

        return value;
    }

    /**
     * Read a double-precision floating point number.
     *
     * @return the decoded value.
     */
    public double readDouble() {
        long longValue = (long) readWord(4, false) << Coder.ALIGN_WORD;
        longValue |= readWord(4, false) & 0x00000000FFFFFFFFL;

        return Double.longBitsToDouble(longValue);
    }

    /**
     * Searches for a word and advances the pointer to the location where it was
     * found, returning true to signal a successful search. If word cannot be
     * found then the method returns false and the position of the internal
     * pointer is not changed.
     *
     * @param value
     *            the value to search for.
     *
     * @param numberOfBytes
     *            the number of bytes from the value to compare.
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
