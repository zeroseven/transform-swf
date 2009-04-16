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

public final class FocalGradientFill implements FillStyle {

	private int spread;
	private int interpolation;
	private int focalPoint;
	private List<Gradient> gradients;
	
	private transient int count;

	public FocalGradientFill(final SWFDecoder coder, final SWFContext context) throws CoderException {
		coder.adjustPointer(8);
		count = coder.readByte();
		gradients = new ArrayList<Gradient>(count);

		for (int i=0; i<count; i++) {
			gradients.add(new Gradient(coder, context));
		}
	}

	public FocalGradientFill(FocalGradientFill object) {
		spread = object.spread;
		interpolation = object.interpolation;
		focalPoint = object.focalPoint;
		gradients = new ArrayList<Gradient>(object.gradients.size());
		
		for (Gradient gradient : object.gradients) {
			gradients.add(gradient.copy());
		}
	}

	public int getSpread() {
		return spread;
	}

	public void setSpread(int spread) {
		this.spread = spread;
	}

	public int getInterpolation() {
		return interpolation;
	}

	public void setInterpolation(int interpolation) {
		this.interpolation = interpolation;
	}

	public int getFocalPoint() {
		return focalPoint;
	}

	public void setFocalPoint(int focalPoint) {
		this.focalPoint = focalPoint;
	}

	/**
	 * Add a Gradient object to the array of gradient objects. For Flash 7 and
	 * earlier versions there can be up to 8 Gradients. For Flash 8 onwards this
	 * number was increased to 15.
	 * 
	 * @param aGradient
	 *            an Gradient object. Must not be null.
	 */
	public FocalGradientFill add(final Gradient aGradient) {
		gradients.add(aGradient);
		return this;
	}

	/**
	 * Returns the array of Gradient objects defining the points for the
	 * gradient fill.
	 */
	public List<Gradient> getGradients() {
		return gradients;
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

	@Override
	public FocalGradientFill copy() {
		return new FocalGradientFill(this);
	}

	@Override
	public String toString() {
		return "";
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context) {
		Iterator<Gradient> iter;
				
		int length = 2;
		count = gradients.size();

		for (iter = gradients.iterator(); iter.hasNext();) {
			length += iter.next().prepareToEncode(coder, context);
		}

		return length;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException {
		Iterator<Gradient> iter;
		
		coder.writeWord(count, 1);

		for (iter = gradients.iterator(); iter.hasNext();) {
			iter.next().encode(coder, context);
		}
	}
}
