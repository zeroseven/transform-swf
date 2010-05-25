/*
 * InitializeMovieClipTest.java
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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.flagstone.transform.action.Action;
import com.flagstone.transform.action.ActionTypes;
import com.flagstone.transform.action.BasicAction;
import com.flagstone.transform.coder.ActionDecoder;
import java.io.IOException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.DecoderRegistry;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

public final class InitializeMovieClipTest {

    private static int identifier = 1;
    private static List<Action> list = new ArrayList<Action>();

    static {
        list.add(BasicAction.ADD);
        list.add(BasicAction.END);
    }

    private transient InitializeMovieClip fixture;

    private final transient byte[] encoded = new byte[] {(byte) 0xC4, 0x0E,
            0x01, 0x00, ActionTypes.ADD, ActionTypes.END };

    private final transient byte[] extended = new byte[] {(byte) 0xFF, 0x0E,
            0x04, 0x00, 0x00, 0x00, 0x01, 0x00, ActionTypes.ADD,
            ActionTypes.END };

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessorForIdentifierWithLowerBound() {
        fixture = new InitializeMovieClip(0, list);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessorForIdentifierWithUpperBound() {
        fixture = new InitializeMovieClip(65536, list);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkAddNullNull() {
        fixture = new InitializeMovieClip(identifier, null);
    }

    @Test
    public void checkCopy() {
        fixture = new InitializeMovieClip(identifier, list);
        final InitializeMovieClip copy = fixture.copy();

        assertEquals(fixture.getIdentifier(), copy.getIdentifier());
        assertNotSame(fixture.getActions(), copy.getActions());
        assertEquals(fixture.toString(), copy.toString());
    }

    @Test
    public void encode() throws IOException {
        final SWFEncoder encoder = new SWFEncoder(encoded.length);
        final Context context = new Context();

        fixture = new InitializeMovieClip(identifier, list);
        assertEquals(encoded.length, fixture.prepareToEncode(context));
        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertArrayEquals(encoded, encoder.getData());
    }

    @Test
    public void decode() throws IOException {
        final SWFDecoder decoder = new SWFDecoder(encoded);
        final Context context = new Context();
        final DecoderRegistry registry = new DecoderRegistry();
        registry.setActionDecoder(new ActionDecoder());
        context.setRegistry(registry);

        fixture = new InitializeMovieClip(decoder, context);

        assertTrue(decoder.eof());
        assertEquals(identifier, fixture.getIdentifier());
        assertEquals(list, fixture.getActions());
    }

    @Test
    public void decodeExtended() throws IOException {
        final SWFDecoder decoder = new SWFDecoder(extended);
        final Context context = new Context();
        final DecoderRegistry registry = new DecoderRegistry();
        registry.setActionDecoder(new ActionDecoder());
        context.setRegistry(registry);

        fixture = new InitializeMovieClip(decoder, context);

        assertTrue(decoder.eof());
        assertEquals(identifier, fixture.getIdentifier());
        assertEquals(list, fixture.getActions());
    }
}
