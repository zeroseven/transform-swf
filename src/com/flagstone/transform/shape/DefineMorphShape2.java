/*
 * DefineMorphShape.java
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

package com.flagstone.transform.shape;

import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.Bounds;
import com.flagstone.transform.DefineTag;
import com.flagstone.transform.MovieTypes;
import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Encoder;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.fillstyle.FillStyle;
import com.flagstone.transform.linestyle.MorphLineStyle2;

//TODO(doc) Review
/**
 * DefineMorphShape defines a shape that will morph from one form into
 * another.
 * 
 * <p>Only the start and end shapes are defined the Flash Player will perform the
 * interpolation that transforms the shape at each staging in the morphing
 * process.</p>
 * 
 * <p>Morphing can be applied to any shape, however there are a few restrictions:
 * </p>
 * 
 * <ul>
 * <li>The start and end shapes must have the same number of edges (Line and
 * Curve objects).</li>
 * <li>The fill style (Solid, Bitmap or Gradient) must be the same in the start
 * and end shape.</li>
 * <li>If a bitmap fill style is used then the same image must be used in the
 * start and end shapes.</li>
 * <li>If a gradient fill style is used then the gradient must contain the same
 * number of points in the start and end shape.</li>
 * <li>The start and end shape must contain the same set of ShapeStyle
 * objects.</li>
 * </ul>
 * 
 * <p>To perform the morphing of a shape the shape is placed in the display list
 * using a PlaceObject2 object. The ratio attribute in the PlaceObject2 object 
 * defines the progress of the morphing process. The ratio ranges between 0 and 
 * 65535 where 0 represents the start of the morphing process and 65535, the 
 * end.</p>
 * 
 * <p>The edges in the shapes may change their type when a shape is morphed. Straight 
 * edges can become curves and vice versa.</p>
 * 
 */
public final class DefineMorphShape2 implements DefineTag
{	
	private static final String FORMAT = "DefineMorphShape2: { identifier=%d; startShapeBounds=%s; endShapeBounds=%s; startEdgeBounds=%s; endEdgeBounds=%s; fillStyles=%s; lineStyles=%s; startShape=%s; endShape=%s }";

	private int identifier;
	private Bounds startShapeBounds;
	private Bounds endShapeBounds;
	private Bounds startEdgeBounds;
	private Bounds endEdgeBounds;

	private List<FillStyle> fillStyles;
	private List<MorphLineStyle2> lineStyles;

	private Shape startShape;
	private Shape endShape;
	
	private transient int start;
	private transient int end;
	private transient int length;
	private transient int fillBits;
	private transient int lineBits;
	private transient boolean scaling;

	//TODO(doc)
	public DefineMorphShape2(final SWFDecoder coder, final SWFContext context) throws CoderException
	{
		start = coder.getPointer();
		length = coder.readWord(2, false) & 0x3F;
		
		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}
		end = coder.getPointer() + (length << 3);
		identifier = coder.readWord(2, false);

		context.setTransparent(true);
		context.setArrayExtended(true);

		startShapeBounds = new Bounds(coder);
		endShapeBounds = new Bounds(coder);
		startEdgeBounds = new Bounds(coder);
		endEdgeBounds = new Bounds(coder);

		fillStyles = new ArrayList<FillStyle>();
		lineStyles = new ArrayList<MorphLineStyle2>();
		coder.readByte();
		
		int offset = coder.readWord(4, false); //TODO(code) fix
		int first = coder.getPointer(); //TODO(code) fix

		int fillStyleCount = coder.readByte();

		if (context.isArrayExtended() && fillStyleCount == 0xFF) {
			fillStyleCount = coder.readWord(2, false);
		}
		
		FillStyle fillStyle;
		int type;

		for (int i = 0; i < fillStyleCount; i++) {
			type = coder.scanByte();
			fillStyle = context.morphFillStyleOfType(coder, context);

			if (fillStyle == null) {
				throw new CoderException(String.valueOf(type), start >>> 3, 0, 0, Strings.UNSUPPORTED_FILL_STYLE);
			}

			fillStyles.add(fillStyle);
		}

		int lineStyleCount = coder.readByte();

		if (context.isArrayExtended() && lineStyleCount == 0xFF) {
			lineStyleCount = coder.readWord(2, false);
		}

		for (int i = 0; i < lineStyleCount; i++) {
			lineStyles.add(new MorphLineStyle2(coder, context));
		}

		if (context.isDecodeShapes()) {
			startShape = new Shape(coder, context);
			endShape = new Shape(coder, context);
		}
		else {
			startShape = new Shape(offset - ((coder.getPointer()-first) >> 3), coder, context);			
			endShape = new Shape(length - ((coder.getPointer()-start) >> 3), coder, context);
		}

