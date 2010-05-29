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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public final class EncoderTest {
    private transient SWFEncoder fixture;

    private transient byte[] data;

    @Before
    public void setUp() {
        fixture = new SWFEncoder(0);
    }

    @Test
    public void sizeForSignedByte() {
        assertEquals(8, SWFEncoder.size(-128));
        assertEquals(8, SWFEncoder.size(127));
    }

    @Test
    public void sizeForSignedShort() {
        assertEquals(16, SWFEncoder.size(-32768));
        assertEquals(16, SWFEncoder.size(32767));
    }

    @Test
    public void sizeForSignedInt() {
        assertEquals(32, SWFEncoder.size(-2147483648));
        assertEquals(32, SWFEncoder.size(2147483647));
    }

    @Test
    public void sizeForUnsignedByte() {
        assertEquals(8, SWFEncoder.unsignedSize(255));
    }

    @Test
    public void sizeForUnsignedShort() {
        assertEquals(16, SWFEncoder.unsignedSize(32768));
    }

    @Test
    public void sizeForUnsignedInt() {
        assertEquals(31, SWFEncoder.unsignedSize(2147483647));
    }

    @Test
    public void writeBits() throws IOException {
        fixture = new SWFEncoder(1);
        fixture.writeBits(3, 2);

//        assertEquals(2, fixture.getPointer());
//        assertEquals(-64, fixture.data[0]);
    }

    @Test
    public void writeBitsToEndOfBuffer() throws IOException {
        fixture = new SWFEncoder(1);
        fixture.setPointer(4);
        fixture.writeBits(3, 4);

//        assertEquals(8, fixture.getPointer());
//        assertEquals(3, fixture.data[0]);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void writeBitsBeyondEndOfBuffer() throws IOException {
        fixture = new SWFEncoder(1);
        fixture.writeBits(3, 9);
    }

    @Test
    public void writeBitsAcrossByteBoundary() throws IOException {
        fixture = new SWFEncoder(2);
        fixture.writeBits(15, 10);

//        assertEquals(10, fixture.getPointer());
//        assertEquals(3, fixture.data[0]);
//        assertEquals(-64, fixture.data[1]);
    }

    @Test
    public void writeBitsAcrossByteBoundaryWithOffset() throws IOException {
        fixture = new SWFEncoder(2);
        fixture.setPointer(1);
        fixture.writeBits(1, 8);

//        assertEquals(9, fixture.getPointer());
//        assertEquals(0, fixture.data[0]);
//        assertEquals(-128, fixture.data[1]);
    }

    @Test
    public void writeBitsAcrossShortBoundary() throws IOException {
        fixture = new SWFEncoder(3);
        fixture.writeBits(15, 18);

//        assertEquals(18, fixture.getPointer());
//        assertEquals(0, fixture.data[0]);
//        assertEquals(3, fixture.data[1]);
//        assertEquals(-64, fixture.data[2]);
    }

    @Test
    public void writeBitsAcrossIntBoundary() throws IOException {
        fixture = new SWFEncoder(5);
        fixture.setPointer(2);
        fixture.writeBits(15, 32);

//        assertEquals(0, fixture.data[0]);
//        assertEquals(0, fixture.data[1]);
//        assertEquals(0, fixture.data[2]);
//        assertEquals(3, fixture.data[3]);
//        assertEquals(-64, fixture.data[4]);
    }

    @Test
    public void writeByte() throws IOException {
        fixture = new SWFEncoder(1);
        fixture.writeByte(3);

//        assertEquals(8, fixture.getPointer());
//        assertEquals(3, fixture.data[0]);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void writeByteBeyondBufferBoundary() throws IOException {
        fixture = new SWFEncoder(1);
        fixture.setPointer(8);

        fixture.writeByte(1);
    }

    @Test
    public void writeBytes() throws IOException {
        data = new byte[] {1, 2, 3, 4, 5, 6, 7, 8 };
        fixture = new SWFEncoder(data.length);

        fixture.writeBytes(data);

//        assertEquals(data.length << 3, fixture.getPointer());
//        assertArrayEquals(data, fixture.data);
    }

    @Test
    public void writeString() throws IOException {
        fixture = new SWFEncoder(4);
        fixture.setEncoding("UTF-8");

        fixture.writeString("123");

//        assertEquals(32, fixture.getPointer());
//        assertArrayEquals(new byte[] {0x31, 0x32, 0x33, 0x00 }, fixture.data);
    }
}
