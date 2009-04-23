/*
 * FocalGradientFill.java
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.FillStyle;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.datatype.CoordTransform;
import com.flagstone.transform.fillstyle.GradientFill.Interpolation;
import com.flagstone.transform.fillstyle.GradientFill.Spread;

//TODO(doc) Add documentation
//TODO(code) Implement
//TODO(optimise) Add pack/unpack methods
public final class FocalGradientFill implements FillStyle {

	public enum Spread {
		PAD(0), REFLECT(0x40), REPEAT(0xC0);
		
		private static final Map<Integer,Spread>table 
			= new LinkedHashMap<Integer,Spread>();

		static {
			for (Spread type : values()) {
				table.put(type.value, type);
			}
		}
		
		public static Spread fromInt(int type) {
			return table.get(type);
		}

		private int value;
		
		private Spread(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
	}
	
	public enum Interpolation {
		NORMAL(0), LINEAR(0x10);
		
		private static final Map<Integer,Interpolation>table 
			= new LinkedHashMap<Integer,Interpolation>();

		static {
			for (Interpolation type : values()) {
				table.put(type.value, type);
			}
		}
		
		public static Interpolation fromInt(int type) {
			return table.get(type);
		}

		private int value;
		
		private Interpolation(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
	}

	private int type;
	private int spread;
	private int interpolation;
	private int focalPoint;
	private List<Gradient> gradients;
	
	private transient int count;

	public FocalGradientFill(final SWFDecoder coder, final Context context) throws CoderException {
		type = coder.readByte();
		count = coder.readByte();
		gradients = new ArrayList<Gradient>(count);

		for (int i=0; i<count; i++) {
			gradients.add(new Gradient(coder, context));
		}
	}

	public FocalGradientFill(Spread spread, Interpolation interpolation, float point, final List<Gradient> anArray) {
		type = 0x13;
		setSpread(spread);
		setInterpolation(interpolation);
		setGradients(anArray);
		setFocalPoint(point);
	}

	public FocalGradientFill(FocalGradientFill object) {
		type = object.type;
		spread = object.spread;
		interpolation = object.interpolation;
		focalPoint = object.focalPoint;
		gradients = new ArrayList<Gradient>(object.gradients);
	}
	
	public Spread getSpread() {
		return Spread.fromInt(spread);
	}
	
	public void setSpread(Spread spread) {
		this.spread = spread.getValue();
	}
	
	public Interpolation getInterpolation() {
		return Interpolation.fromInt(interpolation);
	}
	
	public void setInterpolation(Interpolation interpolation) {
		this.interpolation = interpolation.getValue();
	}

	public float getFocalPoint() {
		return focalPoint/256.0f;
	}

	public void setFocalPoint(float point) {
		//TODO value checking required ?
		this.focalPoint = (int)(point*256);
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
		//TODO value checking required ?
		//TODO Check whether count will exceed 15.
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
		//TODO Check whether array size is > 15
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
	//TODO add format string
	public String toString() {
		return "";
	}

	public int prepareToEncode(final SWFEncoder coder, final Context context) {
		//TODO(optimise) Calculate size of gradient array directly.
		int length = 2;
		count = gradients.size();

		for (Gradient gradient : gradients) {
			length += gradient.prepareToEncode(coder, context);
		}

		return length;
	}

	public void encode(final SWFEncoder coder, final Context context) throws CoderException {
		coder.writeByte(type);
		coder.writeWord(count | spread | interpolation, 1);

		for (Gradient gradient : gradients) {
			gradient.encode(coder, context);
		}
	}
}
