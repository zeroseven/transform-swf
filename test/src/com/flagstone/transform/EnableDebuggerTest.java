/*
 * EnableDebuggerTest.java
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

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

@SuppressWarnings( { "PMD.LocalVariableCouldBeFinal",
		"PMD.JUnitAssertionsShouldIncludeMessage" })
public final class EnableDebuggerTest {

	private transient final String password = "ABC123";

	private transient EnableDebugger fixture;

	private transient final byte[] encoded = new byte[] { (byte) 0x89, 0x0E,
			0x00, 0x00, 0x41, 0x42, 0x043, 0x31, 0x32, 0x33, 0x00 };

	private transient final byte[] extended = new byte[] { (byte) 0xBF, 0x0E,
			0x09, 0x00, 0x00, 0x00, 0x00, 0x00, 0x41, 0x42, 0x043, 0x31, 0x32,
			0x33, 0x00 };

	@Test(expected = IllegalArgumentException.class)
	public void checkAccessorForPasswordWithNull() {
		fixture = new EnableDebugger((String) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkAccessorForPasswordWithEmpty() {
		fixture = new EnableDebugger("");
	}

	@Test
	public void checkCopy() {
		fixture = new EnableDebugger(password);
		EnableDebugger copy = fixture.copy();

		assertEquals(fixture.getPassword(), copy.getPassword());
		assertEquals(fixture.toString(), copy.toString());
	}

	@Test
	public void encode() throws CoderException {
		SWFEncoder encoder = new SWFEncoder(encoded.length);
		Context context = new Context();

		fixture = new EnableDebugger(password);
		assertEquals(encoded.length, fixture.prepareToEncode(encoder, context));
		fixture.encode(encoder, context);

		assertTrue(encoder.eof());
		assertArrayEquals(encoded, encoder.getData());
	}

	@Test
	public void decode() throws CoderException {
		SWFDecoder decoder = new SWFDecoder(encoded);

		fixture = new EnableDebugger(decoder);

		assertTrue(decoder.eof());
		assertEquals(password, fixture.getPassword());
	}

	@Test
	public void decodeExtended() throws CoderException {
		SWFDecoder decoder = new SWFDecoder(extended);

		fixture = new EnableDebugger(decoder);

		assertTrue(decoder.eof());
		assertEquals(password, fixture.getPassword());
	}
}
