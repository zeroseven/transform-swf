/*
 * PlaceTest.java
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
package com.flagstone.transform.movie;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.datatype.ColorTransform;
import com.flagstone.transform.movie.datatype.CoordTransform;

@SuppressWarnings( { 
	"PMD.LocalVariableCouldBeFinal",
	"PMD.JUnitAssertionsShouldIncludeMessage" 
})
public final class PlaceTest {
	
	private transient final int identifier = 1;
	private transient final int layer = 2;
	private transient final CoordTransform transform = 
		CoordTransform.translate(1,2);
	private transient final ColorTransform colorTransform = 
		new ColorTransform(1,2,3,4);
	
	private transient Place fixture;
	
	private transient final byte[] empty = new byte[] { 0x05, 0x01, 0x00, 0x00,
			0x00, 0x00, 0x00};

	private transient final byte[] coord = new byte[] { 0x06, 0x01, 
			0x01, 0x00, 0x02, 0x00, 0x06, 0x50};
	
	private transient final byte[] coordAndColor = new byte[] { 0x08, 0x01, 
			0x01, 0x00, 0x02, 0x00, 0x06, 0x50, (byte)0x8C, (byte)0xA6};
	
	private transient final byte[] extended = new byte[] { 0x7F, 0x01, 
			0x06, 0x00, 0x00, 0x00, 0x01, 0x00, 0x02, 0x00, 0x06, 0x50};

	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForDepthWithLowerBound() {
		fixture = new Place(0, layer, transform);
	}

	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForDepthWithUpperBound() {
		fixture = new Place(65536, layer, transform);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForTimeoutWithLowerBound() {
		fixture = new Place(identifier, 0, transform);
	}

	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForTimeoutWithUpperBound() {
		fixture = new Place(identifier, 65536, transform);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForCoordTransformWithNull() {
		fixture = new Place(identifier, layer, null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForColorTransformWithNull() {
		fixture = new Place(identifier, layer, transform, null);
	}

	@Test
	public void checkCopy() {
		fixture = new Place(identifier, layer, transform, colorTransform);
		Place copy = fixture.copy();

		assertNotSame(fixture, copy);
		assertEquals(fixture.getIdentifier(), copy.getIdentifier());
		assertEquals(fixture.getLayer(), copy.getLayer());
		assertNotSame(fixture.getTransform(), copy.getTransform());
		assertNotSame(fixture.getColorTransform(), copy.getColorTransform());
		assertEquals(fixture.toString(), copy.toString());
	}
	
	@Test
	public void encodeCoordTransform() throws CoderException {		
		SWFEncoder encoder = new SWFEncoder(coord.length);		
		
		fixture = new Place(identifier, layer, transform);
		assertEquals(coord.length, fixture.prepareToEncode(encoder));
		fixture.encode(encoder);
		
		assertTrue(encoder.eof());
		assertArrayEquals(coord, encoder.getData());
	}
	
	@Test
	public void encodeCoordAndColorTransforms() throws CoderException {		
		SWFEncoder encoder = new SWFEncoder(coordAndColor.length);		
		
		fixture = new Place(identifier, layer, transform, colorTransform);
		assertEquals(coordAndColor.length, fixture.prepareToEncode(encoder));
		fixture.encode(encoder);
		
		assertTrue(encoder.eof());
		assertArrayEquals(coordAndColor, encoder.getData());
	}
	
	@Test
	public void decode() throws CoderException {
		SWFDecoder decoder = new SWFDecoder(coord);
		
		fixture = new Place(decoder);
		
		assertTrue(decoder.eof());
		assertEquals(identifier, fixture.getIdentifier());
		assertEquals(layer, fixture.getLayer());
		assertEquals(transform.getTranslateX(), fixture.getTransform().getTranslateX());
		assertEquals(transform.getTranslateY(), fixture.getTransform().getTranslateY());
	}
	
	@Test
	public void decodeExtended() throws CoderException {
		SWFDecoder decoder = new SWFDecoder(extended);
		
		fixture = new Place(decoder);
		
		assertTrue(decoder.eof());
		assertEquals(identifier, fixture.getIdentifier());
		assertEquals(layer, fixture.getLayer());
		assertEquals(transform.getTranslateX(), fixture.getTransform().getTranslateX());
		assertEquals(transform.getTranslateY(), fixture.getTransform().getTranslateY());
	}
	
	@Test
	public void decodeCoordAndColorTransforms() throws CoderException {
		SWFDecoder decoder = new SWFDecoder(coordAndColor);
		
		fixture = new Place(decoder);
		
		assertTrue(decoder.eof());
		assertEquals(identifier, fixture.getIdentifier());
		assertEquals(layer, fixture.getLayer());
		assertEquals(transform.getTranslateX(), fixture.getTransform().getTranslateX());
		assertEquals(transform.getTranslateY(), fixture.getTransform().getTranslateY());
		assertEquals(colorTransform.getAddRed(), fixture.getColorTransform().getAddRed());
		assertEquals(colorTransform.getAddGreen(), fixture.getColorTransform().getAddGreen());
		assertEquals(colorTransform.getAddBlue(), fixture.getColorTransform().getAddBlue());
	}
}
