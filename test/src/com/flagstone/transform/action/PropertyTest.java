/*
 * CoordTransformTest.java
 * Transform
 *
 * Copyright (c) 2001-2009 Flagstone Software Ltd. All rights reserved.
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

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

@SuppressWarnings( { "PMD.LocalVariableCouldBeFinal" })
public final class PropertyTest {

	private static final String VALUE_MISMATCH = "Values do not match for %s";
	private static final String NO_SUCH_PROPERTY = "Cannot look up property for %d";

	private transient int flashVersion;

	@Test
	public void checkPropertyReturnsIntegerValue() {

		Map<Property, Integer> table = new LinkedHashMap<Property, Integer>();
		flashVersion = 5;

		table.put(Property.X, 0);
		table.put(Property.Y, 1);
		table.put(Property.XSCALE, 2);
		table.put(Property.YSCALE, 3);
		table.put(Property.CURRENT_FRAME, 4);
		table.put(Property.TOTAL_FRAMES, 5);
		table.put(Property.ALPHA, 6);
		table.put(Property.VISIBLE, 7);
		table.put(Property.WIDTH, 8);
		table.put(Property.HEIGHT, 9);
		table.put(Property.ROTATION, 10);
		table.put(Property.TARGET, 11);
		table.put(Property.FRAMES_LOADED, 12);
		table.put(Property.NAME, 13);
		table.put(Property.DROP_TARGET, 14);
		table.put(Property.URL, 15);
		table.put(Property.HIGH_QUALITY, 16);
		table.put(Property.FOCUS_RECT, 17);
		table.put(Property.SOUND_BUF_TIME, 18);
		table.put(Property.QUALITY, 19);
		table.put(Property.XMOUSE, 20);
		table.put(Property.YMOUSE, 21);

		for (Property property : Property.values()) {
			assertEquals(String.format(VALUE_MISMATCH, property), table
					.get(property), property.getValue(flashVersion));
		}
	}

	@Test
	public void checkIntegerValueReturnsProperty() {

		Map<Integer, Property> table = new LinkedHashMap<Integer, Property>();

		table.put(0, Property.X);
		table.put(1, Property.Y);
		table.put(2, Property.XSCALE);
		table.put(3, Property.YSCALE);
		table.put(4, Property.CURRENT_FRAME);
		table.put(5, Property.TOTAL_FRAMES);
		table.put(6, Property.ALPHA);
		table.put(7, Property.VISIBLE);
		table.put(8, Property.WIDTH);
		table.put(9, Property.HEIGHT);
		table.put(10, Property.ROTATION);
		table.put(11, Property.TARGET);
		table.put(12, Property.FRAMES_LOADED);
		table.put(13, Property.NAME);
		table.put(14, Property.DROP_TARGET);
		table.put(15, Property.URL);
		table.put(16, Property.HIGH_QUALITY);
		table.put(17, Property.FOCUS_RECT);
		table.put(18, Property.SOUND_BUF_TIME);
		table.put(19, Property.QUALITY);
		table.put(20, Property.XMOUSE);
		table.put(21, Property.YMOUSE);

		for (Integer value : table.keySet()) {
			assertEquals(String.format(NO_SUCH_PROPERTY, value), Property
					.fromInt(value), table.get(value));
		}
	}

	@Test
	public void checkPropertyReturnsFloatValue() {

		Map<Property, Integer> table = new LinkedHashMap<Property, Integer>();
		flashVersion = 3;

		table.put(Property.X, 0x00000000);
		table.put(Property.Y, 0x3f800000);
		table.put(Property.XSCALE, 0x40000000);
		table.put(Property.YSCALE, 0x40400000);
		table.put(Property.CURRENT_FRAME, 0x40800000);
		table.put(Property.TOTAL_FRAMES, 0x40a00000);
		table.put(Property.ALPHA, 0x40c00000);
		table.put(Property.VISIBLE, 0x40e00000);
		table.put(Property.WIDTH, 0x41000000);
		table.put(Property.HEIGHT, 0x41100000);
		table.put(Property.ROTATION, 0x41200000);
		table.put(Property.TARGET, 0x41300000);
		table.put(Property.FRAMES_LOADED, 0x41400000);
		table.put(Property.NAME, 0x41500000);
		table.put(Property.DROP_TARGET, 0x41600000);
		table.put(Property.URL, 0x41700000);
		table.put(Property.HIGH_QUALITY, 0x41800000);
		table.put(Property.FOCUS_RECT, 0x41880000);
		table.put(Property.SOUND_BUF_TIME, 0x41900000);
		table.put(Property.QUALITY, 0x41980000);
		table.put(Property.XMOUSE, 0x41a00000);
		table.put(Property.YMOUSE, 0x41a80000);

		for (Property property : Property.values()) {
			assertEquals(String.format(VALUE_MISMATCH, property), table
					.get(property), property.getValue(flashVersion));
		}
	}

	@Test
	public void checkFloatValueReturnsProperty() {

		Map<Integer, Property> table = new LinkedHashMap<Integer, Property>();

		table.put(0x00000000, Property.X);
		table.put(0x3f800000, Property.Y);
		table.put(0x40000000, Property.XSCALE);
		table.put(0x40400000, Property.YSCALE);
		table.put(0x40800000, Property.CURRENT_FRAME);
		table.put(0x40a00000, Property.TOTAL_FRAMES);
		table.put(0x40c00000, Property.ALPHA);
		table.put(0x40e00000, Property.VISIBLE);
		table.put(0x41000000, Property.WIDTH);
		table.put(0x41100000, Property.HEIGHT);
		table.put(0x41200000, Property.ROTATION);
		table.put(0x41300000, Property.TARGET);
		table.put(0x41400000, Property.FRAMES_LOADED);
		table.put(0x41500000, Property.NAME);
		table.put(0x41600000, Property.DROP_TARGET);
		table.put(0x41700000, Property.URL);
		table.put(0x41800000, Property.HIGH_QUALITY);
		table.put(0x41880000, Property.FOCUS_RECT);
		table.put(0x41900000, Property.SOUND_BUF_TIME);
		table.put(0x41980000, Property.QUALITY);
		table.put(0x41a00000, Property.XMOUSE);
		table.put(0x41a80000, Property.YMOUSE);

		for (Integer value : table.keySet()) {
			assertEquals(String.format(NO_SUCH_PROPERTY, value), Property
					.fromInt(value), table.get(value));
		}
	}
}
