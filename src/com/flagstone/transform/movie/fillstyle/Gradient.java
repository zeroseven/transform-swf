/*
 * Gradient.java
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

package com.flagstone.transform.movie.fillstyle;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.Encodeable;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.datatype.Color;
import com.flagstone.transform.movie.font.Kerning;

/**
 * Gradient defines a control point that is used to specify how a gradient
 * colour is displayed.
 * 
 * <p>Two or more control points are used to define how the colour changes across
 * the gradient square. Each control point specifies the ratio indicating the 
 * location of the control point across the gradient square and the colour to
 * be displayed at that point.</p>
 * </p>
 * 
 * <p>The ratio is a number between 0 and 255 - that specifies the relative location 
 * in the square. For Linear Gradient Fills a ratio of zero is mapped to the left 
 * side of the gradient square and 255 is mapped to the right side of the square. 
 * For Radial Gradient Fills a ratio of zero is mapped to the centre of the 
 * gradient square and 255 is mapped to the edge of the largest circle that fits 
 * inside the gradient square. A ratio is used rather than specifying coordinates 
 * within the gradient square as the coordinate space is transformed to fit the 
 * shape that the gradient is being displayed in.</p>
 * 
 * <p>Note that the object used to create the shape definition determines whether
 * the alpha channel is encoded in the gradient colours. Simply specifying the
 * level of transparency in the Color object is not sufficient.</p>
 * 
 * @see GradientFill
 */
public final class Gradient implements Encodeable
{
	private static final String FORMAT = "Gradient: { ratio=%d; color=%s }";
	
	private final transient int ratio;
	private final transient Color color;

	public Gradient(final SWFDecoder coder, final SWFContext context) throws CoderException
	{
		ratio = coder.readByte();
		color = new Color(coder, context);
	}

	/**
	 * Creates a Gradient object with the specified ratio and color.
	 * 
	 * @param aRatio
	 *            the ratio along the gradient square. Must be in the range 0..255.
	 * @param aColor
	 *            the color at the control point. Must not be null.
	 */
	public Gradient(final int aRatio, final Color aColor)
	{
		if (aRatio < 0 || aRatio > 255) {
			throw new IllegalArgumentException(Strings.RATIO_OUT_OF_RANGE);
		}
		ratio = aRatio;
		
		if (aColor == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		color = aColor;
	}
	
	/**
	 * Returns the ratio that defines the relative point across the gradient
	 * square.
	 */
	public int getRatio()
	{
		return ratio;
	}

	/**
	 * Returns the colour that is displayed at the control point across the
	 * gradient square defined by the ratio.
	 */
	public Color getColor()
	{
		return color;
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, ratio, color);
	}
	
	@Override
	public boolean equals(final Object object) {
		boolean result;
		Gradient gradient;
		
		if (object == null) {
			result = false;
		} else if (object == this) {
			result = true;
		} else if (object instanceof Kerning) {
			gradient = (Gradient)object;
			result = ratio == gradient.ratio && color.equals(gradient.color);
		} else {
			result = false;
		}
		return result;
	}
	
	@Override
	public int hashCode() {
		return (ratio*31) + color.hashCode();
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context)
	{
		int length = 1;

		length += color.prepareToEncode(coder, context);

		return length;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
		coder.writeByte(ratio);
		color.encode(coder, context);
	}
}
