/*
 * DefineJPEGImage3.java
 * Transform
 *
 * Copyright (c) 2001-2008 Flagstone Software Ltd. All rights reserved.
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

package com.flagstone.transform.movie.image;

import java.util.Arrays;

import com.flagstone.transform.coder.BigEndianDecoder;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.ImageTag;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.Types;

/**
 * DefineJPEGImage3 is used to define a transparent JPEG encoded image.
 * 
 * <p>It extends the DefineJPEGImage2 class by including a separate zlib
 * compressed table of alpha channel values. This allows the transparency of
 * existing JPEG encoded images to be changed without re-encoding the original
 * image.</p>
 * 
 * @see DefineJPEGImage3
 */
public final class DefineJPEGImage3 implements ImageTag 
{
	private static final String FORMAT = "DefineJPEGImage3: { identifier=%d; image=%d; alpha=%d }";

	private transient int start;
	private transient int end;
	protected transient int length;
	protected transient int width;
	protected transient int height;
	
	protected byte[] image;
	protected byte[] alpha;
	private int identifier;

	public DefineJPEGImage3()
	{
		image = new byte[0];
		alpha = new byte[0];
		width = 0;
		height = 0;
	}

	/**
	 * Creates a DefineJPEGImage3 object with the specified image data,
	 * and alpha channel data.
	 * 
	 * @param uid
	 *            the unique identifier for this object. Must be in the range 
	 *            1..65535.
	 * @param image
	 *            the JPEG encoded image data. Must not be null.
	 * @param alpha
	 *            byte array containing the zlib compressed alpha channel data.
	 *            Must not be null.
	 */
	public DefineJPEGImage3(int uid, byte[] image, byte[] alpha)
	{
		setIdentifier(uid);
		setImage(image);
		setCompressedAlpha(alpha);
	}
	
	public DefineJPEGImage3(DefineJPEGImage3 object) {
		identifier = object.identifier;
		width = object.width;
		height = object.height;
		image = Arrays.copyOf(object.image, object.image.length);
		alpha = Arrays.copyOf(object.alpha, object.alpha.length);
	}
	
	public int getIdentifier() {
		return identifier;
	}

	public void setIdentifier(final int uid) {
		if (uid < 0 || uid > 65535) {
			throw new IllegalArgumentException(Strings.IDENTIFIER_OUT_OF_RANGE);
		}
		identifier = uid;
	}

	/**
	 * Returns the width of the image in pixels.
	 */
	public int getWidth()
	{
		return width;
	}

	/**
	 * Returns the height of the image in pixels.
	 */
	public int getHeight()
	{
		return height;
	}

	/**
	 * Returns the image data.
	 */
	public byte[] getImage()
	{
		return image;
	}

	/**
	 * Returns the alpha channel data.
	 */
	public byte[] getCompressedAlpha()
	{
		return alpha;
	}

	/**
	 * Sets the image data.
	 * 
	 * @param bytes
	 *            an array of bytes containing the image table. Must not be null.
	 */
	public void setImage(byte[] bytes)
	{
		image = bytes;
		decodeInfo();
	}

	/**
	 * Sets the alpha channel data with the zlib compressed data.
	 * 
	 * @param bytes
	 *            array of bytes containing zlib encoded alpha channel. Must not 
	 *            be null.
	 */
	public void setCompressedAlpha(byte[] bytes)
	{
		alpha = bytes;
	}

	/**
	 * Creates and returns a deep copy of this object.
	 */
	public DefineJPEGImage3 copy() 
	{
		return new DefineJPEGImage3(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, identifier, image.length, alpha.length);
	}

	public int prepareToEncode(final SWFEncoder coder)
	{
		length = 6;
		length += image.length;
		length += alpha.length;

		return (length > 62 ? 6:2) + length;
	}

	public void encode(final SWFEncoder coder) throws CoderException
	{
		start = coder.getPointer();

		if (length >= 63) {
			coder.writeWord((Types.DEFINE_JPEG_IMAGE_3 << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((Types.DEFINE_JPEG_IMAGE_3 << 6) | length, 2);
		}
		end = coder.getPointer() + (length << 3);
		
		coder.writeWord(identifier, 2);
		coder.writeWord(image.length, 4);
		coder.writeBytes(image);
		coder.writeBytes(alpha);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}

	public void decode(final SWFDecoder coder) throws CoderException
	{
		start = coder.getPointer();
		length = coder.readWord(2, false) & 0x3F;
		
		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}
		end = coder.getPointer() + (length << 3);
		identifier = coder.readWord(2, false);

		int offset = coder.readWord(4, false);

		image = coder.readBytes(new byte[offset]);
		alpha = coder.readBytes(new byte[length - offset - 6]);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}

	private void decodeInfo()
	{
		BigEndianDecoder coder = new BigEndianDecoder(image);

		if (coder.readWord(2, false) == 0xffd8) 
		{
			int marker;
			
			do {
				marker = coder.readWord(2, false);
				
				if ((marker & 0xff00) == 0xff00)
				{
					if (marker >= 0xffc0 && marker <= 0xffcf && marker != 0xffc4
									&& marker != 0xffc8)
					{
						coder.adjustPointer(24);
						height = coder.readWord(2, false);
						width = coder.readWord(2, false);
						break;
					} 
					else
					{
						coder.adjustPointer((coder.readWord(2, false) - 2) << 3);
					}
				}
				
			} while ((marker & 0xff00) == 0xff00);
		}
	}
}