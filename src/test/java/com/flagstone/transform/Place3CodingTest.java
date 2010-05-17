/*
 * PlaceCodingTest.java
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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.datatype.CoordTransform;

@RunWith(Parameterized.class)
public final class Place3CodingTest {

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
    public void checkAddWithPositionIsEncoded() throws CoderException {
        final int uid = 1;
        final int layer = 2;
        final int xcoord = 1;
        final int ycoord = 2;
        final Place3 object = new Place3.Builder().show(uid, layer,
                xcoord, ycoord);

        final byte[] binary = new byte[] {(byte) 0x88, 0x11, 0x06, 0x00, 0x01,
                0x00, 0x02, 0x00, 0x06, 0x50};

        final SWFEncoder encoder = new SWFEncoder(binary.length);
        final Context context = new Context();

        final int length = object.prepareToEncode(context);
        object.encode(encoder, context);

        assertEquals(CALCULATED_LENGTH, binary.length, length);
        assertTrue(NOT_FULLY_ENCODED, encoder.eof());
        assertArrayEquals(NOT_ENCODED, binary, encoder.getData());
    }

    @Test
    public void checkAddWithPositionIsDecoded() throws CoderException {
        final int identifier = 1;
        final int layer = 2;
        final CoordTransform transform = CoordTransform.translate(1, 1);

        final byte[] binary = new byte[] {(byte) 0x88, 0x11, 0x06, 0x00, 0x01,
                0x00, 0x02, 0x00, 0x06, 0x50};

        final SWFDecoder decoder = new SWFDecoder(binary);
        final Context context = new Context();
        final Place3 object = new Place3(decoder, context);

        assertEquals(NOT_DECODED, identifier, object.getIdentifier());
        assertEquals(NOT_DECODED, layer, object.getLayer());
        assertEquals(NOT_DECODED, transform, object.getTransform());
        assertEquals(NOT_DECODED, null, object.getColorTransform());
        assertTrue(NOT_FULLY_DECODED, decoder.eof());
   }

    @Test
    public void checkExtendedPlaceIsDecoded() throws CoderException {
        final int identifier = 1;
        final int layer = 2;
        final CoordTransform transform = CoordTransform.translate(1, 2);

        final byte[] binary = new byte[] {(byte) 0x8B, 0x11, 0x0E, 0x00, 0x01,
                0x00, 0x02, 0x00, 0x06, 0x50, (byte) 0x90, 0x48, (byte) 0xD0 };

        final SWFDecoder decoder = new SWFDecoder(binary);
        final Context context = new Context();
        final Place3 object = new Place3(decoder, context);

        assertEquals(NOT_DECODED, identifier, object.getIdentifier());
        assertEquals(NOT_DECODED, layer, object.getLayer());
        assertEquals(NOT_DECODED, transform, object.getTransform());
        assertEquals(NOT_DECODED, null, object.getColorTransform());
        assertTrue(NOT_FULLY_DECODED, decoder.eof());
   }
}
