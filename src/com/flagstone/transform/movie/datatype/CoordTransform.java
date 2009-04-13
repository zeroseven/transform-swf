/*
 * CoordTransform.java
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
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.Codeable;
import com.flagstone.transform.movie.Copyable;

/**
 * <p>
 * CoordTransform is used to specify two-dimensional coordinate transforms,
 * allowing an object to be scaled, rotated or moved without changing the
 * original definition of how the object is drawn.
 * </p>
 * 
 * <p>
 * A two-dimensional transform is defined using a 3x3 matrix and the new values
 * for a pair of coordinates (x,y) are calculated using the following matrix
 * multiplication:
 * </p>
 * 
 * <img src="doc-files/transform.gif">
 * 
 * <p>
 * Different transformations such as scaling, rotation, shearing and translation
 * can be performed using the above matrix multiplication. More complex
 * transformations can be defined by performing successive matrix
 * multiplications in a process known as compositing. This allows a complex
 * transformations to performed on an object. However not that compositing
 * transforms is not commutative, the order in which transformations are applied
 * will affect the final result.
 * </p>
 * 
 */
@SuppressWarnings( { "PMD.TooManyMethods", "PMD.LocalVariableCouldBeFinal" })
public final class CoordTransform implements Codeable, Copyable<CoordTransform> {

	private static final String FORMAT = "CoordTransform: { scaleX=%f; scaleY=%f; shearX=%f; shearY=%f; transX=%d; transY=%d }";

	/**
	 * Create a new coordinate transform by multiplying two matrices together to
	 * calculate the product. Since matrix multiplication is not commutative the
	 * order in which the arguments are passed is important.
	 * 
	 * @param left
	 *            a 3x3 matrix
	 * @param right
	 *            a 3x3 matrix
	 * @return a new 3x3 matrix contains the product of the two arguments.
	 */
	public static float[][] product(final float[][] left, final float[][] right) {
		return new float[][] {
				{
						left[0][0] * right[0][0] + left[0][1] * right[1][0]
								+ left[0][2] * right[2][0],
						left[0][0] * right[0][1] + left[0][1] * right[1][1]
								+ left[0][2] * right[2][1],
						left[0][0] * right[0][2] + left[0][1] * right[1][2]
								+ left[0][2] * right[2][2] },
				{
						left[1][0] * right[0][0] + left[1][1] * right[1][0]
								+ left[1][2] * right[2][0],
						left[1][0] * right[0][1] + left[1][1] * right[1][1]
								+ left[1][2] * right[2][1],
						left[1][0] * right[0][2] + left[1][1] * right[1][2]
								+ left[1][2] * right[2][2] },
				{
						left[2][0] * right[0][0] + left[2][1] * right[1][0]
								+ left[2][2] * right[2][0],
						left[2][0] * right[0][1] + left[2][1] * right[1][1]
								+ left[2][2] * right[2][1],
						left[2][0] * right[0][2] + left[2][1] * right[1][2]
								+ left[2][2] * right[2][2] } };
	}

	/**
	 * Returns a translation transform.
	 * 
	 * @param xCoord
	 *            the x coordinate of the transformation.
	 * @param yCoord
	 *            the y coordinate of the transformation.
	 * @return a CoordTransform containing the translation.
	 */
	public static CoordTransform translate(final int xCoord, final int yCoord) {
		CoordTransform transform = new CoordTransform();
		transform.setTranslate(xCoord, yCoord);
		return transform;
	}

	/**
	 * Returns a scaling transform.
	 * 
	 * @param xScale
	 *            the scaling factor along the x-axis.
	 * @param yScale
	 *            the scaling factor along the y-axis
	 * @return a CoordTransform containing the scaling transform.
	 */
	public static CoordTransform scale(final float xScale, final float yScale) {
		CoordTransform transform = new CoordTransform();
		transform.setScale(xScale, yScale);
		return transform;
	}

	/**
	 * Returns a CoordTransform initialised for a shearing operation.
	 * 
	 * @param xShear
	 *            the shearing factor along the x-axis.
	 * @param yShear
	 *            the shearing factor along the y-axis
	 * @return a CoordTransform containing the shearing transform.
	 */
	public static CoordTransform shear(final float xShear, final float yShear) {
		CoordTransform transform = new CoordTransform();
		transform.setShear(xShear, yShear);
		return transform;
	}

