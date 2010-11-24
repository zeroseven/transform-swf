/*
 * ColorTransformCodingTest.java
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.flagstone.transform.AbstractCodingTest;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

@SuppressWarnings({"PMD.TooManyMethods" })
public final class ColorTransformCodingTest extends AbstractCodingTest {

    @Test
    public void checkOpaqueAddTermsAreEncoded() throws IOException {
        final ColorTransform object = new ColorTransform(1, 2, 3, 0);
        final byte[] binary = new byte[] {(byte) 0x8C, (byte) 0xA6 };

        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream);
        final Context context = new Context();

        final int length = object.prepareToEncode(context);
        object.encode(encoder, context);
        encoder.flush();

        assertEquals(CALCULATED_LENGTH, binary.length, length);

        assertArrayEquals(NOT_ENCODED, binary, stream.toByteArray());
    }

    @Test
    public void checkOpaqueAddTermsAreDecoded() throws IOException {
        final ColorTransform object = new ColorTransform(1, 2, 3, 0);
        final byte[] binary = new byte[] {(byte) 0x8C, (byte) 0xA6 };

        final ByteArrayInputStream stream = new ByteArrayInputStream(binary);
        final SWFDecoder decoder = new SWFDecoder(stream);
        final Context context = new Context();

        assertEquals(NOT_DECODED, object, new ColorTransform(decoder, context));
    }

    @Test
    public void checkOpaqueMultiplyTermsAreDefaults() throws IOException {
        final byte[] binary = new byte[] {(byte) 0x8C, (byte) 0xA6 };

        final ByteArrayInputStream stream = new ByteArrayInputStream(binary);
        final SWFDecoder decoder = new SWFDecoder(stream);
        final Context context = new Context();
        final ColorTransform decoded = new ColorTransform(decoder, context);

        assertEquals(1.0f, decoded.getMultiplyRed(), 0.0f);
        assertEquals(1.0f, decoded.getMultiplyGreen(), 0.0f);
        assertEquals(1.0f, decoded.getMultiplyBlue(), 0.0f);
        assertEquals(1.0f, decoded.getMultiplyAlpha(), 0.0f);
    }

    @Test
    public void checkOpaqueMultiplyTermsAreEncoded() throws IOException {
        final ColorTransform object =
            new ColorTransform(1.0f, 2.0f, 3.0f, 0.0f);
        final byte[] binary =
            new byte[] {0x6C, (byte) 0x80, 0x20, 0x06, 0x00 };

        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream);
        final Context context = new Context();

        final int length = object.prepareToEncode(context);
        object.encode(encoder, context);
        encoder.flush();

        assertEquals(CALCULATED_LENGTH, binary.length, length);

        assertArrayEquals(NOT_ENCODED, binary, stream.toByteArray());
    }

    @Test
    public void checkOpaqueMultiplyTermsAreDecoded() throws IOException {
        final ColorTransform object =
            new ColorTransform(1.0f, 2.0f, 3.0f, 1.0f);
        final byte[] binary =
            new byte[] {0x6C, (byte) 0x80, 0x20, 0x06, 0x00 };

        final ByteArrayInputStream stream = new ByteArrayInputStream(binary);
        final SWFDecoder decoder = new SWFDecoder(stream);
        final Context context = new Context();

        assertEquals(NOT_DECODED, object, new ColorTransform(decoder, context));
    }

    @Test
    public void checkOpaqueAddTermsAreDefaults() throws IOException {
        final byte[] binary =
            new byte[] {0x6C, (byte) 0x80, 0x20, 0x06, 0x00 };

        final ByteArrayInputStream stream = new ByteArrayInputStream(binary);
        final SWFDecoder decoder = new SWFDecoder(stream);
        final Context context = new Context();
        final ColorTransform decoded = new ColorTransform(decoder, context);

        assertEquals(0, decoded.getAddRed());
        assertEquals(0, decoded.getAddGreen());
        assertEquals(0, decoded.getAddBlue());
        assertEquals(0, decoded.getAddAlpha());
    }

    @Test
    public void checkOpaqueTermsAreEncoded() throws IOException {
        final ColorTransform object =
            new ColorTransform(1, 2, 3, 0, 1.0f, 2.0f, 3.0f, 1.0f);
        final byte[] binary = new byte[] {(byte) 0xEC, (byte) 0x80, 0x20, 0x06,
                0x00, 0x00, 0x40, 0x10, 0x03 };

        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream);
        final Context context = new Context();

        final int length = object.prepareToEncode(context);
        object.encode(encoder, context);
        encoder.flush();

        assertEquals(CALCULATED_LENGTH, binary.length, length);

        assertArrayEquals(NOT_ENCODED, binary, stream.toByteArray());
    }

    @Test
    public void checkOpaqueTermsAreDecoded() throws IOException {
        final ColorTransform object =
            new ColorTransform(1, 2, 3, 0, 1.0f, 2.0f, 3.0f, 1.0f);
        final byte[] binary = new byte[] {(byte) 0xEC, (byte) 0x80, 0x20, 0x06,
                0x00, 0x00, 0x40, 0x10, 0x03 };

        final ByteArrayInputStream stream = new ByteArrayInputStream(binary);
        final SWFDecoder decoder = new SWFDecoder(stream);
        final Context context = new Context();

        assertEquals(NOT_DECODED, object, new ColorTransform(decoder, context));
    }


    @Test
    public void checkTransparentAddTermsAreEncoded() throws IOException {
        final ColorTransform object = new ColorTransform(1, 2, 3, 4);
        final byte[] binary = new byte[] {(byte) 0x90, 0x48, (byte) 0xD0 };

        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream);
        final Context context = new Context();
        context.put(Context.TRANSPARENT, 1);

        final int length = object.prepareToEncode(context);
        object.encode(encoder, context);
        encoder.flush();

        assertEquals(CALCULATED_LENGTH, binary.length, length);

        assertArrayEquals(NOT_ENCODED, binary, stream.toByteArray());
    }

    @Test
    public void checkTransparentAddTermsAreDecoded() throws IOException {
        final ColorTransform object = new ColorTransform(1, 2, 3, 4);
        final byte[] binary = new byte[] {(byte) 0x90, 0x48, (byte) 0xD0 };

        final ByteArrayInputStream stream = new ByteArrayInputStream(binary);
        final SWFDecoder decoder = new SWFDecoder(stream);
        final Context context = new Context();
        context.put(Context.TRANSPARENT, 1);

        assertEquals(NOT_DECODED, object, new ColorTransform(decoder, context));
    }

    @Test
    public void checkTransparentMultiplyTermsAreDefaults()
                throws IOException {
        final byte[] binary = new byte[] {(byte) 0x90, 0x48, (byte) 0xD0 };

        final ByteArrayInputStream stream = new ByteArrayInputStream(binary);
        final SWFDecoder decoder = new SWFDecoder(stream);
        final Context context = new Context();
        final ColorTransform decoded = new ColorTransform(decoder, context);

        assertEquals(1.0f, decoded.getMultiplyRed(), 0.0f);
        assertEquals(1.0f, decoded.getMultiplyGreen(), 0.0f);
        assertEquals(1.0f, decoded.getMultiplyBlue(), 0.0f);
        assertEquals(1.0f, decoded.getMultiplyAlpha(), 0.0f);
    }

    @Test
    public void checkTransparentMultiplyTermsAreEncoded()
                throws IOException {
        final ColorTransform object =
            new ColorTransform(1.0f, 2.0f, 3.0f, 4.0f);
        final byte[] binary =
            new byte[] {0x70, 0x40, 0x08, 0x00, (byte) 0xC0, 0x10, 0x00 };

        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream);
        final Context context = new Context();
        context.put(Context.TRANSPARENT, 1);

        final int length = object.prepareToEncode(context);
        object.encode(encoder, context);
        encoder.flush();

        assertEquals(CALCULATED_LENGTH, binary.length, length);

        assertArrayEquals(NOT_ENCODED, binary, stream.toByteArray());
    }

    @Test
    public void checkTransparentMultiplyTermsAreDecoded()
                throws IOException {
        final ColorTransform object =
            new ColorTransform(1.0f, 2.0f, 3.0f, 4.0f);
        final byte[] binary =
            new byte[] {0x70, 0x40, 0x08, 0x00, (byte) 0xC0, 0x10, 0x00 };

        final ByteArrayInputStream stream = new ByteArrayInputStream(binary);
        final SWFDecoder decoder = new SWFDecoder(stream);
        final Context context = new Context();
        context.put(Context.TRANSPARENT, 1);

        assertEquals(NOT_DECODED, object, new ColorTransform(decoder, context));
    }

    @Test
    public void checkTransparentAddTermsAreDefaults() throws IOException {
        final byte[] binary =
            new byte[] {0x70, 0x40, 0x08, 0x00, (byte) 0xC0, 0x10, 0x00 };

        final ByteArrayInputStream stream = new ByteArrayInputStream(binary);
        final SWFDecoder decoder = new SWFDecoder(stream);
        final Context context = new Context();
        final ColorTransform decoded = new ColorTransform(decoder, context);

        assertEquals(0, decoded.getAddRed());
        assertEquals(0, decoded.getAddGreen());
        assertEquals(0, decoded.getAddBlue());
        assertEquals(0, decoded.getAddAlpha());
    }

    @Test
    public void checkTransparentTermsAreEncoded() throws IOException {
        final ColorTransform object =
            new ColorTransform(1, 2, 3, 4, 1.0f, 2.0f, 3.0f, 4.0f);
        final byte[] binary = new byte[] {(byte) 0xF0, 0x40, 0x08, 0x00,
                (byte) 0xC0, 0x10, 0x00, 0x00,
                0x40, 0x08, 0x00, (byte) 0xC0, 0x10 };

        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream);
        final Context context = new Context();
        context.put(Context.TRANSPARENT, 1);

        final int length = object.prepareToEncode(context);
        object.encode(encoder, context);
        encoder.flush();

        assertEquals(CALCULATED_LENGTH, binary.length, length);

        assertArrayEquals(NOT_ENCODED, binary, stream.toByteArray());
    }

    @Test
    public void checkTransparentTermsAreDecoded() throws IOException {
        final ColorTransform object =
            new ColorTransform(1, 2, 3, 4, 1.0f, 2.0f, 3.0f, 4.0f);
        final byte[] binary = new byte[] {(byte) 0xF0, 0x40, 0x08, 0x00,
                (byte) 0xC0, 0x10, 0x00, 0x00,
                0x40, 0x08, 0x00, (byte) 0xC0, 0x10 };

        final ByteArrayInputStream stream = new ByteArrayInputStream(binary);
        final SWFDecoder decoder = new SWFDecoder(stream);
        final Context context = new Context();
        context.put(Context.TRANSPARENT, 1);

        assertEquals(NOT_DECODED, object, new ColorTransform(decoder, context));
    }

}
