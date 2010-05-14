/*
 * Coder.java
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

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;



/**
 * <p>
 * Coder is the base class for the classes used to encode and decode the data
 * structures that make up the Flash File and Flash Video formats.
 * </p>
 */
public class Coder {
    /** Number of bits in an int. */
    public static final int BITS_PER_INT = 32;
    /** Number of bits in a short integer. */
    public static final int BITS_PER_SHORT = 16;
    /** Number of bits in a byte. */
    public static final int BITS_PER_BYTE = 8;

    /** Offset to add to number of bits when calculating number of bytes. */
    public static final int ROUND_TO_BYTES = 7;
    /** Right shift to convert number of bits to number of bytes. */
    public static final int BITS_TO_BYTES = 3;
    /** Left shift to convert number of bytes to number of bits. */
    public static final int BYTES_TO_BITS = 3;

    /** Bit mask applied to bytes when converting to unsigned integers. */
    public static final int UNSIGNED_BYTE_MASK = 255;
    /** Bit mask with most significant bit of a 32-bit integer set. */
    public static final int MSB_INT = 0x80000000;

    /** Number of bits to shift when aligning a value to the second byte. */
    public static final int ALIGN_BYTE_1 = 8;
    /** Number of bits to shift when aligning a value to the third byte. */
    public static final int ALIGN_BYTE_2 = 16;
    /** Number of bits to shift when aligning a value to the fourth byte. */
    public static final int ALIGN_BYTE_3 = 24;
    /** Number of bits to shift when aligning an int in a long value. */
    public static final int ALIGN_WORD = 32;

    protected String encoding;
    protected byte[] data;

    protected transient int pointer;
    protected transient int index;
    protected transient int offset;
    protected transient int end;

    /**
     * Creates a new Coder object.
     */
    public Coder() {
        encoding = "UTF-8";
    }

    /**
     * Returns character encoding scheme used when encoding or decoding strings.
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
     * Returns the array of bytes containing the encoded data.
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
     * Returns the location, in bits, where the next value will be read or
     * written.
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
     * Returns true of the internal pointer is at the end of the buffer.
     */
    public final boolean eof() {
        return (index == data.length) && (offset == 0);
    }
}
