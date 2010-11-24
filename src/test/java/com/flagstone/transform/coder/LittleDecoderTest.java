/*
 * LittleDecoderTest.java
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.EmptyStackException;

import org.junit.Test;

@SuppressWarnings({"PMD.TooManyMethods" })
public final class LittleDecoderTest {

    @Test
    public void markReturnsLocation() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);
        fixture.readInt();

        assertEquals(4, fixture.mark());
    }

    @Test
    public void markTracksBufferRefills() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream, 2);
        fixture.readBytes(new byte[4]);

        assertEquals(4, fixture.mark());
    }

    @Test(expected = EmptyStackException.class)
    public void unmarkClearsLocation() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);
        fixture.readByte();
        fixture.mark();
        fixture.unmark();
        fixture.unmark();
    }

    @Test
    public void resetRestoresLocations() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream, 2);

        fixture.readByte();
        fixture.mark();
        fixture.readByte();
        fixture.reset();
        assertEquals(2, fixture.readByte());
    }

    @Test
    public void resetWithoutMarkReturnsToStart() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);

        fixture.readBytes(new byte[3]);
        fixture.reset();
        assertEquals(1, fixture.readByte());
    }

    @Test(expected = IOException.class)
    public void resetWithoutMarkAfterRefill() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream, 2);

        fixture.readBytes(new byte[3]);
        fixture.reset();
    }

    @Test(expected = IOException.class)
    public void resetAfterRefill() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream, 2);

        fixture.mark();
        fixture.readBytes(new byte[3]);
        fixture.reset();
    }

    @Test
    public void skip() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);

        fixture.skip(2);
        assertEquals(3, fixture.readByte());
    }

    @Test
    public void skipWorksWithBufferRefills() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream, 2);

        fixture.skip(3);
        assertEquals(4, fixture.readByte());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void skipBeyondAvailableData() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);

        fixture.skip(6);
    }

    @Test
    public void byteAlign() throws IOException {
        final byte[] data = new byte[] {-64 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);
        fixture.readBits(2, false);
        fixture.alignToByte();

        assertEquals(1, fixture.mark());
    }

    @Test
    public void noByteAlignOnByteBoundary() throws IOException {
        final byte[] data = new byte[] {1, 2 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);
        fixture.readByte();
        fixture.alignToByte();

        assertEquals(1, fixture.mark());
    }

    @Test
    public void wordAlignReadingBits() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4};
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);
        fixture.readBits(2, false);
        fixture.alignToWord();

        assertEquals(4, fixture.mark());
    }

    @Test
    public void wordAlignReadingBytes() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4};
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);
        fixture.readByte();
        fixture.alignToWord();

        assertEquals(4, fixture.mark());
    }

    @Test
    public void byteRead() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);
        fixture.mark();
        fixture.readInt();

        assertEquals(4, fixture.bytesRead());
    }

    @Test
    public void bytesReadTracksBufferRefills() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream, 2);
        fixture.mark();
        fixture.readByte();
        fixture.readByte();
        fixture.readByte();
        fixture.readByte();

        assertEquals(4, fixture.bytesRead());
    }

    @Test(expected = EmptyStackException.class)
    public void bytesReadWithMarkThrowsException() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream, 2);
        fixture.readByte();
        fixture.readByte();
        fixture.readByte();
        fixture.readByte();

        assertEquals(4, fixture.bytesRead());
    }

    @Test
    public void readBitsForUnsignedNumber() throws IOException {
        final byte[] data = new byte[] {3 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);

        assertEquals(0, fixture.readBits(6, false));
        assertEquals(3, fixture.readBits(2, false));
    }

    @Test
    public void readBitsForSignedNumber() throws IOException {
        final byte[] data = new byte[] {3 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);

        assertEquals(0, fixture.readBits(6, false));
        assertEquals(-1, fixture.readBits(2, true));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void readBitsBeyondEndOfBuffer() throws IOException {
        final byte[] data = new byte[] {3 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);

        fixture.readBits(6, false);
        fixture.readBits(4, true);
    }

    @Test
    public void readBitsAcrossByteBoundary() throws IOException {
        final byte[] data = new byte[] {3, (byte) 0xC0 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);

        fixture.readBits(6, false);
        assertEquals(-1, fixture.readBits(4, true));
    }

    @Test
    public void readBitsAcrossIntBoundary() throws IOException {
        final byte[] data = new byte[] {0, 0, 0, 3, (byte) 0xC0 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);

        fixture.readBits(30, false);
        assertEquals(-1, fixture.readBits(4, true));
    }

    @Test
    public void readZeroBits() throws IOException {
        final byte[] data = new byte[] {3, (byte) 0xC0 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);

        fixture.readBits(6, false);
        assertEquals(0, fixture.readBits(0, true));
    }

    @Test
    public void readByte() throws IOException {
        final byte[] data = new byte[] {1, 2 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);

        assertEquals(1, fixture.readByte());
        assertEquals(2, fixture.readByte());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void readByteBeyondEndOfBuffer() throws IOException {
        final byte[] data = new byte[] {1, 2 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);

        fixture.readByte();
        fixture.readByte();
        fixture.readByte();
    }

    @Test
    public void readBytes() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4, 5, 6, 7, 8 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);

        final byte[] buffer = new byte[data.length];
        fixture.readBytes(buffer);

        assertArrayEquals(data, buffer);
    }

    @Test
    public void readUI16() throws IOException {
        final byte[] data = new byte[] {2, 1, 0, 0 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);

        assertEquals(0x0102, fixture.readUnsignedShort());
    }

    @Test
    public void readUI16DoesNotSignExtend() throws IOException {
        final byte[] data = new byte[] {-1, 0, 0, 0 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);

        assertEquals(255, fixture.readUnsignedShort());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void readUI16WithNoDataAvailable() throws IOException {
        final byte[] data = new byte[] {1, 2 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);
        fixture.readUnsignedShort();
        fixture.readUnsignedShort();
    }

    @Test
    public void readSI16() throws IOException {
        final byte[] data = new byte[] {-1, -1, 0, 0 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);

        assertEquals(-1, fixture.readShort());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void readSI16WithNoDataAvailable() throws IOException {
        final byte[] data = new byte[] {1, 2 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);
        fixture.readShort();
        fixture.readShort();
    }

    @Test
    public void readUI32() throws IOException {
        final byte[] data = new byte[] {4, 3, 2, 1, 0, 0, 0, 0 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);

        assertEquals(0x01020304, fixture.readInt());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void readUI32WithNoDataAvailable() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);
        fixture.readInt();
        fixture.readInt();
    }

    @Test
    public void readUI32DoesNotSignExtend() throws IOException {
        final byte[] data = new byte[] {-1, 0, 0, 0, 0, 0, 0, 0 };
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final LittleDecoder fixture = new LittleDecoder(stream);

        assertEquals(255, fixture.readInt());
    }
}
