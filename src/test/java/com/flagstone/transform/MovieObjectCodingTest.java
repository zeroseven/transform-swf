/*
 * MovieObjectCodingTest.java
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

public final class MovieObjectCodingTest {

    private static final String CALCULATED_LENGTH =
        "Incorrect calculated length";
    private static final String NOT_ENCODED =
        "Object was not encoded properly";
    private static final String NOT_DECODED =
        "Object was not decoded properly";

    @Test
    public void checkMovieObjectIsEncoded() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4};
        final MovieObject object = new MovieObject(1, data);
        final byte[] binary = new byte[] {0x44, 0x00, 0x01, 0x02, 0x03, 0x04};

        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream);
        final Context context = new Context();

        final int length = object.prepareToEncode(context);
        object.encode(encoder, context);
        encoder.flush();

        assertEquals(CALCULATED_LENGTH, binary.length, length);

        assertArrayEquals(NOT_ENCODED, binary, stream.toByteArray());
    }

    @Test
    public void checkMovieObjectIsDecoded() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4};
        final byte[] binary = new byte[] {0x44, 0x00, 0x01, 0x02, 0x03, 0x04};

        final ByteArrayInputStream stream = new ByteArrayInputStream(binary);
        final SWFDecoder decoder = new SWFDecoder(stream);
        final MovieObject object = new MovieObject(decoder);

        assertArrayEquals(NOT_DECODED, data, object.getData());
   }

    @Test
    public void checkExtendedMovieObjectIsDecoded() throws IOException {
        final byte[] data = new byte[] {1, 2, 3, 4};
        final byte[] binary = new byte[] {0x7F, 0x00, 0x04, 0x00, 0x00, 0x00,
                0x01, 0x02, 0x03, 0x04};

        final ByteArrayInputStream stream = new ByteArrayInputStream(binary);
        final SWFDecoder decoder = new SWFDecoder(stream);
        final MovieObject object = new MovieObject(decoder);

        assertArrayEquals(NOT_DECODED, data, object.getData());
   }
}
