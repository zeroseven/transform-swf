/*
 * JPEGEncodingTable.java
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

package com.flagstone.transform.image;

import java.util.Arrays;

import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

/**
 * JPEGEncodingTable defines the Huffman encoding table for JPEG images.
 * 
 * <p>
 * The encoding table is shared between all images defined using the
 * DefineJPEGImage class so there should only be one JPEGEncodingTable object
 * defined in a movie.
 * </p>
 * 
 * <p>
 * The JPEGEncodingTable class is not essential to define JPEG encoded images in
 * a movie using the DefineJPEGImage class. You can still display an image if it
 * contains the encoding table. There is no need to separate it and add it to a
 * JPEGEncodingTable object, particularly since different images contain
 * different encoding tables.
 * </p>
 * 
 * @see DefineJPEGImage
 */
public final class JPEGEncodingTable implements MovieTag {
	private static final String FORMAT = "JPEGEncodingTable: { encodingTable=%d }";
	private byte[] table;

	private transient int length;

	// TODO(doc)
	public JPEGEncodingTable(final SWFDecoder coder)
			throws CoderException {
		final int start = coder.getPointer();
		length = coder.readWord(2, false) & 0x3F;

		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}
		final int end = coder.getPointer() + (length << 3);

		table = coder.readBytes(new byte[length]);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}

	/**
	 * Creates a JPEGEncodingTable object with the encoding table data.
	 * 
	 * @param bytes
	 *            an array of bytes contains the data for the encoding table.
	 *            Must not be null.
	 */
	public JPEGEncodingTable(final byte[] bytes) {
		setTable(bytes);
	}

	// TODO(doc)
	public JPEGEncodingTable(final JPEGEncodingTable object) {
		table = Arrays.copyOf(object.table, object.table.length);
	}

	/**
	 * Returns the encoding table.
	 */
	public byte[] getTable() {
		return table;
	}

	/**
	 * Sets the encoding table.
	 * 
	 * @param bytes
	 *            an array of bytes contains the data for the encoding table.
	 *            Must not be null or zero length.
	 */
	public void setTable(final byte[] bytes) {
		if (bytes == null) {
			throw new IllegalArgumentException(Strings.DATA_IS_NULL);
		}

		if (bytes.length == 0) {
			throw new IllegalArgumentException(Strings.DATA_IS_EMPTY);
		}

		table = bytes;
	}

	public JPEGEncodingTable copy() {
		return new JPEGEncodingTable(this);
	}

	@Override
	public String toString() {
		return String.format(FORMAT, table.length);
	}

	public int prepareToEncode(final SWFEncoder coder, final Context context) {
		length = table.length;

		return (length > 62 ? 6 : 2) + length;
	}

	public void encode(final SWFEncoder coder, final Context context)
			throws CoderException {
		final int start = coder.getPointer();

		if (length >= 63) {
			coder.writeWord((MovieTypes.JPEG_TABLES << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((MovieTypes.JPEG_TABLES << 6) | length, 2);
		}
		final int end = coder.getPointer() + (length << 3);

		coder.writeBytes(table);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}
}
