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

import java.io.IOException;

import org.junit.Test;

import com.flagstone.transform.datatype.ColorTransform;
import com.flagstone.transform.datatype.CoordTransform;

public final class PlaceCodingTest extends AbstractCodingTest {

    @Test
    public void checkPlaceWithPositionLengthForEncoding() throws IOException {
        final int uid = 1;
        final int layer = 2;
        final CoordTransform transform = CoordTransform.translate(1, 2);
        final Place object = new Place(uid, layer, transform);

        final byte[] binary = new byte[] {0x06, 0x01, 0x01, 0x00, 0x02, 0x00,
                0x06, 0x50 };

        assertEquals(CALCULATED_LENGTH, binary.length, prepare(object));
    }

    @Test
    public void checkPlaceWithPositionIsEncoded() throws IOException {
        final int uid = 1;
        final int layer = 2;
        final CoordTransform transform = CoordTransform.translate(1, 2);
        final Place object = new Place(uid, layer, transform);

        final byte[] binary = new byte[] {0x06, 0x01, 0x01, 0x00, 0x02, 0x00,
                0x06, 0x50 };

        assertArrayEquals(NOT_ENCODED, binary, encode(object));
    }

    @Test
    public void checkPlaceWithPositionIsDecoded() throws IOException {
        final int identifier = 1;
        final int layer = 2;
        final CoordTransform transform = CoordTransform.translate(1, 2);

        final byte[] binary = new byte[] {0x06, 0x01, 0x01, 0x00, 0x02, 0x00,
                0x06, 0x50 };

        Place object = (Place) decodeMovieTag(binary);
        assertEquals(NOT_DECODED, identifier, object.getIdentifier());
        assertEquals(NOT_DECODED, layer, object.getLayer());
        assertEquals(NOT_DECODED, transform, object.getTransform());
        assertEquals(NOT_DECODED, null, object.getColorTransform());
   }

    @Test
    public void checkPlaceWithColorIsDecoded() throws IOException {
        final int identifier = 1;
        final int layer = 2;
        final CoordTransform transform = CoordTransform.translate(1, 2);
        final ColorTransform color = new ColorTransform(1, 2, 3, 0);

        final byte[] binary = new byte[] {0x08, 0x01, 0x01, 0x00, 0x02, 0x00,
                0x06, 0x50, (byte) 0x8C, (byte) 0xA6 };

        Place object = (Place) decodeMovieTag(binary);
        assertEquals(NOT_DECODED, identifier, object.getIdentifier());
        assertEquals(NOT_DECODED, layer, object.getLayer());
        assertEquals(NOT_DECODED, transform, object.getTransform());
        assertEquals(NOT_DECODED, color, object.getColorTransform());
   }
}