	/**
	 * Returns a CoordTransform initialised for a rotation in degrees.
	 * 
	 * @param angle
	 *            the of rotation in degrees.
	 * @return a CoordTransform containing the rotation.
	 */
	public static CoordTransform rotate(final int angle) {

		double radians = Math.toRadians(angle);
		float cos = (float) Math.cos(radians);
		float sin = (float) Math.sin(radians);

		CoordTransform transform = new CoordTransform();
		transform.setScale(cos, cos);
		transform.setShear(sin, -sin);
		return transform;
	}

	private int scaleX;
	private int scaleY;
	private int shearX;
	private int shearY;
	private int translateX;
	private int translateY;

	private transient int scaleSize;
	private transient int shearSize;
	private transient int transSize;

	private transient boolean hasScale;
	private transient boolean hasShear;

	/**
	 * Creates a unity coordinate transform - one that will not change the
	 * location or appearance when it is applied to an object.
	 */
	public CoordTransform() {
		scaleX = 65536;
		scaleY = 65536;
	}

	/**
	 * Create a copy of a CoordTransform object.
	 * 
	 * @param object
	 *            the CoordTransform object used to initialise this one.
	 */
	public CoordTransform(final CoordTransform object) {
		scaleX = object.scaleX;
		scaleY = object.scaleY;
		shearX = object.shearX;
		shearY = object.shearY;
		translateX = object.translateX;
		translateY = object.translateY;
	}

	/**
	 * Returns the scaling factor along the x-axis.
	 */
	public float getScaleX() {
		return scaleX / 65536.0f;
	}

	/**
	 * Sets the scaling factor along the x-axis.
	 * 
	 * @param scale
	 *            the scaling factor in the x direction.
	 */
	public void setScaleX(final float scale) {
		scaleX = (int) (scale * 65536);
	}

	/**
	 * Returns the scaling factor along the y-axis.
	 */
	public float getScaleY() {
		return scaleY / 65536.0f;
	}

	/**
	 * Sets the scaling factor along the y-axis.
	 * 
	 * @param scale
	 *            the scaling factor in the y direction.
	 */
	public void setScaleY(final float scale) {
		scaleY = (int) (scale * 65536);
	}

	/**
	 * Sets the scaling factor for the transform.
	 * 
	 * This method sets the individual values used in the transform directly. It
	 * does not perform any compositing.
	 * 
	 * @param xScale
	 *            the scale to the applied to the object along the x-axis.
	 * @param yScale
	 *            the scale to the applied to the object along the x-axis.
	 */
	public void setScale(final float xScale, final float yScale) {
		scaleX = (int) (xScale * 65536.0);
		scaleY = (int) (yScale * 65536.0);
	}

	/**
	 * Returns the shearing factor along the x-axis.
	 */
	public float getShearX() {
		return shearX / 65536.0f;
	}

	/**
	 * Sets the shearing factor along the x-axis.
	 * 
	 * @param shear
	 *            the shearing factor in the x direction.
	 */
	public void setShearX(final float shear) {
		shearX = (int) (shear * 65536);
	}

	/**
	 * Returns the shearing factor along the y-axis.
	 */
	public float getShearY() {
		return shearY / 65536.0f;
	}

	/**
	 * Sets the shearing factor along the y-axis.
	 * 
	 * @param shear
	 *            the shearing factor in the y direction.
	 */
	public void setShearY(final float shear) {
		shearY = (int) (shear * 65536);
	}

	/**
	 * Sets the shearing factor for the transform.
	 * 
	 * This method sets the individual values used in the transform directly. It
	 * does not perform any compositing.
	 * 
	 * @param xShear
	 *            the shear to the applied to the object along the x-axis.
	 * @param yShear
	 *            the shear to the applied to the object along the x-axis.
	 */
	public void setShear(final float xShear, final float yShear) {
		shearX = (int) (xShear * 65536.0);
		shearY = (int) (yShear * 65536.0);
	}

	/**
	 * Returns the translation in the x direction.
	 */
	public int getTranslateX() {
		return translateX;
	}

	/**
	 * Sets the translation along the x-axis.
	 * 
	 * @param coord
	 *            the translation in the x direction.
	 */
	public void setTranslateX(final int coord) {
		translateX = coord;
	}

	/**
	 * Returns the translation along the y-axis.
	 */
	public int getTranslateY() {
		return translateY;
	}

	/**
	 * Sets the translation along the y-axis.
	 * 
	 * @param coord
	 *            the translation in the y direction.
	 */
	public void setTranslateY(final int coord) {
		translateY = coord;
	}

