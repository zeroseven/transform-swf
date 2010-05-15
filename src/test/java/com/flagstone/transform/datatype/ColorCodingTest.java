/*
 * ColorCodingTest.java
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
package com.flagstone.transform.datatype;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

public final class ColorCodingTest {

    @Test
    public void checkOpaqueColorIsDecoded() throws CoderException {
        final SWFDecoder decoder = new SWFDecoder(new byte[] {1, 2, 3});
        final Context context = new Context();
        final Color color = new Color(decoder, context);
        assertEquals(1, color.getRed());
        assertEquals(2, color.getGreen());
        assertEquals(3, color.getBlue());
        assertEquals(255, color.getAlpha());
    }

    @Test
    public void checkAlphaColorIsDecoded() throws CoderException {
        final SWFDecoder decoder = new SWFDecoder(new byte[] {1, 1, 1, 4});
        final Context context = new Context();
        final Color color = new Color(decoder, context);
        assertEquals(4, color.getAlpha());
    }

    @Test
    public void checkSizeForOpaqueColour() throws CoderException {
        final Context context = new Context(Context.TRANSPARENT, 0);
        assertEquals(3, new Color(0, 0, 0).prepareToEncode(context));
    }

    @Test
    public void checkSizeForTransparentColour() throws CoderException {
        final Context context = new Context(Context.TRANSPARENT, 1);
        assertEquals(4, new Color(0, 0, 0).prepareToEncode(context));
    }

    @Test
    public void checkOpaqueColourIsEncoded() throws CoderException {
        final byte[] expected = new byte[] {1, 2, 3};
        final Color color = new Color(1, 2, 3);

        final SWFEncoder encoder = new SWFEncoder(expected.length);
        final Context context = new Context();

        color.prepareToEncode(context);
        color.encode(encoder, context);

        assertArrayEquals(expected, encoder.getData());
    }

    @Test
    public void checkTransparentColourIsEncoded() throws CoderException {
        final byte[] expected = new byte[] {1, 2, 3, 4};
        final Color color = new Color(1, 2, 3, 4);

        final SWFEncoder encoder = new SWFEncoder(expected.length);
        final Context context = new Context(Context.TRANSPARENT, 1);

        color.prepareToEncode(context);
        color.encode(encoder, context);

        assertArrayEquals(expected, encoder.getData());
    }
 }
