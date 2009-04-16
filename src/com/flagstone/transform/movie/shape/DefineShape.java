/*
 * DefineShape.java
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
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.DefineTag;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.Types;
import com.flagstone.transform.movie.datatype.Bounds;
import com.flagstone.transform.movie.fillstyle.FillStyle;
import com.flagstone.transform.movie.linestyle.LineStyle;

/**
 * DefineShape defines a shape to be displayed.
 * 
 * <p>The shape defines a path containing a mix of straight and curved edges and
 * pen move actions. A path need not be contiguous. When the shape is drawn the
 * ShapeStyle object selects the line and fill styles, from the respective
 * array, to be used. ShapeStyle objects can be defined in the shape at any
 * time to change the styles being used. The fill style used can either be a
 * solid colour, a bitmap image or a gradient. The line style specifies the
 * colour and thickness of the line drawn around the shape outline.
 * </p>
 * 
 * <p>For both line and fill styles the selected style may be undefined, allowing
 * the shape to be drawn without an outline or left unfilled.</p>
 * 
 * @see DefineShape2
 * @see DefineShape3
 */
public final class DefineShape implements DefineTag
{
	private static final String FORMAT = "DefineShape: { identifier=%d; bounds=%s; fillStyles=%s; lineStyles=%s; shape=%s }";

	private int identifier;
	protected Bounds bounds;
	protected List<FillStyle> fillStyles;
	protected List<LineStyle> lineStyles;
	protected Shape shape;
	
	private transient int start;
	private transient int end;
	private transient int length;
	private transient int fillBits;
	private transient int lineBits;

	public DefineShape(final SWFDecoder coder, final SWFContext context) throws CoderException
	{
		start = coder.getPointer();
		length = coder.readWord(2, false) & 0x3F;
		
		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}
		end = coder.getPointer() + (length << 3);

		identifier = coder.readWord(2, false);
		bounds = new Bounds(coder);
		
		fillStyles = new ArrayList<FillStyle>();
		lineStyles = new ArrayList<LineStyle>();

		int fillStyleCount = coder.readByte();
		
		FillStyle fill;
		int type;

		for (int i = 0; i < fillStyleCount; i++) {
			
			type = coder.scanByte();
			fill = context.fillStyleOfType(coder, context);

			if (fill == null) {
				throw new CoderException(String.valueOf(type), start >>> 3, 0, 0, Strings.UNSUPPORTED_FILL_STYLE);
			}

			fillStyles.add(fill);
		}

		int lineStyleCount = coder.readByte();

		for (int i = 0; i < lineStyleCount; i++) {
			lineStyles.add(new LineStyle(coder, context));
		}

