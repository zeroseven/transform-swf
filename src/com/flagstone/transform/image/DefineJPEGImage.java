/*
 * DefineJPEGImage.java
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
import com.flagstone.transform.coder.FLVDecoder;
import com.flagstone.transform.coder.ImageTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

//TODO(doc) Review
/**
 * DefineJPEGImage is used to define a JPEG encoded image.
 * 
 * <p>
 * DefineJPEGImage objects only contain the image data, the encoding table for
 * the image is defined in a JPEGEncodingTable object. All images using a shared
 * JPEGEncodingTable object to represent the encoding table have the same
 * compression ratio.
 * </p>
 * 
 * <p>
 * Although the DefineJPEGImage class is supposed to be used with the
 * JPEGEncodingTable class which defines the encoding table for the images it is
 * not essential. If an JPEGEncodingTable object is created with an empty
 * encoding table then the Flash Player will still display the JPEG image
 * correctly if it contain the encoding table block in the image data.
 * </p>
 * 
 * @see JPEGEncodingTable
 * @see DefineJPEGImage2
 * @see DefineJPEGImage3
 */
public final class DefineJPEGImage implements ImageTag {
	private static final String FORMAT = "DefineJPEGImage: { identifier=%d; image=%d; }";

	private int identifier;
	private byte[] image;

	private transient int length;
	private transient int width;
	private transient int height;

	// TODO(doc)
	public DefineJPEGImage(final SWFDecoder coder)
			throws CoderException {
		final int start = coder.getPointer();
		length = coder.readWord(2, false) & 0x3F;

		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}
		final int end = coder.getPointer() + (length << 3);
		identifier = coder.readWord(2, false);
		image = coder.readBytes(new byte[length - 2]);

		decodeInfo();

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}

	/**
	 * Creates a DefineJPEGImage object with the identifier and JPEG data.
	 * 
	 * @param uid
	 *            the unique identifier for this object. Must be in the range
	 *            1..65535.
	 * @param bytes
	 *            the JPEG encoded image data. Must not be null.
	 */
	public DefineJPEGImage(final int uid, final byte[] bytes) {
		setIdentifier(uid);
		setImage(bytes);
	}

	// TODO(doc)
	public DefineJPEGImage(final DefineJPEGImage object) {
		identifier = object.identifier;
		width = object.width;
		height = object.height;
		image = Arrays.copyOf(object.image, object.image.length);
	}

	public int getIdentifier() {
		return identifier;
	}

	public void setIdentifier(final int uid) {
		if (uid < 0 || uid > 65535) {
			throw new IllegalArgumentException(Strings.IDENTIFIER_RANGE);
		}
		identifier = uid;
	}

	/**
	 * Returns the width of the image in pixels.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Returns the height of the image in pixels.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the image data.
	 */
	public byte[] getImage() {
		return image;
	}

	/**
	 * Sets the image data. The image data may be taken directly from a file
	 * containing a JPEG encoded image. if the image contains an encoding table
	 * the Flash Player will display it correctly and there is no need to
	 * specify a separate table using a JPEGEncodingTable object.
	 * 
	 * @param bytes
	 *            an array of bytes containing the image data. Must not be null
	 *            or empty.
	 */
	public void setImage(final byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			throw new IllegalArgumentException(Strings.DATA_NOT_SET);
		}
		image = bytes;
		decodeInfo();
	}

	public DefineJPEGImage copy() {
		return new DefineJPEGImage(this);
	}

	@Override
	public String toString() {
		return String.format(FORMAT, identifier, image.length);
	}

	public int prepareToEncode(final SWFEncoder coder, final Context context) {
		length = 2 + image.length;

		return (length > 62 ? 6 : 2) + length;
	}

	public void encode(final SWFEncoder coder, final Context context)
			throws CoderException {
		final int start = coder.getPointer();

		if (length >= 63) {
			coder.writeWord((MovieTypes.DEFINE_JPEG_IMAGE << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((MovieTypes.DEFINE_JPEG_IMAGE << 6) | length, 2);
		}
		final int end = coder.getPointer() + (length << 3);

		coder.writeWord(identifier, 2);
		coder.writeBytes(image);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}

	private void decodeInfo() {
		final FLVDecoder coder = new FLVDecoder(image);

		if (coder.readWord(2, false) == 0xffd8) {
			int marker;

			do {
				marker = coder.readWord(2, false);

				if ((marker & 0xff00) == 0xff00) {
					if (marker >= 0xffc0 && marker <= 0xffcf
							&& marker != 0xffc4 && marker != 0xffc8) {
						coder.adjustPointer(24);
						height = coder.readWord(2, false);
						width = coder.readWord(2, false);
						break;
					} else {
						coder
								.adjustPointer((coder.readWord(2, false) - 2) << 3);
					}
				}

			} while ((marker & 0xff00) == 0xff00);
		}
	}
}
