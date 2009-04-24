/*
 * FontAlignment.java
 * Transform
 * 
 * Copyright (c) 2009 Flagstone Software Ltd. All rights reserved.
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

import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

//TODO(doc)
public final class FontAlignment implements MovieTag {
	public enum StrokeWidth {
		THIN, MEDIUM, THICK
	};

	private static final String FORMAT = "FontAlignment: { identifier=%d; strokeWidth=%s; zones=%s }";

	private int identifier;
	private int hints;
	private List<GlyphAlignment> zones;

	private transient int start;
	private transient int end;
	private transient int length;

	public FontAlignment(final SWFDecoder coder, final Context context)
			throws CoderException {
		start = coder.getPointer();
		length = coder.readWord(2, false) & 0x3F;

		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}
		end = coder.getPointer() + (length << 3);

		identifier = coder.readWord(2, false);
		hints = coder.readByte();

		zones = new ArrayList<GlyphAlignment>();

		while (coder.getPointer() < end) {
			zones.add(new GlyphAlignment(coder, context));
		}

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}

	public FontAlignment(int uid, StrokeWidth stroke, List<GlyphAlignment>list) {
		setIdentifier(uid);
		setStrokeWidth(stroke);
		setAlignmentZones(list);
	}

	public FontAlignment(FontAlignment object) {
		identifier = object.identifier;
		hints = object.hints;
		zones = new ArrayList<GlyphAlignment>(object.zones);
	}

	/**
	 * Returns the unique identifier of the font definition that the alignment
	 * information is for.
	 */
	public int getIdentifier() {
		return identifier;
	}

	/**
	 * Sets the identifier of the font that this alignment information is for.
	 * 
	 * @param uid
	 *            the unique identifier of the DefineFont that contains the
	 *            glyphs for the font. Must be in the range 1..65535.
	 */
	public void setIdentifier(int uid) {
		if (uid < 1 || uid > 65535) {
			throw new IllegalArgumentException(Strings.IDENTIFIER_OUT_OF_RANGE);
		}
		identifier = uid;
	}

	public StrokeWidth getStrokeWidth() {
		StrokeWidth stroke;
		switch (hints) {
		case 0x40:
			stroke = StrokeWidth.MEDIUM;
			break;
		case 0x80:
			stroke = StrokeWidth.THICK;
			break;
		default:
			stroke = StrokeWidth.THIN;
			break;
		}
		return stroke;
	}

	public void setStrokeWidth(StrokeWidth stroke) {
		switch (stroke) {
		case MEDIUM:
			hints = 0x80;
			break;
		case THICK:
			hints = 0x40;
			break;
		default:
			hints = 0x00;
			break;
		}
	}

	public List<GlyphAlignment> getAlignmentZones() {
		return zones;
	}

	public void setAlignmentZones(List<GlyphAlignment> array) {
		if (array == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		zones = array;
	}

	public FontAlignment addZone(GlyphAlignment zone) {
		if (zone == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		zones.add(zone);
		return this;
	}

	public FontAlignment copy() {
		return new FontAlignment(this);
	}

	@Override
	public String toString() {
		return String.format(FORMAT, identifier, getStrokeWidth(), zones);
	}

	public int prepareToEncode(final SWFEncoder coder, final Context context) {
		length = 3;

		for (GlyphAlignment zone : zones) {
			length += zone.prepareToEncode(coder, context);
		}

		return (length > 62 ? 6 : 2) + length;
	}

	public void encode(final SWFEncoder coder, final Context context)
			throws CoderException {
		start = coder.getPointer();

		if (length >= 63) {
			coder.writeWord((MovieTypes.FONT_ALIGNMENT << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((MovieTypes.FONT_ALIGNMENT << 6) | length, 2);
		}
		end = coder.getPointer() + (length << 3);

		coder.writeWord(identifier, 2);
		coder.writeByte(hints);

		for (GlyphAlignment zone : zones) {
			zone.encode(coder, context);
		}

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}
}
