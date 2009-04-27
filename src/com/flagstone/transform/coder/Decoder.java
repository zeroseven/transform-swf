/*
 * Decoder.java
 * Transform
 * 
 * Copyright (c) 2001-2009 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its contributors 
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.flagstone.transform.coder;

import com.flagstone.transform.Strings;

/**
 * Decoder provides a set of method for decoding data that is not byte-ordered,
 * specifically bit fields and strings.
 */
public class Decoder extends Coder {
	/**
	 * Creates a Decoder object initialised with the data to be decoded.
	 * 
	 * @param data
	 *            an array of bytes to be decoded.
	 */
	public Decoder(final byte[] data) {
		super();
		setData(data);
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
		pointer = (index << 3) + offset;

		if (pointer + numberOfBits > end) {
			throw new ArrayIndexOutOfBoundsException(index);
		}

		int value = 0;

		if (numberOfBits > 0) {

			for (int i = 32; i > 0 && index < data.length; i -= 8, index++) {
				value |= (data[index] & 0x000000FF) << (i - 8);
			}

			value <<= offset;

			if (signed) {
				value >>= 32 - numberOfBits;
			} else {
				value >>>= 32 - numberOfBits;
			}

			pointer += numberOfBits;
			index = pointer >>> 3;
			offset = pointer & 7;
		}

		return value;
	}

	/**
	 * Read a 16-bit field.
	 * 
	 * The internal pointer must aligned on a byte boundary. The value returned
	 * is equivalent to reading a 16-bit integer with big-ending byte ordering.
	 */
	public int readB16() {
		return ((data[index++] & 0x000000FF) << 8)
				+ (data[index++] & 0x000000FF);
	}

	/**
	 * Read an unsigned byte without changing the internal pointer.
	 */
	public int scanByte() {
		return data[index] & 0x000000FF;
	}

	/**
	 * Read an unsigned byte.
	 */
	public int readByte() {
		return data[index++] & 0x000000FF;
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
	 * Read a string using the default character set defined in the decoder.
	 * 
	 * @param numberOfBytes
	 *            the number of bytes to read.
	 * 
	 * @return the decoded string.
	 */
	public String readString(final int numberOfBytes) {
		return readString(numberOfBytes, encoding);
	}

	/**
	 * Read a string using the character set specified.
	 * 
	 * @param numberOfBytes
	 *            the number of bytes to read.
	 * @param charset
	 *            the name of the character set used to encode the string.
	 * 
	 * @return the decoded string.
	 */
	public String readString(final int numberOfBytes, final String charset) {
		try {
			return new String(readBytes(new byte[numberOfBytes]), 0,
					numberOfBytes, charset);
		} catch (java.io.UnsupportedEncodingException e) {
			throw new IllegalArgumentException(String.format(
					Strings.INVALID_ENCODING, charset), e);
		}
	}

	/**
	 * Read a null-terminated string using the default character set defined in
	 * the decoder.
	 * 
	 * @return the decoded string.
	 */
	public String readString() {
		String value;

		int mark = index;
		int length = 0;

		while (data[mark++] != 0) {
			length += 1;
		}

		value = readString(length, encoding);
		index++;

		return value;
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
	public boolean findBits(final int value, final int numberOfBits, final int step) {
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

}
