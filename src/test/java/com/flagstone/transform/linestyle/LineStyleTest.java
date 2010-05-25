/*
 * LineStyleTest.java
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

package com.flagstone.transform.linestyle;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.IOException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.datatype.Color;

public final class LineStyleTest {

    private static final transient int WIDTH = 1;
    private final transient Color color = new Color(2, 3, 4);

    private transient LineStyle fixture;

    private final transient byte[] encoded = new byte[] {0x01, 0x00, 0x02,
            0x03, 0x04 };

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessorForWidthWithLowerBound() {
        fixture = new LineStyle(-1, color);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessorForWidthWithUpperBound() {
        fixture = new LineStyle(65536, color);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessorForColorWithNull() {
        fixture = new LineStyle(1, null);
    }

    @Test
    public void checkCopy() {
        fixture = new LineStyle(WIDTH, color);
        final LineStyle copy = fixture.copy();

        assertNotSame(fixture, copy);
        assertSame(fixture.getColor(), copy.getColor());
        assertEquals(fixture.toString(), copy.toString());
    }

    @Test
    public void encode() throws IOException {
        final SWFEncoder encoder = new SWFEncoder(encoded.length);
        final Context context = new Context();

        fixture = new LineStyle(WIDTH, color);
        assertEquals(encoded.length, fixture.prepareToEncode(context));
        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertArrayEquals(encoded, encoder.getData());
    }

    @Test
    public void decode() throws IOException {
        final SWFDecoder decoder = new SWFDecoder(encoded);
        final Context context = new Context();

        fixture = new LineStyle(decoder, context);

        assertTrue(decoder.eof());
        assertEquals(WIDTH, fixture.getWidth());
        assertEquals(color.getRed(), fixture.getColor().getRed());
        assertEquals(color.getGreen(), fixture.getColor().getGreen());
        assertEquals(color.getBlue(), fixture.getColor().getBlue());
        assertEquals(color.getAlpha(), fixture.getColor().getAlpha());
    }
}
