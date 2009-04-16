/*
 * ColorTransform.java
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

package com.flagstone.transform.movie.datatype;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Encoder;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.Encodeable;

/**
 * <p>
 * A ColorTransform is used to change the colour of a shape or button without
 * changing the values in the original definition of the object.
 * </p>
 * 
 * <p>
 * Two types of transformation are supported: Add and Multiply. In Add
 * transformations a value is added to each colour channel:
 * </p>
 * 
 * <pre>
 * newRed = red + addRedTerm
 * newGreen = green + addGreenTerm
 * newBlue = blue + addBlueTerm
 * newAlpha = alpha + addAlphaTerm
 * </pre>
 * 
 * <p>
 * In Multiply transformations each colour channel is multiplied by a given
 * value:
 * </p>
 * 
 * <pre>
 * newRed = red * multiplyRedTerm
 * newGreen = green * multiplyGreenTerm
 * newBlue = blue * multiplyBlueTerm
 * newAlpha = alpha * multiplyAlphaTerm
 * </pre>
 * 
 * <p>
 * Add and Multiply transforms may be combined in which case the multiply terms
 * are applied to the colour channel before the add terms.
 * </p>
 * 
 * <pre>
 * newRed = (red * multiplyRedTerm) + addRedTerm
 * newGreen = (green * multiplyGreenTerm) + addGreenTerm
 * newBlue = (blue * multiplyBlueTerm) + addBlueTerm
 * newAlpha = (alpha * multiplyAlphaTerm) + addAlphaTerm
 * </pre>
 * 
 * <p>
 * For each type of transform the result of the calculation is limited to the
 * range 0..255. If the result is less than 0 or greater than 255 then it is
 * clamped at 0 and 255 respectively.
 * </p>
 * 
 * <p>
 * Not all objects containing a colour transform use the add or multiply terms
 * defined for the alpha channel. The colour objects defined in an DefineButton,
 * ButtonColorTransform or PlaceObject object do not use the alpha channel while
 * DefineButton2 and PlaceObject2 do. The "parent" object is stored in a Context
 * when objects are encoded or decoded allowing the alpha terms to be
 * selectively encoded or decoded.
 * </p>
 * 
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class ColorTransform implements Encodeable {

	private static final String FORMAT = 
		"ColorTransform: { multiply=[%f, %f, %f, %f]; add=[%d, %d, %d, %d] }";

	private final transient int multiplyRed;
	private final transient int multiplyGreen;
	private final transient int multiplyBlue;
	private final transient int multiplyAlpha;

	private final transient int addRed;
	private final transient int addGreen;
	private final transient int addBlue;
	private final transient int addAlpha;

	private transient int size;
	private transient boolean hasMultiply;
	private transient boolean hasAdd;
	private transient boolean hasAlpha;

	public ColorTransform(final SWFDecoder coder, final SWFContext context) throws CoderException {

		coder.alignToByte();

		hasAdd = coder.readBits(1, false) != 0;
		hasMultiply = coder.readBits(1, false) != 0;
		hasAlpha = context.isTransparent();
		size = coder.readBits(4, false);

		if (hasMultiply) {
			multiplyRed = coder.readBits(size, true);
			multiplyGreen = coder.readBits(size, true);
			multiplyBlue = coder.readBits(size, true);
			multiplyAlpha = hasAlpha ? coder.readBits(size, true) : 256;
		} else {
			multiplyRed = 256;
			multiplyGreen = 256;
			multiplyBlue = 256;
			multiplyAlpha = 256;
		}

		if (hasAdd) {
			addRed = coder.readBits(size, true);
			addGreen = coder.readBits(size, true);
			addBlue = coder.readBits(size, true);
			addAlpha = hasAlpha ? coder.readBits(size, true) : 0;
		} else {
			addRed = 0;
			addGreen = 0;
			addBlue = 0;
			addAlpha = 0;
		}

		coder.alignToByte();
	}

	/**
	 * Creates an add colour transform.
	 * 
	 * @param addRed
	 *            value to add to the red colour channel.
	 * @param addGreen
	 *            value to add to the green colour channel.
	 * @param addBlue
	 *            value to add to the blue colour channel.
	 * @param addAlpha
	 *            value to add to the alpha colour channel.
	 */
	public ColorTransform(final int addRed, final int addGreen,
			final int addBlue, final int addAlpha) {
		multiplyRed = 256;
		multiplyGreen = 256;
		multiplyBlue = 256;
		multiplyAlpha = 256;

		this.addRed = addRed;
		this.addGreen = addGreen;
		this.addBlue = addBlue;
		this.addAlpha = addAlpha;
	}

	/**
	 * Creates a multiply colour transform that will apply the colour channels.
	 * 
	 * @param mulRed
	 *            value to multiply the red colour channel by.
	 * @param mulGreen
	 *            value to multiply the green colour channel by.
	 * @param mulBlue
	 *            value to multiply the blue colour channel by.
	 * @param mulAlpha
	 *            value to multiply the alpha colour channel by.
	 */
	public ColorTransform(final float mulRed, final float mulGreen,
			final float mulBlue, final float mulAlpha) {
		multiplyRed = (int) (mulRed * 256);
		multiplyGreen = (int) (mulGreen * 256);
		multiplyBlue = (int) (mulBlue * 256);
		multiplyAlpha = (int) (mulAlpha * 256);

		addRed = 0;
		addGreen = 0;
		addBlue = 0;
		addAlpha = 0;
	}

	public ColorTransform(final int addRed, final int addGreen,
			final int addBlue, final int addAlpha,
			final float mulRed, final float mulGreen,
			final float mulBlue, final float mulAlpha) {
		multiplyRed = (int) (mulRed * 256);
		multiplyGreen = (int) (mulGreen * 256);
		multiplyBlue = (int) (mulBlue * 256);
		multiplyAlpha = (int) (mulAlpha * 256);

		this.addRed = addRed;
		this.addGreen = addGreen;
		this.addBlue = addBlue;
		this.addAlpha = addAlpha;
	}

	/**
	 * Create a copy of a ColorTransform object.
	 * 
	 * @param object
	 *            the ColorTransform object used to initialise this one.
	 */
	public ColorTransform(final ColorTransform object) {

		multiplyRed = object.multiplyRed;
		multiplyGreen = object.multiplyGreen;
		multiplyBlue = object.multiplyBlue;
		multiplyAlpha = object.multiplyAlpha;

		addRed = object.addRed;
		addGreen = object.addGreen;
		addBlue = object.addBlue;
		addAlpha = object.addAlpha;
	}

	/**
	 * Returns true if the colour of an object will be unchanged by the
	 * transform.
	 */
	public boolean isUnityTransform() {
		return (multiplyRed == 256) && (multiplyGreen == 256)
				&& (multiplyBlue == 256) && (multiplyAlpha == 256)
				&& (addRed == 0) && (addGreen == 0) && (addBlue == 0)
				&& (addAlpha == 0);
	}

	/**
	 * Returns the value of the multiply term for the red channel.
	 */
	public float getMultiplyRed() {
		return multiplyRed / 256.0f;
	}

	/**
	 * Returns the value of the multiply term for the green channel.
	 */
	public float getMultiplyGreen() {
		return multiplyGreen / 256.0f;
	}

	/**
	 * Returns the value of the multiply term for the blue channel.
	 */
	public float getMultiplyBlue() {
		return multiplyBlue / 256.0f;
	}

	/**
	 * Returns the value of the multiply term for the alpha channel.
	 */
	public float getMultiplyAlpha() {
		return multiplyAlpha / 256.0f;
	}

	/**
	 * Returns the value of the add term for the red channel.
	 */
	public int getAddRed() {
		return addRed;
	}

	/**
	 * Returns the value of the add term for the green channel.
	 */
	public int getAddGreen() {
		return addGreen;
	}

	/**
	 * Returns the value of the add term for the blue channel.
	 */
	public int getAddBlue() {
		return addBlue;
	}

	/**
	 * Returns the value of the add term for the alpha channel.
	 */
	public int getAddAlpha() {
		return addAlpha;
	}

	@Override
	public String toString() {
		return String.format(FORMAT, multiplyRed / 256.0f,
				multiplyGreen / 256.0f, multiplyBlue / 256.0f,
				multiplyAlpha / 256.0f, addRed, addGreen, addBlue, addAlpha);
	}
	
	@Override
	public boolean equals(final Object object) {
		boolean result;
		ColorTransform transform;
		
		if (object == null) {
			result = false;
		} else if (object == this) {
			result = true;
		} else if (object instanceof ColorTransform) {
			transform = (ColorTransform)object;
			result = addRed == transform.addRed && addGreen == transform.addGreen &&
				addBlue == transform.addBlue && addAlpha == transform.addAlpha &&
				multiplyRed == transform.multiplyRed && 
				multiplyGreen == transform.multiplyGreen && 
				multiplyBlue == transform.multiplyBlue &&
				multiplyAlpha == transform.multiplyAlpha;
		} else {
			result = false;
		}
		return result;
	}
	
	@Override
	public int hashCode() {
		return ((((((addRed*31 + addGreen)*31 + addBlue)*31 + addAlpha)*31 + 
				multiplyRed)* 31 + multiplyGreen)* 31 + multiplyBlue)* 31 + multiplyAlpha;
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context) {

		int numberOfBits = 13; // include extra 7 bits for byte alignment

		hasMultiply = containsMultiplyTerms(context);
		hasAdd = containsAddTerms(context);
		hasAlpha = context.isTransparent();
		size = fieldSize(context);

		if (hasMultiply) {
			numberOfBits += size * (hasAlpha ? 4 : 3);
		}

		if (hasAdd) {
			numberOfBits += size * (hasAlpha ? 4 : 3);
		}

		return numberOfBits >> 3;
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException {

		coder.alignToByte();

		coder.writeBits(hasAdd ? 1 : 0, 1);
		coder.writeBits(hasMultiply ? 1 : 0, 1);
		coder.writeBits(size, 4);

		if (hasMultiply) {
			coder.writeBits(multiplyRed, size);
			coder.writeBits(multiplyGreen, size);
			coder.writeBits(multiplyBlue, size);

			if (hasAlpha) {
				coder.writeBits(multiplyAlpha, size);
			}
		}

		if (hasAdd) {
			coder.writeBits(addRed, size);
			coder.writeBits(addGreen, size);
			coder.writeBits(addBlue, size);

			if (hasAlpha) {
				coder.writeBits(addAlpha, size);
			}
		}

		coder.alignToByte();
	}

	private boolean containsAddTerms(final SWFContext context) {
		return (addRed != 0) || (addGreen != 0) || (addBlue != 0)
				|| (context.isTransparent() && addAlpha != 0);
	}

	private boolean containsMultiplyTerms(final SWFContext context) {
		return multiplyRed != 256 || multiplyGreen != 256
				|| multiplyBlue != 256
				|| (context.isTransparent() && multiplyAlpha != 256);
	}

	private int addFieldSize(final SWFContext context) {

		int size;

		if (context.isTransparent()) {
			size = Encoder.maxSize(addRed, addGreen, addBlue, addAlpha);
		} else {
			size = Encoder.maxSize(addRed, addGreen, addBlue);
		}
		return size;
	}

	private int multiplyFieldSize(final SWFContext context) {

		int size;

		if (context.isTransparent()) {
			size = Encoder.maxSize(multiplyRed, multiplyGreen, multiplyBlue,
					multiplyAlpha);
		} else {
			size = Encoder.maxSize(multiplyRed, multiplyGreen, multiplyBlue);
		}

		return size;
	}

	private int fieldSize(final SWFContext context) {
		int numberOfBits;

		if (hasAdd && !hasMultiply) {
			numberOfBits = addFieldSize(context);
		} else if (!hasAdd && hasMultiply) {
			numberOfBits = multiplyFieldSize(context);
		} else if (hasAdd && hasMultiply) {
			numberOfBits = Math.max(addFieldSize(context),
					multiplyFieldSize(context));
		} else {
			numberOfBits = 1;
		}

		return numberOfBits;
	}
}
