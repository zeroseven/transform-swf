/*
 * MorphGradientTest.java
 * Transform
 *
 * Copyright (c) 2009 Flagstone Software Ltd. All rights reserved.
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

import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.datatype.Color;



public final class MorphGradientTest {

	private static transient final int startRatio = 1;
	private static transient final Color startColor = new Color(2, 3, 4, 5);
	private static transient final int endRatio = 6;
	private static transient final Color endColor = new Color(7, 8, 9, 10);

	private transient MorphGradient fixture;

	private transient final byte[] encoded = new byte[] { 0x01, 0x02, 0x03,
			0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A };

	@Test(expected = IllegalArgumentException.class)
	public void checkAccessorForRatioWithLowerBound() {
		fixture = new MorphGradient(new Gradient(-1, startColor), new Gradient(
				endRatio, endColor));
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkAccessorForRatioWithUpperBound() {
		fixture = new MorphGradient(new Gradient(256, startColor),
				new Gradient(endRatio, endColor));
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkAccessorForColorWithNull() {
		fixture = new MorphGradient(new Gradient(startRatio, null),
				new Gradient(endRatio, endColor));
	}

	@Test
	public void checkCopy() {
		fixture = new MorphGradient(new Gradient(startRatio, startColor),
				new Gradient(endRatio, endColor));
		final MorphGradient copy = fixture.copy();

		assertNotSame(fixture, copy);
		assertSame(fixture.getStart().getColor(), copy.getStart().getColor());
		assertEquals(fixture.toString(), copy.toString());
	}

	@Test
	public void encode() throws CoderException {
		final SWFEncoder encoder = new SWFEncoder(encoded.length);
		final Context context = new Context();
		context.getVariables().put(Context.TRANSPARENT, 1);

		fixture = new MorphGradient(new Gradient(startRatio, startColor),
				new Gradient(endRatio, endColor));
		assertEquals(encoded.length, fixture.prepareToEncode(encoder, context));
		fixture.encode(encoder, context);

		assertTrue(encoder.eof());
		assertArrayEquals(encoded, encoder.getData());
	}

	@Test
	public void decode() throws CoderException {
		final SWFDecoder decoder = new SWFDecoder(encoded);
		final Context context = new Context();
		context.getVariables().put(Context.TRANSPARENT, 1);

		fixture = new MorphGradient(decoder, context);

		assertTrue(decoder.eof());
		assertEquals(startRatio, fixture.getStart().getRatio());
		assertEquals(startColor.getRed(), fixture.getStart().getColor()
				.getRed());
		assertEquals(startColor.getGreen(), fixture.getStart().getColor()
				.getGreen());
		assertEquals(startColor.getBlue(), fixture.getStart().getColor()
				.getBlue());
		assertEquals(startColor.getAlpha(), fixture.getStart().getColor()
				.getAlpha());
	}
}
