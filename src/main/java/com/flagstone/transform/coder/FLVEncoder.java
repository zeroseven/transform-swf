/*
 * FLVEncoder.java
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

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;

/** TODO(class). */
public final class FLVEncoder {


    /** The character encoding used for strings. */
    protected String encoding;
    /** The internal buffer containing data read from or written to a file. */
    protected byte[] data;

    /** The index in bits to the current location in the buffer. */
    protected transient int pointer;
    /** The index in bytes to the current location in the buffer. */
    protected transient int index;
    /** The offset in bits to the location in the current byte. */
    protected transient int offset;
    /** The last location in the buffer. */
    protected transient int end;

    public FLVEncoder(final int size) {
        super();
        data = new byte[size];
        index = 0;
        offset = 0;
        pointer = 0;
        end = size << 3;
    }

    /**
     * Get character encoding scheme used when encoding or decoding strings.
     *
     * @return the character encoding used for strings.
     */
    public final String getEncoding() {
        return encoding;
    }

    /**
     * Sets the character encoding scheme used when encoding or decoding
     * strings.
     *
     * If the character set encoding is not supported by the Java environment
     * then an UnsupportedCharsetException will be thrown. If the character set
     * cannot be identified then an IllegalCharsetNameException will be thrown.
     *
     * @param charSet
     *            the name of the character set used to encode strings.
     */
    public final void setEncoding(final String charSet) {
        if (!Charset.isSupported(charSet)) {
            throw new UnsupportedCharsetException(charSet);
        }
        encoding = charSet;
    }

    /**
     * Calculates the length of a string when encoded using the specified
     * character set.
     *
     * @param string
     *            the string to be encoded.
     *
     * @return the number of bytes required to encode the string plus 1 for a
     *         terminating null character.
     */

    public final int strlen(final String string) {
        try {
            return string.getBytes(encoding).length + 1;
        } catch (final UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Get the array of bytes containing the encoded data.
     *
     * @return a copy of the internal buffer.
     */
    public final byte[] getData() {
        return Arrays.copyOf(data, data.length);
    }

    /**
     * Sets the array of bytes used for the encoded data.
     *
     * @param bytes
     *            an array of bytes.
     */
    public final void setData(final byte[] bytes) {
        data = new byte[bytes.length];
        index = 0;
        offset = 0;
        pointer = 0;
        end = bytes.length << 3;
        data = Arrays.copyOf(bytes, bytes.length);
    }

    /**
     * Sets the internal buffer used for encoding objects to the specified size.
     *
     * @param size
     *            the size of the internal buffer in bytes.
     */
    public final void setData(final int size) {
        data = new byte[size];
        index = 0;
        offset = 0;
        pointer = 0;
        end = data.length << 3;
    }

    /**
     * Get the location, in bits, where the next value will be read or
     * written.
     *
     * @return the location of the next bit to be accessed.
     */
    public final int getPointer() {
        return (index << 3) + offset;
    }

    /**
     * Sets the location, in bits, where the next value will be read or written.
     *
     * @param location
     *            the offset in bits from the start of the array of bytes.
     */
    public final void setPointer(final int location) {
        index = location >>> 3;
        offset = location & 7;
    }

    /**
     * Changes the location where the next value will be read or written by.
     *
     * @param numberOfBits
     *            the number of bits to add to the current location.
     */
    public final void adjustPointer(final int numberOfBits) {
        pointer = (index << 3) + offset + numberOfBits;
        index = pointer >>> 3;
        offset = pointer & 7;
    }

    /**
     * Changes the location to the next byte boundary.
     */
    public final void alignToByte() {
        if (offset > 0) {
            index += 1;
            offset = 0;
        }
    }

    /**
     * Is the internal index at the end of the buffer.
     *
     * @return true if the internal pointer is at the end of the buffer.
     */
    public final boolean eof() {
        return (index == data.length) && (offset == 0);
    }

    /**
     * Write a 16-bit integer.
     *
     * @param value
     *            an integer containing the value to be written.
     */
    public void writeI16(final int value) {
        data[index++] = (byte) (value >>> 8);
        data[index++] = (byte) value;
    }

    /**
     * Write a 32-bit integer.
     *
     * @param value
     *            an integer containing the value to be written.
     */
    public void writeI32(final int value) {
        data[index++] = (byte) (value >>> 24);
        data[index++] = (byte) (value >>> 16);
        data[index++] = (byte) (value >>> 8);
        data[index++] = (byte) value;
    }

    /**
     * Write a word.
     *
     * @param value
     *            the value to be written.
     * @param numberOfBytes
     *            the number of (least significant) bytes that will be written.
     */
    public void writeWord(final int value, final int numberOfBytes) {
        index += numberOfBytes - 1;

        for (int i = 0; i < numberOfBytes; i++) {
            data[index--] = (byte) (value >>> (i << 3));
        }

        index += numberOfBytes + 1;
    }

    /**
     * Write a double-precision floating point number.
     *
     * @param value
     *            the value to be written.
     */
    public void writeDouble(final double value) {
        final long longValue = Double.doubleToLongBits(value);

        writeWord((int) (longValue >>> 32), 4);
        writeWord((int) longValue, 4);
    }
}
