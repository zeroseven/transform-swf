/*
 * DoABCTest.java
 * Transform
 *
 * Copyright (c) 2009 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.flagstone.transform;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

public final class DoABCTest {

    private static transient final String name = "script";
    private static transient final boolean defer = true;
    private static transient final byte[] data = new byte[] { 1, 2, 3, 4 };

    private transient DoABC fixture;

    private transient final byte[] encoded = new byte[] { (byte) 0x8F, 0x14,
            0x00, 0x00, 0x00, 0x01, 0x73, 0x63, 0x72, 0x69, 0x70, 0x74, 0x00,
            0x01, 0x02, 0x03, 0x04 };

    private transient final byte[] extended = new byte[] { (byte) 0xBF, 0x14,
            0x0F, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x73, 0x63, 0x72,
            0x69, 0x70, 0x74, 0x00, 0x01, 0x02, 0x03, 0x04 };

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessorForNameWithNull() {
        fixture = new DoABC(null, defer, data);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessorForNameWithEmpty() {
        fixture = new DoABC("", defer, data);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessorForDataWithNull() {
        fixture = new DoABC(name, defer, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessorForDataWithEmpty() {
        fixture = new DoABC(name, defer, new byte[0]);
    }

    @Test
    public void checkCopy() {
        fixture = new DoABC(name, false, data);
        final DoABC copy = fixture.copy();

        assertEquals(name, copy.getName());
        assertEquals(false, copy.isDeferred());
        assertNotSame(data, copy.getData());
        assertArrayEquals(data, copy.getData());
        assertEquals(fixture.toString(), copy.toString());
    }

    @Test
    public void encode() throws CoderException {
        final SWFEncoder encoder = new SWFEncoder(encoded.length);
        final Context context = new Context();

        fixture = new DoABC(name, defer, data);
        assertEquals(encoded.length, fixture.prepareToEncode(encoder, context));
        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertArrayEquals(encoded, encoder.getData());
    }

    @Test
    public void encodeDefault() throws CoderException {
        final SWFEncoder encoder = new SWFEncoder(encoded.length);
        final Context context = new Context();

        fixture = new DoABC(name, defer, data);
        assertEquals(encoded.length, fixture.prepareToEncode(encoder, context));
        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertArrayEquals(encoded, encoder.getData());
    }

    @Test
    public void encodeExtended() throws CoderException {
        final SWFEncoder encoder = new SWFEncoder(117);
        final Context context = new Context();

        fixture = new DoABC(name, defer, new byte[100]);
        assertEquals(117, fixture.prepareToEncode(encoder, context));
        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
    }

    @Test
    public void checkDecode() throws CoderException {
        final SWFDecoder decoder = new SWFDecoder(encoded);

        fixture = new DoABC(decoder);

        assertTrue(decoder.eof());
        assertEquals(name, fixture.getName());
        assertEquals(defer, fixture.isDeferred());
        assertArrayEquals(data, fixture.getData());
    }

    @Test
    public void checkDecodeExtended() throws CoderException {
        final SWFDecoder decoder = new SWFDecoder(extended);

        fixture = new DoABC(decoder);

        assertTrue(decoder.eof());
        assertEquals(name, fixture.getName());
        assertEquals(defer, fixture.isDeferred());
        assertArrayEquals(data, fixture.getData());
    }
}
