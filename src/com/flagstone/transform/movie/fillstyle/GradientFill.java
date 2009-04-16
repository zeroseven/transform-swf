/*
 * GradientFill.java
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.datatype.CoordTransform;

/**
 * GradientFill defines how a colour changes across an area to be filled with
 * colour. Two types of gradient fill are supported:
 * 
 * <ol>
 * <li>Linear - where the gradient changes in one direction across the area to
 * be filled.</li>
 * 
 * <li>Radial - where the gradient changes radially from the centre of the area
 * to be filled.</li>
 * </ol>
 * 
 * <p>
 * Gradients are defined in terms of a standard space called the gradient
 * square, centred at (0,0) and extending from (-16384, -16384) to (16384,
 * 16384).
 * </p>
 * 
 * <img src="doc-files/gradientSquare.gif">
 * 
 * <p>
 * A coordinate transform is required to map the gradient square to the
 * coordinates of the filled area. The transformation is applied in two steps.
 * First the gradient square is scaled so the colour covers the shape followed
 * by a translation to map the gradient square coordinates to the coordinate
 * range of the shape.
 * </p>
 * 
 * <img src="gradientMapping.gif">
 * 
 * <p>
 * A series of gradient points is used to control how the colour displayed
 * changes across the gradient. At least two points are required to define a
 * gradient - one for the starting colour and one for the final colour. When the
 * Flash Player displays the control points they are sorted by the ratio defined
 * in each Gradient object, with the smallest ratio value displayed first.
 * </p>
 * 
 * @see Gradient
 */
public final class GradientFill implements FillStyle {

	private static final String FORMAT = "GradientFill: { transform=%s; gradients=%s }";

	private int type;
	private CoordTransform transform;
	private List<Gradient> gradients;
	
	private transient int count;

	public GradientFill(final SWFDecoder coder, final SWFContext context) throws CoderException {
		type = coder.readByte();
		transform = new CoordTransform(coder);
		count = coder.readByte();
		gradients = new ArrayList<Gradient>(count);
		
		for (int i=0; i<count; i++) {
			gradients.add(new Gradient(coder, context));
		}
	}


	/**
	 * Creates a GradientFill object specifying the type, coordinate transform
	 * and array of gradient points.
	 * 
	 * @param type
	 *            the type of gradient fill, either FillStyle.LINEAR or
	 *            FillStyle.RADIAL.
	 * @param aTransform
	 *            the coordinate transform mapping the gradient square onto
	 *            physical coordinates. Must not be null.
	 * @param anArray
	 *            an array of Gradient objects defining the control points for
	 *            the gradient. For Flash 7 and earlier versions there can be up
	 *            to 8 Gradients. For Flash 8 onwards this number was increased
	 *            to 15. Must not be null.
	 */
	public GradientFill(final int type, final CoordTransform aTransform,
			final List<Gradient> anArray) {
		this.type = type;
		setTransform(aTransform);
		setGradients(anArray);
	}

	public GradientFill(GradientFill object) {
		type = object.type;
		transform = object.transform;
		gradients = new ArrayList<Gradient>(object.gradients);
	}

	/**
	 * Add a Gradient object to the array of gradient objects. For Flash 7 and
	 * earlier versions there can be up to 8 Gradients. For Flash 8 onwards this
	 * number was increased to 15.
	 * 
	 * @param aGradient
	 *            an Gradient object. Must not be null.
	 */
	public GradientFill add(final Gradient aGradient) {
		if (aGradient == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		gradients.add(aGradient);
		return this;
	}

	/**
	 * Returns the coordinate transform mapping the gradient square onto
	 * physical coordinates.
	 */
	public CoordTransform getTransform() {
		return transform;
	}

	/**
	 * Returns the array of Gradient objects defining the points for the
	 * gradient fill.
	 */
	public List<Gradient> getGradients() {
		return gradients;
	}

	/**
	 * Sets the coordinate transform mapping the gradient square onto physical
	 * coordinates.
	 * 
	 * @param aTransform
	 *            the coordinate transform. Must not be null.
	 */
	public void setTransform(final CoordTransform aTransform) {
		if (aTransform == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		transform = aTransform;
	}

	/**
	 * Sets the array of control points that define the gradient. For Flash 7
	 * and earlier this array can contain up to 8 Gradient objects. For Flash 8
	 * onwards this limit was increased to 15.
	 * 
	 * @param anArray
	 *            an array of Gradient objects. Must not be null.
	 */
	public void setGradients(final List<Gradient> anArray) {
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		gradients = anArray;
	}

	public GradientFill copy() {
		return new GradientFill(this);
	}

	@Override
	public String toString() {
		return String.format(FORMAT, transform, gradients);
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context) {
		Iterator<Gradient> iter;
				
		int length = 2 + transform.prepareToEncode(coder, context);
		count = gradients.size();

		for (iter = gradients.iterator(); iter.hasNext();) {
			length += iter.next().prepareToEncode(coder, context);
		}

		return length;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException {
		Iterator<Gradient> iter;
		
		coder.writeByte(type);
		transform.encode(coder, context);
		coder.writeWord(count, 1);

		for (iter = gradients.iterator(); iter.hasNext();) {
			iter.next().encode(coder, context);
		}
	}
}
