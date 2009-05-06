/*
 * ColorTransformTest.java
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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

public final class ColorTransformTest {

    private static float mulRed = 1.0f;
    private static float mulGreen = 2.0f;
    private static float mulBlue = 3.0f;
    private static float mulAlpha = 4.0f;

    private static int addRed = 1;
    private static int addGreen = 2;
    private static int addBlue = 3;
    private static int addAlpha = 4;

    private static byte[] multiplyNoAlpha = {108, -128, 32, 6, 0};
    private static byte[] multiplyWithAlpha = {112, 64, 8, 0, -64, 16, 0};

    private static byte[] addNoAlpha = {-116, -90};
    private static byte[] addWithAlpha = {-112, 72, -48};

    private static byte[] noAlpha = {-20, -128, 32, 6, 0, 0, 64, 16, 3};
    private static byte[] withAlpha = {-16, 64, 8, 0, -64, 16, 0, 0, 64, 8, 0, -64, 16};

    @Test
    public void encodeMultiplyWithoutAlpha() throws CoderException {

        final SWFEncoder encoder = new SWFEncoder(multiplyNoAlpha.length);
        final Context context = new Context();

        final ColorTransform fixture = new ColorTransform(mulRed, mulGreen,
                mulBlue, mulAlpha);
        final int length = fixture.prepareToEncode(encoder, context);

        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertEquals(multiplyNoAlpha.length, length);
        assertArrayEquals(multiplyNoAlpha, encoder.getData());
    }

    @Test
    public void encodeMultiplyWithAlpha() throws CoderException {

        final SWFEncoder encoder = new SWFEncoder(multiplyWithAlpha.length);
        final Context context = new Context().put(Context.TRANSPARENT, 1);

        final ColorTransform fixture = new ColorTransform(mulRed, mulGreen,
                mulBlue, mulAlpha);
        final int length = fixture.prepareToEncode(encoder, context);

        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertEquals(multiplyWithAlpha.length, length);
        assertArrayEquals(multiplyWithAlpha, encoder.getData());
    }

    @Test
    public void encodeAddWithoutAlpha() throws CoderException {

        final SWFEncoder encoder = new SWFEncoder(addNoAlpha.length);
        final Context context = new Context();

        final ColorTransform fixture = new ColorTransform(addRed, addGreen,
                addBlue, addAlpha);
        final int length = fixture.prepareToEncode(encoder, context);

        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertEquals(addNoAlpha.length, length);
        assertArrayEquals(addNoAlpha, encoder.getData());
    }

    @Test
    public void encodeAddWithAlpha() throws CoderException {

        final SWFEncoder encoder = new SWFEncoder(addWithAlpha.length);
        final Context context = new Context().put(Context.TRANSPARENT, 1);

        final ColorTransform fixture = new ColorTransform(addRed, addGreen,
                addBlue, addAlpha);
        final int length = fixture.prepareToEncode(encoder, context);

        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertEquals(addWithAlpha.length, length);
        assertArrayEquals(addWithAlpha, encoder.getData());
    }

    @Test
    public void encodeWithoutAlpha() throws CoderException {

        final SWFEncoder encoder = new SWFEncoder(noAlpha.length);
        final Context context = new Context();

        final ColorTransform fixture = new ColorTransform(addRed, addGreen,
                addBlue, addAlpha, mulRed, mulGreen, mulBlue, mulAlpha);
        final int length = fixture.prepareToEncode(encoder, context);

        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertEquals(noAlpha.length, length);
        assertArrayEquals(noAlpha, encoder.getData());
    }

    @Test
    public void encodeWithAlpha() throws CoderException {

        final SWFEncoder encoder = new SWFEncoder(withAlpha.length);
        final Context context = new Context().put(Context.TRANSPARENT, 1);

        final ColorTransform fixture = new ColorTransform(addRed, addGreen,
                addBlue, addAlpha, mulRed, mulGreen, mulBlue, mulAlpha);
        final int length = fixture.prepareToEncode(encoder, context);

        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertEquals(withAlpha.length, length);
        assertArrayEquals(withAlpha, encoder.getData());
    }

    @Test
    public void decodeMultiplyWithoutAlpha() throws CoderException {

        final SWFDecoder decoder = new SWFDecoder(multiplyNoAlpha);
        final Context context = new Context();

        final ColorTransform fixture = new ColorTransform(decoder, context);

        assertTrue(decoder.eof());
        assertEquals(fixture, new ColorTransform(mulRed, mulGreen, mulBlue,
                1.0f));
    }

    @Test
    public void decodeMultiplyWithAlpha() throws CoderException {

        final SWFDecoder decoder = new SWFDecoder(multiplyWithAlpha);
        final Context context = new Context().put(Context.TRANSPARENT, 1);

        final ColorTransform fixture = new ColorTransform(decoder, context);

        assertTrue(decoder.eof());
        assertEquals(fixture, new ColorTransform(mulRed, mulGreen, mulBlue,
                mulAlpha));
    }

    @Test
    public void decodeAddWithoutAlpha() throws CoderException {

        final SWFDecoder decoder = new SWFDecoder(addNoAlpha);
        final Context context = new Context();

        final ColorTransform fixture = new ColorTransform(decoder, context);

        assertTrue(decoder.eof());
        assertEquals(fixture, new ColorTransform(addRed, addGreen, addBlue, 0));
    }

    @Test
    public void decodeAddWithAlpha() throws CoderException {

        final SWFDecoder decoder = new SWFDecoder(addWithAlpha);
        final Context context = new Context().put(Context.TRANSPARENT, 1);

        final ColorTransform fixture = new ColorTransform(decoder, context);

        assertTrue(decoder.eof());
        assertEquals(fixture, new ColorTransform(addRed, addGreen, addBlue,
                addAlpha));
    }

    @Test
    public void decodeWithoutAlpha() throws CoderException {

        final SWFDecoder decoder = new SWFDecoder(noAlpha);
        final Context context = new Context();

        final ColorTransform fixture = new ColorTransform(decoder, context);

        assertTrue(decoder.eof());
        assertEquals(fixture, new ColorTransform(addRed, addGreen, addBlue, 0,
                        mulRed, mulGreen, mulBlue, 1.0f));
    }

    @Test
    public void decodeWithAlpha() throws CoderException {

        final SWFDecoder decoder = new SWFDecoder(withAlpha);
        final Context context = new Context().put(Context.TRANSPARENT, 1);

        final ColorTransform fixture = new ColorTransform(decoder, context);

        assertTrue(decoder.eof());
        assertEquals(fixture, new ColorTransform(addRed, addGreen, addBlue,
                addAlpha, mulRed, mulGreen, mulBlue, mulAlpha));
    }
}
