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

package com.flagstone.transform.font;

import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.MovieTag;
import com.flagstone.transform.MovieTypes;
import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

//TODO(doc)
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
public final class FontAlignment implements MovieTag
{
	public enum StrokeWidth { THIN, MEDIUM, THICK };
	
	private int identifier;
	private StrokeWidth strokeWidth; 
	private List<AlignmentZone> zones;
	
	private transient int start;
	private transient int end;
	private transient int length;

	public FontAlignment(final SWFDecoder coder, final SWFContext context) throws CoderException
	{
		zones = new ArrayList<AlignmentZone>();

		start = coder.getPointer();
		length = coder.readWord(2, false) & 0x3F;
		
		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}
		end = coder.getPointer() + (length << 3);

		identifier = coder.readWord(2, false);

		int bytesRead = 3;
		
		while (bytesRead < length)
		{
			zones.add(new AlignmentZone(coder, context));
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
	public FontAlignment(int uid, String name, boolean bold, boolean italic)
	{
		setIdentifier(uid);
		zones = new ArrayList<AlignmentZone>();
	}
	
	public FontAlignment(FontAlignment object) {
		identifier = object.identifier;
		strokeWidth = object.strokeWidth;	
		zones = new ArrayList<AlignmentZone>(object.zones);
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
	 * Returns the array of character codes.
	 */
	public List<AlignmentZone> getZones()
	{
		return zones;
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
	 * Add a code to the array of codes. The index position of a character code
	 * in the array identifies the index of the corresponding glyph in the
	 * DefineFont object.
	 * 
	 * @param aCode
	 *            a code for a glyph. Must be in the range 0..65535.
	 */
	public void addZone(AlignmentZone zone)
	{
		zones.add(zone);
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
	public void setZones(List<AlignmentZone> array)
	{
		if (array == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		zones = array;
	}

	/**
	 * Creates and returns a deep copy of this object.
	 */
	public FontAlignment copy() 
	{
		return new FontAlignment(this);
	}

	@Override
	public String toString()
	{
		return "";
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context)
	{
		length = 4;
		//TODO(code) Implement
		for (AlignmentZone zone : zones) {
		}

		return (length > 62 ? 6:2) + length;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
		start = coder.getPointer();
		//TODO(code) use correct type.
		if (length >= 63) {
			coder.writeWord((MovieTypes.FONT_INFO << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((MovieTypes.FONT_INFO << 6) | length, 2);
		}
		end = coder.getPointer() + (length << 3);
		
		coder.writeWord(identifier, 2);

		for (AlignmentZone zone : zones) {
			zone.encode(coder, context);
		}

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}
}
