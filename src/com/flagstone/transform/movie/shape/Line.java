/*
 * Line.java
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

package com.flagstone.transform.movie.shape;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Encoder;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.linestyle.LineStyle;

//TODO(doc) Review
/**
 * Line defines a straight line. The line is drawn from the current drawing
 * point to the end point specified in the Line object which is specified
 * relative to the current drawing point. Once the line is drawn, the end of the
 * line is now the current drawing point.
 * 
 * <p>Lines are drawn with rounded corners and line ends. Different join and line
 * end styles can be created by drawing line segments as a sequence of filled
 * shapes. With 1 twip equal to 1/20th of a pixel this technique can easily be
 * used to draw the narrowest of visible lines. In flash 8, SolidLine2 line style
 * was added that supports a range of different mitering options.</p>
 * 
 * @see LineStyle
 */
public final class Line implements ShapeRecord
{
	private static final String FORMAT = "Line: (%d, %d);";
	
	private int xCoord;
	private int yCoord;
	
	private transient boolean vertical;
	private transient boolean general;
	private transient int size;

	//TODO(doc)
	//TODO(optimise)
	//TODO(api) Reduce number of set methods
	public Line(final SWFDecoder coder, final SWFContext context) throws CoderException
	{
		coder.adjustPointer(2); // shape and edge

		size = coder.readBits(4, false) + 2;

		if (coder.readBits(1, false) == 0)
		{
			if (coder.readBits(1, false) == 0)
			{
				xCoord = coder.readBits(size, true);
				yCoord = 0;
			} 
			else
			{
				xCoord = 0;
				yCoord = coder.readBits(size, true);
			}
		} 
		else
		{
			xCoord = coder.readBits(size, true);
			yCoord = coder.readBits(size, true);
		}
	}

	/**
	 * Creates a Line with the specified relative coordinates.
	 * 
	 * @param xCoord
	 *            the x-coordinate of the end point, specified relative to the
	 *            current drawing point. Must be in the range -65536..65535.
	 * @param yCoord
	 *            the y-coordinate of the end point, specified relative to the
	 *            current drawing point. Must be in the range -65536..65535.
	 */
	public Line(int xCoord, int yCoord)
	{
		super();
		
		setX(xCoord);
		setY(yCoord);
	}
	
	//TODO(doc)
	public Line(Line object) {
		xCoord = object.xCoord;
		yCoord = object.yCoord;
	}

	/**
	 * Returns the relative x-coordinate.
	 */
	public int getX()
	{
		return xCoord;
	}

	/**
	 * Returns the relative y-coordinate.
	 */
	public int getY()
	{
		return yCoord;
	}

	/**
	 * Sets the relative x-coordinate.
	 * 
	 * @param coord
	 *            the x-coordinate of the end point. Must be in the range -65536..65535.
	 */
	public void setX(int coord)
	{
		if (coord < -65536 || coord > 65535) {
			throw new IllegalArgumentException(Strings.COORDINATES_OUT_OF_RANGE);
		}
		xCoord = coord;
	}

	/**
	 * Sets the relative y-coordinate.
	 * 
	 * @param coord
	 *            the y-coordinate of the end point. Must be in the range -65536..65535.
	 */
	public void setY(int coord)
	{
		if (coord < -65536 || coord > 65535) {
			throw new IllegalArgumentException(Strings.COORDINATES_OUT_OF_RANGE);
		}
		yCoord = coord;
	}

	/**
	 * Sets the relative x and y coordinates.
	 * 
	 * @param xCoord
	 *            the x-coordinate of the end point. Must be in the range -65536..65535.
	 * @param yCoord
	 *            the y-coordinate of the end point. Must be in the range -65536..65535.
	 */
	public void setPoint(int xCoord, int yCoord)
	{
		setX(xCoord);
		setY(yCoord);
	}

	public Line copy()
	{
		return new Line(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, xCoord, yCoord);
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context)
	{
		vertical = xCoord == 0;
		general = xCoord != 0 && yCoord != 0;
		size = Encoder.maxSize(xCoord, yCoord, 1);

		int numberOfBits = 7;

		if (general) {
			numberOfBits += size << 1;
		}
		else {
			numberOfBits += 1 + size;
		}

		context.setShapeSize(context.getShapeSize()+numberOfBits);
		
		return numberOfBits;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
		coder.writeBits(3, 2);
		coder.writeBits(size - 2, 4);
		coder.writeBits(general ? 1 : 0, 1);

		if (general)
		{
			coder.writeBits(xCoord, size);
			coder.writeBits(yCoord, size);
		} else
		{
			coder.writeBits(vertical ? 1 : 0, 1);
			coder.writeBits(vertical ? yCoord : xCoord, size);
		}
	}
}
