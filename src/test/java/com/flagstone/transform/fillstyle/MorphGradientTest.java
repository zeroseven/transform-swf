/*
 * MorphGradientTest.java
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

public final class MorphGradientTest {

    private static final int START_RATIO = 1;
    private static final Color START_COLOR = new Color(2, 3, 4, 5);
    private static final int END_RATIO = 6;
    private static final Color END_COLOR = new Color(7, 8, 9, 10);

    private transient MorphGradient fixture;

    private final transient byte[] encoded = new byte[] {0x01, 0x02, 0x03,
            0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A };

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessorForRatioWithLowerBound() {
        fixture = new MorphGradient(new Gradient(-1, START_COLOR), new Gradient(
                END_RATIO, END_COLOR));
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessorForRatioWithUpperBound() {
        fixture = new MorphGradient(new Gradient(256, START_COLOR),
                new Gradient(END_RATIO, END_COLOR));
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessorForColorWithNull() {
        fixture = new MorphGradient(new Gradient(START_RATIO, null),
                new Gradient(END_RATIO, END_COLOR));
    }

    @Test
    public void checkCopy() {
        fixture = new MorphGradient(new Gradient(START_RATIO, START_COLOR),
                new Gradient(END_RATIO, END_COLOR));
        final MorphGradient copy = fixture.copy();

        assertNotSame(fixture, copy);
        assertSame(fixture.getStart().getColor(), copy.getStart().getColor());
        assertEquals(fixture.toString(), copy.toString());
    }

    @Test
    public void encode() throws IOException {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream);
        final Context context = new Context();
        context.put(Context.TRANSPARENT, 1);

        fixture = new MorphGradient(new Gradient(START_RATIO, START_COLOR),
                new Gradient(END_RATIO, END_COLOR));
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

        fixture = new MorphGradient(decoder, context);

        assertEquals(START_RATIO, fixture.getStart().getRatio());
        assertEquals(START_COLOR.getRed(), fixture.getStart().getColor()
                .getRed());
        assertEquals(START_COLOR.getGreen(), fixture.getStart().getColor()
                .getGreen());
        assertEquals(START_COLOR.getBlue(), fixture.getStart().getColor()
                .getBlue());
        assertEquals(START_COLOR.getAlpha(), fixture.getStart().getColor()
                .getAlpha());
    }
}
