/*
 * RemoveObject2.java
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

package com.flagstone.transform;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

/**
 * RemoveObject2 removes an object from the display list, requiring only the
 * layer number.
 * 
 * @see Remove
 */
public final class Remove2 implements MovieTag {
	
	private static final String FORMAT = "Remove2: { layer=%d }";

	private int layer;

	//TODO(doc)
	public Remove2(final SWFDecoder coder, final Context context) throws CoderException {

		if ((coder.readWord(2, false) & 0x3F) == 0x3F) {
			coder.readWord(4, false);
		}

		layer = coder.readWord(2, false);
	}

	/**
	 * Creates a RemoveObject2, specifying the layer in the display list where
	 * the object to be removed is currently displayed.
	 * 
	 * @param layer
	 *            the layer number on which the object is displayed. Must be in
	 *            the range 1.65535.
	 */
	public Remove2(int layer) {
		setLayer(layer);
	}

	//TODO(doc)
	public Remove2(Remove2 object) {
		layer = object.layer;
	}

	/**
	 * Returns the layer in the display list where the object to be removed is
	 * currently displayed.
	 */
	public int getLayer() {
		return layer;
	}

	/**
	 * Sets the layer in the display list from which the object will be removed.
	 * 
	 * @param aLayer
	 *            the layer number on which the object is displayed. Must be in
	 *            the range 1.65535.
	 */
	public void setLayer(int aLayer) {
		if (aLayer < 1 || aLayer > 65535) {
			throw new IllegalArgumentException(Strings.LAYER_OUT_OF_RANGE);
		}
		layer = aLayer;
	}

	public Remove2 copy() {
		return new Remove2(this);
	}

	/**
	 * Returns a short description of this action.
	 */
	@Override
	public String toString() {
		return String.format(FORMAT, layer);
	}

	public int prepareToEncode(final SWFEncoder coder, final Context context) {
		return 4;
	}

	public void encode(final SWFEncoder coder, final Context context) throws CoderException {
		coder.writeWord((MovieTypes.REMOVE_2 << 6) | 2, 2);
		coder.writeWord(layer, 2);
	}
}
