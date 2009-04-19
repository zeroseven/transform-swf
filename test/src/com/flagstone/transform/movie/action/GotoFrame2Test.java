/*
 * GotoFrame2Test.java
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
package com.flagstone.transform.movie.action;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;


@SuppressWarnings( { 
	"PMD.LocalVariableCouldBeFinal",
	"PMD.JUnitAssertionsShouldIncludeMessage" 
})
public final class GotoFrame2Test {
	
	private transient final int type = ActionTypes.GOTO_FRAME_2;
	private transient boolean play = true;
	private transient final int offset = 1;
	
	private transient GotoFrame2 fixture;
	
	private transient final byte[] encoded = new byte[] { (byte)type, 0x03, 0x00, 
			0x03, 0x01, 0x00};

	private transient final byte[] stop = new byte[] { (byte)type, 0x01, 0x00, 
			0x00};

	private transient final byte[] noOffset = new byte[] { (byte)type, 0x01, 0x00, 
			0x01};

	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForIdentifierWithLowerBound() {
		fixture = new GotoFrame2(-1, play);
	}

	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForIdentifierWithUpperBound() {
		fixture = new GotoFrame2(65536, play);
	}
	
	@Test
	public void checkCopy() {
		fixture = new GotoFrame2(offset, play);
		GotoFrame2 copy = fixture.copy();

		assertNotSame(fixture, copy);
		assertEquals(fixture.toString(), copy.toString());
	}
	
	@Test
	public void encode() throws CoderException {		
		SWFEncoder encoder = new SWFEncoder(encoded.length);		
		SWFContext context = new SWFContext();

		fixture = new GotoFrame2(offset, play);
		assertEquals(encoded.length, fixture.prepareToEncode(encoder, context));
		fixture.encode(encoder, context);
		
		assertTrue(encoder.eof());
		assertArrayEquals(encoded, encoder.getData());
	}
	
	@Test
	public void encodeWithNoOffset() throws CoderException {		
		SWFEncoder encoder = new SWFEncoder(noOffset.length);		
		SWFContext context = new SWFContext();

		fixture = new GotoFrame2(0, play);
		assertEquals(noOffset.length, fixture.prepareToEncode(encoder, context));
		fixture.encode(encoder, context);
		
		assertTrue(encoder.eof());
		assertArrayEquals(noOffset, encoder.getData());
	}
	
	@Test
	public void encodeWithPlaySetToFalse() throws CoderException {		
		SWFEncoder encoder = new SWFEncoder(stop.length);		
		SWFContext context = new SWFContext();

		fixture = new GotoFrame2(0, false);
		assertEquals(stop.length, fixture.prepareToEncode(encoder, context));
		fixture.encode(encoder, context);
		
		assertTrue(encoder.eof());
		assertArrayEquals(stop, encoder.getData());
	}

	@Test
	public void decode() throws CoderException {
		SWFDecoder decoder = new SWFDecoder(encoded);
		SWFContext context = new SWFContext();

		fixture = new GotoFrame2(decoder, context);
		
		assertTrue(decoder.eof());
		assertEquals(offset, fixture.getFrameOffset());
		assertEquals(play, fixture.playFrame());
	}

	@Test
	public void decodeWithNoOffset() throws CoderException {
		SWFDecoder decoder = new SWFDecoder(noOffset);
		SWFContext context = new SWFContext();

		fixture = new GotoFrame2(decoder, context);
		
		assertTrue(decoder.eof());
		assertEquals(0, fixture.getFrameOffset());
		assertEquals(play, fixture.playFrame());
	}

	@Test
	public void decodeWithPlaySetToFalse() throws CoderException {
		SWFDecoder decoder = new SWFDecoder(stop);
		SWFContext context = new SWFContext();

		fixture = new GotoFrame2(decoder, context);
		
		assertTrue(decoder.eof());
		assertEquals(0, fixture.getFrameOffset());
		assertEquals(false, fixture.playFrame());
	}
}
