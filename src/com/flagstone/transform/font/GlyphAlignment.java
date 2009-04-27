/*
 * ZonePair.java
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

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFEncodeable;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

//TODO(code) Implement
//TODO(doc)
public final class GlyphAlignment implements SWFEncodeable {
	private static final String FORMAT = "GlyphAlignment: { alignments=%s; alignX=%s; alignY=%s }";

	private List<AlignmentZone> alignments;
	private transient int masks;

	public GlyphAlignment(final SWFDecoder coder)
			throws CoderException {
		final int count = coder.readByte();

		alignments = new ArrayList<AlignmentZone>(count);

		for (int i = 0; i < count; i++) {
			alignments.add(new AlignmentZone(coder));
		}
		masks = coder.readByte();
	}

	public GlyphAlignment(final List<AlignmentZone> list, final boolean xAlign,
			final boolean yAlign) {
		setAlignments(list);
		setAlignmentX(xAlign);
		setAlignmentY(yAlign);
	}

	public GlyphAlignment(final GlyphAlignment object) {
		alignments = new ArrayList<AlignmentZone>(object.alignments);
		masks = object.masks;
	}

	public boolean alignmentX() {
		return (masks & 0x01) != 0;
	}

	public void setAlignmentX(final boolean hasAlign) {
		masks &= 0xFE;
		if (hasAlign) {
			masks |= 1;
		}
	}

	public boolean alignmentY() {
		return (masks & 0x02) != 0;
	}

	public void setAlignmentY(final boolean hasAlign) {
		masks &= 0xFD;
		if (hasAlign) {
			masks |= 2;
		}
	}

	public List<AlignmentZone> getAlignments() {
		return alignments;
	}

	public void setAlignments(final List<AlignmentZone> aligments) {
		this.alignments = aligments;
	}

	public GlyphAlignment copy() {
		return new GlyphAlignment(this);
	}

	@Override
	public String toString() {
		return String.format(FORMAT, alignments, String.valueOf(alignmentX()),
				String.valueOf(alignmentY()));
	}

	public int prepareToEncode(final SWFEncoder coder, final Context context) {
		return 10;
	}

	public void encode(final SWFEncoder coder, final Context context)
			throws CoderException {
		coder.writeByte(2);

		for (AlignmentZone zone : alignments) {
			zone.encode(coder, context);
		}
		coder.writeByte(masks);
	}
}
