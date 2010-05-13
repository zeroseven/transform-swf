/*
 * FLVEncoderTest.java
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

public final class FLVEncoderTest {
    private transient FLVEncoder fixture;

    private transient byte[] data;

    @Before
    public void setUp() {
        fixture = new FLVEncoder(0);
    }

    @Test
    public void writeWordUnsigned() {
        data = new byte[] {1, 2, 3, 4 };

        fixture.setData(new byte[data.length]);
        fixture.writeWord(0x01020304, data.length);

        assertArrayEquals(data, fixture.data);
        assertEquals(data.length << 3, fixture.getPointer());
    }

    @Test
    public void writeWordSigned() {
        data = new byte[] {-1, -128, 3, 4 };

        fixture.setData(new byte[data.length]);
        fixture.writeWord(0xFF800304, data.length);

        assertArrayEquals(data, fixture.data);
        assertEquals(data.length << 3, fixture.getPointer());
    }

    @Test
    public void readDouble() {
        data = new byte[] {0x3F, (byte) 0xF0, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00 };

        fixture.setData(new byte[data.length]);
        fixture.writeDouble(1.0);

        assertArrayEquals(data, fixture.data);
        assertEquals(data.length << 3, fixture.getPointer());
    }
}
