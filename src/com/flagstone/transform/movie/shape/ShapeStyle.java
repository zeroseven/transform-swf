/*
 * ShapeStyle.java
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

import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Encoder;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.Movie;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.fillstyle.FillStyle;
import com.flagstone.transform.movie.linestyle.LineStyle;

/**
 * ShapeStyle is used to change the drawing environment when a shape is drawn.
 * Three operations can be performed:
 * 
 * <ul>
 * <li>Select a line style or fill style.</li>
 * <li>Move the current drawing point.</li>
 * <li>Define a new set of line and fill styles.</li>
 * </ul>
 * 
 * <p>An ShapeStyle object can specify one or more of the operations rather than
 * specifying them in separate ShapeStyle objects - compacting the size of the
 * binary data when the object is encoded. Conversely if an operation is not
 * defined then the values may be omitted.</p>
 * 
 * <p>Line and Fill styles are selected by the index position, starting at 1, of
 * the style in an array of styles. An index of zero means that no style is
 * used. Using the constant VALUE_NOT_SET means that the current
 * style is unchanged. Two types of fill style are supported: fillStyle is used
 * where a shape does not contain overlapping areas and altFillStyle is used
 * where areas overlap. This differs from graphics environments that only
 * support one fill style as the overlapping area would form a hole in the shape
 * and not be filled.</p>
 * 
 * <p>A new drawing point is specified using the absolute x and y coordinates. If
 * an ShapeStyle object is the first in a shape then the current drawing point
 * is the origin of the shape (0,0). As with the line and fill styles if no
 * drawing point is set then the x and y coordinates may be set to VALUE_NOT_SET.
 * </p>
 * 
 * <p>Finally the line or fill style arrays may left empty if no new styles are 
 * being specified.
 * </p>
 * 
 * <p>Note that the values for the moveX and moveY attributes and the line and 
 * fill styles arrays are defined in pairs and are optional only if both are set 
 * to VALUE_NOT_SET.</p>
 * 
 */
public final class ShapeStyle implements ShapeRecord
{
	private static final String FORMAT = "ShapeStyle: { move=(%d, %d); fill=%d; alt=%d; line=%d; fillStyles=%s; lineStyles=%s }";
	
	protected int moveX;
	protected int moveY;
	protected int fillStyle;
	protected int altFillStyle;
	protected int lineStyle;
	protected List<FillStyle> fillStyles;
	protected List<LineStyle> lineStyles;
	
	private transient boolean hasStyles;
	private transient boolean hasLine;
	private transient boolean hasAlt;
	private transient boolean hasFill;
	private transient boolean hasMove;

	public ShapeStyle(final SWFDecoder coder) throws CoderException
	{
		int start = coder.getPointer();
		
		int numberOfFillBits = coder.getContext().getFillSize();
		int numberOfLineBits = coder.getContext().getLineSize();

		/* shapeType */coder.readBits(1, false);
		hasStyles = coder.readBits(1, false) != 0;
		hasLine = coder.readBits(1, false) != 0;
		hasAlt = coder.readBits(1, false) != 0;
		hasFill = coder.readBits(1, false) != 0;
		hasMove = coder.readBits(1, false) != 0;

		if (hasMove)
		{
			int moveFieldSize = coder.readBits(5, false);
			moveX = coder.readBits(moveFieldSize, true);
			moveY = coder.readBits(moveFieldSize, true);
		}
		fillStyles = new ArrayList<FillStyle>();
		lineStyles = new ArrayList<LineStyle>();

		fillStyle = hasFill ? coder.readBits(numberOfFillBits, false) : Movie.VALUE_NOT_SET;
		altFillStyle = hasAlt ? coder.readBits(numberOfFillBits, false) : Movie.VALUE_NOT_SET;
		lineStyle = hasLine ? coder.readBits(numberOfLineBits, false) : Movie.VALUE_NOT_SET;

		if (hasStyles)
		{
			coder.alignToByte();

			int fillStyleCount = coder.readByte();

			if (coder.getContext().isArrayExtended() && fillStyleCount == 0xFF) {
				fillStyleCount = coder.readWord(2, false);
			}

			FillStyle fill;
			int type;

			for (int i = 0; i < fillStyleCount; i++) {
				type = coder.scanByte();
				fill = coder.fillStyleOfType(coder);

				if (fill == null) {
					throw new CoderException(String.valueOf(type), start >>> 3, 0, 0, Strings.UNSUPPORTED_FILL_STYLE);
				}

				fillStyles.add(fill);
			}

			int lineStyleCount = coder.readByte();

			if (coder.getContext().isArrayExtended() && lineStyleCount == 0xFF) {
				lineStyleCount = coder.readWord(2, false);
			}

			LineStyle style;

			for (int i = 0; i < lineStyleCount; i++) {
				lineStyles.add(new LineStyle(coder));
			}

			numberOfFillBits = coder.readBits(4, false);
			numberOfLineBits = coder.readBits(4, false);

			coder.getContext().setFillSize(numberOfFillBits);
			coder.getContext().setLineSize(numberOfLineBits);
		}
	}


