/*
 * BigEndianEncoder.java
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

/**
 * BigEndianEncoder extends the Encoder class by adding methods for encoding
 * multi-bytes words with Big-Endian byte ordering.
 */
public class BigEndianEncoder extends Encoder {
	/**
	 * Creates an BigEndianEncoder with the buffer used to encode data set to
	 * the specified size.
	 * 
	 * @param size
	 *            the number of bytes in the internal buffer.
	 */
	public BigEndianEncoder(final int size) {
		super(size);
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
		long longValue = Double.doubleToLongBits(value); //TODO(code) fix

		writeWord((int) (longValue >>> 32), 4);
		writeWord((int) longValue, 4);
	}
}
