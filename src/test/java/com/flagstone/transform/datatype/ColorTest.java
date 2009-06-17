/*
 * ColorTest.java
 * Transform
 *
 * Copyright (c) 2009 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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

public final class ColorTest {

    private static final int RED = 1;
    private static final int GREEN = 2;
    private static final int BLUE = 3;
    private static final int ALPHA = 4;

    private static final byte[] OPAQUE = {1, 2, 3};
    private static final byte[] TRANSPARENT = {1, 2, 3, 4};

    @Test(expected = IllegalArgumentException.class)
    public void checkLowValueThrowsException() {
        new Color(Color.MIN_LEVEL-1, GREEN, BLUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkHighValueThrowsException() {
        new Color(Color.MAX_LEVEL+1, GREEN, BLUE);
    }

    @Test
    public void checkSameIsEqual() {
        final Color color = new Color(RED, GREEN, BLUE, ALPHA);

        assertEquals(color, color);
    }

    @Test
    public void checkDifferentIsEqual() {
        final Color color = new Color(RED, GREEN, BLUE, ALPHA);
        final Color other = new Color(RED, GREEN, BLUE, ALPHA);

        assertEquals(color, other);
    }

    @Test
    public void checkOtherIsNotEqual() {
        final Color color = new Color(RED, GREEN, BLUE, ALPHA);
        final Color other = new Color(ALPHA, BLUE, GREEN, RED);

        assertFalse(color.equals(other));
    }

    @Test
    public void checkObjectIsNotEqual() {
        final Color color = new Color(RED, GREEN, BLUE, ALPHA);
        final Object other = new Object();

        assertFalse(color.equals(other));
    }

    @Test
    public void checkNullIsNotEqual() {
        final Color color = new Color(RED, GREEN, BLUE, ALPHA);
        final Color other = null;

        assertFalse(color.equals(other));
    }

    @Test
    public void encodeOpaqueColour() throws CoderException {
        final SWFEncoder encoder = new SWFEncoder(OPAQUE.length);
        final Context context = new Context();

        final Color color = new Color(RED, GREEN, BLUE);
        final int length = color.prepareToEncode(encoder, context);
        color.encode(encoder, context);

        assertTrue(encoder.eof());
        assertEquals(OPAQUE.length, length);
        assertArrayEquals(OPAQUE, encoder.getData());
    }

    @Test
    public void encodeTransparentColour() throws CoderException {
        final SWFEncoder encoder = new SWFEncoder(TRANSPARENT.length);
        final Context context = new Context().put(Context.TRANSPARENT, 1);

        final Color color = new Color(RED, GREEN, BLUE, ALPHA);
        final int length = color.prepareToEncode(encoder, context);
        color.encode(encoder, context);

        assertTrue(encoder.eof());
        assertEquals(TRANSPARENT.length, length);
        assertArrayEquals(TRANSPARENT, encoder.getData());
    }

    @Test
    public void decodeOpaqueColour() throws CoderException {
        final SWFDecoder decoder = new SWFDecoder(OPAQUE);
        final Context context = new Context();

        final Color color = new Color(decoder, context);

        assertTrue(decoder.eof());
        assertEquals(RED, color.getRed());
        assertEquals(GREEN, color.getGreen());
        assertEquals(BLUE, color.getBlue());
        assertEquals(Color.MAX_LEVEL, color.getAlpha());
    }

    @Test
    public void decodeTransparentColour() throws CoderException {
        final SWFDecoder decoder = new SWFDecoder(TRANSPARENT);
        final Context context = new Context().put(Context.TRANSPARENT, 1);

        final Color color = new Color(decoder, context);

        assertTrue(decoder.eof());
        assertEquals(ALPHA, color.getAlpha());
    }
}
