/*
 * GradientFillTest.java
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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.datatype.Color;
import com.flagstone.transform.datatype.CoordTransform;

public final class GradientFillTest {

    private static transient GradientType radial = GradientType.RADIAL;
    private static transient CoordTransform transform = CoordTransform
            .translate(1, 2);

    private static List<Gradient> list;

    @BeforeClass
    public static void initialize() {
        list = new ArrayList<Gradient>();
        list.add(new Gradient(1, new Color(2, 3, 4)));
        list.add(new Gradient(5, new Color(6, 7, 8)));
    }

    private transient GradientFill fixture;

    private final transient byte[] encoded = new byte[] {0x10, 0x06, 0x50,
            0x02, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 };

    @Test(expected = IllegalArgumentException.class)
    public void checkAddNullGradient() {
        fixture = new GradientFill(radial, transform, list);
        fixture.add(null);
    }

    @Test
    public void checkCopy() {
        fixture = new GradientFill(radial, transform, list);
        final GradientFill copy = fixture.copy();

        assertSame(fixture.getTransform(), copy.getTransform());
        assertNotSame(fixture.getGradients(), copy.getGradients());
        assertEquals(fixture.toString(), copy.toString());
    }

    @Test
    public void encode() throws CoderException {
        final SWFEncoder encoder = new SWFEncoder(encoded.length);
        final Context context = new Context();

        fixture = new GradientFill(radial, transform, list);
        assertEquals(encoded.length, fixture.prepareToEncode(encoder, context));
        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertArrayEquals(encoded, encoder.getData());
    }

    @Test
    public void decode() throws CoderException {
        final SWFDecoder decoder = new SWFDecoder(encoded);
        final Context context = new Context();

        fixture = new GradientFill(decoder, context);

        assertTrue(decoder.eof());
        // TODO(code) compare fields
    }
}
