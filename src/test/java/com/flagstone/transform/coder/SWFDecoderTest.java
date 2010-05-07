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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public final class SWFDecoderTest {
    private transient SWFDecoder fixture;

    private transient byte[] data;

    @Before
    public void setUp() {
        fixture = new SWFDecoder(new byte[0]);
    }

    @Test
    public void readUnsignedShort() {
        data = new byte[] { -1, -1 };
        fixture.setData(data);

        assertEquals(65535, fixture.scanUnsignedShort());
        assertEquals(0, fixture.getPointer());
    }

    @Test
    public void readWordUnsigned() {
        data = new byte[] { 4, 3, 2, 1 };

        fixture.setData(data);

        assertEquals(0x01020304, fixture.readWord(data.length, false));
        assertEquals(data.length << 3, fixture.getPointer());
    }

    @Test
    public void readWordSigned() {
        data = new byte[] { 4, 3, -128, -1 };

        fixture.setData(data);

        assertEquals(0xFF800304, fixture.readWord(data.length, true));
        assertEquals(data.length << 3, fixture.getPointer());
    }

    @Test
    public void readWordWithSignExtension() {
        data = new byte[] { 4, 3, -128 };

        fixture.setData(data);

        assertEquals(0xFF800304, fixture.readWord(data.length, true));
        assertEquals(data.length << 3, fixture.getPointer());
    }

    @Test
    public void readVariableU32InOneByte() {
        data = new byte[] { 127 };
        fixture.data = data;

        assertEquals(127, fixture.readVariableU32());
        assertEquals(data.length << 3, fixture.getPointer());
    }

    @Test
    public void readVariableU32InTwoBytes() {
        data = new byte[] { -1, 1 };
        fixture.data = data;

        assertEquals(255, fixture.readVariableU32());
        assertEquals(data.length << 3, fixture.getPointer());
    }

    @Test
    public void readVariableU32InThreeBytes() {
        data = new byte[] { -1, -1, 3 };
        fixture.data = data;

        assertEquals(65535, fixture.readVariableU32());
        assertEquals(data.length << 3, fixture.getPointer());
    }

    @Test
    public void readVariableU32InFourBytes() {
        data = new byte[] { -1, -1, -1, 7 };
        fixture.data = data;

        assertEquals(16777215, fixture.readVariableU32());
        assertEquals(data.length << 3, fixture.getPointer());
    }

    @Test
    public void readVariableU32InFiveBytes() {
        data = new byte[] { -1, -1, -1, -1, 7 };
        fixture.data = data;

        assertEquals(2147483647, fixture.readVariableU32());
        assertEquals(data.length << 3, fixture.getPointer());
    }

    @Test
    public void readNegativeHalf() {
        data = new byte[] { 0x00, (byte) 0xC0 };
        fixture.setData(data);

        assertEquals(-2.0, fixture.readHalf(), 0.0);
        assertEquals(16, fixture.getPointer());
    }

    @Test
    public void readHalfFraction() {
        data = new byte[] { 0x55, (byte) 0x35 };
        fixture.setData(data);

        assertEquals(0.333251953125, fixture.readHalf(), 0.0);
        assertEquals(16, fixture.getPointer());
    }

    @Test
    public void readFloat() {
        data = new byte[] { 0x00, 0x00, 0x00, (byte) 0xC0 };
        fixture.setData(data);

        assertEquals(-2.0, fixture.readFloat(), 0.0);
        assertEquals(32, fixture.getPointer());
    }

    @Test
    public void readDouble() {
        data = new byte[] { 0x00, 0x00, (byte) 0xF0, 0x3F, 0x00, 0x00, 0x00,
                0x00 };
        fixture.setData(data);

        assertEquals(1.0, fixture.readDouble(), 0.0);
        assertEquals(64, fixture.getPointer());
    }

    @Test
    public void findWordWithSuccess() {
        data = new byte[] { 0x30, 0x30, 0x31 };
        fixture.setData(data);

        assertTrue(fixture.findWord(0x3130, 2, 1));
        assertEquals(8, fixture.getPointer());
    }

    @Test
    public void findWordWithoutSuccess() {
        data = new byte[] { 0x30, 0x30, 0x31 };
        fixture.setData(data);
        fixture.setPointer(8);

        assertFalse(fixture.findWord(0x41, 1, 1));
        assertEquals(8, fixture.getPointer());
    }
}
