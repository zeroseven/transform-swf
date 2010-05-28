/*
 * SWFDecoderTest.java
 * Transform
 *
 * Copyright (c) 2001-2010 Flagstone Software Ltd. All rights reserved.
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

package com.flagstone.transform.coder;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;

public final class SWFDecoderTest {
    private transient SWFDecoder fixture;

    private transient byte[] data;

    @Test
    public void readUI16() throws IOException {
        data = new byte[] {2, 1, 0, 0 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        fixture = new SWFDecoder(stream);

        assertEquals(0x0102, fixture.readUnsignedShort());
    }

    @Test
    public void testReadUI16DoesNotSignExtend() throws IOException {
        data = new byte[] { -1, 0, 0, 0 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        fixture = new SWFDecoder(stream);

        assertEquals(255, fixture.readUnsignedShort());
    }

    @Test
    public void readUI32() throws IOException {
        data = new byte[] {4, 3, 2, 1, 0, 0, 0, 0 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        fixture = new SWFDecoder(stream);

        assertEquals(0x01020304, fixture.readInt());
    }

    @Test
    public void testReadUI32DoesNotSignExtend() throws IOException {
        data = new byte[] { -1, 0, 0, 0, 0, 0, 0, 0 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        fixture = new SWFDecoder(stream);

        assertEquals(255, fixture.readInt());
    }

    @Test
    public void readVariableU32InOneByte() throws IOException {
        data = new byte[] {127 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        fixture = new SWFDecoder(stream);

        assertEquals(127, fixture.readVarInt());
    }

    @Test
    public void readVariableU32InTwoBytes() throws IOException {
        data = new byte[] {-1, 1 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        fixture = new SWFDecoder(stream);

        assertEquals(255, fixture.readVarInt());
    }

    @Test
    public void readVariableU32InThreeBytes() throws IOException {
        data = new byte[] {-1, -1, 3 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        fixture = new SWFDecoder(stream);

        assertEquals(65535, fixture.readVarInt());
    }

    @Test
    public void readVariableU32InFourBytes() throws IOException {
        data = new byte[] {-1, -1, -1, 7 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        fixture = new SWFDecoder(stream);

        assertEquals(16777215, fixture.readVarInt());
    }

    @Test
    public void readVariableU32InFiveBytes() throws IOException {
        data = new byte[] {-1, -1, -1, -1, 7 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        fixture = new SWFDecoder(stream);

        assertEquals(2147483647, fixture.readVarInt());
    }

    @Test
    public void readNegativeHalf() throws IOException {
        data = new byte[] {0x00, (byte) 0xC0 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        fixture = new SWFDecoder(stream);

        assertEquals(-2.0, fixture.readHalf(), 0.0);
    }

    @Test
    public void readHalfFraction() throws IOException {
        data = new byte[] {0x55, (byte) 0x35 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        fixture = new SWFDecoder(stream);

        assertEquals(0.333251953125, fixture.readHalf(), 0.0);
    }
}
