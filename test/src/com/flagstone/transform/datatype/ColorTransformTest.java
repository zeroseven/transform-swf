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

    private static final float MRED = 1.0f;
    private static final float MGREEN = 2.0f;
    private static final float MBLUE = 3.0f;
    private static final float MALPHA = 4.0f;

    private static final int ARED = 1;
    private static final int AGREEN = 2;
    private static final int ABLUE = 3;
    private static final int AALPHA = 4;

    private static final byte[] MULTIPLY_NO_ALPHA = {108, -128, 32, 6, 0};
    private static final byte[] MULTIPLY_WITH_ALPHA = {112, 64, 8, 0, -64, 16, 0};

    private static final byte[] ADD_NO_ALPHA = {-116, -90};
    private static final byte[] ADD_WITH_ALPHA = {-112, 72, -48};

    private static final byte[] NO_ALPHA = {-20, -128, 32, 6, 0, 0, 64, 16, 3};
    private static final byte[] WITH_ALPHA = {-16, 64, 8, 0, -64, 16, 0, 0, 64, 8, 0, -64, 16};

    @Test
    public void encodeMultiplyWithoutAlpha() throws CoderException {

        final SWFEncoder encoder = new SWFEncoder(MULTIPLY_NO_ALPHA.length);
        final Context context = new Context();

        final ColorTransform fixture = new ColorTransform(MRED, MGREEN,
                MBLUE, MALPHA);
        final int length = fixture.prepareToEncode(encoder, context);

        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertEquals(MULTIPLY_NO_ALPHA.length, length);
        assertArrayEquals(MULTIPLY_NO_ALPHA, encoder.getData());
    }

    @Test
    public void encodeMultiplyWithAlpha() throws CoderException {

        final SWFEncoder encoder = new SWFEncoder(MULTIPLY_WITH_ALPHA.length);
        final Context context = new Context().put(Context.TRANSPARENT, 1);

        final ColorTransform fixture = new ColorTransform(MRED, MGREEN,
                MBLUE, MALPHA);
        final int length = fixture.prepareToEncode(encoder, context);

        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertEquals(MULTIPLY_WITH_ALPHA.length, length);
        assertArrayEquals(MULTIPLY_WITH_ALPHA, encoder.getData());
    }

    @Test
    public void encodeAddWithoutAlpha() throws CoderException {

        final SWFEncoder encoder = new SWFEncoder(ADD_NO_ALPHA.length);
        final Context context = new Context();

        final ColorTransform fixture = new ColorTransform(ARED, AGREEN,
                ABLUE, AALPHA);
        final int length = fixture.prepareToEncode(encoder, context);

        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertEquals(ADD_NO_ALPHA.length, length);
        assertArrayEquals(ADD_NO_ALPHA, encoder.getData());
    }

    @Test
    public void encodeAddWithAlpha() throws CoderException {

        final SWFEncoder encoder = new SWFEncoder(ADD_WITH_ALPHA.length);
        final Context context = new Context().put(Context.TRANSPARENT, 1);

        final ColorTransform fixture = new ColorTransform(ARED, AGREEN,
                ABLUE, AALPHA);
        final int length = fixture.prepareToEncode(encoder, context);

        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertEquals(ADD_WITH_ALPHA.length, length);
        assertArrayEquals(ADD_WITH_ALPHA, encoder.getData());
    }

    @Test
    public void encodeWithoutAlpha() throws CoderException {

        final SWFEncoder encoder = new SWFEncoder(NO_ALPHA.length);
        final Context context = new Context();

        final ColorTransform fixture = new ColorTransform(ARED, AGREEN,
                ABLUE, AALPHA, MRED, MGREEN, MBLUE, MALPHA);
        final int length = fixture.prepareToEncode(encoder, context);

        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertEquals(NO_ALPHA.length, length);
        assertArrayEquals(NO_ALPHA, encoder.getData());
    }

    @Test
    public void encodeWithAlpha() throws CoderException {

        final SWFEncoder encoder = new SWFEncoder(WITH_ALPHA.length);
        final Context context = new Context().put(Context.TRANSPARENT, 1);

        final ColorTransform fixture = new ColorTransform(ARED, AGREEN,
                ABLUE, AALPHA, MRED, MGREEN, MBLUE, MALPHA);
        final int length = fixture.prepareToEncode(encoder, context);

        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertEquals(WITH_ALPHA.length, length);
        assertArrayEquals(WITH_ALPHA, encoder.getData());
    }

    @Test
    public void decodeMultiplyWithoutAlpha() throws CoderException {

        final SWFDecoder decoder = new SWFDecoder(MULTIPLY_NO_ALPHA);
        final Context context = new Context();

        final ColorTransform fixture = new ColorTransform(decoder, context);

        assertTrue(decoder.eof());
        assertEquals(fixture, new ColorTransform(MRED, MGREEN, MBLUE,
                1.0f));
    }

    @Test
    public void decodeMultiplyWithAlpha() throws CoderException {

        final SWFDecoder decoder = new SWFDecoder(MULTIPLY_WITH_ALPHA);
        final Context context = new Context().put(Context.TRANSPARENT, 1);

        final ColorTransform fixture = new ColorTransform(decoder, context);

        assertTrue(decoder.eof());
        assertEquals(fixture, new ColorTransform(MRED, MGREEN, MBLUE,
                MALPHA));
    }

    @Test
    public void decodeAddWithoutAlpha() throws CoderException {

        final SWFDecoder decoder = new SWFDecoder(ADD_NO_ALPHA);
        final Context context = new Context();

        final ColorTransform fixture = new ColorTransform(decoder, context);

        assertTrue(decoder.eof());
        assertEquals(fixture, new ColorTransform(ARED, AGREEN, ABLUE, 0));
    }

    @Test
    public void decodeAddWithAlpha() throws CoderException {

        final SWFDecoder decoder = new SWFDecoder(ADD_WITH_ALPHA);
        final Context context = new Context().put(Context.TRANSPARENT, 1);

        final ColorTransform fixture = new ColorTransform(decoder, context);

        assertTrue(decoder.eof());
        assertEquals(fixture, new ColorTransform(ARED, AGREEN, ABLUE,
                AALPHA));
    }

    @Test
    public void decodeWithoutAlpha() throws CoderException {

        final SWFDecoder decoder = new SWFDecoder(NO_ALPHA);
        final Context context = new Context();

        final ColorTransform fixture = new ColorTransform(decoder, context);

        assertTrue(decoder.eof());
        assertEquals(fixture, new ColorTransform(ARED, AGREEN, ABLUE, 0,
                        MRED, MGREEN, MBLUE, 1.0f));
    }

    @Test
    public void decodeWithAlpha() throws CoderException {

        final SWFDecoder decoder = new SWFDecoder(WITH_ALPHA);
        final Context context = new Context().put(Context.TRANSPARENT, 1);

        final ColorTransform fixture = new ColorTransform(decoder, context);

        assertTrue(decoder.eof());
        assertEquals(fixture, new ColorTransform(ARED, AGREEN, ABLUE,
                AALPHA, MRED, MGREEN, MBLUE, MALPHA));
    }
}
