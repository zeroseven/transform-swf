/*
 * DecoderTest.java
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
package com.flagstone.transform.coder;

import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public final class DecoderTest {
	private transient Decoder fixture;

	@Before
	public void setUp() {
		fixture = new Decoder(new byte[0]);
	}

	@Test
	public void readBitsForUnsignedNumber() {
		fixture.setData(new byte[] { 3 });
		fixture.setPointer(6);

		assertEquals(3, fixture.readBits(2, false));
		assertEquals(8, fixture.getPointer());
	}

	@Test
	public void readBitsForSignedNumber() {
		fixture.setData(new byte[] { 3 });
		fixture.setPointer(6);

		assertEquals(-1, fixture.readBits(2, true));
		assertEquals(8, fixture.getPointer());
	}

	@Test
	public void readBitsToEndOfBuffer() {
		fixture.setData(new byte[] { 3 });
		fixture.setPointer(6);

		assertEquals(3, fixture.readBits(2, false));
		assertEquals(8, fixture.getPointer());
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void readBitsBeyondEndOfBuffer() {
		fixture.setData(new byte[] { 3 });
		fixture.setPointer(6);

		fixture.readBits(4, true);
	}

	@Test
	public void readBitsAcrossByteBoundary() {
		fixture.setData(new byte[] { 3, (byte) 0xC0 });
		fixture.setPointer(6);

		assertEquals(-1, fixture.readBits(4, true));
	}

	@Test
	public void readBitsAcrossIntBoundary() {
		fixture.setData(new byte[] { 0, 0, 0, 3, (byte) 0xC0 });
		fixture.setPointer(30);

		assertEquals(-1, fixture.readBits(4, true));
	}

	@Test
	public void readZeroBits() {
		fixture.setData(new byte[] { 3 });
		fixture.setPointer(2);

		assertEquals(0, fixture.readBits(0, true));
		assertEquals(2, fixture.getPointer());
	}

	@Test
	public void readB16() {
		fixture.setData(new byte[] { 1, 2 });

		assertEquals(0x0102, fixture.readB16());
		assertEquals(16, fixture.getPointer());
	}

	@Test
	public void readByte() {
		fixture.setData(new byte[] { 1, 2 });

		assertEquals(1, fixture.readByte());
		assertEquals(2, fixture.readByte());
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void readByteBeyondEndOfBuffer() {
		fixture.setData(new byte[] { 1, 2 });

		fixture.readByte();
		fixture.readByte();
		fixture.readByte();
	}

	@Test
	public void readBytes() {
		final byte[] data = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };
		final byte[] buffer = new byte[data.length];

		fixture.setData(data);
		fixture.readBytes(buffer);

		assertEquals(data.length << 3, fixture.getPointer());
		assertArrayEquals(data, buffer);
	}

	@Test
	public void readNullTerminatedString() {
		fixture.setData(new byte[] { 0x31, 0x32, 0x33, 0x00 });
		fixture.encoding = "UTF-8";

		assertEquals("123", fixture.readString());
		assertEquals(32, fixture.getPointer());
	}

	@Test
	public void readStringWithLength() {
		fixture.setData(new byte[] { 0x31, 0x32, 0x33, 0x00 });
		fixture.encoding = "UTF-8";

		assertEquals("123", fixture.readString(3));
		assertEquals(24, fixture.getPointer());
	}

	@Test
	public void readNullTerminatedStringWithLength() {
		fixture.setData(new byte[] { 0x31, 0x32, 0x33, 0x00 });
		fixture.encoding = "UTF-8";

		assertEquals("123\0", fixture.readString(4));
		assertEquals(32, fixture.getPointer());
	}

	@Test
	public void findBitsWithSuccess() {
		fixture.setData(new byte[] { 0x30 });

		assertTrue(fixture.findBits(3, 2, 1));
		assertEquals(2, fixture.getPointer());
	}

	@Test
	public void findBitsWithoutSuccess() {
		fixture.setData(new byte[] { 0x0C });
		fixture.setPointer(2);

		assertFalse(fixture.findBits(5, 3, 1));
		assertEquals(2, fixture.getPointer());
	}

	@Test
	public void findBitsWithSuccessAtEndOfBuffer() {
		fixture.setData(new byte[] { 0x05 });

		assertTrue(fixture.findBits(5, 3, 1));
		assertEquals(5, fixture.getPointer());
	}
}
