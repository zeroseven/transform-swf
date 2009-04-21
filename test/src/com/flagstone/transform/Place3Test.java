/*
 * Place3Test.java
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

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import com.flagstone.transform.ColorTransform;
import com.flagstone.transform.CoordTransform;
import com.flagstone.transform.Place2;
import com.flagstone.transform.Place3;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

@SuppressWarnings( { 
	"PMD.LocalVariableCouldBeFinal",
	"PMD.JUnitAssertionsShouldIncludeMessage" 
})
public final class Place3Test {
	
	private transient final int identifier = 1;
	private transient final int layer = 2;
	private transient final CoordTransform transform = 
		CoordTransform.translate(1,2);
	private transient final ColorTransform colorTransform = 
		new ColorTransform(1,2,3,4);
	
	private transient Place3 fixture;
	
	private transient final byte[] empty = new byte[] { 0x05, 0x01, 0x00, 0x00,
			0x00, 0x00, 0x00};

	private transient final byte[] coord = new byte[] { 0x06, 0x01, 
			0x01, 0x00, 0x02, 0x00, 0x06, 0x50};
	
	private transient final byte[] coordAndColor = new byte[] { 0x08, 0x01, 
			0x01, 0x00, 0x02, 0x00, 0x06, 0x50, (byte)0x8C, (byte)0xA6};
	
	private transient final byte[] extended = new byte[] { 0x7F, 0x01, 
			0x06, 0x00, 0x00, 0x00, 0x01, 0x00, 0x02, 0x00, 0x06, 0x50};

	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForIdentifierWithLowerBound() {
		fixture = new Place3().setIdentifier(0);
	}

	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForIdentifierWithUpperBound() {
		fixture = new Place3().setIdentifier(65536);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForLayerWithLowerBound() {
		fixture = new Place3().setLayer(0);
	}

	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForLayerWithUpperBound() {
		fixture = new Place3().setLayer(65536);
	}
	
	@Test(expected=IllegalArgumentException.class) @Ignore
	public void checkAccessorForColorTransformWithNull() {
		fixture = new Place3().setColorTransform(null);
	}

	@Test @Ignore
	public void checkCopy() {
		//fixture = new Place3(identifier, layer, transform, colorTransform);
		Place3 copy = fixture.copy();

		assertNotSame(fixture, copy);
		assertEquals(fixture.getIdentifier(), copy.getIdentifier());
		assertEquals(fixture.getLayer(), copy.getLayer());
		assertSame(fixture.getTransform(), copy.getTransform());
		assertSame(fixture.getColorTransform(), copy.getColorTransform());
		assertEquals(fixture.toString(), copy.toString());
	}
	
	@Test @Ignore
	public void encodeCoordTransform() throws CoderException {		
		SWFEncoder encoder = new SWFEncoder(coord.length);		
		SWFContext context = new SWFContext();

		fixture = new Place3().setPlacement(Placement.NEW).setIdentifier(identifier).setLayer(layer).setTransform(transform);
		assertEquals(coord.length, fixture.prepareToEncode(encoder, context));
		fixture.encode(encoder, context);
		
		assertTrue(encoder.eof());
		assertArrayEquals(coord, encoder.getData());
	}
	
	@Test @Ignore
	public void encodeCoordAndColorTransforms() throws CoderException {		
		SWFEncoder encoder = new SWFEncoder(coordAndColor.length);		
		SWFContext context = new SWFContext();

		//fixture = new Place3(identifier, layer, transform, colorTransform);
		assertEquals(coordAndColor.length, fixture.prepareToEncode(encoder, context));
		fixture.encode(encoder, context);
		
		assertTrue(encoder.eof());
		assertArrayEquals(coordAndColor, encoder.getData());
	}
	
	@Test @Ignore
	public void decode() throws CoderException {
		SWFDecoder decoder = new SWFDecoder(coord);
		SWFContext context = new SWFContext();

		fixture = new Place3(decoder, context);
		
		assertTrue(decoder.eof());
		assertEquals(identifier, fixture.getIdentifier());
		assertEquals(layer, fixture.getLayer());
		assertEquals(transform.getTranslateX(), fixture.getTransform().getTranslateX());
		assertEquals(transform.getTranslateY(), fixture.getTransform().getTranslateY());
	}
	
	@Test @Ignore
	public void decodeExtended() throws CoderException {
		SWFDecoder decoder = new SWFDecoder(extended);
		SWFContext context = new SWFContext();

		fixture = new Place3(decoder, context);
		
		assertTrue(decoder.eof());
		assertEquals(identifier, fixture.getIdentifier());
		assertEquals(layer, fixture.getLayer());
		assertEquals(transform.getTranslateX(), fixture.getTransform().getTranslateX());
		assertEquals(transform.getTranslateY(), fixture.getTransform().getTranslateY());
	}
	
	@Test @Ignore
	public void decodeCoordAndColorTransforms() throws CoderException {
		SWFDecoder decoder = new SWFDecoder(coordAndColor);
		SWFContext context = new SWFContext();

		fixture = new Place3(decoder, context);
		
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
