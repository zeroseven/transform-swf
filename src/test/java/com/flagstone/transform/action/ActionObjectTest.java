/*
 * ActionObjectTest.java
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
package com.flagstone.transform.action;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

public final class ActionObjectTest {

    private static final transient int TYPE = 128;
    private final transient byte[] data = new byte[] {1, 2, 3, 4 };

    private transient ActionObject fixture;

    private final transient byte[] basic = new byte[] {(byte) 0x01 };

    private final transient byte[] empty =
        new byte[] {(byte) 0x80, 0x00, 0x00 };

    private final transient byte[] encoded = new byte[] {(byte) 0x80, 0x04,
            0x00, 0x01, 0x02, 0x03, 0x04 };

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessorForDataWithNull() {
        fixture = new ActionObject(TYPE, (byte[]) null);
    }

    @Test
    public void checkCopy() {
        fixture = new ActionObject(TYPE, data);
        final ActionObject copy = fixture.copy();

        assertNotSame(fixture, copy);
        assertNotSame(fixture.getData(), copy.getData());
        assertEquals(fixture.toString(), copy.toString());
    }

    @Test
    public void encode() throws IOException {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream);
        final Context context = new Context();

        fixture = new ActionObject(TYPE, data);
        assertEquals(encoded.length, fixture.prepareToEncode(context));
        fixture.encode(encoder, context);


        assertArrayEquals(encoded, stream.toByteArray());
    }

    @Test
    public void encodeBasic() throws IOException {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream);
        final Context context = new Context();

        fixture = new ActionObject(1);
        assertEquals(basic.length, fixture.prepareToEncode(context));
        fixture.encode(encoder, context);


        assertArrayEquals(basic, stream.toByteArray());
    }

    @Test
    public void encodeEmpty() throws IOException {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream);
        final Context context = new Context();

        fixture = new ActionObject(TYPE, new byte[0]);
        assertEquals(empty.length, fixture.prepareToEncode(context));
        fixture.encode(encoder, context);


        assertArrayEquals(empty, stream.toByteArray());
    }

    @Test
    public void decode() throws IOException {
        final ByteArrayInputStream stream = new ByteArrayInputStream(encoded);
        final SWFDecoder decoder = new SWFDecoder(stream);

        fixture = new ActionObject(decoder.readByte(), decoder);

        assertEquals(TYPE, fixture.getType());
        assertArrayEquals(data, fixture.getData());
    }

    @Test
    public void decodeBasic() throws IOException {
        final ByteArrayInputStream stream = new ByteArrayInputStream(basic);
        final SWFDecoder decoder = new SWFDecoder(stream);

        fixture = new ActionObject(decoder.readByte(), decoder);

        assertEquals(1, fixture.getType());
        assertArrayEquals(null, fixture.getData());
    }

    @Test
    public void decodeEmpty() throws IOException {
        final ByteArrayInputStream stream = new ByteArrayInputStream(empty);
        final SWFDecoder decoder = new SWFDecoder(stream);

        fixture = new ActionObject(decoder.readByte(), decoder);

        assertEquals(TYPE, fixture.getType());
        assertArrayEquals(new byte[0], fixture.getData());
    }
}
