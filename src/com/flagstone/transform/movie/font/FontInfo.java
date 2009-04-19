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

package com.flagstone.transform.movie.font;

import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.MovieTag;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.Types;

//TODO(doc) Review
/**
 * FontInfo defines the name and face of a font and maps the codes for a given
 * character set to the glyphs that are drawn to represent each character.
 * 
 * <p>Three different encoding schemes are supported for the character codes.
 * The ANSI character set is used for Latin languages, SJIS is used for Japanese
 * language characters and Unicode is used for any character set. Since Flash 5
 * Unicode is the preferred encoding scheme.</p>
 * 
 * <p>The index of each entry in the codes array matches the index in the
 * corresponding glyph in the shapes array of an DefineFont object, allowing a
 * given character code to be mapped to a given glyph. </p>
 * 
 * <p>FontInfo also allows the font associated with a Flash file to be mapped to 
 * a font installed on the device where the Flash Player displaying the file is 
 * hosted. The use of a font from a device is not automatic but is determined by 
 * the HTML tag option <i>deviceFont</i> which is passed to the Flash Player when
 * it is first started. If a device does not support a given font then the
 * glyphs in the DefineFont class are used to render the characters.</p>
 * 
 * <p>An important distinction between the host device to specify the font and
 * using the glyphs in an DefineFont object is that the device is not anti-aliased 
 * and the rendering is dependent on the host device. The glyphs in an DefineFont 
 * object are anti-aliased and are guaranteed to look identical on every device 
 * the text is displayed.</p>
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class FontInfo implements MovieTag
{
	private static final String FORMAT="FontInfo: { identifier=%d; encoding=%s; small=%s; italic=%s; bold=%s; name=%s; codes=%s }";
	
	private int identifier;
	private String name;
	private CharacterEncoding encoding;
	private boolean small;
	private boolean italic;
	private boolean bold;
	private List<Integer> codes;

	private transient int start;
	private transient int end;
	private transient int length;
	private transient boolean wideCodes = false;

	//TODO(doc)
	//TODO(optimise)
	public FontInfo(final SWFDecoder coder, final SWFContext context) throws CoderException
	{
		codes = new ArrayList<Integer>();

		start = coder.getPointer();
		length = coder.readWord(2, false) & 0x3F;
		
		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}
		end = coder.getPointer() + (length << 3);

		identifier = coder.readWord(2, false);
		int nameLength = coder.readByte();
		name = coder.readString(nameLength, coder.getEncoding());

		if (name.length() > 0)
		{
			while (name.charAt(name.length() - 1) == 0)
			{
				name = name.substring(0, name.length() - 1);
			}
		}

		/* reserved */coder.readBits(2, false);
		small = coder.readBits(1, false) != 0;
		encoding = CharacterEncoding.fromInt(coder.readBits(2, false));
		italic = coder.readBits(1, false) != 0;
		bold = coder.readBits(1, false) != 0;
		wideCodes = coder.readBits(1, false) != 0;

		int bytesRead = 3 + nameLength + 1;

		while (bytesRead < length)
		{
			codes.add(coder.readWord(wideCodes ? 2 : 1, false));
			bytesRead += (wideCodes) ? 2 : 1;
		}

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}


	/**
	 * Constructs a basic FontInfo object specifying only the name and style of 
	 * the font.
	 * 
	 * @param uid
	 *            the unique identifier of the DefineFont that contains the
	 *            glyphs for the font.
	 * @param name
	 *            the name assigned to the font, identifying the font family.
	 * @param bold
	 *            indicates whether the font weight is bold (true) or normal (false).
	 * @param italic
	 *            indicates whether the font style is italic (true) or plain (false).
 	 */
	public FontInfo(int uid, String name, boolean bold, boolean italic)
	{
		setIdentifier(uid);
		setName(name);
		setItalic(italic);
		setBold(bold);
		small = false;
		encoding = CharacterEncoding.UCS2;
		codes = new ArrayList<Integer>();
	}

	//TODO(doc)
	public FontInfo(FontInfo object)
	{
		identifier = object.identifier;
		name = object.name;
		italic = object.italic;
		bold = object.bold;
		small = object.small;
		encoding = object.encoding;
		codes = new ArrayList<Integer>(object.codes);
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
	 * Returns the name of the font family.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the encoding scheme used for characters rendered in the font, either
	 * ASCII, SJIS or UCS2.
	 */
	public CharacterEncoding getEncoding()
	{
		return encoding;
	}

	/**
	 * Does the font have a small point size. This is used only with a Unicode
	 * font encoding.
	 */
	public boolean isSmall()
	{
		return small;
	}

	/**
	 * Sets the font is small. Used only with Unicode fonts ot provide better 
	 * appearance when the point size is small.
	 * 
	 * @param aBool
	 *            true if the font will be aligned on pixel boundaries.
	 */
	public void setSmall(boolean aBool)
	{
		small = aBool;
	}

	/**
	 * Is the font style italics.
	 */
	public boolean isItalic()
	{
		return italic;
	}

	/**
	 * Is the font weight bold.
	 */
	public boolean isBold()
	{
		return bold;
	}

	/**
	 * Returns the array of character codes.
	 */
	public List<Integer> getCodes()
	{
		return codes;
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

	/**
	 * Sets the name of the font. The name be omitted (set to an empty string)
	 * if the font is embedded in the Flash file, i.e. the corresponding 
	 * DefineFont object has all the glyph information.
	 * 
	 * @param aString
	 *            the name assigned to the font, identifying the font family.
	 *            Must not be null.
 	 */
	public void setName(String aString)
	{
		if (aString == null) {
			throw new IllegalArgumentException(Strings.STRING_CANNOT_BE_NULL);
		}
		name = aString;
	}

	/**
	 * Sets the font character encoding.
	 * 
	 * @param anEncoding
	 *            the encoding used to identify characters, either ASCII,
	 *            SJIS or UCS2.
	 */
	public void setEncoding(CharacterEncoding anEncoding)
	{
		encoding = anEncoding;
	}

	/**
	 * Sets the font is italics.
	 * 
	 * @param aBool
	 *            a boolean flag indicating whether the font will be rendered in
	 *            italics.
	 */
	public void setItalic(boolean aBool)
	{
		italic = aBool;
	}

	/**
	 * Sets the font is bold.
	 * 
	 * @param aBool
	 *            a boolean flag indicating whether the font will be rendered in
	 *            bold face.
	 */
	public void setBold(boolean aBool)
	{
		bold = aBool;
	}

	/**
	 * Add a code to the array of codes. The index position of a character code
	 * in the array identifies the index of the corresponding glyph in the
	 * DefineFont object.
	 * 
	 * @param aCode
	 *            a code for a glyph. Must be in the range 0..65535.
	 */
	public void addCode(int aCode)
	{
		if (aCode < 0 || aCode > 65535) {
			throw new IllegalArgumentException(Strings.CHARACTER_CODE_OUT_OF_RANGE);
		}
		codes.add(aCode);
	}

	/**
	 * Sets the array of character codes. The index position of a character code
	 * in the array identifies the index of the corresponding glyph in the
	 * DefineFont object.
	 * 
	 * @param anArray
	 *            the array mapping glyphs to particular character codes. Must 
	 *            not be null.
	 */
	public void setCodes(List<Integer> anArray)
	{
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		codes = anArray;
	}

	public FontInfo copy() 
	{
		return new FontInfo(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, identifier, encoding, small, italic, bold, name, codes);
	}

	//TODO(optimise)
	public int prepareToEncode(final SWFEncoder coder, final SWFContext context)
	{
		length = 3;
		length += coder.strlen(name);

		wideCodes = false;

		for (Integer code : codes)
		{
			if (code.intValue() > 255) {
				wideCodes = true;
			}
		}

		length += codes.size() * (wideCodes ? 2 : 1);

		return (length > 62 ? 6:2) + length;
	}

	//TODO(optimise)
	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
		start = coder.getPointer();

		if (length >= 63) {
			coder.writeWord((Types.FONT_INFO << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((Types.FONT_INFO << 6) | length, 2);
		}
		end = coder.getPointer() + (length << 3);

		coder.writeWord(identifier, 2);
		coder.writeWord(coder.strlen(name)-1, 1);
		coder.writeString(name);
		coder.adjustPointer(-8);
		coder.writeBits(0, 2);
		coder.writeBits(small ? 1 : 0, 1);
		coder.writeBits(encoding.getValue(), 2);
		coder.writeBits(italic ? 1 : 0, 1);
		coder.writeBits(bold ? 1 : 0, 1);
		coder.writeBits(wideCodes ? 1 : 0, 1);

		for (Integer code : codes) {
			coder.writeWord(code.intValue(), wideCodes ? 2 : 1);
		}

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}
}
