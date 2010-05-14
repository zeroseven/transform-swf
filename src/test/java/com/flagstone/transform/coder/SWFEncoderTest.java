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

import org.junit.Before;
import org.junit.Test;

public final class SWFEncoderTest {
    private transient SWFEncoder fixture;

    private transient byte[] data;

    @Before
    public void setUp() {
        fixture = new SWFEncoder(0);
    }

    @Test
    public void sizeVariableU32InOneByte() {
        assertEquals(1, SWFEncoder.sizeVariableU32(127));
    }

    @Test
    public void sizeVariableU32InTwoBytes() {
        assertEquals(2, SWFEncoder.sizeVariableU32(255));
    }

    @Test
    public void sizeVariableU32InThreeBytes() {
        assertEquals(3, SWFEncoder.sizeVariableU32(65535));
    }

    @Test
    public void sizeVariableU32InFourBytes() {
        assertEquals(4, SWFEncoder.sizeVariableU32(16777215));
    }

    @Test
    public void sizeVariableU32InFiveBytes() {
        assertEquals(5, SWFEncoder.sizeVariableU32(2147483647));
    }

    @Test
    public void writeI16() {
        data = new byte[] { 2, 1 };

        fixture.data = new byte[data.length];
        fixture.writeI16(0x0102);

        assertArrayEquals(data, fixture.data);
        assertEquals(data.length << 3, fixture.getPointer());
    }

    @Test
    public void writeI32() {
        data = new byte[] {4, 3, 2, 1 };

        fixture.data = new byte[data.length];
        fixture.writeI32(0x01020304);

        assertArrayEquals(data, fixture.data);
        assertEquals(data.length << 3, fixture.getPointer());
    }

    @Test
    public void writeWordUnsigned() {
        data = new byte[] {4, 3, 2, 1 };

        fixture.data = new byte[data.length];
        fixture.writeWord(0x01020304, data.length);

        assertArrayEquals(data, fixture.data);
        assertEquals(data.length << 3, fixture.getPointer());
    }

    @Test
    public void writeWordSigned() {
        data = new byte[] {4, 3, -128, -1 };

        fixture.data = new byte[data.length];
        fixture.writeWord(0xFF800304, data.length);

        assertArrayEquals(data, fixture.data);
        assertEquals(data.length << 3, fixture.getPointer());
    }

    @Test
    public void writeVariableU32InOneByte() {
        data = new byte[] {127 };

        fixture.data = new byte[data.length];
        fixture.writeVariableU32(127);

        assertArrayEquals(data, fixture.data);
        assertEquals(data.length << 3, fixture.getPointer());
    }

    @Test
    public void writeVariableU32InTwoBytes() {
        data = new byte[] {-1, 1 };

        fixture.data = new byte[data.length];
        fixture.writeVariableU32(255);

        assertArrayEquals(data, fixture.data);
        assertEquals(data.length << 3, fixture.getPointer());
    }

    @Test
    public void writeVariableU32InThreeBytes() {
        data = new byte[] {-1, -1, 3 };

        fixture.data = new byte[data.length];
        fixture.writeVariableU32(65535);

        assertArrayEquals(data, fixture.data);
        assertEquals(data.length << 3, fixture.getPointer());
    }

    @Test
    public void writeVariableU32InFourBytes() {
        data = new byte[] {-1, -1, -1, 7 };

        fixture.data = new byte[data.length];
        fixture.writeVariableU32(16777215);

        assertArrayEquals(data, fixture.data);
        assertEquals(data.length << 3, fixture.getPointer());
    }

    @Test
    public void writeVariableU32InFiveBytes() {
        data = new byte[] {-1, -1, -1, -1, 7 };

        fixture.data = new byte[data.length];
        fixture.writeVariableU32(2147483647);

        assertArrayEquals(data, fixture.data);
        assertEquals(data.length << 3, fixture.getPointer());
    }

    @Test
    public void writeNegativeHalf() {
        data = new byte[] {0x00, (byte) 0xC0 };

        fixture.data = new byte[data.length];
        fixture.writeHalf(-2.0f);

        assertArrayEquals(data, fixture.data);
        assertEquals(data.length << 3, fixture.getPointer());
    }

    @Test
    public void writeHalfFraction() {
        data = new byte[] {0x55, (byte) 0x35 };

        fixture.data = new byte[data.length];
        fixture.writeHalf(0.333251953125f);

        assertArrayEquals(data, fixture.data);
        assertEquals(data.length << 3, fixture.getPointer());
    }

    @Test
    public void writeFloat() {
        data = new byte[] {0x00, 0x00, 0x00, (byte) 0xC0 };

        fixture.data = new byte[data.length];
        fixture.writeFloat(-2);

        assertArrayEquals(data, fixture.data);
        assertEquals(data.length << 3, fixture.getPointer());
    }

    @Test
    public void writeDouble() {
        data = new byte[] {0x00, 0x00, (byte) 0xF0, 0x3F, 0x00, 0x00, 0x00,
                0x00 };

        fixture.data = new byte[data.length];
        fixture.writeDouble(1.0);

        assertArrayEquals(data, fixture.data);
        assertEquals(data.length << 3, fixture.getPointer());
    }
}
