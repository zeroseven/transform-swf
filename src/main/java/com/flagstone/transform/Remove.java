/*
 * RemoveObject.java
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

package com.flagstone.transform;

import java.io.IOException;

import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.exception.IllegalArgumentRangeException;

/**
 * RemoveObject removes an object from the Flash Player's Display List.
 *
 * <p>
 * An object placed on the display list is displayed in every frame of a movie
 * until it is explicitly removed. Objects must also be removed if its location
 * or appearance is changed using PlaceObject.
 * </p>
 *
 * <p>
 * Although only one object can be placed on any layer in the display list both
 * the object's unique identifier and the layer number must be specified. The
 * RemoveObject class is superseded in Flash 3 by the RemoveObject2 class which
 * lifts this requirement allowing an object to be referenced by the layer
 * number it occupies in the display list.
 * </p>
 *
 * @see Remove2
 * @see Place
 * @see Place2
 */
public final class Remove implements MovieTag {

    /** Format string used in toString() method. */
    private static final String FORMAT = "Remove: { identifier=%d; layer=%d}";

    /** The unique identifier of the object on the display list. */
    private int identifier;
    /** The layer where the object is displayed. */
    private int layer;

    /**
     * Creates and initialises a Remove object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws IOException
     *             if an error occurs while decoding the data.
     */
    public Remove(final SWFDecoder coder) throws IOException {
        if ((coder.readUnsignedShort() & Coder.LENGTH_FIELD)
        		== Coder.IS_EXTENDED) {
            coder.readInt();
        }
        identifier = coder.readUnsignedShort();
        layer = coder.readUnsignedShort();
    }

    /**
     * Creates a RemoveObject object that will remove an object with the
     * specified identifier from the given layer in the display list.
     *
     * @param uid
     *            the unique identifier for the object currently on the display
     *            list. Must be in the range 1.65535.
     * @param level
     *            the layer in the display list where the object is being
     *            displayed. Must be in the range 1.65535.
     */
    public Remove(final int uid, final int level) {
        setIdentifier(uid);
        setLayer(level);
    }

    /**
     * Creates and initialises a Remove object using the values copied
     * from another Remove object.
     *
     * @param object
     *            a Remove object from which the values will be
     *            copied.
     */
    public Remove(final Remove object) {
        identifier = object.identifier;
        layer = object.layer;
    }

    /**
     * Get the identifier of the object to be removed from the display list.
     *
     * @return the identifier if the object to be removed.
     */
    public int getIdentifier() {
        return identifier;
    }

    /**
     * Sets the identifier of the object to be removed.
     *
     * @param uid
     *            the unique identifier for the object currently on the display
     *            list. Must be in the range 1.65535.
     */
    public void setIdentifier(final int uid) {
        if ((uid < 1) || (uid > Coder.USHORT_MAX)) {
            throw new IllegalArgumentRangeException(
                    1, Coder.USHORT_MAX, uid);
        }
        identifier = uid;
    }

    /**
     * Get the layer in the display list where the object will be displayed.
     *
     * @return the layer number.
     */
    public int getLayer() {
        return layer;
    }

    /**
     * Sets the layer in the display list where the object will be displayed.
     *
     * @param aLayer
     *            the layer in the display list where the object is being
     *            displayed. Must be in the range 1.65535.
     */
    public void setLayer(final int aLayer) {
        if ((aLayer < 1) || (aLayer > Coder.USHORT_MAX)) {
            throw new IllegalArgumentRangeException(
                    1, Coder.USHORT_MAX, aLayer);
        }
        layer = aLayer;
    }

    /** {@inheritDoc} */
    @Override
	public Remove copy() {
        return new Remove(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, identifier, layer);
    }

    /** {@inheritDoc} */
    @Override
	public int prepareToEncode(final Context context) {
        // CHECKSTYLE IGNORE MagicNumberCheck FOR NEXT 1 LINES
        return 6;
    }

    /** {@inheritDoc} */
    @Override
	public void encode(final SWFEncoder coder, final Context context)
            throws IOException {
        // CHECKSTYLE IGNORE MagicNumberCheck FOR NEXT 1 LINES
        coder.writeShort((MovieTypes.REMOVE << Coder.LENGTH_FIELD_SIZE) | 4);
        coder.writeShort(identifier);
        coder.writeShort(layer);
    }
}
