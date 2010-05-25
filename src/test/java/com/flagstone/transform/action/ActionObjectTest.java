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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.IOException;
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
        final SWFEncoder encoder = new SWFEncoder(encoded.length);
        final Context context = new Context();

        fixture = new ActionObject(TYPE, data);
        assertEquals(encoded.length, fixture.prepareToEncode(context));
        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertArrayEquals(encoded, encoder.getData());
    }

    @Test
    public void encodeBasic() throws IOException {
        final SWFEncoder encoder = new SWFEncoder(basic.length);
        final Context context = new Context();

        fixture = new ActionObject(1);
        assertEquals(basic.length, fixture.prepareToEncode(context));
        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertArrayEquals(basic, encoder.getData());
    }

    @Test
    public void encodeEmpty() throws IOException {
        final SWFEncoder encoder = new SWFEncoder(empty.length);
        final Context context = new Context();

        fixture = new ActionObject(TYPE, new byte[0]);
        assertEquals(empty.length, fixture.prepareToEncode(context));
        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertArrayEquals(empty, encoder.getData());
    }

    @Test
    public void decode() throws IOException {
        final SWFDecoder decoder = new SWFDecoder(encoded);

        fixture = new ActionObject(decoder.readByte(), decoder);

        assertTrue(decoder.eof());
        assertEquals(TYPE, fixture.getType());
        assertArrayEquals(data, fixture.getData());
    }

    @Test
    public void decodeBasic() throws IOException {
        final SWFDecoder decoder = new SWFDecoder(basic);

        fixture = new ActionObject(decoder.readByte(), decoder);

        assertTrue(decoder.eof());
        assertEquals(1, fixture.getType());
        assertArrayEquals(null, fixture.getData());
    }

    @Test
    public void decodeEmpty() throws IOException {
        final SWFDecoder decoder = new SWFDecoder(empty);

        fixture = new ActionObject(decoder.readByte(), decoder);

        assertTrue(decoder.eof());
        assertEquals(TYPE, fixture.getType());
        assertArrayEquals(new byte[0], fixture.getData());
    }
}