	/**
	 * Sets the translation for the transform.
	 * 
	 * This method sets the individual values used in the transform directly. It
	 * does not perform any compositing.
	 * 
	 * @param xCoord
	 *            the translation along the x-axis.
	 * @param yCoord
	 *            the translation along the x-axis.
	 */
	public void setTranslate(final int xCoord, final int yCoord) {
		translateX = xCoord;
		translateY = yCoord;
	}

	/**
	 * Returns the 3 X 3 array that is used to store the transformation values.
	 */
	public float[][] getMatrix() {
		return new float[][] {
				{ scaleX / 65536.0f, shearY / 65536.0f, translateX },
				{ shearX / 65536.0f, scaleY / 65536.0f, translateY },
				{ 0.0f, 0.0f, 1.0f } };
	}

	/**
	 * Sets the transform values from a 3x3 matrix.
	 * 
	 * @param matrix
	 *            a 3x3 array of floats containing the values defining the
	 *            transform.
	 */
	public void setMatrix(final float[][] matrix) {
		scaleX = (int) (matrix[0][0] * 65536.0f);
		scaleY = (int) (matrix[1][1] * 65536.0f);
		shearX = (int) (matrix[1][0] * 65536.0f);
		shearY = (int) (matrix[0][1] * 65536.0f);
		translateX = (int) matrix[0][2];
		translateY = (int) matrix[1][2];
	}

	/**
	 * Returns true if the transform will leave the position, size and rotation
	 * of the object unchanged.
	 */
	public boolean isUnityTransform() {
		return scaleX == 65536 && scaleY == 65536 && shearX == 0 && shearY == 0
				&& translateX == 0 && translateY == 0;
	}

	public CoordTransform copy() {
		return new CoordTransform(this);
	}

	@Override
	public String toString() {
		return String.format(FORMAT, scaleX / 65536.0f, scaleY / 65536.0f,
				shearX / 65536.0f, shearY / 65536.0f, translateX, translateY);
	}

	public int prepareToEncode(final SWFEncoder coder) {
		int numberOfBits = 14; // include extra 7 bits for ensuring byte
		// alignment

		hasScale = scaleX != 65536 || scaleY != 65536;
		hasShear = shearX != 0 || shearY != 0;

		if (hasScale || hasShear || (translateX != 0 || translateY != 0)) {
			transSize = Math.max(Encoder.size(translateX), Encoder
					.size(translateY));
		} else {
			transSize = 0;
		}

		numberOfBits += transSize << 1;

		if (hasScale) {
			scaleSize = Math.max(Encoder.size(scaleX), Encoder.size(scaleY));
			numberOfBits += 5 + (scaleSize << 1);
		}

		if (hasShear) {
			shearSize = Math.max(Encoder.size(shearX), Encoder.size(shearY));
			numberOfBits += 5 + (shearSize << 1);
		}

		return numberOfBits >> 3;
	}

	public void encode(final SWFEncoder coder) throws CoderException {
		coder.alignToByte();

		coder.writeBits(hasScale ? 1 : 0, 1);

		if (hasScale) {
			coder.writeBits(scaleSize, 5);
			coder.writeBits(scaleX, scaleSize);
			coder.writeBits(scaleY, scaleSize);
		}

		coder.writeBits(hasShear ? 1 : 0, 1);

		if (hasShear) {
			coder.writeBits(shearSize, 5);
			coder.writeBits(shearX, shearSize);
			coder.writeBits(shearY, shearSize);
		}

		coder.writeBits(transSize, 5);
		coder.writeBits(translateX, transSize);
		coder.writeBits(translateY, transSize);

		coder.alignToByte();
	}

	public void decode(final SWFDecoder coder) throws CoderException {
		coder.alignToByte();

		hasScale = coder.readBits(1, false) != 0;

		if (hasScale) {
			scaleSize = coder.readBits(5, false);
			scaleX = coder.readBits(scaleSize, true);
			scaleY = coder.readBits(scaleSize, true);
		}

		hasShear = coder.readBits(1, false) != 0;

		if (hasShear) {
			shearSize = coder.readBits(5, false);
			shearX = coder.readBits(shearSize, true);
			shearY = coder.readBits(shearSize, true);
		}

		transSize = coder.readBits(5, false);
		translateX = coder.readBits(transSize, true);
		translateY = coder.readBits(transSize, true);

		coder.alignToByte();
	}
}
