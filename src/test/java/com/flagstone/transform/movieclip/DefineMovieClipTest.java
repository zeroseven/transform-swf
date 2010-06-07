/*
 * DefineMovieClipTest.java
 * Transform
 *
 * Copyright (c) 2009-2010 Flagstone Software Ltd. All rights reserved.
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

package com.flagstone.transform.movieclip;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.flagstone.transform.MovieDecoder;
import com.flagstone.transform.MovieTag;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.DecoderRegistry;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

public final class DefineMovieClipTest {

    private final int identifier = 1;
    private final List<MovieTag> list = new ArrayList<MovieTag>();

    private transient DefineMovieClip fixture;

    private final transient byte[] encoded = new byte[] {(byte) 0xC8, 0x09,
            0x01, 0x00, 0x01, 0x00, 0x40, 0x00, 0x00, 0x00 };

    private final transient byte[] extended = new byte[] {(byte) 0xFF, 0x09,
            0x08, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x40, 0x00, 0x00,
            0x00 };

    @Before
    public void setup() {
        list.add(ShowFrame.getInstance());
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessorForIdentifierWithLowerBound() {
        fixture = new DefineMovieClip(0, list);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessorForIdentifierWithUpperBound() {
        fixture = new DefineMovieClip(65536, list);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkAddNullTag() {
        fixture = new DefineMovieClip(identifier, null);
    }

    @Test
    public void checkCopy() {
        fixture = new DefineMovieClip(identifier, list);
        final DefineMovieClip copy = fixture.copy();

        assertEquals(fixture.getIdentifier(), copy.getIdentifier());
        assertNotSame(fixture.getObjects(), copy.getObjects());
        assertEquals(fixture.toString(), copy.toString());
    }

    @Test
    public void encode() throws IOException {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream);
        final Context context = new Context();
        context.put(Context.FRAMES, 0);

        fixture = new DefineMovieClip(identifier, list);
        assertEquals(encoded.length, fixture.prepareToEncode(context));
        fixture.encode(encoder, context);
        encoder.flush();

        assertArrayEquals(encoded, stream.toByteArray());
    }

    @Test
    public void decode() throws IOException {
        final ByteArrayInputStream stream = new ByteArrayInputStream(encoded);
        final SWFDecoder decoder = new SWFDecoder(stream);
        final Context context = new Context();
        final DecoderRegistry registry = new DecoderRegistry();
        registry.setMovieDecoder(new MovieDecoder());
        context.setRegistry(registry);
        context.put(Context.FRAMES, 0);

        fixture = new DefineMovieClip(decoder, context);

        assertTrue(true);
        assertEquals(identifier, fixture.getIdentifier());
        assertEquals(list, fixture.getObjects());
    }

    @Test
    public void decodeExtended() throws IOException {
        final ByteArrayInputStream stream = new ByteArrayInputStream(extended);
        final SWFDecoder decoder = new SWFDecoder(stream);
        final Context context = new Context();
        final DecoderRegistry registry = new DecoderRegistry();
        registry.setMovieDecoder(new MovieDecoder());
        context.setRegistry(registry);
        context.put(Context.FRAMES, 0);

        fixture = new DefineMovieClip(decoder, context);

        assertTrue(true);
        assertEquals(identifier, fixture.getIdentifier());
        assertEquals(list, fixture.getObjects());
    }
}
