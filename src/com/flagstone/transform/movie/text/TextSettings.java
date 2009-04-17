/*
 * FontInfo.java
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

package com.flagstone.transform.movie.text;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.MovieTag;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.Types;

public final class TextSettings implements MovieTag
{
	private int identifier;

	private transient int length;

	private TextSettings()
	{
	}

	public TextSettings(int uid, String name, boolean bold, boolean italic)
	{
		setIdentifier(uid);
	}
	
	public TextSettings(TextSettings object) {
		identifier = object.identifier;
	}

	/**
	 * Returns the unique identifier of the font definition that this font 
	 * information is for.
	 */
	public int getIdentifier()
	{
		return identifier;
	}

	/**
	 * Sets the identifier of the font that this font information is for.
	 * 
	 * @param uid
	 *            the unique identifier of the DefineFont that contains the
	 *            glyphs for the font. Must be in the range 1..65535.
 	 */
	public void setIdentifier(int uid)
	{
		if (uid < 1 || uid > 65535) {
			throw new IllegalArgumentException(Strings.IDENTIFIER_OUT_OF_RANGE);
		}
		identifier = uid;
	}

	public TextSettings copy() 
	{
		return new TextSettings(this);
	}

	/**
	 * Returns a short description of this action.
	 */
	@Override
	public String toString()
	{
		return "";
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context)
	{
		length = 4;

		return length;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
		if (length >= 63) {
			coder.writeWord((Types.FONT_INFO << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((Types.FONT_INFO << 6) | length, 2);
		}
		
		coder.writeWord(identifier, 2);
	}

	public void decode(final SWFDecoder coder, final SWFContext context) throws CoderException
	{
		length = coder.readWord(2, false) & 0x3F;
		
		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}

		identifier = coder.readWord(2, false);
	}
}
