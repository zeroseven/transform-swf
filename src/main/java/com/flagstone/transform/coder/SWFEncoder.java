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

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import java.util.Stack;

/**
 * SWFEncoder extends LittleEndianEncoder by adding a context used to pass
 * information between classes during encoding.
 */
//TODO(class)
public final class SWFEncoder {

    /** The default size, in bytes, for the internal buffer. */
    public static final int BUFFER_SIZE = 4096;

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

    /** Number of bits in an int. */
    public static final int BITS_PER_INT = 32;
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
    /** Offset to add to number of bits when calculating number of bytes. */
    public static final int ROUND_TO_BYTES = 7;
    /** Right shift to convert number of bits to number of bytes. */
    public static final int BITS_TO_BYTES = 3;
    /** Left shift to convert number of bytes to number of bits. */
    public static final int BYTES_TO_BITS = 3;
    /** Bit mask applied to bytes when converting to unsigned integers. */
    public static final int BYTE_MASK = 255;
    /** Bit mask applied to bytes when converting to unsigned integers. */
    public static final int UNSIGNED_BYTE_MASK = 255;

    /**
     * Calculates the minimum number of bits required to encoded an unsigned
     * integer in a bit field.
     *
     * @param value
     *            the unsigned value to be encoded.
     *
     * @return the number of bits required to encode the value.
     */
    public static int unsignedSize(final int value) {

        final int val = (value < 0) ? -value - 1 : value;
        int counter = BITS_PER_INT;
        int mask = MSB_INT;

        while (((val & mask) == 0) && (counter > 0)) {
            mask >>>= 1;
            counter -= 1;
        }
        return counter;
    }

    /**
     * Calculates the minimum number of bits required to encoded a signed
     * integer in a bit field.
     *
     * @param value
     *            the signed value to be encoded.
     *
     * @return the number of bits required to encode the value.
     */
    public static int size(final int value) {
        int counter = BITS_PER_INT;
        int mask = MSB_INT;
        final int val = (value < 0) ? -value - 1 : value;

        while (((val & mask) == 0) && (counter > 0)) {
            mask >>>= 1;
            counter -= 1;
        }
        return counter + 1;
    }

    /**
     * Returns the minimum number of bits required to encode all the signed
     * values in an array as a set of bit fields with the same size.
     *
     * @param values
     *            an array of signed integers.
     *
     * @return the minimum number of bits required to encode each of the values.
     */
    public static int maxSize(final int... values) {

        int max = 0;
        int size;

        for (final int value : values) {
            size = size(value);
            max = (max > size) ? max : size;
        }
        return max;
    }

    /** The underlying input stream. */
    private final transient OutputStream stream;
    /** The buffer for data read from the stream. */
    private final transient byte[] buffer;
    /** The index in bits to the current location in the buffer. */
    protected transient int pointer;
    /** The index in bytes to the current location in the buffer. */
    protected transient int index;
    /** The offset in bits to the location in the current byte. */
    protected transient int offset;
    /** The last location in the buffer. */
    protected transient int end;
    /** The character encoding used for strings. */
    private transient String encoding;
    /** Stack for storing file locations. */
    private final transient Stack<Integer>locations;
    /** The position of the buffer relative to the start of the stream. */
    private transient int pos;

    /**
     * Create a new SWFDecoder for the underlying InputStream with the
     * specified buffer size.
     *
     * @param streamIn the stream from which data will be read.
     * @param length the size in bytes of the buffer.
     */
    public SWFEncoder(final OutputStream streamOut, final int length) {
        stream = streamOut;
        buffer = new byte[length];
        encoding = "UTF-8";
        locations = new Stack<Integer>();
        pos = 0;
    }

    /**
     * Create a new SWFDecoder for the underlying InputStream using the
     * default buffer size.
     *
     * @param streamIn the stream from which data will be read.
     */
    public SWFEncoder(final OutputStream streamOut) {
        stream = streamOut;
        buffer = new byte[BUFFER_SIZE];
        encoding = "UTF-8";
        locations = new Stack<Integer>();
        pos = 0;
    }

