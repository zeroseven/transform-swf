/*
 * SWFEncoderTest.java
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.flagstone.transform.CharacterEncoding;

public final class SWFEncoderTest {


    @Test
    public void markReturnsLocation() throws IOException {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder fixture = new SWFEncoder(stream);
        fixture.writeInt(0x04030201);

        assertEquals(4, fixture.mark());
    }

    @Test
    public void markTracksBufferRefills() throws IOException {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder fixture = new SWFEncoder(stream, 2);
        fixture.writeBytes(new byte[4]);

        assertEquals(4, fixture.mark());
    }

    @Test
    public void checkWithExpectedCount() throws IOException {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder fixture = new SWFEncoder(stream, 2);
        fixture.mark();
        fixture.writeByte(0);
        fixture.check(1);
    }

    @Test(expected = CoderException.class)
    public void checkWithUnexpectedCount() throws IOException {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder fixture = new SWFEncoder(stream, 2);
        fixture.mark();
        fixture.writeByte(0);
        fixture.check(2);
    }

    @Test
    public void writeBits() throws IOException {
        byte[] data = new byte[] {-64 };
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream);
        encoder.writeBits(3, 2);
        encoder.alignToByte();
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    public void writeBitsWithFlush() throws IOException {
        byte[] data = new byte[] {0, 1, -128 };
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream, 2);
        encoder.writeByte(0);
        encoder.writeBits(0, 7);
        encoder.writeBits(3, 2);
        encoder.alignToByte();
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    public void writeBitsAcrossByteBoundary() throws IOException {
        byte[] data = new byte[] {3, -64 };
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream);
        encoder.writeBits(15, 10);
        encoder.alignToByte();
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    public void writeBitsAcrossByteBoundaryWithOffset() throws IOException {
        byte[] data = new byte[] {0, -128 };
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream);

        encoder.writeBits(0, 1);
        encoder.writeBits(1, 8);
        encoder.alignToByte();
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    public void writeBitsAcrossShortBoundary() throws IOException {
        byte[] data = new byte[] {0, 3, -64 };
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream);
        encoder.writeBits(15, 18);
        encoder.alignToByte();
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    public void writeBitsAcrossIntBoundary() throws IOException {
        byte[] data = new byte[] {0, 0, 0, 3, -64 };
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream);
        encoder.writeBits(0, 2);
        encoder.writeBits(15, 32);
        encoder.alignToByte();
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    public void writeByte() throws IOException {
        byte[] data = new byte[] {3 };
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream);
        encoder.writeByte(3);
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    public void writeByteWithFlush() throws IOException {
        byte[] data = new byte[] {0, 3 };
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream, 1);
        encoder.writeByte(0);
        encoder.writeByte(3);
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    public void writeBytes() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4, 5, 6, 7, 8 };
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream);

        encoder.writeBytes(data);
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    public void writeString() throws IOException {
        final byte[] data = new byte[] {0x31, 0x32, 0x33, 0x00 };
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream);
        encoder.setEncoding(CharacterEncoding.UTF8);

        encoder.writeString("123");
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    public void writeStringWithFlush() throws IOException {
        final byte[] data = new byte[] {0x00, 0x31, 0x32, 0x33, 0x00 };
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream, 2);
        encoder.setEncoding(CharacterEncoding.UTF8);
        encoder.writeByte(0);
        encoder.writeString("123");
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    public void writeShort() throws IOException {
        byte[] data = new byte[] { 2, 1 };
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        SWFEncoder encoder = new SWFEncoder(stream);
        encoder.writeShort(0x0102);
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    public void writeShortWithFlush() throws IOException {
        byte[] data = new byte[] { 0, 2, 1 };
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        SWFEncoder encoder = new SWFEncoder(stream, 2);
        encoder.writeByte(0);
        encoder.writeShort(0x0102);
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    public void writeInt() throws IOException {
        byte[] data = new byte[] {4, 3, 2, 1 };
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        SWFEncoder encoder = new SWFEncoder(stream);
        encoder.writeInt(0x01020304);
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    public void writeIntWithFlush() throws IOException {
        byte[] data = new byte[] {0, 0, 4, 3, 2, 1 };
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        SWFEncoder encoder = new SWFEncoder(stream, 4);
        encoder.writeByte(0);
        encoder.writeByte(0);
        encoder.writeInt(0x01020304);
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    public void writeVarIntInOneByte() throws IOException {
        byte[] data = new byte[] {127 };
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        SWFEncoder encoder = new SWFEncoder(stream);
        encoder.writeVarInt(127);
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    public void writeVarIntInTwoBytes() throws IOException {
        byte[] data = new byte[] {-1, 1 };
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        SWFEncoder encoder = new SWFEncoder(stream);
        encoder.writeVarInt(255);
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    public void writeVarIntInThreeBytes() throws IOException {
        byte[] data = new byte[] {-1, -1, 3 };
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        SWFEncoder encoder = new SWFEncoder(stream);
        encoder.writeVarInt(65535);
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    public void writeVarIntInFourBytes() throws IOException {
        byte[] data = new byte[] {-1, -1, -1, 7 };
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        SWFEncoder encoder = new SWFEncoder(stream);
        encoder.writeVarInt(16777215);
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    public void writeVarIntInFiveBytes() throws IOException {
        byte[] data = new byte[] {-1, -1, -1, -1, 7 };
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        SWFEncoder encoder = new SWFEncoder(stream);
        encoder.writeVarInt(2147483647);
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    public void writeVarIntWithFlush() throws IOException {
        byte[] data = new byte[] {0, 0, 127 };
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        SWFEncoder encoder = new SWFEncoder(stream, 2);
        encoder.writeByte(0);
        encoder.writeByte(0);
        encoder.writeVarInt(127);
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    public void writeNegativeHalf() throws IOException {
        byte[] data = new byte[] {0x00, (byte) 0xC0 };
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        SWFEncoder encoder = new SWFEncoder(stream);
        encoder.writeHalf(-2.0f);
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    public void writeHalfFraction() throws IOException {
        byte[] data = new byte[] {0x55, (byte) 0x35 };
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        SWFEncoder encoder = new SWFEncoder(stream);
        encoder.writeHalf(0.333251953125f);
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    public void writeHalfOverflow() throws IOException {
        final byte[] data = new byte[] {0x00, 0x7C };
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        SWFEncoder encoder = new SWFEncoder(stream);
        encoder.writeHalf(100000);
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    public void writeHalfPositiveMin() throws IOException {
        final byte[] data = new byte[] {0x00, 0x00 };
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        SWFEncoder encoder = new SWFEncoder(stream);
        encoder.writeHalf(6.0E-9f);
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    public void writeHalfPositiveInfinity() throws IOException {
        final byte[] data = new byte[] {0x00, 0x7C };
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        SWFEncoder encoder = new SWFEncoder(stream);
        encoder.writeHalf(Float.POSITIVE_INFINITY);
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    public void writeHalfNaN() throws IOException {
        final byte[] data = new byte[] {0x00, 0x7E };
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        SWFEncoder encoder = new SWFEncoder(stream);
        encoder.writeHalf(Float.NaN);
        encoder.flush();

        assertArrayEquals(data, stream.toByteArray());
    }
}
