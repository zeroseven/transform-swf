/*
 * GetUrlTest.java
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
package com.flagstone.transform.action;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import com.flagstone.transform.action.ActionTypes;
import com.flagstone.transform.action.GetUrl;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;


@SuppressWarnings( { 
	"PMD.LocalVariableCouldBeFinal",
	"PMD.JUnitAssertionsShouldIncludeMessage" 
})
public final class GetUrlTest {
	
	private transient final int type = ActionTypes.GET_URL;
	private transient final String url = "url";
	private transient final String target = "_blank";
	
	private transient GetUrl fixture;
	
	private transient final byte[] encoded = new byte[] { (byte)type, 0x0B, 0x00, 
			0x75, 0x72, 0x6C, 0x00, 0x5F, 0x62, 0x6C, 0x61, 0x6E, 0x6B, 0x00};
	
	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForUrlWithNull() {
		fixture = new GetUrl(null, target);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForUrlWithEmpty() {
		fixture = new GetUrl("", target);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForTargetWithNull() {
		fixture = new GetUrl(url, null);
	}

	@Test
	public void checkCopy() {
		fixture = new GetUrl(url, target);
		GetUrl copy = fixture.copy();

		assertNotSame(fixture, copy);
		assertEquals(fixture.toString(), copy.toString());
	}
	
	@Test
	public void encode() throws CoderException {		
		SWFEncoder encoder = new SWFEncoder(encoded.length);		
		Context context = new Context();

		fixture = new GetUrl(url, target);
		assertEquals(encoded.length, fixture.prepareToEncode(encoder, context));
		fixture.encode(encoder, context);
		
		assertTrue(encoder.eof());
		assertArrayEquals(encoded, encoder.getData());
	}

	@Test
	public void decode() throws CoderException {
		SWFDecoder decoder = new SWFDecoder(encoded);
		Context context = new Context();

		fixture = new GetUrl(decoder, context);
		
		assertTrue(decoder.eof());
		assertEquals(url, fixture.getUrl());
		assertEquals(target, fixture.getTarget());
	}
}
