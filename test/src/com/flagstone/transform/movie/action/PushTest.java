/*
 * PushTest.java
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

import java.util.ArrayList;
import java.util.List;
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
public final class PushTest {
		
	private transient static final List<Object> values = new ArrayList<Object>();
	
	static {
		values.add("a");
		values.add(Property.ALPHA);
		values.add(Null.getInstance());
		values.add(Void.getInstance());
		values.add(new RegisterIndex(1));
		values.add(new Boolean(true));
		values.add(new Double(1));
		values.add(new Integer(1));
		values.add(new TableIndex(1));
		values.add(new TableIndex(256));
	}

	private transient final int type = ActionTypes.PUSH;
	private transient Push fixture;
	
	// Values forming a function body are not part of the definition so the 
	// length must be adjusted accordingly.
		
	private transient final byte[] empty = new byte[] { (byte)type, 0x00, 0x00 };

	private transient final byte[] encoded = new byte[] { (byte)type, 0x21, 0x00, 
			 0x00, 0x61, 0x00,
			 0x01, 0x00, 0x00, (byte)0xC0, 0x40,
			 0x02,
			 0x03,
			 0x04, 0x01,
			 0x05, 0x01,
			 0x06, 0x00, 0x00, (byte)0xF0, 0x3F, 0x00, 0x00, 0x00, 0x00,
			 0x07, 0x01, 0x00, 0x00, 0x00,
			 0x08, 0x01,
			 0x09, 0x00, 0x01,
			 };

	@Test
	public void checkCopy() {
		fixture = new Push(values);
		Push copy = fixture.copy();

		assertNotSame(fixture.getValues(), copy.getValues());
		assertEquals(fixture.toString(), copy.toString());
	}
	
	@Test
	public void encode() throws CoderException {
		SWFEncoder encoder = new SWFEncoder(encoded.length);		
		SWFContext context = new SWFContext();

		fixture = new Push(values);
		assertEquals(encoded.length, fixture.prepareToEncode(encoder, context));
		fixture.encode(encoder, context);
		
		assertTrue(encoder.eof());
		assertArrayEquals(encoded, encoder.getData());
	}
	
	@Test
	public void decode() throws CoderException {
		SWFDecoder decoder = new SWFDecoder(encoded);
		SWFContext context = new SWFContext();

		fixture = new Push(decoder, context);
		
		assertTrue(decoder.eof());
		assertEquals(values, fixture.getValues());
	}
}
