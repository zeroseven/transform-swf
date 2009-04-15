/*
 * NewFunction2Test.java
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.Types;

@SuppressWarnings( { 
	"PMD.LocalVariableCouldBeFinal",
	"PMD.JUnitAssertionsShouldIncludeMessage" 
})
public final class NewFunction2Test {
	
	private static final String name = "function";
	private static final Map<String,Integer> args = new LinkedHashMap<String,Integer>();
	private static final List<Action> actions = new ArrayList<Action>();
	
	static {
		args.put("a", 1);
		args.put("b", 2);
		
		actions.add(BasicAction.ADD);
		actions.add(BasicAction.END);
	}
	
	private transient final int type = Types.NEW_FUNCTION_2;
	private transient NewFunction2 fixture;
	
	// Actions forming a function body are not part of the definition so the 
	// length must be adjusted accordingly.
		
	private transient final byte[] empty = new byte[] { (byte)type, 0x08, 0x00, 
			0x00, 
			0x00, 0x00, 0x00, 0x00, 0x00,
			0x01, 0x00, Types.END };

	private transient final byte[] encoded = new byte[] { (byte)type, 0x16, 0x00, 
			0x66, 0x75, 0x6E, 0x63, 0x74, 0x69, 0x6F, 0x6E, 0x00, 
			0x02, 0x00, 0x00, 0x00, 0x00,
			0x01, 0x61, 0x00, 
			0x02, 0x62, 0x00,
			0x02, 0x00, Types.ADD, Types.END };

	@Test(expected=IllegalArgumentException.class)
	public void checkAddNullArgument() {
		fixture = new NewFunction2(name, args, actions);
		fixture.add((String)null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void checkAddEmptyArgument() {
		fixture = new NewFunction2(name, args, actions);
		fixture.add("");
	}

	@Test(expected=IllegalArgumentException.class)
	public void checkAddNullAction() {
		fixture = new NewFunction2(name, args, actions);
		fixture.add((Action)null);
	}

	@Test
	public void checkCopy() {
		fixture = new NewFunction2(name, args, actions);
		NewFunction2 copy = fixture.copy();

		assertNotSame(fixture.getActions(), copy.getActions());
		assertEquals(fixture.toString(), copy.toString());
	}
	
	@Test
	public void encode() throws CoderException {
		SWFEncoder encoder = new SWFEncoder(encoded.length);		
		
		fixture = new NewFunction2(name, args, actions);
		assertEquals(encoded.length, fixture.prepareToEncode(encoder));
		fixture.encode(encoder);
		
		assertTrue(encoder.eof());
		assertArrayEquals(encoded, encoder.getData());
	}
	
	@Test
	public void decode() throws CoderException {
		SWFDecoder decoder = new SWFDecoder(encoded);
		
		fixture = new NewFunction2(decoder);

		assertTrue(decoder.eof());
		assertEquals(name, fixture.getName());
		assertEquals(args, fixture.getArguments());
		assertEquals(actions, fixture.getActions());
	}
}
