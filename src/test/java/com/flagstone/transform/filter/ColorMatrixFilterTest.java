/*
 * ColorMatrixFilterTest.java
 * Transform
 *
 * Copyright (c) 2009-2010 Flagstone Software Ltd. All rights reserved.
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

package com.flagstone.transform.filter;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFEncoder;

public final class ColorMatrixFilterTest {

    private transient ColorMatrixFilter fixture;

    private final transient byte[] encoded = new byte[] {0x06, 0x01, 0x01,
            0x00, 0x02, 0x00, 0x06, 0x50 };

//    private final transient byte[] extended = new byte[] {0x7F, 0x01, 0x06,
//            0x00, 0x00, 0x00, 0x01, 0x00, 0x02, 0x00, 0x06, 0x50 };

    @Test
    @Ignore 
    public void encodeCoordTransform() throws IOException {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream);
        final Context context = new Context();

        // fixture = new ColorMatrixFilter(identifier, layer, transform);
        assertEquals(encoded.length, fixture.prepareToEncode(context));
        fixture.encode(encoder, context);


    }

    @Test
    @Ignore 
    public void decode() throws IOException {
//        final ByteArrayInputStream stream = new ByteArrayInputStream(encoded);
//        final SWFDecoder decoder = new SWFDecoder(stream);
//
//        // Context context = new Context();
//
//        // fixture = new ColorMatrixFilter(decoder, context);
//
//        assertTrue(true);
    }

    @Test
    @Ignore 
    public void decodeExtended() throws IOException {
//        final ByteArrayInputStream stream =
//               new ByteArrayInputStream(extended);
//        final SWFDecoder decoder = new SWFDecoder(stream);
//        // Context context = new Context();
//
//        // fixture = new ColorMatrixFilter(decoder, context);
//
//        assertTrue(true);
    }
}
