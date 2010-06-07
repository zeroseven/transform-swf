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

import org.junit.Ignore;
import org.junit.Test;

public final class LittleDecoderTest {
    private transient LittleDecoder fixture;

    @Test @Ignore
    public void readBitsForUnsignedNumber() {
//        fixture = new LittleDecoder(new byte[] {3 });
//        fixture.setPointer(6);
//
//        assertEquals(3, fixture.readBits(2, false));
//        assertEquals(8, fixture.getPointer());
    }

    @Test @Ignore
    public void readBitsForSignedNumber() {
//        fixture = new LittleDecoder(new byte[] {3 });
//        fixture.setPointer(6);
//
//        assertEquals(-1, fixture.readBits(2, true));
//        assertEquals(8, fixture.getPointer());
    }

    @Test @Ignore
    public void readBitsToEndOfBuffer() {
//        fixture = new LittleDecoder(new byte[] {3 });
//        fixture.setPointer(6);
//
//        assertEquals(3, fixture.readBits(2, false));
//        assertEquals(8, fixture.getPointer());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)  @Ignore
    public void readBitsBeyondEndOfBuffer() {
//        fixture = new LittleDecoder(new byte[] {3 });
//        fixture.setPointer(6);
//
//        fixture.readBits(4, true);
    }

    @Test @Ignore
    public void readBitsAcrossByteBoundary() {
//        fixture = new LittleDecoder(new byte[] {3, (byte) 0xC0 });
//        fixture.setPointer(6);
//
//        assertEquals(-1, fixture.readBits(4, true));
    }

    @Test @Ignore
    public void readBitsAcrossIntBoundary() {
//        fixture = new LittleDecoder(new byte[] {0, 0, 0, 3, (byte) 0xC0 });
//        fixture.setPointer(30);
//
//        assertEquals(-1, fixture.readBits(4, true));
    }

    @Test @Ignore
    public void readZeroBits() {
//        fixture = new LittleDecoder(new byte[] {3 });
//        fixture.setPointer(2);
//
//        assertEquals(0, fixture.readBits(0, true));
//        assertEquals(2, fixture.getPointer());
    }

    @Test @Ignore
    public void readByte() {
//        fixture = new LittleDecoder(new byte[] {1, 2 });
//
//        assertEquals(1, fixture.readByte());
//        assertEquals(2, fixture.readByte());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class) @Ignore
    public void readByteBeyondEndOfBuffer() {
//        fixture = new LittleDecoder(new byte[] {1, 2 });
//
//        fixture.readByte();
//        fixture.readByte();
//        fixture.readByte();
    }

    @Test @Ignore
    public void readBytes() {
//        final byte[] data = new byte[] {1, 2, 3, 4, 5, 6, 7, 8 };
//        final byte[] buffer = new byte[data.length];
//
//        fixture = new LittleDecoder(data);
//        fixture.readBytes(buffer);
//
//        assertEquals(data.length << 3, fixture.getPointer());
//        assertArrayEquals(data, buffer);
    }

    @Test @Ignore
    public void findBitsWithSuccess() {
//        fixture = new LittleDecoder(new byte[] {0x30 });
//
//        assertTrue(fixture.findBits(3, 2, 1));
//        assertEquals(2, fixture.getPointer());
    }

    @Test @Ignore
    public void findBitsWithoutSuccess() {
//        fixture = new LittleDecoder(new byte[] {0x0C });
//        fixture.setPointer(2);
//
//        assertFalse(fixture.findBits(5, 3, 1));
//        assertEquals(2, fixture.getPointer());
    }

    @Test @Ignore
    public void findBitsWithSuccessAtEndOfBuffer() {
//        fixture = new LittleDecoder(new byte[] {0x05 });
//
//        assertTrue(fixture.findBits(5, 3, 1));
//        assertEquals(5, fixture.getPointer());
    }
}
