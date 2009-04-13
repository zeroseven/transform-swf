/*
 * ColorTest.java
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

import org.junit.Before;
import org.junit.Test;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;

@SuppressWarnings( { "PMD.TooManyMethods",
    "PMD.LocalVariableCouldBeFinal",
	"PMD.JUnitAssertionsShouldIncludeMessage" })
public final class ColorTest {
	private transient Color fixture;

	private transient SWFEncoder encoder;
	private transient SWFDecoder decoder;
	private transient byte[] data;

	@Before
	public void setUp() {
		fixture = new Color();
		encoder = new SWFEncoder(0);
		decoder = new SWFDecoder(new byte[] {});
	}

	@Test
	public void checkValueOfString() {
		fixture = Color.valueOf("01020408");

		assertEquals(1, fixture.getRed());
		assertEquals(2, fixture.getGreen());
		assertEquals(4, fixture.getBlue());
		assertEquals(8, fixture.getAlpha());
	}

	@Test
	public void checkValueOfInteger() {
		fixture = Color.valueOf(0x01020408);

		assertEquals(1, fixture.getRed());
		assertEquals(2, fixture.getGreen());
		assertEquals(4, fixture.getBlue());
		assertEquals(8, fixture.getAlpha());
	}

	@Test
	public void checkConstructorSetsOpaqueAttributes() {
		fixture = new Color(1, 2, 4);

		assertEquals(1, fixture.getRed());
		assertEquals(2, fixture.getGreen());
		assertEquals(4, fixture.getBlue());
		assertEquals(255, fixture.getAlpha());
	}

	@Test
	public void checkConstructorSetsTransparentAttributes() {
		fixture = new Color(1, 2, 4, 8);

		assertEquals(1, fixture.getRed());
		assertEquals(2, fixture.getGreen());
		assertEquals(4, fixture.getBlue());
		assertEquals(8, fixture.getAlpha());
	}

	@Test
	public void checkConstructorCreatesCopy() {
		fixture = new Color(1, 2, 4, 8);
		Color copy = new Color(fixture);
		
		assertEquals(1, copy.getRed());
		assertEquals(2, copy.getGreen());
		assertEquals(4, copy.getBlue());
		assertEquals(8, copy.getAlpha());
	}

	@Test
	public void checkAccessorsForRedChannel() {
		fixture.setRed(1);

		assertEquals(1, fixture.getRed());
		assertEquals(0, fixture.getGreen());
		assertEquals(0, fixture.getBlue());
		assertEquals(255, fixture.getAlpha());
	}

	@Test
	public void checkAccessorsForGreenChannel() {
		fixture.setGreen(1);

		assertEquals(0, fixture.getRed());
		assertEquals(1, fixture.getGreen());
		assertEquals(0, fixture.getBlue());
		assertEquals(255, fixture.getAlpha());
	}

	@Test
	public void checkAccessorsForBlueChannel() {
		fixture.setBlue(1);

		assertEquals(0, fixture.getRed());
		assertEquals(0, fixture.getGreen());
		assertEquals(1, fixture.getBlue());
		assertEquals(255, fixture.getAlpha());
	}

	@Test
	public void checkAccessorsForAlphaChannel() {
		fixture.setAlpha(1);

		assertEquals(0, fixture.getRed());
		assertEquals(0, fixture.getGreen());
		assertEquals(0, fixture.getBlue());
		assertEquals(1, fixture.getAlpha());
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkRedBelowRangeThrowsException() {
		fixture.setRed(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkGreenBelowRangeThrowsException() {
		fixture.setGreen(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkBlueBelowRangeThrowsException() {
		fixture.setBlue(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkAlphaBelowRangeThrowsException() {
		fixture.setAlpha(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkRedAboveRangeThrowsException() {
		fixture.setRed(256);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkGreenAboveRangeThrowsException() {
		fixture.setGreen(256);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkBlueAboveRangeThrowsException() {
		fixture.setBlue(256);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkAlphaAboveRangeThrowsException() {
		fixture.setAlpha(256);
	}

	@Test
	public void getColourAsRGB() {
		fixture = new Color(1, 2, 4);

		assertEquals(0x010204, fixture.getRGB());
	}

	@Test
	public void setColourFromRGB() {
		fixture.setRGB(0x010204);

		assertEquals(1, fixture.getRed());
		assertEquals(2, fixture.getGreen());
		assertEquals(4, fixture.getBlue());
	}

	@Test
	public void getColourAsRGBA() {
		fixture = new Color(1, 2, 4, 8);

		assertEquals(0x01020408, fixture.getRGBA());
	}

	@Test
	public void setColourFromRGBA() {
		fixture.setRGBA(0x01020408);

		assertEquals(1, fixture.getRed());
		assertEquals(2, fixture.getGreen());
		assertEquals(4, fixture.getBlue());
		assertEquals(8, fixture.getAlpha());
	}

	@Test
	public void checkCopyIsEqual() {
		fixture = new Color(1, 2, 4, 8);
		Color copy = fixture.copy();
		
		assertEquals(fixture.getRed(), copy.getRed());
		assertEquals(fixture.getGreen(), copy.getGreen());
		assertEquals(fixture.getBlue(), copy.getBlue());
		assertEquals(fixture.getAlpha(), copy.getAlpha());
	}

	@Test
	public void checkCopyIsNotSame() {
		fixture = new Color(1, 2, 4, 8);
		assertNotSame(fixture, fixture.copy());
	}
	
	@Test
	public void checkToStringCompletesFormat() {
		assertFalse(fixture.toString().contains("%"));
	}

	@Test
	public void encodeOpaqueColour() throws CoderException {
		data = new byte[] { 1, 2, 4 };
		encoder.setData(data.length);

		fixture.setRed(data[0]);
		fixture.setGreen(data[1]);
		fixture.setBlue(data[2]);

		assertEquals(3, fixture.prepareToEncode(encoder));
		fixture.encode(encoder);

		assertEquals(24, encoder.getPointer());
		assertArrayEquals(data, encoder.getData());
	}

	@Test
	public void encodeTransparentColour() throws CoderException {
		data = new byte[] { 1, 2, 4, 8 };
		encoder.setData(data.length);
		encoder.getContext().setTransparent(true);

		fixture.setRed(data[0]);
		fixture.setGreen(data[1]);
		fixture.setBlue(data[2]);
		fixture.setAlpha(data[3]);

		assertEquals(4, fixture.prepareToEncode(encoder));
		fixture.encode(encoder);

		assertEquals(32, encoder.getPointer());
		assertArrayEquals(data, encoder.getData());
	}

	@Test
	public void decodeOpaqueColour() throws CoderException {
		data = new byte[] { 1, 2, 4 };
		decoder.setData(data);

		fixture.decode(decoder);

		assertEquals(24, decoder.getPointer());
		assertEquals(1, fixture.getRed());
		assertEquals(2, fixture.getGreen());
		assertEquals(4, fixture.getBlue());
		assertEquals(255, fixture.getAlpha());
	}

	@Test
	public void decodeTransparentColour() throws CoderException {
		data = new byte[] { 1, 2, 4, 8 };
		decoder.setData(data);
		decoder.getContext().setTransparent(true);

		fixture.decode(decoder);

		assertEquals(32, decoder.getPointer());
		assertEquals(1, fixture.getRed());
		assertEquals(2, fixture.getGreen());
		assertEquals(4, fixture.getBlue());
		assertEquals(8, fixture.getAlpha());
	}
}
