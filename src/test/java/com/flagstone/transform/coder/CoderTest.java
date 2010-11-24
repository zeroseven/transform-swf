/*
 * CoderTest.java
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

import org.junit.Test;

@SuppressWarnings({"PMD.TooManyMethods" })
public final class CoderTest {

    @Test
    public void sizeForSignedByte() {
        assertEquals(8, Coder.size(-128));
        assertEquals(8, Coder.size(127));
    }

    @Test
    public void sizeForSignedShort() {
        assertEquals(16, Coder.size(-32768));
        assertEquals(16, Coder.size(32767));
    }

    @Test
    public void sizeForSignedInt() {
        assertEquals(32, Coder.size(-2147483648));
        assertEquals(32, Coder.size(2147483647));
    }

    @Test
    public void maxSizeForSignedInts() {
        assertEquals(3, Coder.maxSize(-1, 2, 1, 3));
    }

    @Test
    public void sizeForNegativeByte() {
        assertEquals(8, Coder.unsignedSize(-255));
    }

    @Test
    public void sizeForPositiveByte() {
        assertEquals(8, Coder.unsignedSize(255));
    }

    @Test
    public void sizeForUnsignedShort() {
        assertEquals(16, Coder.unsignedSize(32768));
    }

    @Test
    public void sizeForUnsignedInt() {
        assertEquals(31, Coder.unsignedSize(2147483647));
    }

    @Test
    public void sizeVariableU32InOneByte() {
        assertEquals(1, Coder.sizeVariableU32(127));
    }

    @Test
    public void sizeVariableU32InTwoBytes() {
        assertEquals(2, Coder.sizeVariableU32(255));
    }

    @Test
    public void sizeVariableU32InThreeBytes() {
        assertEquals(3, Coder.sizeVariableU32(65535));
    }

    @Test
    public void sizeVariableU32InFourBytes() {
        assertEquals(4, Coder.sizeVariableU32(16777215));
    }

    @Test
    public void sizeVariableU32InFiveBytes() {
        assertEquals(5, Coder.sizeVariableU32(2147483647));
    }
}
