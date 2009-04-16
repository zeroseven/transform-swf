/*
 * MovieObject.java
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

package com.flagstone.transform.movie;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.datatype.Bounds;

public final class ScalingGrid implements DefineTag {
	private static final String FORMAT = "ScalingGrid: { identifier=%d; bounds=%s; }";

	private int identifier;
	private Bounds bounds;

	private transient int length;

	public ScalingGrid(final SWFDecoder coder, final SWFContext context) throws CoderException {

		if ((coder.readWord(2, false) & 0x3F) == 0x3F) {
			coder.readWord(4, false);
		}

		identifier = coder.readWord(2, false);
		bounds = new Bounds(coder);
	}

	public ScalingGrid(final int identifier, final Bounds bounds) {
		setIdentifier(identifier);
		setBounds(bounds);
	}

	public ScalingGrid(ScalingGrid object) {
		identifier = object.identifier;
		bounds = object.bounds;
	}

	public int getIdentifier() {
		return identifier;
	}

	public void setIdentifier(final int uid) {
		if (uid < 1 || uid > 65535) {
			throw new IllegalArgumentException(Strings.IDENTIFIER_OUT_OF_RANGE);
		}
		identifier = uid;
	}

	public Bounds getBounds() {
		return bounds;
	}

	/**
	 * Sets the bounding rectangle that encloses the shape.
	 * 
	 * @param aBounds
	 *            set the bounding rectangle for the shape. Must not be null.
	 */
	public void setBounds(Bounds aBounds) {
		if (aBounds == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		bounds = aBounds;
	}

	public ScalingGrid copy() {
		return new ScalingGrid(this);
	}

	@Override
	public String toString() {
		return String.format(FORMAT, identifier, bounds.toString());
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context) {
		length = 2 + bounds.prepareToEncode(coder, context);
		return 2 + length;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException {
		
		coder.writeWord((Types.DEFINE_SCALING_GRID << 6) | length, 2);
		coder.writeWord(identifier, 2);
		bounds.encode(coder, context);
	}
}