	/**
	 * Creates a ShapeStyle object, selecting the line and fill styles.
	 * 
	 * @param lineIndex
	 *            selects the line style at lineIndex in the line styles
	 *            array of the parent Shape object.
	 * @param fillIndex
	 *            selects the fill style at fillIndex in the fill styles
	 *            array of the parent Shape object.
	 * @param altIndex
	 *            selects the alternate fill style at altIndex in the
	 *            fill styles array of the parent Shape object.
	 */
	public ShapeStyle(int lineIndex, int fillIndex, int altIndex)
	{
		super();

		moveX = Movie.VALUE_NOT_SET;
		moveY = Movie.VALUE_NOT_SET;
		setLineStyle(lineIndex);
		setFillStyle(fillIndex);
		setAltFillStyle(altIndex);
		fillStyles = new ArrayList<FillStyle>();
		lineStyles = new ArrayList<LineStyle>();
	}

	/**
	 * Creates a ShapeStyle object, selecting the relative drawing point.
	 * 
	 * @param relativeX
	 *            move the current point by relativeX in the x direction.
	 * @param relativeY
	 *            move the current point by relativeY in the y direction.
	 */
	public ShapeStyle(int relativeX, int relativeY)
	{
		super();

		setMoveX(relativeX);
		setMoveY(relativeY);
		lineStyle = Movie.VALUE_NOT_SET;
		fillStyle = Movie.VALUE_NOT_SET;
		altFillStyle = Movie.VALUE_NOT_SET;
		fillStyles = new ArrayList<FillStyle>();
		lineStyles = new ArrayList<LineStyle>();
	}

	/**
	 * Creates a ShapeStyle object, specifying the new set of line and
	 * fill styles for the parent shape.
	 * 
	 * @param lines
	 *            an array of LineStyle objects. Must not be null.
	 * @param fills
	 *            an array of fill style objects. Must not be null.
	 */
	public ShapeStyle(List<LineStyle> lines, List<FillStyle> fills)
	{
		super();

		moveX = Movie.VALUE_NOT_SET;
		moveY = Movie.VALUE_NOT_SET;
		lineStyle = Movie.VALUE_NOT_SET;
		fillStyle = Movie.VALUE_NOT_SET;
		altFillStyle = Movie.VALUE_NOT_SET;
		setLineStyles(lines);
		setFillStyles(fills);
	}

	public ShapeStyle(ShapeStyle object)
	{
		moveX = object.moveX;
		moveY = object.moveY;
		lineStyle = object.lineStyle;
		fillStyle = object.fillStyle;
		altFillStyle = object.altFillStyle;

		lineStyles = new ArrayList<LineStyle>(object.lineStyles.size());
		
		for (LineStyle style : object.lineStyles) {
			lineStyles.add(style.copy());
		}

		fillStyles = new ArrayList<FillStyle>(object.fillStyles.size());
		
		for (FillStyle style : object.fillStyles) {
			fillStyles.add(style.copy());
		}
	}