		context.setTransparent(false);
		context.setArrayExtended(false);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}

	/**
	 * Creates a DefineMorphShape object.
	 * 
	 * @param uid
	 *            an unique identifier for this object. Must be in the range 1..65535.
	 * @param startBounds
	 *            the bounding rectangle enclosing the start shape. Must not be null.
	 * @param endBounds
	 *            the bounding rectangle enclosing the end shape. Must not be null.
	 * @param fills
	 *            an array of MorphSolidFill, MorphBitmapFill and
	 *            MorphGradientFill objects. Must not be null.
	 * @param lines
	 *            an array of MorphLineStyle objects. Must not be null.
	 * @param startShape
	 *            the shape at the start of the morphing process. Must not be null.
	 * @param endShape
	 *            the shape at the end of the morphing process. Must not be null.
	 */
	public DefineMorphShape2(int uid, Bounds startBounds, Bounds endBounds, 
								List<FillStyle> fills, List<MorphLineStyle2> lines, 
								Shape startShape, Shape endShape)
	{
		setIdentifier(uid);
		setStartShapeBounds(startBounds);
		setEndShapeBounds(endBounds);
		setFillStyles(fills);
		setLineStyles(lines);
		setStartShape(startShape);
		setEndShape(endShape);
	}
	
	//TODO(doc)
	public DefineMorphShape2(DefineMorphShape2 object)
	{
		identifier = object.identifier;
		startShapeBounds = object.startShapeBounds;
		endShapeBounds = object.endShapeBounds;
		startEdgeBounds = object.startEdgeBounds;
		endEdgeBounds = object.endEdgeBounds;
		fillStyles = new ArrayList<FillStyle>(object.fillStyles.size());
		for (FillStyle style : object.fillStyles) {
			fillStyles.add(style.copy());
		}
		lineStyles = new ArrayList<MorphLineStyle2>(object.lineStyles.size());
		for (MorphLineStyle2 style : object.lineStyles) {
			lineStyles.add(style.copy());
		}
		startShape = object.startShape.copy();
		endShape = object.endShape.copy();
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
	 * Returns the width of the shape at the start of the morphing process.
	 */
	public int getWidth()
	{
		return startShapeBounds.getWidth();
	}
	
	/**
	 * Returns the height of the shape at the start of the morphing process.
	 */
	public int getHeight()
	{
		return startShapeBounds.getHeight();
	}
	
	/**
	 * Add a LineStyle object to the array of line styles.
	 * 
	 * @param aLineStyle
	 *            and LineStyle object. Must not be null.
	 */
	public DefineMorphShape2 add(MorphLineStyle2 aLineStyle)
	{
		lineStyles.add(aLineStyle);
		return this;
	}

	/**
	 * Add the fill style object to the array of fill styles.
	 * 
	 * @param aFillStyle
	 *            an FillStyle object. Must not be null.
	 */
	public DefineMorphShape2 add(FillStyle aFillStyle)
	{
		fillStyles.add(aFillStyle);
		return this;
	}

	/**
	 * Returns the Bounds object that defines the bounding rectangle enclosing
	 * the start shape.
	 */
	public Bounds getStartShapeBounds()
	{
		return startShapeBounds;
	}

	/**
	 * Returns the Bounds object that defines the bounding rectangle enclosing
	 * the end shape.
	 */
	public Bounds getEndShapeBounds()
	{
		return endShapeBounds;
	}
	
	/**
	 * Returns the Bounds object that defines the bounding rectangle enclosing
	 * the start shape.
	 */
	public Bounds getStartEdgeBounds()
	{
		return startEdgeBounds;
	}

	/**
	 * Returns the Bounds object that defines the bounding rectangle enclosing
	 * the end shape.
	 */
	public Bounds getEndEdgeBounds()
	{
		return endEdgeBounds;
	}


	/**
	 * Returns the array of fill styles (MorphSolidFill, MorphBitmapFill and
	 * MorphGradientFill objects) for the shapes.
	 */
	public List<FillStyle> getFillStyles()
	{
		return fillStyles;
	}

	/**
	 * Returns the array of line styles (MorphLineStyle objects) for the shapes.
	 */
	public List<MorphLineStyle2> getLineStyles()
	{
		return lineStyles;
	}

	/**
	 * Returns the starting shape.
	 */
	public Shape getStartShape()
	{
		return startShape;
	}

	/**
	 * Returns the ending shape.
	 */
	public Shape getEndShape()
	{
		return endShape;
	}

	/**
	 * Sets the starting bounds of the shape.
	 * 
	 * @param aBounds
	 *            the bounding rectangle enclosing the start shape. Must not be null.
	 */
	public void setStartShapeBounds(Bounds aBounds)
	{
		if (aBounds == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		startShapeBounds = aBounds;
	}

	/**
	 * Sets the ending bounds of the shape.
	 * 
	 * @param aBounds
	 *            the bounding rectangle enclosing the end shape. Must not be null.
	 */
	public void setEndShapeBounds(Bounds aBounds)
	{
		if (aBounds == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		endShapeBounds = aBounds;
	}

	/**
	 * Sets the starting bounds of the shape.
	 * 
	 * @param aBounds
	 *            the bounding rectangle enclosing the start shape. Must not be null.
	 */
	public void setStartEdgeBounds(Bounds aBounds)
	{
		if (aBounds == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		startEdgeBounds = aBounds;
	}

	/**
	 * Sets the ending bounds of the shape.
	 * 
	 * @param aBounds
	 *            the bounding rectangle enclosing the end shape. Must not be null.
	 */
	public void setEndEdgeBounds(Bounds aBounds)
	{
		if (aBounds == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		endEdgeBounds = aBounds;
	}

	/**
	 * Sets the array of morph fill styles.
	 * 
	 * @param anArray
	 *            an array of MorphSolidFill, MorphBitmapFill and
	 *            MorphGradientFill objects. Must not be null.
	 */
	public void setFillStyles(List<FillStyle> anArray)
	{
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		fillStyles = anArray;
	}

	/**
	 * Sets the array of morph line styles.
	 * 
	 * @param anArray
	 *            an array of MorphLineStyle objects. Must not be null.
	 */
	public void setLineStyles(List<MorphLineStyle2> anArray)
	{
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		lineStyles = anArray;
	}

	/**
	 * Sets the shape that will be displayed at the start of the morphing process.
	 * 
	 * @param aShape
	 *            the shape at the start of the morphing process. Must not be null.
	 */
	public void setStartShape(Shape aShape)
	{
		if (aShape == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		startShape = aShape;
	}

	/**
	 * Sets the shape that will be displayed at the end of the morphing process.
	 * 
	 * @param aShape
	 *            the shape at the end of the morphing process. Must not be null.
	 */
	public void setEndShape(Shape aShape)
	{
		if (aShape == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		endShape = aShape;
	}

	/**
	 * Creates and returns a deep copy of this object.
	 */
	public DefineMorphShape2 copy() 
	{
		return new DefineMorphShape2(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, identifier, startShapeBounds, endShapeBounds,
				startEdgeBounds, endEdgeBounds, fillStyles, lineStyles, startShape, endShape);
	}

	//TODO(optimise)
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

		context.setTransparent(true);

		length = 3;
		length += startShapeBounds.prepareToEncode(coder, context);
		length += endShapeBounds.prepareToEncode(coder, context);
		length += startEdgeBounds.prepareToEncode(coder, context);
		length += endEdgeBounds.prepareToEncode(coder, context);
		length += 4;

		length += (fillStyles.size() >= 255) ? 3 : 1;

		for (FillStyle style : fillStyles) {
			length += style.prepareToEncode(coder, context);
		}

		context.setScalingStroke(false);
		
		length += (lineStyles.size() >= 255) ? 3 : 1;

		for (MorphLineStyle2 style : lineStyles) {
			length += style.prepareToEncode(coder, context);
		}

		scaling = context.isScalingStoke();

		context.setArrayExtended(true);
		context.setFillSize(fillBits);
		context.setLineSize(lineBits);

		length += startShape.prepareToEncode(coder, context);

		// Number of Fill and Line bits is zero for end shape.
		context.setFillSize(0);
		context.setLineSize(0);

		length += endShape.prepareToEncode(coder, context);

		context.setArrayExtended(false);
		context.setTransparent(false);
		context.setScalingStroke(false);

		return (length > 62 ? 6:2) + length;
	}

	//TODO(optimise)
	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
		start = coder.getPointer();

		if (length >= 63) {
			coder.writeWord((MovieTypes.DEFINE_MORPH_SHAPE << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((MovieTypes.DEFINE_MORPH_SHAPE << 6) | length, 2);
		}
		end = coder.getPointer() + (length << 3);

		coder.writeWord(identifier, 2);
		context.setTransparent(true);

		startShapeBounds.encode(coder, context);
		endShapeBounds.encode(coder, context);
		startEdgeBounds.encode(coder, context);
		endEdgeBounds.encode(coder, context);

		coder.writeByte(scaling ? 1:2);
		
		int offsetStart = coder.getPointer();
		coder.writeWord(0, 4);

		if (fillStyles.size() >= 255)
		{
			coder.writeWord(0xFF, 1);
			coder.writeWord(fillStyles.size(), 2);
		} else
		{
			coder.writeWord(fillStyles.size(), 1);
		}

		for (FillStyle style : fillStyles) {
			style.encode(coder, context);
		}

		if (lineStyles.size() >= 255)
		{
			coder.writeWord(0xFF, 1);
			coder.writeWord(lineStyles.size(), 2);
		} 
		else
		{
			coder.writeWord(lineStyles.size(), 1);
		}

		for (MorphLineStyle2 style : lineStyles) {
			 style.encode(coder, context);
		}

		context.setArrayExtended(true);
		context.setFillSize(fillBits);
		context.setLineSize(lineBits);

		startShape.encode(coder, context);

		int offsetEnd = (coder.getPointer() - offsetStart) >> 3;
		int currentCursor = coder.getPointer();

		coder.setPointer(offsetStart);
		coder.writeWord(offsetEnd - 4, 4);
		coder.setPointer(currentCursor);

		// Number of Fill and Line bits is zero for end shape.

		context.setFillSize(0);
		context.setLineSize(0);

		endShape.encode(coder, context);

		context.setArrayExtended(false);
		context.setTransparent(false);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}
}
