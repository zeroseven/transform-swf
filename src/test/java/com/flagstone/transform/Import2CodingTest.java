/*
 * Import22CodingTest.java
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
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

public final class Import2CodingTest extends AbstractCodingTest {

    @Test
    public void checkImport2LengthForEncoding() throws IOException {
        final Map<Integer, String>map = new LinkedHashMap<Integer, String>();
        map.put(1, "A");
        map.put(2, "B");
        map.put(3, "C");

        final Import2 object = new Import2("ABC", map);
        final byte[] binary = new byte[] {(byte) 0xD4, 0x11, 0x41, 0x42, 0x43,
                0x00, 0x01, 0x00, 0x03, 0x00, 0x01, 0x00, 0x41, 0x00, 0x02,
                0x00, 0x42, 0x00, 0x03, 0x00, 0x43, 0x00 };

        assertEquals(CALCULATED_LENGTH, binary.length, prepare(object));
    }

    @Test
    public void checkImport2IsEncoded() throws IOException {
        final Map<Integer, String>map = new LinkedHashMap<Integer, String>();
        map.put(1, "A");
        map.put(2, "B");
        map.put(3, "C");

        final Import2 object = new Import2("ABC", map);
        final byte[] binary = new byte[] {(byte) 0xD4, 0x11, 0x41, 0x42, 0x43,
                0x00, 0x01, 0x00, 0x03, 0x00, 0x01, 0x00, 0x41, 0x00, 0x02,
                0x00, 0x42, 0x00, 0x03, 0x00, 0x43, 0x00 };

        assertArrayEquals(NOT_ENCODED, binary, encode(object));
    }

    @Test
    public void checkImport2IsDecoded() throws IOException {
        final Map<Integer, String>map = new LinkedHashMap<Integer, String>();
        map.put(1, "A");
        map.put(2, "B");
        map.put(3, "C");

        final byte[] binary = new byte[] {(byte) 0xD4, 0x11, 0x41, 0x42, 0x43,
                0x00, 0x01, 0x00, 0x03, 0x00, 0x01, 0x00, 0x41, 0x00, 0x02,
                0x00, 0x42, 0x00, 0x03, 0x00, 0x43, 0x00 };

        Import2 object = (Import2) decodeMovieTag(binary);
        assertEquals(NOT_DECODED, "ABC", object.getUrl());
        assertEquals(NOT_DECODED, map, object.getObjects());
    }

    @Test
    public void checkExtendedImport2IsDecoded() throws IOException {
        final Map<Integer, String>map = new LinkedHashMap<Integer, String>();
        map.put(1, "A");
        map.put(2, "B");
        map.put(3, "C");

        final byte[] binary = new byte[] {(byte) 0xFF, 0x11, 0x14, 0x00, 0x00,
                0x00, 0x41, 0x42, 0x43, 0x00, 0x01, 0x00, 0x03, 0x00, 0x01,
                0x00, 0x41, 0x00, 0x02, 0x00, 0x42, 0x00, 0x03, 0x00, 0x43,
                0x00 };

        Import2 object = (Import2) decodeMovieTag(binary);
        assertEquals(NOT_DECODED, "ABC", object.getUrl());
        assertEquals(NOT_DECODED, map, object.getObjects());
   }
}
