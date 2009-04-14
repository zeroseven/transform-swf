/*
 * DefineFont.java
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

package com.flagstone.transform.movie.font;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.DefineTag;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.Types;
import com.flagstone.transform.movie.shape.Shape;

/**
 * DefineFont defines the glyphs that are drawn when text characters are
 * rendered in a particular font.
 * 
 * <p>A complete definition of a font is created using the DefineFont object for
 * the glyphs along with an FontInfo or FontInfo2 object which contains the name 
 * of the font, whether the font face is bold or italics and a table that maps
 * character codes to the glyphs that is drawn to represent the character.
 * </p>
 * 
 * <p>When defining a font only the glyphs used from a particular font are
 * included. Unused glyphs can be omitted greatly reducing the amount of
 * information that is encoded.</p>
 * 
 * @see FontInfo
 * @see FontInfo2
 */
public final class DefineFont implements DefineTag
{
	private static final String FORMAT = "DefineFont: { identifier=%d; shapes=%s }";
		
	private int identifier;
	protected List<Shape> shapes;
	
	private transient int start;
	private transient int end;
	private transient int length;

	public DefineFont(final SWFDecoder coder) throws CoderException
	{
		start = coder.getPointer();
		length = coder.readWord(2, false) & 0x3F;
		
		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}
		end = coder.getPointer() + (length << 3);
		
		identifier = coder.readWord(2, false);
		shapes = new ArrayList<Shape>();

		int offsetStart = coder.getPointer();
		int shapeCount = coder.readWord(2, false) / 2;

		coder.setPointer(offsetStart);

		int[] offset = new int[shapeCount + 1]; // NOPMD

		for (int i = 0; i < shapeCount; i++) {
			offset[i] = coder.readWord(2, false); // NOPMD
		}

		offset[shapeCount] = length - 2; // NOPMD
		
		for (int i = 0; i < shapeCount; i++)
		{
			coder.setPointer(offsetStart + (offset[i] << 3));

			shapes.add(new Shape(coder));
		}

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}

	/**
	 * Creates a DefineFont object setting the unique identifier for the
	 * object and the array of glyphs used to render the characters used from
	 * the font.
	 * 
	 * @param uid
	 *            the unique identifier for this object.
	 * @param anArray
	 *            an array of Shape objects that define the outlines for each
	 *            glyph in the font.
	 */
	public DefineFont(int uid, List<Shape> anArray)
	{
		setIdentifier(uid);
		setShapes(anArray);
	}
	
	public DefineFont(DefineFont object)
	{
		identifier = object.identifier;
		shapes = new ArrayList<Shape>(object.shapes.size());
		for (Shape shape : object.shapes) {
			shapes.add(shape.copy());
		}
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
	 * Add a shape to the array of shapes that represent the glyphs for the font.
	 * 
	 * @param obj
	 *            a shape which must not be null.
	 */
	public void add(Shape obj)
	{
		if (obj == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		shapes.add(obj);
	}

	/**
	 * Returns the array of shapes that define the outline for each glyph.
	 */
	public List<Shape> getShapes()
	{
		return shapes;
	}

	/**
	 * Sets the array of shapes that describe each glyph.
	 * 
	 * @param anArray
	 *            an array of Shape objects that define the outlines for each
	 *            glyph in the font. Must not be null.
	 */
	public void setShapes(List<Shape> anArray)
	{
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		shapes = anArray;
	}

	/**
	 * Creates and returns a deep copy of this object.
	 */
	public DefineFont copy() 
	{
		return new DefineFont(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, identifier, shapes);
	}

	public int prepareToEncode(final SWFEncoder coder)
	{
		length = 2;
		
		coder.getContext().setFillSize(1);
		coder.getContext().setLineSize(coder.getContext().isPostscript() ? 1 : 0);

		length += shapes.size() * 2;

		for (Shape shape : shapes) {
			length += shape.prepareToEncode(coder);
		}

		coder.getContext().setFillSize(0);
		coder.getContext().setLineSize(0);

		return (length > 62 ? 6:2) + length;
	}

	public void encode(final SWFEncoder coder) throws CoderException
	{
		start = coder.getPointer();
		
		if (length >= 63) {
			coder.writeWord((Types.DEFINE_FONT << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((Types.DEFINE_FONT << 6) | length, 2);
		}
		end = coder.getPointer() + (length << 3);
		coder.writeWord(identifier, 2);
		
		coder.getContext().setFillSize(1);
		coder.getContext().setLineSize(coder.getContext().isPostscript() ? 1 : 0);

		int currentLocation;
		int offset;

		int tableStart = coder.getPointer();

		for (int i = 0; i < shapes.size(); i++) {
			coder.writeWord(0, 2);
		}

		int tableEntry = tableStart; // NOPMD

		for (Iterator<Shape> i = shapes.iterator(); i.hasNext(); tableEntry += 16)
		{
			currentLocation = coder.getPointer();
			offset = (coder.getPointer() - tableStart) >> 3;

			coder.setPointer(tableEntry);
			coder.writeWord(offset, 2);
			coder.setPointer(currentLocation);

			i.next().encode(coder);
		}

		coder.getContext().setFillSize(0);
		coder.getContext().setLineSize(0);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}
}
