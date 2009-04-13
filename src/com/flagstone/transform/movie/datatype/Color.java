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

package com.flagstone.transform.movie.datatype;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.Codeable;
import com.flagstone.transform.movie.Copyable;
import com.flagstone.transform.movie.Strings;

/**
 * <p>
 * Color is used to represent 32-bit colours in the RGB colour space with 8 bits
 * per channel and an optional alpha channel.
 * </p>
 * 
 * <p>
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
public final class Color implements Codeable, Copyable<Color> {

	private static final String FORMAT = "Color: { red=%d; green=%d; blue=%d; alpha=%d }";
	
	/**
	 * Parses a String representing a 32-bit hexadecimal number and returns the
	 * corresponding Color object.
	 * 
	 * @param colour
	 *            a 32-bit hexadecimal string with the values for the red,
	 *            green, blue and alpha colour channels.
	 */
	public static Color valueOf(final String colour) {

		String digits;

		if (colour.startsWith("0x")) {
			digits = colour.substring(2);
		} else {
			digits = colour;
		}
		return valueOf(Integer.parseInt(digits, 16));
	}

	/**
	 * Parses a 32-bit integer representing the values for the red, green, blue
	 * and alpha colour channels and returns the corresponding Color object.
	 * 
	 * @param rgba
	 *            a 32-bit hexadecimal integer with red, green, blue and alpha
	 *            channels.
	 */
	public static Color valueOf(final int rgba) {
		return new Color((rgba >> 24) & 0xFF, (rgba >> 16) & 0xFF,
				(rgba >> 8) & 0xFF, rgba & 0xFF);
	}

	private int red;
	private int green;
	private int blue;
	private int alpha;

	/**
	 * Creates a default Colour object with the opaque colour black.
	 */
	public Color() {
		alpha = 255;
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
		setRed(red);
		setGreen(green);
		setBlue(blue);
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
		setRed(red);
		setGreen(green);
		setBlue(blue);
		setAlpha(alpha);
	}
	
	/**
	 * Create a copy of a Color object.
	 * 
	 * @param object the Color object used to initialise this one.
	 */
	public Color(final Color object) {
		red = object.red;
		green = object.green;
		blue = object.blue;
		alpha = object.alpha;
	}

	/**
	 * Returns the value for the red colour channel.
	 */
	public int getRed() {
		return red;
	}

	/**
	 * Set the value for the red colour channel.
	 * 
	 * @param level
	 *            value for the red channel. Must be in the range 0..255.
	 */
	public void setRed(final int level) {
		if (level < 0 || level > 255) {
			throw new IllegalArgumentException(Strings.COLOR_OUT_OF_RANGE);
		}
		red = level;
	}

	/**
	 * Returns the value for the green colour channel.
	 */
	public int getGreen() {
		return green;
	}

	/**
	 * Set the value for the green colour channel.
	 * 
	 * @param level
	 *            value for the green channel. Must be in the range 0..255.
	 */
	public void setGreen(final int level) {
		if (level < 0 || level > 255) {
			throw new IllegalArgumentException(Strings.COLOR_OUT_OF_RANGE);
		}
		green = level;
	}

	/**
	 * Returns the value for the blue colour channel.
	 */
	public int getBlue() {
		return blue;
	}

	/**
	 * Set the value for the blue colour channel.
	 * 
	 * @param level
	 *            value for the blue channel. Must be in the range 0..255.
	 */
	public void setBlue(final int level) {
		if (level < 0 || level > 255) {
			throw new IllegalArgumentException(Strings.COLOR_OUT_OF_RANGE);
		}
		blue = level;
	}

	/**
	 * Returns the value for the alpha colour channel.
	 */
	public int getAlpha() {
		return alpha;
	}

	/**
	 * Set the value for the alpha channel.
	 * 
	 * @param level
	 *            value for the alpha channel. Must be in the range 0..255.
	 */
	public void setAlpha(final int level) {
		if (level < 0 || level > 255) {
			throw new IllegalArgumentException(Strings.COLOR_OUT_OF_RANGE);
		}
		alpha = level;
	}

	/**
	 * Set the values for the red, green and blue colour channels. The alpha
	 * channel is not changed.
	 * 
	 * @param red
	 *            value for the red channel, in the range 0..255.
	 * @param green
	 *            value for the green channel, in the range 0..255.
	 * @param blue
	 *            value for the blue channel, in the range 0..255.
	 */
	public void setChannels(final int red, final int green, final int blue) {
		setRed(red);
		setGreen(green);
		setBlue(blue);
	}

	/**
	 * Set the values for each of the four colour channels.
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
	public void setChannels(final int red, final int green, final int blue,
			final int alpha) {
		setRed(red);
		setGreen(green);
		setBlue(blue);
		setAlpha(alpha);
	}

	/**
	 * Returns a 24-bit integer with the values for the colour channels. The
	 * value for the red channel is in the most significant byte and blue in the
	 * least significant.
	 */
	public int getRGB() {
		return (red << 16) + (green << 8) + blue;
	}

	/**
	 * Set the value for the red, green and blue colour channels by parsing a
	 * 24-bit integer.
	 * 
	 * @param rgb
	 *            a 24-bit integer with the the value for the red channel in the
	 *            most significant byte and blue in the least significant byte.
	 */
	public void setRGB(final int rgb) {
		red = (rgb >> 16) & 0x00FF;
		green = (rgb >> 8) & 0x00FF;
		blue = rgb & 0x00FF;
	}

	/**
	 * Returns a 32-bit integer with the values for the colour channels. The
	 * value for the red channel is in the most significant byte and alpha in
	 * the least significant.
	 */
	public int getRGBA() {
		return (red << 24) + (green << 16) + (blue << 8) + alpha;
	}

	/**
	 * Set the value for the red, green and blue colour channels by parsing a
	 * 32-bit integer.
	 * 
	 * @param rgba
	 *            a 32-bit integer with the the value for the red channel in the
	 *            most significant byte and alpha in the least significant byte.
	 */
	public void setRGBA(final int rgba) {
		red = (rgba >> 24) & 0x00FF;
		green = (rgba >> 16) & 0x00FF;
		blue = (rgba >> 8) & 0x00FF;
		alpha = rgba & 0x00FF;
	}

	public Color copy() {
		return new Color(this);
	}

	@Override
	public String toString() {
		return String.format(FORMAT, red, green, blue, alpha);
	}

	public int prepareToEncode(final SWFEncoder coder) {
		return (coder.getContext().isTransparent()) ? 4 : 3;
	}

	public void encode(final SWFEncoder coder) throws CoderException {
		coder.writeByte(red);
		coder.writeByte(green);
		coder.writeByte(blue);

		if (coder.getContext().isTransparent()) {
			coder.writeByte(alpha);
		}
	}

	public void decode(final SWFDecoder coder) throws CoderException {
		red = coder.readByte();
		green = coder.readByte();
		blue = coder.readByte();
		alpha = (coder.getContext().isTransparent()) ? coder.readByte() : 255;
	}
}