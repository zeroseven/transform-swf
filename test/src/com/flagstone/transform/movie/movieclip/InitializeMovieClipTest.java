/*
 * InitializeMovieClipTest.java
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
package com.flagstone.transform.movie.movieclip;

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
import com.flagstone.transform.movie.action.Action;
import com.flagstone.transform.movie.action.BasicAction;
import com.flagstone.transform.movie.datatype.Color;
import com.flagstone.transform.movie.font.DefineFontName;
import com.flagstone.transform.movie.meta.Export;

@SuppressWarnings( { 
	"PMD.LocalVariableCouldBeFinal",
	"PMD.JUnitAssertionsShouldIncludeMessage" 
})
public final class InitializeMovieClipTest {
	
	private static final int identifier = 1;
	private static final List<Action> list = new ArrayList<Action>();
	
	static {
		list.add(BasicAction.ADD);
		list.add(BasicAction.END);
	}
	
	private transient InitializeMovieClip fixture;
		
	private transient final byte[] encoded = new byte[] { (byte)0xC4, 0x0E,
			0x01, 0x00, Types.ADD, Types.END };

	private transient final byte[] extended = new byte[] { (byte)0xFF, 0x0E,
			0x04, 0x00, 0x00, 0x00, 0x01, 0x00, Types.ADD, Types.END };

	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForIdentifierWithLowerBound() {
		fixture = new InitializeMovieClip(0, list);
	}

	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForIdentifierWithUpperBound() {
		fixture = new InitializeMovieClip(65536, list);
	}

	@Test(expected=IllegalArgumentException.class)
	public void checkAddNullNull() {
		fixture = new InitializeMovieClip(identifier, null);
	}

	@Test
	public void checkCopy() {
		fixture = new InitializeMovieClip(identifier, list);
		InitializeMovieClip copy = fixture.copy();

		assertEquals(fixture.getIdentifier(), copy.getIdentifier());
		assertNotSame(fixture.getActions(), copy.getActions());
		assertEquals(fixture.toString(), copy.toString());
	}
	
	@Test
	public void encode() throws CoderException {
		SWFEncoder encoder = new SWFEncoder(encoded.length);		
		
		fixture = new InitializeMovieClip(identifier, list);
		assertEquals(encoded.length, fixture.prepareToEncode(encoder));
		fixture.encode(encoder);
		
		assertTrue(encoder.eof());
		assertArrayEquals(encoded, encoder.getData());
	}
	
	@Test
	public void decode() throws CoderException {
		SWFDecoder decoder = new SWFDecoder(encoded);
		decoder.getContext().setDecodeActions(true);
		
		fixture = new InitializeMovieClip(decoder);
		
		assertTrue(decoder.eof());
		assertEquals(identifier, fixture.getIdentifier());
		assertEquals(list, fixture.getActions());
	}
	
	@Test
	public void decodeExtended() throws CoderException {
		SWFDecoder decoder = new SWFDecoder(extended);
		decoder.getContext().setDecodeActions(true);

		fixture = new InitializeMovieClip(decoder);
		
		assertTrue(decoder.eof());
		assertEquals(identifier, fixture.getIdentifier());
		assertEquals(list, fixture.getActions());
	}
}
