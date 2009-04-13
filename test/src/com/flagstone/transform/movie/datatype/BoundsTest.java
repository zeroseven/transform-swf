/*
 * BoundsTest.java
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Before;
import org.junit.Test;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

@SuppressWarnings( { "PMD.TooManyMethods",
    "PMD.LocalVariableCouldBeFinal",
	"PMD.JUnitAssertionsShouldIncludeMessage" })
public final class BoundsTest {

	private transient Bounds fixture;

	private transient SWFEncoder encoder;
	private transient SWFDecoder decoder;
	private transient byte[] data;

	@Before
	public void setUp() {
		fixture = new Bounds();
		encoder = new SWFEncoder(0);
		decoder = new SWFDecoder(new byte[] {});
	}

	@Test
	public void checkConstructor() {
		fixture = new Bounds(1, 2, 3, 4);
		assertEquals(1, fixture.getMinX());
		assertEquals(2, fixture.getMinY());
		assertEquals(3, fixture.getMaxX());
		assertEquals(4, fixture.getMaxY());
	}

	@Test
	public void checkToStringCompletesFormat() {
		assertFalse(fixture.toString().contains("%"));
	}

	@Test
	public void encodeWithBoundsEmpty() throws CoderException {

		data = new byte[] { 8, 0 };
		encoder.setData(data.length);

		assertEquals(2, fixture.prepareToEncode(encoder));
		fixture.encode(encoder);

		assertEquals(16, encoder.getPointer());
		assertArrayEquals(data, encoder.getData());
	}

	@Test
	public void encodeWithBoundsSet() throws CoderException {

		data = new byte[] { 32, -103, 32 };
		encoder.setData(data.length);

		fixture = new Bounds(1, 2, 3, 4);

		assertEquals(3, fixture.prepareToEncode(encoder));
		fixture.encode(encoder);

		assertEquals(24, encoder.getPointer());
		assertArrayEquals(data, encoder.getData());
	}

	@Test
	public void decodeWithBoundsEmpty() throws CoderException {

		data = new byte[] { 8, 0 };
		decoder.setData(data);

		fixture.decode(decoder);

		assertEquals(16, decoder.getPointer());
		assertEquals(0, fixture.getMinX());
		assertEquals(0, fixture.getMinY());
		assertEquals(0, fixture.getMaxX());
		assertEquals(0, fixture.getMaxY());
	}

	@Test
	public void decodeWithBoundsSet() throws CoderException {

		data = new byte[] { 32, -103, 32 };
		decoder.setData(data);

		fixture.decode(decoder);

		assertEquals(24, decoder.getPointer());
		assertEquals(1, fixture.getMinX());
		assertEquals(2, fixture.getMinY());
		assertEquals(3, fixture.getMaxX());
		assertEquals(4, fixture.getMaxY());
	}
}
