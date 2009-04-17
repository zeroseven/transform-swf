/*
 * LineStyle.java
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

package com.flagstone.transform.movie.linestyle;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.Encodeable;
import com.flagstone.transform.movie.Copyable;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.datatype.Color;
import com.flagstone.transform.movie.shape.Line;

/**
 * LineStyle defines the width and colour of a line that is used when drawing
 * the outline of a shape.
 * 
* <p>All lines are drawn with rounded corners and end caps. Different join and
 * line end styles can be created by drawing line segments as a sequence of
 * filled shapes. With 1 twip equal to 1/20th of a pixel this technique can
 * easily be used to draw the narrowest of visible lines.
 * </p>
 * 
 * <p>Whether the alpha channel in the colour is used is determined by the class
 * used to define the shape. Transparent colours are only supported from Flash 3
 * onwards. Simply specifying the level of transparency in the Color object is
 * not sufficient.</p>
 * 
 * <p>Flash only supports contiguous lines. Dashed line styles can be created by
 * drawing the line as a series of short line segments by interspersing
 * ShapeStyle objects to move the current point in between the Line objects
 * that draw the line segments.
 * </p>
 * 
 * @see Line
 */
public final class LineStyle implements Encodeable, Copyable<LineStyle>
{
	private static final String FORMAT = "LineStyle : { width=%d; color=%s }";
	
	private int width;
	private Color color;

	public LineStyle(final SWFDecoder coder, final SWFContext context) throws CoderException
	{
		width = coder.readWord(2, false);
		color = new Color(coder, context);
	}

	/**
	 * Creates a LineStyle, specifying the width and colour of the line.
	 * 
	 * @param aWidth
	 *            the width of the line. Must be in the range 0..65535.
	 * @param aColor
	 *            the colour of the line. Must not be null.
	 */
	public LineStyle(int aWidth, Color aColor)
	{
		setWidth(aWidth);
		setColor(aColor);
	}
	
	public LineStyle(LineStyle object) {
		width = object.width;
		color = object.color;
	}

	/**
	 * Returns the width of the line.
	 */
	public int getWidth()
	{
		return width;
	}

	/**
	 * Returns the colour of the line.
	 */
	public Color getColor()
	{
		return color;
	}

	/**
	 * Sets the width of the line.
	 * 
	 * @param aNumber
	 *            the width of the line. Must be in the range 0..65535.
	 */
	public void setWidth(int aNumber)
	{
		if (aNumber < 0 || aNumber > 65535) {
			throw new IllegalArgumentException(Strings.UNSIGNED_VALUE_OUT_OF_RANGE);
		}
		width = aNumber;
	}

	/**
	 * Sets the colour of the line.
	 * 
	 * @param aColor
	 *            the colour of the line. Must be not be null.
	 */
	public void setColor(Color aColor)
	{
		if (aColor == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		color = aColor;
	}

	public LineStyle copy() 
	{
		return new LineStyle(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, width, color);
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context)
	{
		int length = 2;

		length += context.isTransparent() ? 4:3;

		return length;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
		coder.writeWord(width, 2);
		color.encode(coder, context);
	}
}
