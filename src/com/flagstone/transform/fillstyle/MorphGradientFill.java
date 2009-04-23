/*
 * MorphGradientFill.java
 * Transform
 * 
 * Copyright (c) 2001-2009 Flagstone Software Ltd. All rights reserved.
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

package com.flagstone.transform.fillstyle;

import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.CoordTransform;
import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

//TODO(doc) Review
/**
 * MorphGradientFill defines how a colour gradient changes across an area filled
 * in a shape as it is morphed. {@link GradientFill} has a description of colour 
 * gradients.
 * 
 * @see MorphGradient
 * @see GradientFill
 */
public final class MorphGradientFill implements FillStyle {

	private static final String FORMAT = "MorphGradientFill: { start=%s; end=%s; gradients=%s }";

	private int type;
	private CoordTransform startTransform;
	private CoordTransform endTransform;
	private List<MorphGradient> gradients;
	
	private transient int count;

	//TODO(doc)
	public MorphGradientFill(final SWFDecoder coder, final Context context) throws CoderException {
	    type = coder.readByte();
		startTransform = new CoordTransform(coder);
		endTransform = new CoordTransform(coder);
		count = coder.readByte();

		gradients = new ArrayList<MorphGradient>(count);

		for (int i=0; i<count; i++) {
			gradients.add(new MorphGradient(coder, context));
		}
	}


	/**
	 * Creates a MorphGradientFill object specifying the type of fill, starting
	 * and ending coordinate transforms and the array of gradient records.
	 * 
	 * @param type
	 *            the type of gradient fill, either FillStyle.LINEAR or
	 *            FillStyle.RADIAL.
	 * @param start
	 *            the coordinate transform mapping the gradient square onto
	 *            physical coordinates at the start of the morphing process.
	 * @param end
	 *            the coordinate transform mapping the gradient square onto
	 *            physical coordinates at the end of the morphing process.
	 * @param gradients
	 *            an array of MorphGradient objects defining the control points
	 *            for the gradient.
	 */
	public MorphGradientFill(boolean radial, final CoordTransform start,
			final CoordTransform end, final List<MorphGradient> gradients) {
		setRadial(radial);
		setStartTransform(start);
		setEndTransform(end);
		setGradients(gradients);
	}
	
	//TODO(doc)
	public MorphGradientFill(MorphGradientFill object) {
		type = object.type;
		startTransform = object.startTransform;
		endTransform = object.endTransform;
		
		gradients = new ArrayList<MorphGradient>(object.gradients.size());
		
		for (MorphGradient gradient : object.gradients) {
			gradients.add(gradient.copy());
		}
	}

	/**
	 * Add a MorphGradient object to the array of gradient objects.
	 * 
	 * @param aGradient
	 *            an MorphGradient object. Must not be null.
	 */
	public MorphGradientFill add(final MorphGradient aGradient) {
		if (aGradient == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		//TODO(code) Add check for array size
		gradients.add(aGradient);
		return this;
	}

	
	public boolean isRadial() {
		return (type & 0x02) != 0;
	}
	
	public void setRadial(boolean radial) {
		if (radial) {
			type = 0x12;
		} else {
			type = 0x10;
		}
	}

	/**
	 * Returns the coordinate transform mapping the gradient square onto
	 * physical coordinates at the start of the morphing process.
	 */
	public CoordTransform getStartTransform() {
		return startTransform;
	}

	/**
	 * Returns the coordinate transform mapping the gradient square onto
	 * physical coordinates at the end of the morphing process.
	 */
	public CoordTransform getEndTransform() {
		return endTransform;
	}

	/**
	 * Returns the array of MorphGradients defining the control points for the
	 * gradient.
	 */
	public List<MorphGradient> getGradients() {
		return gradients;
	}

	/**
	 * Sets the coordinate transform mapping the gradient square onto physical
	 * coordinates at the start of the morphing process.
	 * 
	 * @param aTransform
	 *            the starting coordinate transform. Must not be null.
	 */
	public void setStartTransform(final CoordTransform aTransform) {
		if (aTransform == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		startTransform = aTransform;
	}

	/**
	 * Sets the coordinate transform mapping the gradient square onto physical
	 * coordinates at the end of the morphing process.
	 * 
	 * @param aTransform
	 *            the ending coordinate transform. Must not be null.
	 */
	public void setEndTransform(final CoordTransform aTransform) {
		if (aTransform == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		endTransform = aTransform;
	}

	/**
	 * Sets the array of control points that define the gradient. The final
	 * array should contain at least two control points. Up to Flash 7 the array
	 * can contain up to 8 control points. For Flash 8 onwards this limit was
	 * increased to 15.
	 * 
	 * @param anArray
	 *            an array of MorphGradient objects. Must not be null.
	 */
	public void setGradients(final List<MorphGradient> anArray) {
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		//TODO(code) Add check for array size
		gradients = anArray;
	}

	@Override
	public MorphGradientFill copy() {
		return new MorphGradientFill(this);
	}

	@Override
	public String toString() {
		return String.format(FORMAT, startTransform, endTransform, gradients);
	}

	public int prepareToEncode(final SWFEncoder coder, final Context context) {
		int length = 2 + startTransform.prepareToEncode(coder, context)
				+ endTransform.prepareToEncode(coder, context);
		
		//TODO(optimise) calculate gradient array size directly.
		count = gradients.size();
		
		for (MorphGradient gradient : gradients) {
			length += gradient.prepareToEncode(coder, context);
		}

		return length;
	}

	public void encode(final SWFEncoder coder, final Context context) throws CoderException {
		coder.writeByte(type);
		startTransform.encode(coder, context);
		endTransform.encode(coder, context);

		coder.writeByte(count);

		for (MorphGradient gradient : gradients) {
			gradient.encode(coder, context);
		}
	}
}