	/**
	 * Add a LineStyle object to the array of line styles.
	 * 
	 * @param style
	 *            and LineStyle object. Must not be null.
	 */
	public ShapeStyle add(LineStyle style)
	{
		if (style == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		lineStyles.add(style);
		return this;
	}

	/**
	 * Add the fill style object to the array of fill styles.
	 * 
	 * @param style
	 *            and FillStyle object. Must not be null.
	 */
	public ShapeStyle add(FillStyle style)
	{
		if (style == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		fillStyles.add(style);
		return this;
	}

	/**
	 * Returns the x-coordinate of any relative move or VALUE_NOT_SET if no move 
	 * is specified. 
	 */
	public int getMoveX()
	{
		return moveX;
	}

	/**
	 * Returns the y-coordinate of any relative move  or VALUE_NOT_SET if no move 
	 * is specified. 
	 */
	public int getMoveY()
	{
		return moveY;
	}

	/**
	 * Returns the index of the line style that will be applied to any line drawn.
	 * Returns VALUE_NOT_SET if no line style is defined.
	 */
	public int getLineStyle()
	{
		return lineStyle;
	}

	/**
	 * Returns the index of the fill style that will be applied to any area filled.
	 * Returns VALUE_NOT_SET if no fill style is defined.
	 */
	public int getFillStyle()
	{
		return fillStyle;
	}

	/**
	 * Returns the index of the fill style that will be applied to any overlapping
	 * area filled. Returns VALUE_NOT_SET if no alternate fill style is 
	 * defined.
	 */
	public int getAltFillStyle()
	{
		return altFillStyle;
	}

	/**
	 * Returns the array of new line styles.
	 */
	public List<LineStyle> getLineStyles()
	{
		return lineStyles;
	}

	/**
	 * Returns the array of new fill styles.
	 */
	public List<FillStyle> getFillStyles()
	{
		return fillStyles;
	}

	/**
	 * Sets the x-coordinate of any relative move. If the value is set to 
	 * Constants.VALUE_NOT_SET then it will not be encoded.
	 * 
	 * @param coord
	 *            move the current point by aNumber in the x direction. Must be 
	 *            in the range -65535..65535.
	 */
	public void setMoveX(int coord)
	{
		if (coord < -65535 || coord > 65535) {
			throw new IllegalArgumentException(Strings.COORDINATES_OUT_OF_RANGE);
		}
		moveX = coord;
	}

	/**
	 * Sets the y-coordinate of any relative move. If the value is set to 
	 * Constants.VALUE_NOT_SET then it  will not be encoded.
	 * 
	 * @param coord
	 *            move the current point by aNumber in the y direction. Must be 
	 *            in the range -65535..65535.
	 */
	public void setMoveY(int coord)
	{
		if (coord < -65535 || coord > 65535) {
			throw new IllegalArgumentException(Strings.COORDINATES_OUT_OF_RANGE);
		}
		moveY = coord;
	}

	/**
	 * Sets the drawing point. May be set to Constants.VALUE_NOT_SET the object
	 * should be encoded with no move information.
	 * 
	 * @param xCoord
	 *            the x-coordinate of the drawing point. Must be 
	 *            in the range -65535..65535.
	 * @param yCoord
	 *            the y-coordinate of the drawing point. Must be 
	 *            in the range -65535..65535.
	 */
	public void setMove(int xCoord, int yCoord)
	{
		setMoveX(xCoord);
		setMoveY(yCoord);
	}

	/**
	 * Sets the index of the fill style that will be applied to any area filled.
	 * May be set to zero if no style is selected or Constants.VALUE_NOT_SET if 
	 * the line style remains unchanged from a previous value (if any) and should 
	 * not be encoded.
	 * 
	 * @param anIndex
	 *            selects the fill style at anIndex in the fill styles array of
	 *            the parent Shape object.
	 */
	public void setFillStyle(int anIndex)
	{
		fillStyle = anIndex;
	}

	/**
	 * Sets the index of the fill style that will be applied to any overlapping
	 * area filled. May be set to zero if no style is selected or 
	 * Constants.VALUE_NOT_SET if the line style remains unchanged from a previous 
	 * value (if any) and should not be encoded.
	 * 
	 * @param anIndex
	 *            selects the alternate fill style at anIndex in the fill styles
	 *            array of the parent Shape object.
	 */
	public void setAltFillStyle(int anIndex)
	{
		altFillStyle = anIndex;
	}

	/**
	 * Sets the index of the line style that will be applied to any line drawn.
	 * May be set to zero if no style is selected or Constants.VALUE_NOT_SET if 
	 * the line style remains unchanged from a previous value (if any) and should 
	 * not be encoded.
	 * 
	 * @param anIndex
	 *            selects the line style at anIndex in the line styles array of
	 *            the parent Shape object.
	 */
	public void setLineStyle(int anIndex)
	{
		lineStyle = anIndex;
	}

	/**
	 * Sets the array of new line styles. May be set to null if no styles are
	 * being defined.
	 * 
	 * @param anArray
	 *            an array of LineStyle objects. Must not be null.
	 */
	public void setLineStyles(List<LineStyle> anArray)
	{
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		lineStyles = anArray;
	}

	/**
	 * Sets the array of new fill styles. May be set to null if no styles are
	 * being defined.
	 * 
	 * @param anArray
	 *            an array of fill style objects. Must not be null.
	 */
	public void setFillStyles(List<FillStyle> anArray)
	{
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		fillStyles = anArray;
	}

	/**
	 * Creates and returns a deep copy of this object.
	 */
	public ShapeStyle copy()
	{
		return new ShapeStyle(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, moveX, moveY, fillStyle, altFillStyle, lineStyle, fillStyles, lineStyles);
	}

	public int prepareToEncode(final SWFEncoder coder)
	{
		hasLine = lineStyle != Movie.VALUE_NOT_SET;
		hasFill = fillStyle != Movie.VALUE_NOT_SET;
		hasAlt = altFillStyle != Movie.VALUE_NOT_SET;
		hasMove =  moveX != Movie.VALUE_NOT_SET && moveY != Movie.VALUE_NOT_SET;
		hasStyles = !lineStyles.isEmpty() || !fillStyles.isEmpty();
		
		int numberOfBits = 6;

		if (hasMove)
		{
			int _moveFieldSize = Math.max(Encoder.size(moveX), Encoder.size(moveY));
			numberOfBits += 5 + _moveFieldSize * 2;
		}

		numberOfBits += hasFill ? coder.getContext().getFillSize() : 0;
		numberOfBits += hasAlt ? coder.getContext().getFillSize() : 0;
		numberOfBits += (hasLine) ? coder.getContext().getLineSize() : 0;

		coder.getContext().setShapeSize(coder.getContext().getShapeSize()+numberOfBits);

		if (hasStyles)
		{
			int numberOfFillBits = Encoder.unsignedSize(fillStyles.size());
			int numberOfLineBits = Encoder.unsignedSize(lineStyles.size());

			if (numberOfFillBits == 0 && coder.getContext().isPostscript()) {
				numberOfFillBits = 1;
			}

			if (numberOfLineBits == 0 && coder.getContext().isPostscript()) {
				numberOfLineBits = 1;
			}

			boolean countExtended = coder.getContext().isArrayExtended();

			int numberOfStyleBits = 0;
			int flushBits = coder.getContext().getShapeSize();

			numberOfStyleBits += (flushBits % 8 > 0) ? 8 - (flushBits % 8) : 0;
			numberOfStyleBits += (countExtended && fillStyles.size() >= 255) ? 24 : 8;

			for (FillStyle style : fillStyles) {
				numberOfStyleBits += style.prepareToEncode(coder) * 8;
			}

			numberOfStyleBits += (countExtended && lineStyles.size() >= 255) ? 24 : 8;

			for (LineStyle style : lineStyles) {
				numberOfStyleBits += style.prepareToEncode(coder) * 8;
			}

			numberOfStyleBits += 8;

			coder.getContext().setFillSize(numberOfFillBits);
			coder.getContext().setLineSize(numberOfLineBits);
			coder.getContext().setShapeSize(coder.getContext().getShapeSize() + numberOfStyleBits);

			numberOfBits += numberOfStyleBits;
		}
		return numberOfBits;
	}

	public void encode(final SWFEncoder coder) throws CoderException
	{
		coder.writeBits(0, 1);
		coder.writeBits(hasStyles ? 1 : 0, 1);
		coder.writeBits(hasLine ? 1 : 0, 1);
		coder.writeBits(hasAlt ? 1 : 0, 1);
		coder.writeBits(hasFill ? 1 : 0, 1);
		coder.writeBits(hasMove ? 1 : 0, 1);

		if (hasMove)
		{
			int _moveFieldSize = Math.max(Encoder.size(moveX), Encoder.size(moveY));

			coder.writeBits(_moveFieldSize, 5);
			coder.writeBits(moveX, _moveFieldSize);
			coder.writeBits(moveY, _moveFieldSize);
		}

		if (hasFill) {
			coder.writeBits(fillStyle, coder.getContext().getFillSize());
		}

		if (hasAlt) {
			coder.writeBits(altFillStyle, coder.getContext().getFillSize());
		}

		if (hasLine) {
			coder.writeBits(lineStyle, coder.getContext().getLineSize());
		}

		if (hasStyles)
		{
			boolean countExtended = coder.getContext().isArrayExtended();

			coder.alignToByte();

			if (countExtended && fillStyles.size() >= 255)
			{
				coder.writeBits(0xFF, 8);
				coder.writeBits(fillStyles.size(), 16);
			} 
			else
			{
				coder.writeBits(fillStyles.size(), 8);
			}

			for (FillStyle style : fillStyles) {
				style.encode(coder);
			}

			if (countExtended && lineStyles.size() >= 255)
			{
				coder.writeBits(0xFF, 8);
				coder.writeBits(lineStyles.size(), 16);
			} 
			else
			{
				coder.writeBits(lineStyles.size(), 8);
			}

			for (LineStyle style : lineStyles) {
				style.encode(coder);
			}

			int numberOfFillBits = Encoder.unsignedSize(fillStyles.size());
			int numberOfLineBits = Encoder.unsignedSize(lineStyles.size());

			if (coder.getContext().isPostscript()) 
			{
				if (numberOfFillBits == 0) {
					numberOfFillBits = 1;
				}

				if (numberOfLineBits == 0) {
					numberOfLineBits = 1;
				}
			}

			coder.writeBits(numberOfFillBits, 4);
			coder.writeBits(numberOfLineBits, 4);

			// Update the stream with the new numbers of line and fill bits
			coder.getContext().setFillSize(numberOfFillBits);
			coder.getContext().setLineSize(numberOfLineBits);
		}
	}
}
