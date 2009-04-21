/*
 * CoordTransformTest.java
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
package com.flagstone.transform;

import java.util.Arrays;

import org.junit.Test;

import com.flagstone.transform.CoordTransform;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

@SuppressWarnings( { "PMD.TooManyMethods",
		"PMD.JUnitAssertionsShouldIncludeMessage",
		"PMD.LocalVariableCouldBeFinal", 
		"PMD.JUnitTestsShouldIncludeAssert" })
public final class CoordTransformTest {
	private transient CoordTransform fixture;

	private transient SWFEncoder encoder;
	private transient SWFDecoder decoder; 
	private transient SWFContext context;
	private transient byte[] data;

	@Test
	public void checkProduct() {
		float[][] left = new float[][] { { 1.0f, 1.0f, 1.0f },
				{ 2.0f, 2.0f, 2.0f }, { 3.0f, 3.0f, 3.0f } };

		float[][] right = new float[][] { { 1.0f, 2.0f, 3.0f },
				{ 1.0f, 2.0f, 3.0f }, { 1.0f, 2.0f, 3.0f } };

		float[][] expected = new float[][] { { 3.0f, 6.0f, 9.0f },
				{ 6.0f, 12.0f, 18.0f }, { 9.0f, 18.0f, 27.0f } };

		compare(expected, CoordTransform.product(left, right));
	}

	@Test
	public void checkScale() {
		float[][] expected = new float[][] { { 2.0f, 0.0f, 0.0f },
				{ 0.0f, 3.0f, 0.0f }, { 0.0f, 0.0f, 1.0f } };

		compare(expected, CoordTransform.scale(2.0f, 3.0f).getMatrix());
	}

	@Test
	public void checkShear() {
		float[][] expected = new float[][] { { 1.0f, 3.0f, 0.0f },
				{ 2.0f, 1.0f, 0.0f }, { 0.0f, 0.0f, 1.0f } };

		compare(expected, CoordTransform.shear(2.0f, 3.0f).getMatrix());
	}

	@Test
	public void checkTranslate() {
		float[][] expected = new float[][] { { 1.0f, 0.0f, 2.0f },
				{ 0.0f, 1.0f, 3.0f }, { 0.0f, 0.0f, 1.0f } };

		compare(expected, CoordTransform.translate(2, 3).getMatrix());
	}

	@Test
	public void checkRotate() {
		float[][] expected = new float[][] { { 0.5f, -0.8660126f, 0.0f },
				{ 0.8660126f, 0.5f, 0.0f }, { 0.0f, 0.0f, 1.0f } };

		compare(expected, CoordTransform.rotate(60).getMatrix());
	}

	@Test
	public void checkConstructorSetsOnlyTranslation() {
		int xCoord = 100;
		int yCoord = 200;

		fixture = CoordTransform.translate(xCoord, yCoord);

		float[][] expected = new float[][] { { 1.0f, 0.0f, xCoord },
				{ 0.0f, 1.0f, yCoord }, { 0.0f, 0.0f, 1.0f } };

		compare(expected, fixture.getMatrix());
	}

	@Test
	public void checkConstructorCreatesCopy() {
		float[][] expected = new float[][] { { 1.0f, 2.0f, 3.0f },
				{ 4.0f, 5.0f, 6.0f }, { 0.0f, 0.0f, 1.0f } };

		fixture = new CoordTransform(expected);
		
		compare(expected, new CoordTransform(fixture).getMatrix());
	}

	@Test
	public void checkMatrixIsNotUnityTransform() {
		float[][] expected = new float[][] { { 1.0f, 0.0f, 0.0f },
				{ 0.0f, 0.0f, 0.0f }, { 0.0f, 0.0f, 1.0f } };

		fixture = new CoordTransform(expected);

		assertFalse(fixture.isUnityTransform());
	}

	@Test
	public void checkCopy() {
		float[][] expected = new float[][] { { 1.0f, 2.0f, 3.0f },
				{ 4.0f, 5.0f, 6.0f }, { 0.0f, 0.0f, 1.0f } };

		fixture = new CoordTransform(expected);

		compare(expected, new CoordTransform(fixture).getMatrix());
	}

	@Test
	public void checkEncodeTranslation() throws CoderException {
		int xCoord = 1;
		int yCoord = 2;

		fixture = CoordTransform.translate(xCoord, yCoord);

		data = new byte[] { 6, 80 };
		SWFEncoder encoder = new SWFEncoder(data.length);

		assertEquals(2, fixture.prepareToEncode(encoder, context));
		fixture.encode(encoder, context);

		assertEquals(16, encoder.getPointer());
		assertArrayEquals(data, encoder.getData());
	}

	@Test
	public void checkEncodeScale() throws CoderException {
		float scaleX = 1.0f;
		float scaleY = 2.0f;

		fixture = CoordTransform.scale(scaleX, scaleY);

		data = new byte[] { -52, -128, 0, 32, 0, 0, 64 };
		SWFEncoder encoder = new SWFEncoder(data.length);

		assertEquals(7, fixture.prepareToEncode(encoder, context));
		fixture.encode(encoder, context);

		assertEquals(56, encoder.getPointer());
		assertArrayEquals(data, encoder.getData());
	}

	@Test
	public void checkEncodeShear() throws CoderException {
		float shearX = 1.0f;
		float shearY = 2.0f;

		fixture = CoordTransform.shear(shearX, shearY);

		data = new byte[] { 102, 64, 0, 16, 0, 0, 64 };
		SWFEncoder encoder = new SWFEncoder(data.length);

		assertEquals(7, fixture.prepareToEncode(encoder, context));
		fixture.encode(encoder, context);

		assertEquals(56, encoder.getPointer());
		assertArrayEquals(data, encoder.getData());
	}

	@Test
	public void checkEncodeRotate() throws CoderException {
		float[][] matrix = new float[][] { { 1.0f, 4.0f, 0.0f },
				{ 3.0f, 2.0f, 0.0f }, { 0.0f, 0.0f, 1.0f } };

		fixture = new CoordTransform(matrix);

		data = new byte[] { -52, -128, 0, 32, 0, 13, 12, 0, 1, 0, 0, 2, 0 };
		SWFEncoder encoder = new SWFEncoder(data.length);

		assertEquals(13, fixture.prepareToEncode(encoder, context));
		fixture.encode(encoder, context);

		assertEquals(104, encoder.getPointer());
		assertArrayEquals(data, encoder.getData());
	}

	@Test
	public void checkDecodeTranslation() throws CoderException {
		int xCoord = 1;
		int yCoord = 2;

		data = new byte[] { 6, 80 };
		SWFDecoder decoder = new SWFDecoder(data);

		fixture = new CoordTransform(decoder);

		assertEquals(16, decoder.getPointer());
		assertEquals(xCoord, fixture.getMatrix()[0][2]);
		assertEquals(yCoord, fixture.getMatrix()[1][2]);
	}

	@Test
	public void checkDecodeScale() throws CoderException {
		float scaleX = 1.0f;
		float scaleY = 2.0f;

		data = new byte[] { -52, -128, 0, 32, 0, 0, 64 };
		SWFDecoder decoder = new SWFDecoder(data);

		fixture = new CoordTransform(decoder);

		assertEquals(56, decoder.getPointer());
		assertEquals(scaleX, fixture.getMatrix()[0][0]);
		assertEquals(scaleY, fixture.getMatrix()[1][1]);
	}

	@Test
	public void checkDecodeShear() throws CoderException {
		float shearX = 1.0f;
		float shearY = 2.0f;

		data = new byte[] { 102, 64, 0, 16, 0, 0, 64 };
		SWFDecoder decoder = new SWFDecoder(data);

		fixture = new CoordTransform(decoder);

		assertEquals(56, decoder.getPointer());
		assertEquals(shearX, fixture.getMatrix()[1][0]);
		assertEquals(shearY, fixture.getMatrix()[0][1]);
	}

	@Test
	public void checkDecodeRotate() throws CoderException {
		float[][] matrix = new float[][] { { 1.0f, 4.0f, 0.0f },
				{ 3.0f, 2.0f, 0.0f }, { 0.0f, 0.0f, 1.0f } };

		data = new byte[] { -52, -128, 0, 32, 0, 13, 12, 0, 1, 0, 0, 2, 0 };
		SWFDecoder decoder = new SWFDecoder(data);

		fixture = new CoordTransform(decoder);

		assertEquals(104, decoder.getPointer());
		compare(matrix, fixture.getMatrix());
	}

	@SuppressWarnings("PMD.UseAssertEqualsInsteadOfAssertTrue")
	private void compare(final float[][] left, final float[][] right) {
		for (int i = 0; i < left.length; i++) {
			assertTrue(Arrays.equals(left[i], right[i]));
		}
	}
}
