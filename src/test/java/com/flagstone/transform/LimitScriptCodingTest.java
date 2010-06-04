/*
 * LimitScriptCodingTest.java
 * Transform
 *
 * Copyright (c) 2010 Flagstone Software Ltd. All rights reserved.
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
package com.flagstone.transform;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public final class LimitScriptCodingTest extends AbstractCodingTest {

    @Test
    public void checkLimitScriptLengthForEncoding() throws IOException {
        final LimitScript object = new LimitScript(1, 30);
        final byte[] binary = new byte[] {0x44, 0x10, 0x01, 0x00, 0x1E, 0x00};

        assertEquals(CALCULATED_LENGTH, binary.length, prepare(object));
    }

    @Test
    public void checkLimitScriptIsEncoded() throws IOException {
        final LimitScript object = new LimitScript(1, 30);
        final byte[] binary = new byte[] {0x44, 0x10, 0x01, 0x00, 0x1E, 0x00};

        assertArrayEquals(NOT_ENCODED, binary, encode(object));
    }

    @Test
    public void checkLimitScriptIsDecoded() throws IOException {
        final byte[] binary = new byte[] {0x44, 0x10, 0x01, 0x00, 0x1E, 0x00};

        LimitScript object = (LimitScript) decodeMovieTag(binary);
        assertEquals(NOT_DECODED, 1, object.getDepth());
        assertEquals(NOT_DECODED, 30, object.getTimeout());
   }

    @Test
    public void checkExtendedLimitScriptIsDecoded() throws IOException {
        final byte[] binary = new byte[] {0x7F, 0x10, 0x04, 0x00, 0x00, 0x00,
                0x01, 0x00, 0x1E, 0x00};

        LimitScript object = (LimitScript) decodeMovieTag(binary);
        assertEquals(NOT_DECODED, 1, object.getDepth());
        assertEquals(NOT_DECODED, 30, object.getTimeout());
   }
}
