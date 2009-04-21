/*
 * Shape.java
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

import com.flagstone.transform.Encodeable;
import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.font.DefineFont;

/**
 * Shape is a container class for the shape objects (Line, Curve and ShapeStyle 
 * objects) that describe how a particular shape is drawn.
 * 
 * <p>Shapes are used in shape and font definitions. The Shape class is used to
 * simplify the design of these classes and provides no added functionality
 * other than acting as a container class.
 * </p>
 * 
 * @see DefineShape
 * @see DefineFont
 */
public final class Shape implements Encodeable
{
	private static final String FORMAT = "Shape: { records=%s }";
	
	private List<ShapeRecord> objects;
	
	private transient int length;

	//TODO(doc)
	//TODO(code) Check this method is still needed
	public Shape(int length)
	{
		/*
		 * This test is used to overcome a bug in SWFTool's pdf2swf where empty
		 * glyphs are only encoded using 1 byte - should be 2.
		 */
		this.length = length;
	}

	//TODO(doc)
	public Shape(final SWFDecoder coder, final SWFContext context) throws CoderException
	{
		objects = new ArrayList<ShapeRecord>();

		if (context.isDecodeShapes())
		{
			context.setFillSize(coder.readBits(4, false));
			context.setLineSize(coder.readBits(4, false));

			int type;
			ShapeRecord shape;

			do {
				type = coder.readBits(6, false);

				if (type != 0) {

					coder.adjustPointer(-6);

					if ((type & 0x20) > 0) {
						if ((type & 0x10) > 0) {
							shape = new Line(coder, context); //TODO(code) fix
						} else {
							shape = new Curve(coder, context); //TODO(code) fix
						}
					} else {
						shape = new ShapeStyle(coder, context); //TODO(code) fix
					}
					objects.add(shape);
				}
			} while (type != 0);

			coder.alignToByte();
		} 
		else
		{
			objects.add(new ShapeData(coder.readBytes(new byte[length])));
		}
	}

	//TODO(doc)
	public Shape(int length, final SWFDecoder coder, SWFContext context) throws CoderException
	{
		this.length = length;
		objects = new ArrayList<ShapeRecord>();

		if (context.isDecodeShapes())
		{
			context.setFillSize(coder.readBits(4, false));
			context.setLineSize(coder.readBits(4, false));

			int type;
			ShapeRecord shape;

			do {
				type = coder.readBits(6, false);

				if (type != 0) {

					coder.adjustPointer(-6);

					if ((type & 0x20) > 0) {
						if ((type & 0x10) > 0) {
							shape = new Line(coder, context); //TODO(code) fix
						} else {
							shape = new Curve(coder, context); //TODO(code) fix
						}
					} else {
						shape = new ShapeStyle(coder, context); //TODO(code) fix
					}
					objects.add(shape);
				}
			} while (type != 0);

			coder.alignToByte();
		} 
		else
		{
			objects.add(new ShapeData(coder.readBytes(new byte[length])));
		}
	}

	//TODO(doc)
	public Shape()
	{
		objects = new ArrayList<ShapeRecord>();
	}

	/**
	 * Creates a Shape object, specifying the Objects that describe how
	 * the shape is drawn.
	 * 
	 * @param anArray
	 *            the array of shape records. Must not be null.
	 */
	public Shape(List<ShapeRecord> anArray)
	{
		setObjects(anArray);
	}

	//TODO(doc)
	public Shape(Shape object)
	{
		objects = new ArrayList<ShapeRecord>(object.objects.size());
		
		for (ShapeRecord record : object.objects) {
			objects.add(record.copy());
		}
	}
	
	/**
	 * Adds the object to the array of shape records.
	 * 
	 * @param anObject
	 *            an instance of ShapeStyle, Line or Curve. Must not be null.
	 */
	public Shape add(ShapeRecord anObject)
	{
		if (anObject == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		objects.add(anObject);
		return this;
	}

	/**
	 * Returns the array of shape records that define the shape.
	 */
	public List<ShapeRecord> getObjects()
	{
		return objects;
	}

	/**
	 * Sets the array of shape records.
	 * 
	 * @param anArray
	 *            the array of shape records. Must not be null.
	 */
	public void setObjects(List<ShapeRecord> anArray)
	{
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		objects = anArray;
	}

	public Shape copy()
	{
		return new Shape(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, objects);
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context)
	{
		int numberOfBits = 0;

		context.setShapeSize(numberOfBits);

		numberOfBits += 8;

		for (ShapeRecord record : objects) {
			numberOfBits += record.prepareToEncode(coder, context);
		}

		numberOfBits += 6; // Add size of end of shape

		numberOfBits += (numberOfBits % 8 > 0) ? 8 - (numberOfBits % 8) : 0;

		return numberOfBits >> 3;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
		coder.writeBits(context.getFillSize(), 4);
		coder.writeBits(context.getLineSize(), 4);

		for (ShapeRecord record : objects) {
			record.encode(coder, context);
		}

		coder.writeBits(0, 6); // End of shape
		coder.alignToByte();
	}
}