		shape = new Shape(coder, context);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}


	/**
	 * Creates a DefineShape object.
	 * 
	 * @param uid
	 *            the unique identifier for the shape in the range 1..65535.
	 * @param aBounds
	 *            the bounding rectangle for the shape. Must not be null.
	 * @param fillStyleArray
	 *            the array of fill styles used in the shape. Must not be null.
	 * @param lineStyleArray
	 *            the array of line styles used in the shape. Must not be null.
	 * @param aShape
	 *            the shape to be drawn. Must not be null.
	 */
	public DefineShape(int uid, Bounds aBounds,
							List<FillStyle> fillStyleArray, 
							List<LineStyle> lineStyleArray,
							Shape aShape)
	{
		setIdentifier(uid);
		setBounds(aBounds);
		setFillStyles(fillStyleArray);
		setLineStyles(lineStyleArray);
		setShape(aShape);
	}
	
	public DefineShape(DefineShape object)
	{
		identifier = object.identifier;
		bounds = object.bounds;
		fillStyles = new ArrayList<FillStyle>(object.fillStyles.size());
		for (FillStyle style : object.fillStyles) {
			fillStyles.add(style.copy());
		}
		lineStyles = new ArrayList<LineStyle>(object.lineStyles.size());
		for (LineStyle style : object.lineStyles) {
			lineStyles.add(style.copy());
		}
		shape = object.shape.copy();
	}

	public int getIdentifier() {
		return identifier;
	}

	public void setIdentifier(final int uid) {
		if (uid < 0 || uid > 65535) {
			throw new IllegalArgumentException(Strings.IDENTIFIER_OUT_OF_RANGE);
		}
		identifier = uid;
	}

	/**
	 * Returns the width of the shape in twips.
	 */
	public int getWidth() 
	{
		return bounds.getWidth();
	}

	/**
	 * Returns the height of the shape in twips.
	 */
	public int getHeight()
	{
		return bounds.getHeight();
	}
	
	/**
	 * Add a LineStyle to the array of line styles.
	 * 
	 * @param style
	 *            and LineStyle object. Must not be null.
	 */
	public DefineShape add(LineStyle style)
	{
		if (style == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		lineStyles.add(style);
		return this;
	}

	/**
	 * Add the fill style to the array of fill styles.
	 * 
	 * @param style
	 *            and FillStyle object. Must not be null.
	 */
	public DefineShape add(FillStyle style)
	{
		if (style == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		fillStyles.add(style);
		return this;
	}

	/**
	 * Returns the bounding rectangle for the shape.
	 */
	public Bounds getBounds()
	{
		return bounds;
	}

	/**
	 * Returns the array fill styles.
	 */
	public List<FillStyle> getFillStyles()
	{
		return fillStyles;
	}

	/**
	 * Returns the array line styles.
	 */
	public List<LineStyle> getLineStyles()
	{
		return lineStyles;
	}

	/**
	 * Returns the shape.
	 */
	public Shape getShape()
	{
		return shape;
	}

	/**
	 * Sets the bounding rectangle that encloses the shape.
	 * 
	 * @param aBounds
	 *            set the bounding rectangle for the shape. Must not be null.
	 */
	public void setBounds(Bounds aBounds)
	{
		if (aBounds == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		bounds = aBounds;
	}

	/**
	 * Sets the array fill styles that will be used to draw the shape.
	 * 
	 * @param anArray
	 *            set the fill styles for the shape. Must not be null.
	 */
	public void setFillStyles(List<FillStyle> anArray)
	{
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		fillStyles = anArray;
	}

	/**
	 * Sets the array of styles that will be used to draw the outline of the 
	 * shape.
	 * 
	 * @param anArray
	 *            set the line styles for the shape. Must not be null.
	 */
	public void setLineStyles(List<LineStyle> anArray)
	{
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		lineStyles = anArray;
	}

	/**
	 * Sets the shape.
	 * 
	 * @param aShape
	 *            set the shape to be drawn. Must not be null.
	 */
	public void setShape(Shape aShape)
	{
		if (aShape == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		shape = aShape;
	}

	/**
	 * Creates and returns a deep copy of this object.
	 */
	public DefineShape copy() 
	{
		return new DefineShape(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, identifier, bounds, fillStyles, lineStyles, shape);
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context)
	{
		fillBits = Encoder.unsignedSize(fillStyles.size());
		lineBits = Encoder.unsignedSize(lineStyles.size());
		
		if (context.isPostscript()) 
		{
			if (fillBits == 0) {
				fillBits = 1;
			}

			if (lineBits == 0) {
				lineBits = 1;
			}
		}

		length = 2 + bounds.prepareToEncode(coder, context);
		length += 1;

		for (FillStyle style : fillStyles) {
			length += style.prepareToEncode(coder, context);
		}

		length += 1;

		for (LineStyle style : lineStyles) {
			length += style.prepareToEncode(coder, context);
		}

		context.setFillSize(fillBits);
		context.setLineSize(lineBits);

		length += shape.prepareToEncode(coder, context);

		context.setFillSize(0);
		context.setLineSize(0);

		return (length > 62 ? 6:2) + length;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
		start = coder.getPointer();

		if (length >= 63) {
			coder.writeWord((Types.DEFINE_SHAPE << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((Types.DEFINE_SHAPE << 6) | length, 2);
		}
		end = coder.getPointer() + (length << 3);

		coder.writeWord(identifier, 2);
		bounds.encode(coder, context);

		coder.writeWord(fillStyles.size(), 1);

		for (FillStyle style : fillStyles) {
			style.encode(coder, context);
		}

		coder.writeWord(lineStyles.size(), 1);

		for (LineStyle style : lineStyles) {
			style.encode(coder, context);
		}

		context.setFillSize(fillBits);
		context.setLineSize(lineBits);

		shape.encode(coder, context);

		context.setFillSize(0);
		context.setLineSize(0);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}
}
