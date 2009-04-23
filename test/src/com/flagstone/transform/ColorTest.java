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
package com.flagstone.transform;

import org.junit.Test;

import com.flagstone.transform.Color;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SuppressWarnings( { 
    "PMD.LocalVariableCouldBeFinal",
	"PMD.JUnitAssertionsShouldIncludeMessage" })
public final class ColorTest {
	
	private transient final int red = 1;
	private transient final int green = 2;
	private transient final int blue = 3;
	private transient final int alpha = 4;

	private transient Color fixture;

	private transient byte[] opaque = new byte[] {1,2,3};
	private transient byte[] transparent = new byte[] {1,2,3,4};

	private transient SWFEncoder encoder;
	private transient SWFDecoder decoder; 
	private transient Context context;

	@Test(expected = IllegalArgumentException.class)
	public void checkRedBelowRangeThrowsException() {
		fixture = new Color(-1, 2, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkRedAboveRangeThrowsException() {
		fixture = new Color(256, 2, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkGreenBelowRangeThrowsException() {
		fixture = new Color(1, -1, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkGreenAboveRangeThrowsException() {
		fixture = new Color(1, 256, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkBlueBelowRangeThrowsException() {
		fixture = new Color(1, 2, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkBlueAboveRangeThrowsException() {
		fixture = new Color(1, 2, 256);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkAlphaBelowRangeThrowsException() {
		fixture = new Color(1, 2, 3, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkAlphaAboveRangeThrowsException() {
		fixture = new Color(1, 2, 3, 256);
	}

	@Test
	public void checkNullIsnotEqual() {
		assertFalse(new Color(red, green, blue, alpha).equals(null));
	}

	@Test
	public void checkObjectIsNotEqual() {
		assertFalse(new Color(red, green, blue, alpha).equals(new Object()));
	}

	@Test
	public void checkSameIsEqual() {
		fixture = new Color(red, green, blue, alpha);
		assertTrue(fixture.equals(fixture));
	}

	@Test
	public void checkIsNotEqual() {
		fixture = new Color(red, green, blue, alpha);
		assertFalse(fixture.equals(new Color(4,3,2,1)));
	}

	@Test
	public void checkOtherIsEqual() {
		assertTrue(new Color(red, green, blue, alpha).equals(new Color(red, green, blue, alpha)));
	}

	@Test
	public void encodeOpaqueColour() throws CoderException {
		encoder = new SWFEncoder(opaque.length);
		context = new Context();
		
		fixture = new Color(red, green, blue);

		assertEquals(opaque.length, fixture.prepareToEncode(encoder, context));
		fixture.encode(encoder, context);

		assertTrue(encoder.eof());
		assertArrayEquals(opaque, encoder.getData());
	}

	@Test
	public void encodeTransparentColour() throws CoderException {
		encoder = new SWFEncoder(transparent.length);
		context = new Context();
		context.getVariables().put(Context.TRANSPARENT, 1);
		
		fixture = new Color(red, green, blue, alpha);

		assertEquals(transparent.length, fixture.prepareToEncode(encoder, context));
		fixture.encode(encoder, context);

		assertTrue(encoder.eof());
		assertArrayEquals(transparent, encoder.getData());
	}

	@Test
	public void decodeOpaqueColour() throws CoderException {
		decoder = new SWFDecoder(opaque);
		context = new Context();

		fixture = new Color(decoder, context);

		assertTrue(decoder.eof());
		assertEquals(red, fixture.getRed());
		assertEquals(green, fixture.getGreen());
		assertEquals(blue, fixture.getBlue());
		assertEquals(255, fixture.getAlpha());
	}

	@Test
	public void decodeTransparentColour() throws CoderException {
		decoder = new SWFDecoder(transparent);
		context = new Context();
		context.getVariables().put(Context.TRANSPARENT, 0);

		fixture = new Color(decoder, context);

		assertTrue(decoder.eof());
		assertEquals(red, fixture.getRed());
		assertEquals(green, fixture.getGreen());
		assertEquals(blue, fixture.getBlue());
		assertEquals(alpha, fixture.getAlpha());
	}
}
