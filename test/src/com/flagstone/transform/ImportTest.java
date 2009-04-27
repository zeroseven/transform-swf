/*
 * ImportTest.java
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
package com.flagstone.transform;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

@SuppressWarnings( { "PMD.LocalVariableCouldBeFinal",
		"PMD.JUnitAssertionsShouldIncludeMessage" })
public final class ImportTest {

	private static String url = "ABC";

	private static Map<Integer, String> table = new LinkedHashMap<Integer, String>();

	static {
		table.put(1, "A");
		table.put(2, "B");
		table.put(3, "C");
	}

	private transient Import fixture;

	private transient final byte[] encoded = new byte[] { 0x52, 0x0E, 0x41,
			0x42, 0x43, 0x00, 0x03, 0x00, 0x01, 0x00, 0x41, 0x00, 0x02, 0x00,
			0x42, 0x00, 0x03, 0x00, 0x43, 0x00, };

	private transient final byte[] extended = new byte[] { 0x7F, 0x0E, 0x12,
			0x00, 0x00, 0x00, 0x41, 0x42, 0x43, 0x00, 0x03, 0x00, 0x01, 0x00,
			0x41, 0x00, 0x02, 0x00, 0x42, 0x00, 0x03, 0x00, 0x43, 0x00, };

	@Test(expected = IllegalArgumentException.class)
	public void checkAccessorForUrlWithNull() {
		fixture = new Import(null, table);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkAccessorForUrlWithEmpty() {
		fixture = new Import("", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkAccessorForIdentifierWithLowerBound() {
		fixture = new Import(url, table);
		fixture.add(0, "A");
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkAccessorForIdentifierWithUpperBound() {
		fixture = new Import(url, table);
		fixture.add(65536, "A");
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkAccessorForNameWithNull() {
		fixture = new Import(url, table);
		fixture.add(1, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkAccessorForNameWithEmpty() {
		fixture = new Import(url, table);
		fixture.add(1, "");
	}

	@Test
	public void checkCopy() {
		fixture = new Import(url, table);
		Import copy = fixture.copy();

		assertEquals(fixture.getUrl(), copy.getUrl());
		assertNotSame(fixture.getObjects(), copy.getObjects());
		assertEquals(fixture.toString(), copy.toString());
	}

	@Test
	public void encode() throws CoderException {
		SWFEncoder encoder = new SWFEncoder(encoded.length);
		Context context = new Context();

		fixture = new Import(url, table);
		assertEquals(encoded.length, fixture.prepareToEncode(encoder, context));
		fixture.encode(encoder, context);

		assertTrue(encoder.eof());
		assertArrayEquals(encoded, encoder.getData());
	}

	@Test
	public void decode() throws CoderException {
		SWFDecoder decoder = new SWFDecoder(encoded);

		fixture = new Import(decoder);

		assertTrue(decoder.eof());
		assertEquals(table, fixture.getObjects());
	}

	@Test
	public void decodeExtended() throws CoderException {
		SWFDecoder decoder = new SWFDecoder(extended);

		fixture = new Import(decoder);

		assertTrue(decoder.eof());
		assertEquals(table, fixture.getObjects());
	}
}
