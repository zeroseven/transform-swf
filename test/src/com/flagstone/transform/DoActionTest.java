/*
 * DoActionTest.java
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

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import com.flagstone.transform.DoAction;
import com.flagstone.transform.action.Action;
import com.flagstone.transform.action.ActionData;
import com.flagstone.transform.action.BasicAction;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

@SuppressWarnings( { 
	"PMD.LocalVariableCouldBeFinal",
	"PMD.JUnitAssertionsShouldIncludeMessage" })
public final class DoActionTest {
	
	private transient List<Action> actions;

	private transient DoAction fixture;
	
	private transient final byte[] empty = new byte[] { (byte)0x01, 0x03,
			0x00};
	
	private transient final byte[] encoded = new byte[] { (byte)0x02, 0x03,
			0x04, 0x00};
	
	private transient final byte[] extended = new byte[] { (byte)0x3F, 0x03, 
			0x02, 0x00, 0x00, 0x00, 0x04, 0x00};

	@Before
	public void setUp() {
		actions = new ArrayList<Action>();
		actions.add(BasicAction.NEXT_FRAME);
		actions.add(BasicAction.END);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForActionsWithNull() {
		fixture = new DoAction();
		fixture.setActions(null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void checkAddNullAction() {
		fixture = new DoAction();
		fixture.add(null);
	}

	@Test
	public void checkCopy() {
		fixture = new DoAction(actions);
		assertNotSame(fixture, fixture.copy());
		assertEquals(actions, fixture.copy().getActions());
		assertEquals(fixture.toString(), fixture.copy().toString());
	}
	
	@Test
	public void encode() throws CoderException {		
		SWFEncoder encoder = new SWFEncoder(encoded.length);	
		SWFContext context = new SWFContext();

		fixture = new DoAction(actions);
		assertEquals(4, fixture.prepareToEncode(encoder, context));
		fixture.encode(encoder, context);
		
		assertTrue(encoder.eof());
		assertArrayEquals(encoded, encoder.getData());
	}
	
	@Test
	public void encodeDefault() throws CoderException {		
		SWFEncoder encoder = new SWFEncoder(empty.length);	
		SWFContext context = new SWFContext();

		fixture = new DoAction();
		assertEquals(empty.length, fixture.prepareToEncode(encoder, context));
		fixture.encode(encoder, context);
		
		assertTrue(encoder.eof());
		assertArrayEquals(empty, encoder.getData());
	}
	
	@Test
	public void encodeExtended() throws CoderException {		
		SWFEncoder encoder = new SWFEncoder(106);	
		SWFContext context = new SWFContext();

		fixture = new DoAction();
		
		for (int i=0; i<99; i++) {
			fixture.add(BasicAction.ADD);
		}
		
		fixture.add(BasicAction.END);
		
		assertEquals(106, fixture.prepareToEncode(encoder, context));
		fixture.encode(encoder, context);
		
		assertTrue(encoder.eof());
	}
	
	@Test
	public void checkDecode() throws CoderException {
		SWFDecoder decoder = new SWFDecoder(encoded);
		SWFContext context = new SWFContext();

		context.setDecodeActions(true);
		
		fixture = new DoAction(decoder, context);
		
		assertTrue(decoder.eof());
		assertEquals(actions, fixture.getActions());
	}
	
	@Test
	public void checkDecodeExtended() throws CoderException {
		SWFDecoder decoder = new SWFDecoder(extended);
		SWFContext context = new SWFContext();

		context.setDecodeActions(true);
		
		fixture = new DoAction(decoder, context);
		
		assertTrue(decoder.eof());
		assertEquals(actions, fixture.getActions());
	}
	
	@Test
	public void checkDecodeContainsActionData() throws CoderException {
		SWFDecoder decoder = new SWFDecoder(encoded);
		SWFContext context = new SWFContext();

		context.setDecodeActions(false);

		fixture = new DoAction(decoder, context);
		
		assertEquals(1, fixture.getActions().size());
		assertTrue(fixture.getActions().get(0) instanceof ActionData);
	}
}
