/*
 * BigDecoder.java
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
 * BigDecoder wraps an InputStream with a buffer to reduce the amount of
 * memory required to decode an image or sound and to improve efficiency by
 * reading data from a file or external source in blocks. Word data - shorts
 * and ints - are read in Big-Endian format with the most significant byte
 * decoded first.
 */
public final class BigDecoder {
    /** Bit mask applied to bytes when converting to unsigned integers. */
    private static final int BYTE_MASK = 255;
    /** Number of bits in an int. */
    private static final int BITS_PER_INT = 32;
    /** Left shift to convert number of bits to number of bytes. */
    private static final int BITS_TO_BYTES = 3;
    /** Right shift to convert number of bits to number of bytes. */
    private static final int BYTES_TO_BITS = 3;
    /** Number of bits to shift when aligning a value to the second byte. */
    private static final int TO_BYTE1 = 8;
    /** Number of bits to shift when aligning a value to the third byte. */
    private static final int TO_BYTE2 = 16;
    /** Number of bits to shift when aligning a value to the fourth byte. */
    private static final int TO_BYTE3 = 24;

    /** The internal buffer containing data read from or written to a file. */
    private byte[] data;
    /** The index in bits to the current location in the buffer. */
    private transient int pointer;
    /** The index in bytes to the current location in the buffer. */
    private transient int index;
    /** The offset in bits to the location in the current byte. */
    private transient int offset;
    /** The last location in the buffer. */
    private final transient int end;
    /**
     * Create a FLVDecoder initialised with the specified data.
     * @param bytes the array of byes to decode.
     */
    public BigDecoder(final byte[] bytes) {
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
     * Searches for a bit pattern, returning true and advancing the pointer to
     * the location if a match was found. If the bit pattern cannot be found
     * then the method returns false and the position of the internal pointer is
     * not changed.
     *
     * @param value
     *            an integer containing the bit patter to search for.
     * @param numberOfBits
     *            least significant n bits in the value to search for.
     * @param step
     *            the increment in bits to add to the internal pointer as the
     *            buffer is searched.
     *
     * @return true if the pattern was found, false otherwise.
     */
    public boolean findBits(final int value, final int numberOfBits,
            final int step) {
        boolean found;
        final int mark = getPointer();

        while (getPointer() + numberOfBits <= end) {
            if (readBits(numberOfBits, false) == value) {
                adjustPointer(-numberOfBits);
                break;
            }
            adjustPointer(step - numberOfBits);
        }

        if (getPointer() + numberOfBits > end) {
            found = false;
            setPointer(mark);
        } else {
            found = true;
        }

        return found;
    }

    /**
     * Read an unsigned 16-bit integer.
     *
     * @return the value read.
     */
    public int readUI16() {
        int value = (data[index++] & BYTE_MASK) << TO_BYTE1;
        value |= data[index++] & BYTE_MASK;
        return value;
    }

    /**
     * Read a signed 16-bit integer.
     *
     * @return the value read.
     */
    public int readSI16() {
        int value = data[index++] << TO_BYTE1;
        value |= data[index++] & BYTE_MASK;
        return value;
    }

    /**
     * Read an unsigned 32-bit integer.
     *
     * @return the value read.
     */
    public int readUI32() {
        int value = (data[index++] & BYTE_MASK) << TO_BYTE3;
        value |= (data[index++] & BYTE_MASK) << TO_BYTE2;
        value |= (data[index++] & BYTE_MASK) << TO_BYTE1;
        value |= data[index++] & BYTE_MASK;
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
            value += data[index++] & BYTE_MASK;
        }

        if (signed) {
            value <<= BITS_PER_INT
                        - (numberOfBytes << BYTES_TO_BITS);
            value >>= BITS_PER_INT
                        - (numberOfBytes << BYTES_TO_BITS);
        }

        return value;
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
