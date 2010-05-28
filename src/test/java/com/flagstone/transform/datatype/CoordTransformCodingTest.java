/*
 * CoordTransformCodingTest.java
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
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

@RunWith(Parameterized.class)
public final class CoordTransformCodingTest {

    private static final String CALCULATED_LENGTH =
        "Incorrect calculated length";
    private static final String NOT_FULLY_ENCODED =
        "Data was not fully encoded";
    private static final String NOT_FULLY_DECODED =
        "Data was not fully decoded";
    private static final String NOT_ENCODED =
        "Object was not encoded properly";
    private static final String NOT_DECODED =
        "Object was not decoded properly";

    @Test
    public void checkScaleIsEncoded() throws IOException {
        final CoordTransform object = CoordTransform.scale(1.0f, 2.0f);
        final byte[] binary = new byte[] {(byte) 0xCC, (byte) 0x80, 0x00, 0x20,
                0x00, 0x00, 0x40 };

        final SWFEncoder encoder = new SWFEncoder(binary.length);
        final Context context = new Context();

        final int length = object.prepareToEncode(context);
        object.encode(encoder, context);

        assertEquals(CALCULATED_LENGTH, binary.length, length);
        assertTrue(NOT_FULLY_ENCODED, encoder.eof());
        assertArrayEquals(NOT_ENCODED, binary, encoder.getData());
    }

    @Test
    public void checkScaleIsDecoded() throws IOException {
        final CoordTransform object = CoordTransform.scale(1.0f, 2.0f);
        final byte[] binary = new byte[] {(byte) 0xCC, (byte) 0x80, 0x00, 0x20,
                0x00, 0x00, 0x40 };

        final ByteArrayInputStream stream = new ByteArrayInputStream(binary);
        final SWFDecoder decoder = new SWFDecoder(stream);

        assertEquals(NOT_DECODED, object, new CoordTransform(decoder));
    }

    @Test
    public void checkShearIsEncoded() throws IOException {
        final CoordTransform object = CoordTransform.shear(1.0f, 2.0f);
        final byte[] binary = new byte[] { 0x66, 0x40, 0x00, 0x10, 0x00,
                0x00, 0x40 };

        final SWFEncoder encoder = new SWFEncoder(binary.length);
        final Context context = new Context();

        final int length = object.prepareToEncode(context);
        object.encode(encoder, context);

        assertEquals(CALCULATED_LENGTH, binary.length, length);
        assertTrue(NOT_FULLY_ENCODED, encoder.eof());
        assertArrayEquals(NOT_ENCODED, binary, encoder.getData());
    }

    @Test
    public void checkShearIsDecoded() throws IOException {
        final CoordTransform object = CoordTransform.shear(1.0f, 2.0f);
        final byte[] binary = new byte[] { 0x66, 0x40, 0x00, 0x10, 0x00,
                0x00, 0x40 };

        final ByteArrayInputStream stream = new ByteArrayInputStream(binary);
        final SWFDecoder decoder = new SWFDecoder(stream);

        assertEquals(NOT_DECODED, object, new CoordTransform(decoder));
    }

    @Test
    public void checkTranslationIsEncoded() throws IOException {
        final CoordTransform object = CoordTransform.translate(1, 2);
        final byte[] binary = new byte[] { 0x06, 0x50 };

        final SWFEncoder encoder = new SWFEncoder(binary.length);
        final Context context = new Context();

        final int length = object.prepareToEncode(context);
        object.encode(encoder, context);

        assertEquals(CALCULATED_LENGTH, binary.length, length);
        assertTrue(NOT_FULLY_ENCODED, encoder.eof());
        assertArrayEquals(NOT_ENCODED, binary, encoder.getData());
    }

    @Test
    public void checkTranslationIsDecoded() throws IOException {
        final CoordTransform object = CoordTransform.translate(1, 2);
        final byte[] binary = new byte[] { 0x06, 0x50 };

        final ByteArrayInputStream stream = new ByteArrayInputStream(binary);
        final SWFDecoder decoder = new SWFDecoder(stream);

        assertEquals(NOT_DECODED, object, new CoordTransform(decoder));
    }

    @Test
    public void checkTransformIsEncoded() throws IOException {
        final CoordTransform object =
            new CoordTransform(1.0f, 2.0f, 3.0f, 4.0f, 0, 0);
        final byte[] binary = new byte[] {(byte) 0xCC, (byte) 0x80, 0x00, 0x20,
                0x00, 0x0D, 0x0C, 0x00, 0x01, 0x00, 0x00, 0x02, 0x00 };

        final SWFEncoder encoder = new SWFEncoder(binary.length);
        final Context context = new Context();

        final int length = object.prepareToEncode(context);
        object.encode(encoder, context);

        assertEquals(CALCULATED_LENGTH, binary.length, length);
        assertTrue(NOT_FULLY_ENCODED, encoder.eof());
        assertArrayEquals(NOT_ENCODED, binary, encoder.getData());
    }

    @Test
    public void checkTransformIsDecoded() throws IOException {
        final CoordTransform object =
            new CoordTransform(1.0f, 2.0f, 3.0f, 4.0f, 0, 0);
        final byte[] binary = new byte[] {(byte) 0xCC, (byte) 0x80, 0x00, 0x20,
                0x00, 0x0D, 0x0C, 0x00, 0x01, 0x00, 0x00, 0x02, 0x00 };

        final ByteArrayInputStream stream = new ByteArrayInputStream(binary);
        final SWFDecoder decoder = new SWFDecoder(stream);

        assertEquals(NOT_DECODED, object, new CoordTransform(decoder));
    }
}
