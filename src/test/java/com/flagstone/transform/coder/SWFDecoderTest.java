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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.EmptyStackException;

import org.junit.Test;

public final class SWFDecoderTest {


    @Test
    public void markReturnsLocation() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);
        fixture.readInt();

        assertEquals(4, fixture.mark());
    }

    @Test
    public void markTracksBufferRefills() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream, 2);
        fixture.readBytes(new byte[4]);

        assertEquals(4, fixture.mark());
    }

    @Test
    public void unmarkClearsLocation() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);
        fixture.readByte();
        fixture.mark();
        fixture.unmark();

        // CHECKSTYLE IGNORE EmptyBlockCheck FOR NEXT 5 LINES
        try {
            fixture.unmark();
            fail();
        } catch (EmptyStackException e) {
        }
    }

    @Test
    public void resetRestoresLocations() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream, 2);

        fixture.readByte();
        fixture.mark();
        fixture.readByte();
        fixture.reset();
        assertEquals(2, fixture.readByte());
    }

    @Test
    public void resetWithoutMarkReturnsToStart() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        fixture.readBytes(new byte[3]);
        fixture.reset();
        assertEquals(1, fixture.readByte());
    }

    @Test(expected = IOException.class)
    public void resetWithoutMarkAfterRefill() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream, 2);

        fixture.readBytes(new byte[3]);
        fixture.reset();
    }

    @Test(expected = IOException.class)
    public void resetAfterRefill() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream, 2);

        fixture.mark();
        fixture.readBytes(new byte[3]);
        fixture.reset();
    }

    @Test
    public void skip() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        fixture.skip(2);
        assertEquals(3, fixture.readByte());
    }

    @Test
    public void skipWorksWithBufferRefills() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream, 2);

        fixture.skip(3);
        assertEquals(4, fixture.readByte());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void skipBeyondAvailableData() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        fixture.skip(6);
    }

    @Test
    public void byteAlign() throws IOException {
        final byte[] data = new byte[] {-64 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);
        fixture.readBits(2, false);
        fixture.alignToByte();

        assertEquals(1, fixture.mark());
    }

    @Test
    public void noByteAlignOnByteBoundary() throws IOException {
        final byte[] data = new byte[] {1, 2 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);
        fixture.readByte();
        fixture.alignToByte();

        assertEquals(1, fixture.mark());
    }

    @Test
    public void byteRead() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);
        fixture.mark();
        fixture.readInt();

        assertEquals(4, fixture.bytesRead());
    }

    @Test
    public void bytesReadTracksBufferRefills() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream, 2);
        fixture.mark();
        fixture.readByte();
        fixture.readByte();
        fixture.readByte();
        fixture.readByte();

        assertEquals(4, fixture.bytesRead());
    }

    @Test(expected = EmptyStackException.class)
    public void bytesReadWithoutMarkThrowsException() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream, 2);
        fixture.readByte();
        fixture.readByte();
        fixture.readByte();
        fixture.readByte();

        assertEquals(4, fixture.bytesRead());
    }

    @Test
    public void checkWithExpectedCount() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream, 2);
        fixture.mark();
        fixture.readByte();
        fixture.check(1);

        assertEquals(0, fixture.getDelta());
    }

    @Test
    public void checkWithUnexpectedCount() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream, 2);
        fixture.mark();
        fixture.readByte();
        fixture.check(2);

        assertEquals(1, fixture.getDelta());
    }

    @Test
    public void readBitsForUnsignedNumber() throws IOException {
        final byte[] data = new byte[] {3 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals(0, fixture.readBits(6, false));
        assertEquals(3, fixture.readBits(2, false));
    }

    @Test
    public void readBitsForSignedNumber() throws IOException {
        final byte[] data = new byte[] {3 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals(0, fixture.readBits(6, false));
        assertEquals(-1, fixture.readBits(2, true));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void readBitsBeyondEndOfBuffer() throws IOException {
        final byte[] data = new byte[] {3 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        fixture.readBits(6, false);
        fixture.readBits(4, true);
    }

    @Test
    public void readBitsAcrossByteBoundary() throws IOException {
        final byte[] data = new byte[] {3, (byte) 0xC0 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        fixture.readBits(6, false);
        assertEquals(-1, fixture.readBits(4, true));
    }

    @Test
    public void readBitsAcrossIntBoundary() throws IOException {
        final byte[] data = new byte[] {0, 0, 0, 3, (byte) 0xC0 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        fixture.readBits(30, false);
        assertEquals(-1, fixture.readBits(4, true));
    }

    @Test
    public void readZeroBits() throws IOException {
        final byte[] data = new byte[] {3, (byte) 0xC0 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        fixture.readBits(6, false);
        assertEquals(0, fixture.readBits(0, true));
    }

    @Test
    public void scanByte() throws IOException {
        final byte[] data = new byte[] {1, 2 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals(1, fixture.scanByte());
        assertEquals(0, fixture.mark());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void scanByteBeyondEndOfBuffer() throws IOException {
        final byte[] data = new byte[] {1, 2 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        fixture.readByte();
        fixture.readByte();
        fixture.scanByte();
    }

    @Test
    public void readByte() throws IOException {
        final byte[] data = new byte[] {1, 2 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals(1, fixture.readByte());
        assertEquals(2, fixture.readByte());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void readByteBeyondEndOfBuffer() throws IOException {
        final byte[] data = new byte[] {1, 2 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        fixture.readByte();
        fixture.readByte();
        fixture.readByte();
    }

    @Test
    public void readBytes() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4, 5, 6, 7, 8 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        final byte[] buffer = new byte[data.length];
        fixture.readBytes(buffer);

        assertArrayEquals(data, buffer);
    }

    @Test
    public void readString() throws IOException {
        final byte[] data = new byte[] {0x61, 0x62, 0x63, 0x00 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals("abc", fixture.readString());
    }

    @Test
    public void readStringWithRefill() throws IOException {
        final byte[] data = new byte[] {0x61, 0x62, 0x63, 0x00 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream, 2);

        assertEquals("abc", fixture.readString());
    }

    @Test
    public void readStringWithLength() throws IOException {
        final byte[] data = new byte[] {0x61, 0x62, 0x63, 0x00 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals("abc", fixture.readString(3));
    }

    @Test
    public void readStringWithLengthDiscardsNull() throws IOException {
        final byte[] data = new byte[] {0x61, 0x62, 0x63, 0x00 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals("abc", fixture.readString(4));
    }

    @Test
    public void scanUnsignedShort() throws IOException {
        final byte[] data = new byte[] {2, 1, 0, 0 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals(0x0102, fixture.scanUnsignedShort());
        assertEquals(0, fixture.mark());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void scanUnsignedShortWithNoDataAvailable() throws IOException {
        final byte[] data = new byte[] {1, 2 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);
        fixture.readUnsignedShort();
        fixture.scanUnsignedShort();
    }

    @Test
    public void readUnsignedShort() throws IOException {
        final byte[] data = new byte[] {2, 1, 0, 0 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals(0x0102, fixture.readUnsignedShort());
    }

    @Test
    public void testReadUnsignedDoesNotSignExtend() throws IOException {
        final byte[] data = new byte[] { -1, 0, 0, 0 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals(255, fixture.readUnsignedShort());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void readUnsignedShortWithNoDataAvailable() throws IOException {
        final byte[] data = new byte[] {1, 2 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);
        fixture.readUnsignedShort();
        fixture.readUnsignedShort();
    }

    @Test
    public void readSI16() throws IOException {
        final byte[] data = new byte[] {-1, -1, 0, 0 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals(-1, fixture.readSignedShort());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void readSI16WithNoDataAvailable() throws IOException {
        final byte[] data = new byte[] {1, 2 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);
        fixture.readSignedShort();
        fixture.readSignedShort();
    }

    @Test
    public void readInt() throws IOException {
        final byte[] data = new byte[] {4, 3, 2, 1, 0, 0, 0, 0 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals(0x01020304, fixture.readInt());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void readIntWithNoDataAvailable() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);
        fixture.readInt();
        fixture.readInt();
    }

    @Test
    public void testReadIntDoesNotSignExtend() throws IOException {
        final byte[] data = new byte[] { -1, 0, 0, 0, 0, 0, 0, 0 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals(255, fixture.readInt());
    }

    @Test
    public void readVarIntInOneByte() throws IOException {
        final byte[] data = new byte[] {127 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals(127, fixture.readVarInt());
    }

    @Test
    public void readVarIntInTwoBytes() throws IOException {
        final byte[] data = new byte[] {-1, 1 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals(255, fixture.readVarInt());
    }

    @Test
    public void readVarIntInThreeBytes() throws IOException {
        final byte[] data = new byte[] {-1, -1, 3 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals(65535, fixture.readVarInt());
    }

    @Test
    public void readVarIntInFourBytes() throws IOException {
        final byte[] data = new byte[] {-1, -1, -1, 7 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals(16777215, fixture.readVarInt());
    }

    @Test
    public void readVarIntInFiveBytes() throws IOException {
        final byte[] data = new byte[] {-1, -1, -1, -1, 7 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals(2147483647, fixture.readVarInt());
    }

    @Test
    public void readNegativeHalf() throws IOException {
        final byte[] data = new byte[] {0x00, (byte) 0xC0 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals(-2.0, fixture.readHalf(), 0.0);
    }

    @Test
    public void readHalfFraction() throws IOException {
        final byte[] data = new byte[] {0x55, (byte) 0x35 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals(0.333251953125, fixture.readHalf(), 0.0);
    }

    @Test
    public void readHalfDenormalized() throws IOException {
        final byte[] data = new byte[] {(byte) 0x10, (byte) 0x80 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals(-9.5367431640625E-7, fixture.readHalf(), 0.0);
    }

    @Test
    public void readHalfMax() throws IOException {
        final byte[] data = new byte[] {(byte) 0xFF, 0x7B };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals(65504, fixture.readHalf(), 0.0);
    }

    @Test
    public void readHalfPositiveMin() throws IOException {
        final byte[] data = new byte[] {0x00, 0x04 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals(6.103515625E-5, fixture.readHalf(), 0.0);
    }

    @Test
    public void readHalfNaN() throws IOException {
        final byte[] data = new byte[] {0x7B, (byte) 0xFF };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals(Double.NaN, fixture.readHalf(), 0.0);
    }

    @Test
    public void readHalfPositiveInfinity() throws IOException {
        final byte[] data = new byte[] {0x00, 0x7C };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals(Double.POSITIVE_INFINITY, fixture.readHalf(), 0.0);
    }

    @Test
    public void readHalfNegativeInfinity() throws IOException {
        final byte[] data = new byte[] {0x00, (byte) 0xFC};
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals(Double.NEGATIVE_INFINITY, fixture.readHalf(), 0.0);
    }

    @Test
    public void readHalfNegativeZero() throws IOException {
        final byte[] data = new byte[] {0x00, (byte) 0x80 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals(-0.0, fixture.readHalf(), 0.0);
    }

    @Test
    public void readHalfPositiveZero() throws IOException {
        final byte[] data = new byte[] {0x00, 0x00 };
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final SWFDecoder fixture = new SWFDecoder(stream);

        assertEquals(0.0, fixture.readHalf(), 0.0);
    }
}
