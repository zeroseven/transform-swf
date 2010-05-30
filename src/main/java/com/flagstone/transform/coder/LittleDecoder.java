/*
 * LittleDecoder.java
 * Transform
 *
 * Copyright (c) 2010 Flagstone Software Ltd. All rights reserved.
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

import java.util.Arrays;

/**
 * LittleDecoder extends LittleEndianDecoder by adding a context used to pass
 * information between classes during decoding and a factory class for
 * generating instances of objects.
 */
//TODO(class)
public final class LittleDecoder {
    /** Bit mask applied to bytes when converting to unsigned integers. */
    public static final int BYTE_MASK = 255;
    /** Number of bits in an int. */
    public static final int BITS_PER_INT = 32;
    /** Number of bits in a byte. */
    public static final int BITS_PER_BYTE = 8;
    /** Left shift to convert number of bits to number of bytes. */
    public static final int BITS_TO_BYTES = 3;
    /** Right shift to convert number of bits to number of bytes. */
    public static final int BYTES_TO_BITS = 3;
    /** Number of bits to shift when aligning a value to the second byte. */
    public static final int TO_BYTE1 = 8;
    /** Number of bits to shift when aligning a value to the third byte. */
    public static final int TO_BYTE2 = 16;
    /** Number of bits to shift when aligning a value to the fourth byte. */
    public static final int TO_BYTE3 = 24;

    /** The internal buffer containing data read from or written to a file. */
    private byte[] data;
    /** The index in bits to the current location in the buffer. */
    private transient int pointer;
    /** The index in bytes to the current location in the buffer. */
    private transient int index;
    /** The offset in bits to the location in the current byte. */
    private transient int offset;
    /** The last location in the buffer. */
    private transient final int end;
    /**
     * Creates a LittleDecoder object initialised with the data to be decoded.
     *
     * @param bytes
     *            an array of bytes to be decoded.
     */
    public LittleDecoder(final byte[] bytes) {
        super();
        data = new byte[bytes.length];
        index = 0;
        offset = 0;
        pointer = 0;
        end = bytes.length << 3;
        data = Arrays.copyOf(bytes, bytes.length);
    }

    /**
     * Get the location, in bits, where the next value will be read or
     * written.
     *
     * @return the location of the next bit to be accessed.
     */
    public int getPointer() {
        return (index << 3) + offset;
    }

    /**
     * Sets the location, in bits, where the next value will be read or written.
     *
     * @param location
     *            the offset in bits from the start of the array of bytes.
     */
    public void setPointer(final int location) {
        index = location >>> 3;
        offset = location & 7;
    }

    /**
     * Changes the location where the next value will be read or written by.
     *
     * @param numberOfBits
     *            the number of bits to add to the current location.
     */
    public void adjustPointer(final int numberOfBits) {
        pointer = (index << 3) + offset + numberOfBits;
        index = pointer >>> 3;
        offset = pointer & 7;
    }

    /**
     * Is the internal index at the end of the buffer.
     *
     * @return true if the internal pointer is at the end of the buffer.
     */
    public boolean eof() {
        return (index == data.length) && (offset == 0);
    }

    /**
     * Read a bit field.
     *
     * @param numberOfBits
     *            the number of bits to read.
     *
     * @param signed
     *            indicates whether the integer value read is signed.
     *
     * @return the value read.
     */
    public int readBits(final int numberOfBits, final boolean signed) {
        int value = 0;

        pointer = (index << 3) + offset;

        if (numberOfBits > 0) {

            for (int i = BITS_PER_INT; (i > 0)
                    && (index < data.length); i -= 8) {
                value |= (data[index++] & BYTE_MASK) << (i - 8);
            }

            value <<= offset;

            if (signed) {
                value >>= BITS_PER_INT - numberOfBits;
            } else {
                value >>>= BITS_PER_INT - numberOfBits;
            }

            pointer += numberOfBits;
            index = pointer >>> BITS_TO_BYTES;
            offset = pointer & 7;
        }

        return value;
    }

    /**
     * Read an unsigned byte.
     *
     * @return an 8-bit unsigned value.
     */
    public int readByte() {
        return data[index++] & BYTE_MASK;
    }

    /**
     * Reads an array of bytes.
     *
     * @param bytes
     *            the array that will contain the bytes read.
     *
     * @return the array of bytes.
     */
    public byte[] readBytes(final byte[] bytes) {
        System.arraycopy(data, index, bytes, 0, bytes.length);
        index += bytes.length;
        return bytes;
    }

    /**
     * Read an unsigned 16-bit integer.
     *
     * @return the value read.
     */
    public int readUI16() {
        int value = data[index++] & BYTE_MASK;
        value |= (data[index++] & BYTE_MASK) << TO_BYTE1;
        return value;
    }

    /**
     * Read an unsigned 16-bit integer.
     *
     * @return the value read.
     */
    public int readSI16() {
        int value = data[index++] & BYTE_MASK;
        value |= data[index++] << TO_BYTE1;
        return value;
    }

    /**
     * Read an unsigned 32-bit integer.
     *
     * @return the value read.
     */
    public int readUI32() {
        int value = data[index++] & BYTE_MASK;
        value |= (data[index++] & BYTE_MASK) << TO_BYTE1;
        value |= (data[index++] & BYTE_MASK) << TO_BYTE2;
        value |= (data[index++] & BYTE_MASK) << TO_BYTE3;
        return value;
    }
}
