/*
 * Color.java
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
import com.flagstone.transform.coder.Encodeable;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

/**
 * <p>
 * Color is used to represent 32-bit colours in the RGB colour space with 8 bits
 * per channel and an optional alpha channel.
 * </p>
 * 
 * <p>
 * //TODO(doc) This paragraph is too wordy. Mention SWFContext
 * Whether a colour contains transparency information is determined by the
 * context created by the object that contains the colour. For example colours
 * within the DefineShape and DefineShape2 objects do not contain alpha channel
 * information while those in an DefineShape3 object do. To simplify the use of
 * the colours only a single class is provided. The alpha channel can be ignored
 * if not required within the object the colour is defined for. When objects
 * containing colour are encoded or decoded the the colour objects are notified
 * that the encoder or decoder whether the alpha channel information should also
 * be included.
 * </p>
 */
//TODO(doc) Check comments for all methods
public final class Color implements Encodeable {

	private static final String FORMAT = "Color: { red=%d; green=%d; blue=%d; alpha=%d }";
	
	private final transient int red;
	private final transient int green;
	private final transient int blue;
	private final transient int alpha;

	public Color(final SWFDecoder coder, final Context context) throws CoderException {
		red = coder.readByte();
		green = coder.readByte();
		blue = coder.readByte();
		alpha = (context.getVariables().containsKey(Context.TRANSPARENT)) ? coder.readByte() : 255;
	}

	public Color(final int rgb) {
		red = (rgb >>> 16) & 0x00FF;
		green = (rgb >>> 8) & 0x00FF;
		blue = rgb & 0x00FF;
		alpha = 255;
	}

	public Color(final int rgb, int alpha) {
		red = (rgb >>> 16) & 0x00FF;
		green = (rgb >>> 8) & 0x00FF;
		blue = rgb & 0x00FF;
		this.alpha = alpha;
	}

	/**
	 * Creates a Color object containing red, green and blue channels. The alpha
	 * channel defaults to the value 255 - defining an opaque colour.
	 * 
	 * @param red
	 *            value for the red channel, in the range 0..255.
	 * @param green
	 *            value for the green channel, in the range 0..255.
	 * @param blue
	 *            value for the blue channel, in the range 0..255.
	 */
	public Color(final int red, final int green, final int blue) {
		this.red = checkLevel(red);
		this.green = checkLevel(green);
		this.blue = checkLevel(blue);
		alpha = 255;
	}

	/**
	 * Creates a transparent Color object containing red, green, blue and alpha
	 * channels.
	 * 
	 * @param red
	 *            value for the red channel, in the range 0..255.
	 * @param green
	 *            value for the green channel, in the range 0..255.
	 * @param blue
	 *            value for the blue channel, in the range 0..255.
	 * @param alpha
	 *            value for the alpha channel, in the range 0..255.
	 */
	public Color(final int red, final int green, final int blue, final int alpha) {
		this.red = checkLevel(red);
		this.green = checkLevel(green);
		this.blue = checkLevel(blue);
		this.alpha = checkLevel(alpha);
	}
	
	private int checkLevel(final int level) {
		if (level < 0 || level > 255) {
			throw new IllegalArgumentException(Strings.COLOR_OUT_OF_RANGE);
		}
		return level;
	}
	
	/**
	 * Returns the value for the red colour channel.
	 */
	public int getRed() {
		return red;
	}

	/**
	 * Returns the value for the green colour channel.
	 */
	public int getGreen() {
		return green;
	}

	/**
	 * Returns the value for the blue colour channel.
	 */
	public int getBlue() {
		return blue;
	}

	/**
	 * Returns the value for the alpha colour channel.
	 */
	public int getAlpha() {
		return alpha;
	}

	@Override
	public String toString() {
		return String.format(FORMAT, red, green, blue, alpha);
	}
	
	@Override
	public boolean equals(final Object object) {
		boolean result;
		Color color;
		
		if (object == null) {
			result = false;
		} else if (object == this) {
			result = true;
		} else if (object instanceof Color) {
			color = (Color)object;
			result = red == color.red && green == color.green &&
				blue == color.blue && alpha == color.alpha;
		} else {
			result = false;
		}
		return result;
	}
	
	@Override
	public int hashCode() {
		return (((red*31)+green)*31 + blue)*31 + alpha;
	}

	public int prepareToEncode(final SWFEncoder coder, final Context context) {
		return (context.getVariables().containsKey(Context.TRANSPARENT)) ? 4 : 3;
	}

	public void encode(final SWFEncoder coder, final Context context) throws CoderException {
		coder.writeByte(red);
		coder.writeByte(green);
		coder.writeByte(blue);

		if (context.getVariables().containsKey(Context.TRANSPARENT)) {
			coder.writeByte(alpha);
		}
	}
}