/*
 * ButtonColorTransform.java
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

package com.flagstone.transform.movie.button;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.MovieTag;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.Types;
import com.flagstone.transform.movie.datatype.ColorTransform;

/**
 * <p>
 * ButtonColorTransform defines the colour transform that is applied to each
 * shape that is used to draw a button.
 * </p>
 * 
 * <p>
 * This class is only used in conjunction with {@link DefineButton}. The
 * {@link DefineButton2} class allows colour transforms to be specified in the
 * ButtonRecord object that identifies each shape that is displayed for a given
 * button state.
 * </p>
 * 
 * @see DefineButton
 * @see DefineButton2
 */
public final class ButtonColorTransform implements MovieTag {
	
	private static final String FORMAT = "ButtonColorTransform: { identifier=%d; colorTransform=%s }";
	
	private int identifier;
	private ColorTransform colorTransform;

	private transient int start;
	private transient int end;
	private transient int length;

	public ButtonColorTransform() {
		identifier = 0;
		colorTransform = new ColorTransform();
	}

	/**
	 * Creates a ButtonColorTransform object with a colour transform for the
	 * specified button.
	 * 
	 * @param uid
	 *            the unique identifier of a button that this object applies to.
	 *            Must be in the range 1..65535.
	 * @param transform
	 *            an ColorTransform object that will be applied to the button.
	 */
	public ButtonColorTransform(int uid, ColorTransform transform) {
		setIdentifier(uid);
		setColorTransform(transform);
	}

	public ButtonColorTransform(ButtonColorTransform object) {
		identifier = object.identifier;
		colorTransform = object.colorTransform.copy();
	}

	/**
	 * Returns the unique identifier of the button that this object applies to.
	 */
	public int getIdentifier() {
		return identifier;
	}

	/**
	 * Returns the colour transform that will be applied to the button.
	 */
	public ColorTransform getColorTransform() {
		return colorTransform;
	}

	/**
	 * Sets the identifier of the button that the transform applies to.
	 * 
	 * @param uid
	 *            the unique identifier of the button that the colour transform
	 *            will be applied to. Must be in the range 1..65535.
	 */
	public void setIdentifier(int uid) {
		if (uid < 1 || uid > 65535) {
			throw new IllegalArgumentException(Strings.IDENTIFIER_OUT_OF_RANGE);
		}
		identifier = uid;
	}

	/**
	 * Sets the colour transform that will be applied to the button.
	 * 
	 * @param transform
	 *            the colour transform object. Must not be null.
	 */
	public void setColorTransform(ColorTransform transform) {
		if (transform == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		colorTransform = transform;
	}

	public ButtonColorTransform copy() {
		return new ButtonColorTransform(this);
	}

	@Override
	public String toString() {
		return String.format(FORMAT, identifier, colorTransform);
	}

	public int prepareToEncode(final SWFEncoder coder) {
		length = 4 + colorTransform.prepareToEncode(coder);

		return (length > 62 ? 6:2) + length;
	}

	public void encode(final SWFEncoder coder) throws CoderException {

		start = coder.getPointer();

		if (length >= 63) {
			coder.writeWord((Types.BUTTON_COLOR_TRANSFORM << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((Types.BUTTON_COLOR_TRANSFORM << 6) | length, 2);
		}
		end = coder.getPointer() + (length << 3);

		coder.writeWord(identifier, 2);
		colorTransform.encode(coder);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}

	public void decode(final SWFDecoder coder) throws CoderException {

		start = coder.getPointer();
		length = coder.readWord(2, false) & 0x3F;

		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}
		end = coder.getPointer() + (length << 3);

		identifier = coder.readWord(2, false);
		colorTransform.decode(coder);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}
}
