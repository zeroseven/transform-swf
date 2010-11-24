/*
 * MorphSolidFillTest.java
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

package com.flagstone.transform.fillstyle;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.datatype.Color;

public final class MorphSolidFillTest {

    private final transient Color startColor = new Color(1, 2, 3, 4);
    private final transient Color endColor = new Color(5, 6, 7, 8);

    private transient MorphSolidFill fixture;

    private final transient byte[] encoded = new byte[] {0x00, 0x01, 0x02,
            0x03, 0x04, 0x05, 0x06, 0x07, 0x08 };

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessorForStartColorWithNull() {
        fixture = new MorphSolidFill(null, endColor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessorForEndColorWithNull() {
        fixture = new MorphSolidFill(startColor, null);
    }

    @Test
    public void checkCopy() {
        fixture = new MorphSolidFill(startColor, endColor);
        final MorphSolidFill copy = fixture.copy();

        assertNotSame(fixture, copy);
        assertSame(fixture.getStartColor(), copy.getStartColor());
        assertEquals(fixture.toString(), copy.toString());
    }

    @Test
    public void encode() throws IOException {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream);
        final Context context = new Context();
        context.put(Context.TRANSPARENT, 1);

        fixture = new MorphSolidFill(startColor, endColor);
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
        context.put(Context.TRANSPARENT, 1);

        decoder.readByte();
        fixture = new MorphSolidFill(decoder, context);

        assertEquals(startColor.getRed(), fixture.getStartColor().getRed());
        assertEquals(startColor.getGreen(), fixture.getStartColor().getGreen());
        assertEquals(startColor.getBlue(), fixture.getStartColor().getBlue());
        assertEquals(startColor.getAlpha(), fixture.getStartColor().getAlpha());
    }
}
