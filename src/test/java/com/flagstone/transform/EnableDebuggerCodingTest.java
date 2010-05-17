/*
 * EnableDebuggerCodingTest.java
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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

public final class EnableDebuggerCodingTest {

    private static final String CALCULATED_LENGTH =
        "Incorrect calculated length";
    private static final String NOT_FULLY_ENCODED =
        "Data was not fully encoded";
    private static final String NOT_FULLY_DECODED =
        "Data was not fully decoded";
    private static final String NOT_ENCODED =
        "Object was not encoded properly";
    private static final String NOT_DECODED =
        "Object was not decoded properly";

    @Test
    public void checkEnableDebuggerIsEncoded() throws CoderException {
        final EnableDebugger object = new EnableDebugger("ABC123");
        final byte[] binary = new byte[] {(byte) 0x89, 0x0E, 0x00, 0x00, 0x41,
                0x42, 0x43, 0x31, 0x32, 0x33, 0x00 };

        final SWFEncoder encoder = new SWFEncoder(binary.length);
        final Context context = new Context();

        final int length = object.prepareToEncode(context);
        object.encode(encoder, context);

        assertEquals(CALCULATED_LENGTH, binary.length, length);
        assertTrue(NOT_FULLY_ENCODED, encoder.eof());
        assertArrayEquals(NOT_ENCODED, binary, encoder.getData());
    }

    @Test
    public void checkEnableDebuggerIsDecoded() throws CoderException {
        final byte[] binary = new byte[] {(byte) 0x89, 0x0E, 0x00, 0x00, 0x41,
                0x42, 0x43, 0x31, 0x32, 0x33, 0x00 };

        final SWFDecoder decoder = new SWFDecoder(binary);
        final EnableDebugger object = new EnableDebugger(decoder);

        assertEquals(NOT_DECODED, "ABC123", object.getPassword());
        assertTrue(NOT_FULLY_DECODED, decoder.eof());
   }

    @Test
    public void checkExtendedEnableDebuggerIsDecoded() throws CoderException {
        final byte[] binary = new byte[] {(byte) 0xBF, 0x0E, 0x09, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x41, 0x42, 0x43, 0x31, 0x32, 0x33, 0x00 };

        final SWFDecoder decoder = new SWFDecoder(binary);
        final EnableDebugger object = new EnableDebugger(decoder);

        assertEquals(NOT_DECODED, "ABC123", object.getPassword());
        assertTrue(NOT_FULLY_DECODED, decoder.eof());
   }
}
