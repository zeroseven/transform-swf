/*
 * SWFEncoder.java
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
 * SWFEncoder extends LittleEndianEncoder by adding a context used to pass
 * information between classes during encoding.
 */
public final class SWFEncoder extends Encoder {

	/**
	 * Creates an SWFEncoder with the buffer used to encode data set to the
	 * specified size.
	 * 
	 * @param size
	 *            the number of bytes in the internal buffer.
	 */
	public SWFEncoder(final int size) {
		super(size);
	}
	
	/**
	 * Calculate minimum number of bytes a 32-bit unsigned integer can be 
	 * encoded in.
	 * 
	 * @param value
	 *            an integer containing the value to be written.
	 * @return
	 *            the number of bytes required to encode the integer.
	 */
	public static int sizeVariableU32(int value) {

		int val = value;
		int size;

		if (val > 127) {
			size = 2;
			val = val >>> 7;

			if (val > 127) {
				size += 1;
				val = val >>> 7;

				if (val > 127) {
					size += 1;
					val = val >>> 7;

					if (val > 127) {
						size += 1;
					}
				} 
			}
		} else {
			size = 1;
		}
		return size;
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
	public void writeWord(final int value, final int numberOfBytes) {
		for (int i = 0; i < numberOfBytes; i++) {
			data[index++] = (byte) (value >>> (i << 3));
		}
	}


	/**
	 * Write a 32-bit unsigned integer, encoded in a variable number of bytes.
	 * 
	 * @param value
	 *            an integer containing the value to be written.
	 */
	public void writeVariableU32(int value) {

		int val = value;

		if (val > 127) {
			data[index++] = (byte) ((val & 0x007F) | 0x0080);
			val = val >>> 7;

			if (val > 127) {
				data[index++] = (byte) ((val & 0x007F) | 0x0080);
				val = val >>> 7;

				if (val > 127) {
					data[index++] = (byte) ((val & 0x007F) | 0x0080);
					val = val >>> 7;

					if (val > 127) {
						data[index++] = (byte) ((val & 0x007F) | 0x0080);
						val = val >>> 7;

						data[index++] = (byte) (val & 0x007F);
					} else {
						data[index++] = (byte) (val & 0x007F);
					}
				} else {
					data[index++] = (byte) (val & 0x007F);
				}
			} else {
				data[index++] = (byte) (val & 0x007F);
			}
		} else {
			data[index++] = (byte) (value & 0x007F);
		}
	}

	/**
	 * Write a single-precision floating point number.
	 * 
	 * @param value
	 *            the value to be written.
	 */
	public void writeHalf(final float value) {
		int intValue = Float.floatToIntBits(value);
		
		int sign = intValue >>> 16;
		int exp = ((intValue >> 23) & 0x1F) << 10;
		int val = ((intValue >> 13) & 0x3FF);
			
		writeWord(sign | exp | val, 2);
	}

	/**
	 * Write a single-precision floating point number.
	 * 
	 * @param value
	 *            the value to be written.
	 */
	public void writeFloat(final float value) {
		writeWord(Float.floatToIntBits(value), 4);
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
