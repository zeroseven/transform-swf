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
public final class GlyphAlignment implements SWFEncodeable
{
	private boolean alignX;
	private boolean alignY;
	
	private List<AlignmentZone>alignments;
	
	private GlyphAlignment(final SWFDecoder coder, final Context context) throws CoderException
	{
	}

	public GlyphAlignment(GlyphAlignment object)
	{
		alignX = object.alignX;
		alignY = object.alignY;		
		alignments = new ArrayList<AlignmentZone>(object.alignments);
	}

	public boolean isAlignX() {
		return alignX;
	}

	public void setAlignX(boolean hasAlign) {
		this.alignX = hasAlign;
	}

	public boolean isAlignY() {
		return alignY;
	}

	public void setAlignY(boolean hasAlign) {
		this.alignY = hasAlign;
	}

	public List<AlignmentZone> getAlignments() {
		return alignments;
	}

	public void setAlignments(List<AlignmentZone> aligments) {
		this.alignments = aligments;
	}

	/**
	 * Creates and returns a deep copy of this object.
	 */
	public GlyphAlignment copy()
	{
		return new GlyphAlignment(this);
	}

	@Override
	public String toString()
	{
		return "";
	}
	
	public int prepareToEncode(final SWFEncoder coder, final Context context)
	{
		return 0;
	}

	public void encode(final SWFEncoder coder, final Context context) throws CoderException
	{
	}
}
