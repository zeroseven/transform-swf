/*
 * MorphGradientFillTest.java
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

import java.util.ArrayList;
import java.util.List;
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
import com.flagstone.transform.datatype.CoordTransform;



public final class MorphGradientFillTest {

	private static transient boolean radial = false;
	private static transient CoordTransform start = CoordTransform.translate(1, 2);
	private static transient CoordTransform end = CoordTransform.translate(1, 2);
	private static transient List<MorphGradient> list = new ArrayList<MorphGradient>();

	static {
		list.add(new MorphGradient(new Gradient(1, new Color(2, 3, 4, 5)),
				new Gradient(6, new Color(7, 8, 9, 10))));
		list.add(new MorphGradient(new Gradient(11, new Color(12, 13, 14, 15)),
				new Gradient(16, new Color(17, 18, 19, 20))));
	}

	private transient MorphGradientFill fixture;

	private transient final byte[] encoded = new byte[] { 0x10, 0x06, 0x50,
			0x06, 0x50, 0x02, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
			0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10, 0x11, 0x12, 0x13,
			0x14 };

	@Test(expected = IllegalArgumentException.class)
	public void checkAddNullGradient() {
		fixture = new MorphGradientFill(radial, start, end, list);
		fixture.add(null);
	}

	@Test
	public void checkCopy() {
		fixture = new MorphGradientFill(radial, start, end, list);
		final MorphGradientFill copy = fixture.copy();

		assertSame(fixture.getStartTransform(), copy.getStartTransform());
		assertSame(fixture.getEndTransform(), copy.getEndTransform());
		assertNotSame(fixture.getGradients(), copy.getGradients());
		assertEquals(fixture.toString(), copy.toString());
	}

	@Test
	public void encode() throws CoderException {
		final SWFEncoder encoder = new SWFEncoder(encoded.length);
		final Context context = new Context();
		context.getVariables().put(Context.TRANSPARENT, 1);

		fixture = new MorphGradientFill(radial, start, end, list);
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

		fixture = new MorphGradientFill(decoder, context);

		assertTrue(decoder.eof());
		// TODO compare fields
	}
}
