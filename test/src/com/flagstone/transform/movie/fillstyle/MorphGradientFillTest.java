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
package com.flagstone.transform.movie.fillstyle;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.datatype.Color;
import com.flagstone.transform.movie.datatype.CoordTransform;

@SuppressWarnings( { 
	"PMD.LocalVariableCouldBeFinal",
	"PMD.JUnitAssertionsShouldIncludeMessage" 
})
public final class MorphGradientFillTest {
	
	private transient final int type = FillStyle.LINEAR;
	private transient final CoordTransform start = 
		CoordTransform.translate(1,2);
	private transient final CoordTransform end = 
		CoordTransform.translate(1,2);
	private static final List<MorphGradient> list = new ArrayList<MorphGradient>();
	
	static {
		list.add(new MorphGradient(1, 5, new Color(2,3,4), new Color(6,7,8)));
		list.add(new MorphGradient(9, 13, new Color(10,11,12), new Color(14,15,16)));
	}
	
	private transient MorphGradientFill fixture;
		
	private transient final byte[] encoded = new byte[] { type, 
			0x06, 0x50, 0x06, 0x50, 
			0x02,
			0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
			0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10 
			};

	@Test(expected=IllegalArgumentException.class)
	public void checkAddNullGradient() {
		fixture = new MorphGradientFill(type, start, end, list);
		fixture.add(null);
	}

	@Test
	public void checkCopy() {
		fixture = new MorphGradientFill(type, start, end, list);
		MorphGradientFill copy = fixture.copy();

		assertNotSame(fixture.getStartTransform(), copy.getStartTransform());
		assertNotSame(fixture.getEndTransform(), copy.getEndTransform());
		assertNotSame(fixture.getGradients(), copy.getGradients());
		assertEquals(fixture.toString(), copy.toString());
	}
	
	@Test
	public void encode() throws CoderException {
		SWFEncoder encoder = new SWFEncoder(encoded.length);		
		
		fixture = new MorphGradientFill(type, start, end, list);
		assertEquals(encoded.length, fixture.prepareToEncode(encoder));
		fixture.encode(encoder);
		
		assertTrue(encoder.eof());
		assertArrayEquals(encoded, encoder.getData());
	}
	
	@Test
	public void decode() throws CoderException {
		SWFDecoder decoder = new SWFDecoder(encoded);
		
		fixture = new MorphGradientFill(decoder);
		
		assertTrue(decoder.eof());
		//TODO compare fields
	}
}
