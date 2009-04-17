/*
 * MorphGradient.java
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

/**
 * MorphGradient defines the control points that is used to specify how a
 * gradient fill is displayed at the start and end of the shape morphing
 * process.
 * 
 * <p>The ratio is a number between 0 and 255 - that specifies the relative location 
 * in the square. For Linear Gradient Fills a ratio of zero is mapped to the left 
 * side of the gradient square and 255 is mapped to the right side of the square. 
 * For Radial Gradient Fills a ratio of zero is mapped to the centre of the 
 * gradient square and 255 is mapped to the edge of the largest circle that fits 
 * inside the gradient square. The color is the colour to be displayed at the
 * point identified by the ratio.</p>
 * 
 * <p>The MorphGradient defines ratios and colours for the start and end of the
 * morphing process, the Flash Player performs the interpolation between the 
 * two values as the shape is morphed.</p>

 * @see Gradient
 * @see GradientFill
 * @see MorphGradientFill
 */
public final class MorphGradient implements Encodeable
{
	private static final String FORMAT = "MorphGradient: { startRatio=%d; endRatio=%d; startColor=%s; endColor=%s }";
	
	private int startRatio;
	private int endRatio;
	private Color startColor;
	private Color endColor;

	public MorphGradient(final SWFDecoder coder, final SWFContext context) throws CoderException
	{
		startRatio = coder.readByte();
		startColor = new Color(coder, context);
		endRatio = coder.readByte();
		endColor = new Color(coder, context);
	}

	/**
	 * Creates a MorphGradient object specifying the starting and ending
	 * ratios and colours.
	 * 
	 * @param startRatio
	 *            the ratio along the gradient square at the start of the
	 *            morphing process. Must be in the range 0..255.
	 * @param endRatio
	 *            the ratio along the gradient square at the end of the morphing
	 *            process. Must be in the range 0..255.
	 * @param startColor
	 *            the colour at the starting control point. Must not be null.
	 * @param endColor
	 *            the colour at the ending control point. Must not be null.
	 */
	public MorphGradient(int startRatio, int endRatio, Color startColor, Color endColor)
	{
		setStartRatio(startRatio);
		setEndRatio(endRatio);
		setStartColor(startColor);
		setEndColor(endColor);
	}
	
	public MorphGradient(MorphGradient object) {
		startRatio = object.startRatio;
		endRatio = object.endRatio;
		startColor = object.startColor;
		endColor = object.endColor;
	}

	/**
	 * Returns the ratio at the start of the morphing process.
	 */
	public int getStartRatio()
	{
		return startRatio;
	}

	/**
	 * Returns the ratio at the end of the morphing process.
	 */
	public int getEndRatio()
	{
		return endRatio;
	}

	/**
	 * Returns the colour at the start of the morphing process.
	 */
	public Color getStartColor()
	{
		return startColor;
	}

	/**
	 * Returns the colour at the end of the morphing process.
	 */
	public Color getEndColor()
	{
		return endColor;
	}

	/**
	 * Sets the ratio along the gradient square at the start of the morphing
	 * process.
	 * 
	 * @param aNumber
	 *            the starting ratio. Must be in the range 0..255.
	 */
	public void setStartRatio(int aNumber)
	{
		if (aNumber < 0 || aNumber > 255) {
			throw new IllegalArgumentException(Strings.RATIO_OUT_OF_RANGE);
		}
		startRatio = aNumber;
	}

	/**
	 * Sets the ratio along the gradient square at the end of the morphing
	 * process.
	 * 
	 * @param aNumber
	 *            the ending ratio. Must be in the range 0..255.
	 */
	public void setEndRatio(int aNumber)
	{
		if (aNumber < 0 || aNumber > 255) {
			throw new IllegalArgumentException(Strings.RATIO_OUT_OF_RANGE);
		}
		endRatio = aNumber;
	}

	/**
	 * Sets the colour at the start of the morphing process.
	 * 
	 * @param aColor
	 *            the start colour. Must not be null.
	 */
	public void setStartColor(Color aColor)
	{
		if (aColor == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		startColor = aColor;
	}

	/**
	 * Sets the colour at the end of the morphing process.
	 * 
	 * @param aColor
	 *            the end colour. Must not be null.
	 */
	public void setEndColor(Color aColor)
	{
		if (aColor == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		endColor = aColor;
	}

	/**
	 * Creates and returns a deep copy of this object.
	 */
	public MorphGradient copy()
	{
		return new MorphGradient(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, startRatio, endRatio, startColor, endColor);
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context)
	{
		int length = 2;

		length += startColor.prepareToEncode(coder, context);
		length += endColor.prepareToEncode(coder, context);

		return length;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
		coder.writeWord(startRatio, 1);
		startColor.encode(coder, context);
		coder.writeWord(endRatio, 1);
		endColor.encode(coder, context);
	}
}
