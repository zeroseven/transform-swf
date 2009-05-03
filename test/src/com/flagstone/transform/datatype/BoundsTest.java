/*
 * BoundsTest.java
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
package com.flagstone.transform.datatype;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

public final class BoundsTest {

    private static int xmin = 1;
    private static int ymin = 2;
    private static int xmax = 3;
    private static int ymax = 4;

    private final transient byte[] encoded = new byte[] { 32, -103, 32 };

    private transient Bounds fixture;

    @Test
    public void checkWidth() {
        assertEquals(3, new Bounds(1, 2, 4, 8).getWidth());
    }

    @Test
    public void checkHeigth() {
        assertEquals(6, new Bounds(1, 2, 4, 8).getHeight());
    }

    @Test
    public void checkNullIsnotEqual() {
        assertFalse(new Bounds(1, 2, 3, 4).equals(null));
    }

    @Test
    public void checkObjectIsNotEqual() {
        assertFalse(new Bounds(1, 2, 3, 4).equals(new Object()));
    }

    @Test
    public void checkSameIsEqual() {
        fixture = new Bounds(1, 2, 3, 4);
        assertEquals(fixture, fixture);
    }

    @Test
    public void checkIsNotEqual() {
        fixture = new Bounds(1, 2, 3, 4);
        assertFalse(fixture.equals(new Bounds(4, 3, 2, 1)));
    }

    @Test
    public void checkOtherIsEqual() {
        assertTrue(new Bounds(1, 2, 3, 4).equals(new Bounds(1, 2, 3, 4)));
    }

    @Test
    public void encodeWithBoundsSet() throws CoderException {
        final SWFEncoder encoder = new SWFEncoder(encoded.length);
        final Context context = new Context();

        fixture = new Bounds(xmin, ymin, xmax, ymax);

        assertEquals(3, fixture.prepareToEncode(encoder, context));
        fixture.encode(encoder, context);

        assertEquals(24, encoder.getPointer());
        assertArrayEquals(encoded, encoder.getData());
    }

    @Test
    public void decodeWithBoundsSet() throws CoderException {
        final SWFDecoder decoder = new SWFDecoder(encoded);

        fixture = new Bounds(decoder);

        assertTrue(decoder.eof());
        assertEquals(xmin, fixture.getMinX());
        assertEquals(ymin, fixture.getMinY());
        assertEquals(xmax, fixture.getMaxX());
        assertEquals(ymax, fixture.getMaxY());
    }
}