    /**
     * Creates an Encoder with the buffer used to encode data set to the
     * specified size.
     *
     * @param size
     *            the number of bytes in the internal buffer.
     */
    public SWFEncoder(final int size) {
        super();
        stream = null;
        buffer = new byte[BUFFER_SIZE];
        index = 0;
        offset = 0;
        locations = new Stack<Integer>();
        pointer = 0;
        end = size << 3;
    }

    public void flush() throws IOException {
        stream.write(buffer, 0, index);
        stream.flush();

        int diff;
        if (offset != 0) {
            diff = 1;
            buffer[0] = buffer[index];
        } else {
            diff = 0;
        }

        for (int i = diff; i < buffer.length; i++) {
            buffer[i] = 0;
        }

        pos += index;
        index = 0;
    }

    /**
     * Remember the current position.
     */
    public void mark() {
        locations.push(pos + index);
    }

    /**
     * Discard the last saved position.
     */
    public void unmark() {
        locations.pop();
    }

    /**
     * Compare the number of bytes read since the last saved position. The last
     * saved position is discarded.
     *
     * @param expected the expected number of bytes read.
     *
     * @throws IOException if the number of bytes read is different from the
     * expected number.
     */
    public void unmark(final int expected) throws IOException {
        if (bytesRead() != expected) {
            throw new CoderException(locations.peek(), expected,
                    bytesRead() - expected);
        }
        locations.pop();
    }

    /**
     * Get the number of bytes read from the last saved position.
     *
     * @return the number of bytes read since the mark() method was last called.
     */
    public int bytesRead() {
        int count;
        if (pos == 0) {
            count = index - locations.peek();
        } else {
            count = (pos + index) - locations.peek();
        }
        return count;
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
        return Arrays.copyOf(buffer, buffer.length);
    }

    /**
     * Get the location, in bits, where the next value will be read or
     * written.
     *
     * @return the location of the next bit to be accessed.
     */
    public final int getPointer() {
        return ((pos + index) << 3) + offset;
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
        return (index == buffer.length) && (offset == 0);
    }

    /**
     * Write a value to bit field.
     *
     * @param value
     *            the value.
     * @param numberOfBits
     *            the (least significant) number of bits that will be written.
     */
    public final void writeBits(final int value, final int numberOfBits)
                throws IOException {

        int ptr = (index << 3) + offset + numberOfBits;

        if (ptr >= (buffer.length << 3)) {
            flush();
        }

        final int val = ((value << (BITS_PER_INT - numberOfBits)) >>> offset)
                | (buffer[index] << ALIGN_BYTE_3);
        int base = BITS_PER_INT - (((offset + numberOfBits
                + ROUND_TO_BYTES) >>> BITS_TO_BYTES) << BYTES_TO_BITS);
        base = base < 0 ? 0 : base;

        int pointer = (index << 3) + offset;

        for (int i = 24; i >= base; i -= 8) {
            buffer[index++] = (byte) (val >>> i);
        }

        if (offset + numberOfBits > BITS_PER_INT) {
            buffer[index] = (byte) (value
                    << (8 - (offset + numberOfBits - BITS_PER_INT)));
        }

        pointer += numberOfBits;
        index = pointer >>> Coder.BITS_TO_BYTES;
        offset = pointer & 7;
    }

    /**
     * Write a boolean value as a single bit.
     *
     * @param value the boolean flag to write.
     */
    public final void writeBool(final boolean value) throws IOException {
        writeBits(value ? 1 : 0, 1);
    }

