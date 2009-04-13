/*
 * MorphLineStyleTest.java
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
package com.flagstone.transform.movie.linestyle;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.DefineData;
import com.flagstone.transform.movie.Types;
import com.flagstone.transform.movie.datatype.Color;

@SuppressWarnings( { 
	"PMD.LocalVariableCouldBeFinal",
	"PMD.JUnitAssertionsShouldIncludeMessage" 
})
public final class MorphLineStyleTest {
	
	private transient final int startWidth = 1;
	private transient final Color startColor = new Color(2,3,4);
	private transient final int endWidth = 5;
	private transient final Color endColor = new Color(6,7,8);
	
	private transient MorphLineStyle fixture;
	
	private transient final byte[] encoded = new byte[] { 
			0x01, 0x00, 0x05, 0x00, 
			0x02, 0x03, 0x04, 0x06, 0x07, 0x08 };

	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForStartWidthWithLowerBound() {
		fixture = new MorphLineStyle();
		fixture.setStartWidth(-1);
	}

	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForStartWidthWithUpperBound() {
		fixture = new MorphLineStyle();
		fixture.setStartWidth(65536);
	}

	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForEndWidthWithLowerBound() {
		fixture = new MorphLineStyle();
		fixture.setEndWidth(-1);
	}

	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForEndWidthWithUpperBound() {
		fixture = new MorphLineStyle();
		fixture.setEndWidth(65536);
	}

	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForStartColorWithNull() {
		fixture = new MorphLineStyle();
		fixture.setStartColor(null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForEndColorWithNull() {
		fixture = new MorphLineStyle();
		fixture.setEndColor(null);
	}
	
	@Test
	public void checkCopy() {
		fixture = new MorphLineStyle(startWidth, endWidth, startColor, endColor);
		MorphLineStyle copy = fixture.copy();

		assertNotSame(fixture, copy);
		assertNotSame(fixture.getStartColor(), copy.getStartColor());
		assertNotSame(fixture.getEndColor(), copy.getEndColor());
		assertEquals(fixture.toString(), copy.toString());
	}
	
	@Test
	public void encode() throws CoderException {		
		SWFEncoder encoder = new SWFEncoder(encoded.length);		
		
		fixture = new MorphLineStyle(startWidth, endWidth, startColor, endColor);
		assertEquals(encoded.length, fixture.prepareToEncode(encoder));
		fixture.encode(encoder);
		
		assertTrue(encoder.eof());
		assertArrayEquals(encoded, encoder.getData());
	}

	@Test
	public void decode() throws CoderException {
		SWFDecoder decoder = new SWFDecoder(encoded);
		
		fixture = new MorphLineStyle();
		fixture.decode(decoder);
		
		assertTrue(decoder.eof());
		assertEquals(startWidth, fixture.getStartWidth());
		assertEquals(endWidth, fixture.getEndWidth());
		assertEquals(startColor.getRed(), fixture.getStartColor().getRed());
		assertEquals(startColor.getGreen(), fixture.getStartColor().getGreen());
		assertEquals(startColor.getBlue(), fixture.getStartColor().getBlue());
		assertEquals(startColor.getAlpha(), fixture.getStartColor().getAlpha());
		assertEquals(endColor.getRed(), fixture.getEndColor().getRed());
		assertEquals(endColor.getGreen(), fixture.getEndColor().getGreen());
		assertEquals(endColor.getBlue(), fixture.getEndColor().getBlue());
		assertEquals(endColor.getAlpha(), fixture.getEndColor().getAlpha());
	}
}
