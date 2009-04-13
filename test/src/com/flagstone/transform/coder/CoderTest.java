/*
 * CoderTest.java
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

import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

import org.junit.Before;
import org.junit.Test;

import com.flagstone.transform.coder.Coder;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotSame;

@SuppressWarnings( { "PMD.JUnitAssertionsShouldIncludeMessage" })
public final class CoderTest {
	private transient Coder fixture;

	private transient byte[] data;

	@Before
	public void setUp() {
		fixture = new Coder();
	}

	@Test
	public void setStringEncodingToValidCharacterSet() {
		fixture.setEncoding("UTF-8");
		assertEquals("UTF-8", fixture.getEncoding());
	}

	@Test(expected = UnsupportedCharsetException.class)
	public void setStringEncodingToInvalidCharacterSet() {
		fixture.setEncoding("null");
	}

	@Test(expected = IllegalCharsetNameException.class)
	public void setStringEncodingWithEmptyString() {
		fixture.setEncoding("");
	}

	@Test
	public void checkSetDataCreatesCopy() {
		data = new byte[] { 1, 2, 3, 4, 5 };

		fixture.setData(data);

		assertNotSame(data, fixture.data);
		assertArrayEquals(data, fixture.data);
	}

	@Test
	public void checkGetDataCreatesCopy() {
		data = new byte[] { 1, 2, 3, 4, 5 };

		fixture.data = data;

		assertNotSame(data, fixture.getData());
		assertArrayEquals(data, fixture.getData());
	}

	@Test
	public void checkAccessorForPointer() {
		fixture.setData(new byte[] { 1, 2, 3, 4 });
		fixture.setPointer(8);

		assertEquals(8, fixture.getPointer());
	}

	@Test
	public void checkSetPointerGoesToCorrectLocation() {
		fixture.setData(new byte[] { 1, 2, 3, 4 });
		fixture.setPointer(8);

		assertEquals(fixture.data[1], fixture.data[fixture.index]);
	}

	@Test
	public void checkAdjustPointerGoesToCorrectLocation() {
		fixture.setData(new byte[] { 1, 2, 3, 4 });

		fixture.adjustPointer(8);
		assertEquals(8, fixture.getPointer());

		fixture.adjustPointer(-8);
		assertEquals(0, fixture.getPointer());
	}

	@Test
	public void checkAlignToByteOnByteBoundaryLeavesPointerUnchanged() {
		fixture.setData(new byte[] { 1, 2, 3, 4 });
		fixture.setPointer(8);
		fixture.alignToByte();

		assertEquals(8, fixture.getPointer());
	}

	@Test
	public void checkAlignToByteOnBitBoundaryChangesPointer() {
		fixture.setData(new byte[] { 1, 2, 3, 4 });
		fixture.setPointer(9);
		fixture.alignToByte();

		assertEquals(16, fixture.getPointer());
	}

	@Test
	public void checkPointerIsAtEndOfFile() {
		fixture.setData(new byte[] { 1, 2, 3, 4 });
		fixture.setPointer(32);

		assertTrue(fixture.eof());
	}
}