    /**
     * Write a 16-bit field.
     *
     * The internal pointer must aligned on a byte boundary. The value is
     * written as if it was a 16-bit integer with big-ending byte ordering.
     *
     * @param value
     *            the value to be written - only the least significant 16-bits
     *            will be written.
     */
    public final void writeB16(final int value) throws IOException {
        if (index + 2 > buffer.length) {
            flush();
        }
        buffer[index++] = (byte) (value >>> ALIGN_BYTE_1);
        buffer[index++] = (byte) value;
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
     * Write a byte.
     *
     * @param value
     *            the value to be written - only the least significant byte will
     *            be written.
     */
    public final void writeByte(final int value) throws IOException {
        if (index == buffer.length) {
            flush();
        }
        buffer[index++] = (byte) value;
    }

    /**
     * Write an array of bytes.
     *
     * @param bytes
     *            the array to be written.
     *
     * @return the number of bytes written.
     */
    public final int writeBytes(final byte[] bytes) throws IOException {
        if (index + bytes.length < buffer.length) {
            System.arraycopy(bytes, 0, buffer, index, bytes.length);
            index += bytes.length;
        } else {
            flush();
            stream.write(bytes, 0, bytes.length);
            pos += bytes.length;
        }
        return bytes.length;
    }

    /**
     * Write a string using the default character set defined in the encoder.
     *
     * @param str
     *            the string.
     */
    public final void writeString(final String str) throws IOException {
        try {
            writeBytes(str.getBytes(encoding));
            buffer[index++] = 0;
        } catch (final java.io.UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Write a 16-bit integer.
     *
     * @param value
     *            an integer containing the value to be written.
     */
    public void writeI16(final int value) throws IOException {
        if (index + 2 > buffer.length) {
            flush();
        }
        buffer[index++] = (byte) value;
        buffer[index++] = (byte) (value >>> ALIGN_BYTE_1);
    }

    /**
     * Write a 32-bit integer.
     *
     * @param value
     *            an integer containing the value to be written.
     */
    public void writeI32(final int value) throws IOException {
        if (index + 4 > buffer.length) {
            flush();
        }
        buffer[index++] = (byte) value;
        buffer[index++] = (byte) (value >>> ALIGN_BYTE_1);
        buffer[index++] = (byte) (value >>> ALIGN_BYTE_2);
        buffer[index++] = (byte) (value >>> ALIGN_BYTE_3);
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
    public void writeWord(final int value, final int numberOfBytes)
            throws IOException {
        if (index + 4 > buffer.length) {
            flush();
        }
        for (int i = 0; i < numberOfBytes; i++) {
            buffer[index++] = (byte) (value >>> (i << 3));
        }
    }

    /**
     * Write a 32-bit unsigned integer, encoded in a variable number of bytes.
     *
     * @param value
     *            an integer containing the value to be written.
     */
    public void writeVariableU32(final int value) throws IOException {
        if (index + 5 > buffer.length) {
            flush();
        }

        //CHECKSTYLE:OFF
        int val = value;

        if (val > 127) {
            buffer[index++] = (byte) ((val & 0x007F) | 0x0080);
            val = val >>> 7;

            if (val > 127) {
                buffer[index++] = (byte) ((val & 0x007F) | 0x0080);
                val = val >>> 7;

                if (val > 127) {
                    buffer[index++] = (byte) ((val & 0x007F) | 0x0080);
                    val = val >>> 7;

                    if (val > 127) {
                        buffer[index++] = (byte) ((val & 0x007F) | 0x0080);
                        val = val >>> 7;

                        buffer[index++] = (byte) (val & 0x007F);
                    } else {
                        buffer[index++] = (byte) (val & 0x007F);
                    }
                } else {
                    buffer[index++] = (byte) (val & 0x007F);
                }
            } else {
                buffer[index++] = (byte) (val & 0x007F);
            }
        } else {
            buffer[index++] = (byte) (value & 0x007F);
        }
        //CHECKSTYLE:ON
    }

    /**
     * Write a single-precision floating point number.
     *
     * @param value
     *            the value to be written.
     */
    public void writeHalf(final float value) throws IOException {
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
    public void writeFloat(final float value) throws IOException {
        writeI32(Float.floatToIntBits(value));
    }

    /**
     * Write a double-precision floating point number.
     *
     * @param value
     *            the value to be written.
     */
    public void writeDouble(final double value) throws IOException {
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
    public void writeHeader(final int type, final int length) throws IOException {
        if (length > STD_LIMIT) {
            writeI16((type << LENGTH_FIELD_SIZE) | IS_EXTENDED);
            writeI32(length);
        } else {
            writeI16((type << LENGTH_FIELD_SIZE) | length);
        }
    }
}
