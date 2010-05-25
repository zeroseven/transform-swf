/*
 * AlignmentZone.java
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

package com.flagstone.transform.font;

import com.flagstone.transform.Constants;
import java.io.IOException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncodeable;
import com.flagstone.transform.coder.SWFEncoder;

/** TODO(class). */
public final class AlignmentZone implements SWFEncodeable {

    /** Format string used in toString() method. */
    private static final String FORMAT = "AlignmentZone: {"
                + " coordinate=%f; range=%f }";

    private final transient float coordinate;
    private final transient float range;

    /**
     * Creates and initialises an AlignmentZone object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws IOException
     *             if an error occurs while decoding the data.
     */
    public AlignmentZone(final SWFDecoder coder) throws IOException {
        coordinate = coder.readHalf();
        range = coder.readHalf();
    }


    public AlignmentZone(final float coord, final float level) {
        coordinate = coord;
        range = level;
    }


    public float getCoordinate() {
        return coordinate;
    }


    public float getRange() {
        return range;
    }

    @Override
    public String toString() {
        return String.format(FORMAT, coordinate, range);
    }

    @Override
    public boolean equals(final Object object) {
        boolean result;

        if (object == null) {
            result = false;
        } else if (object == this) {
            result = true;
        } else if (object instanceof AlignmentZone) {
            final AlignmentZone zone = (AlignmentZone) object;
            result = (coordinate == zone.coordinate) && (range == zone.range);
        } else {
            result = false;
        }

        return result;
    }

    @Override
    public int hashCode() {
        return (Float.floatToIntBits(coordinate) * Constants.PRIME)
                + Float.floatToIntBits(range);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final Context context) {
        return 4; // SUPPRESS CHECKSTYLE
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws IOException {
        coder.writeHalf(coordinate);
        coder.writeHalf(range);
    }
}
