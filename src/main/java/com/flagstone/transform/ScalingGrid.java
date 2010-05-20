/*
 * ScalingGrid.java
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

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.DefineTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.exception.IllegalArgumentRangeException;

/** TODO(class). */
public final class ScalingGrid implements DefineTag {

    /** Format string used in toString() method. */
    private static final String FORMAT = "ScalingGrid: { identifier=%d;"
            + " bounds=%s; }";

    /** the unique identifier of the object. */
    private int identifier;
    /** The box that defines the centre of the grid. */
    private Bounds bounds;

    /** The length of the object, minus the header, when it is encoded. */
    private transient int length;

    /**
     * Creates and initialises a ScalingGrid object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public ScalingGrid(final SWFDecoder coder) throws CoderException {
        coder.readHeader();
        identifier = coder.readUI16();
        bounds = new Bounds(coder);
    }

    /**
     * Creates and initialises a ScalingGrid with the specified object
     * identifier and bounding box for the centre section.
     *
     * @param uid the unique identifier of the object to which the grid will be
     * applied
     * @param aBounds the bounding box that defines the coordinates of the
     * centre section of the grid.
     */
    public ScalingGrid(final int uid, final Bounds aBounds) {
        setIdentifier(uid);
        setBounds(aBounds);
    }

    /**
     * Creates and initialises a ScalingGrid object using the values copied
     * from another ScalingGrid object.
     *
     * @param object
     *            a ScalingGrid object from which the values will be
     *            copied.
     */
    public ScalingGrid(final ScalingGrid object) {
        identifier = object.identifier;
        bounds = object.bounds;
    }

    /**
     * Get the identifier of the object which the scaling grid will be applied
     * to.
     *
     * @return the object identifier.
     */
    public int getIdentifier() {
        return identifier;
    }

    /**
     * Set the identifier of the object which the scaling grid will be applied
     * to.
     *
     * @param uid the unique identifier of the object.
     */
    public void setIdentifier(final int uid) {
        if ((uid < SWF.MIN_IDENTIFIER) || (uid > SWF.MAX_IDENTIFIER)) {
            throw new IllegalArgumentRangeException(
                    SWF.MIN_IDENTIFIER, SWF.MAX_IDENTIFIER, uid);
        }
        identifier = uid;
    }

    /**
     * Get the bounding box that defined the centre section of the scaling grid.
     * @return the box defining the centre of the grid.
     */
    public Bounds getBounds() {
        return bounds;
    }

    /**
     * Set the bounding box that defined the centre section of the scaling grid.
     * @param aBounds the box defining the centre of the grid.
     */
    public void setBounds(final Bounds aBounds) {
        if (aBounds == null) {
            throw new IllegalArgumentException();
        }
        bounds = aBounds;
    }

    /** {@inheritDoc} */
    public ScalingGrid copy() {
        return new ScalingGrid(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, identifier, bounds.toString());
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final Context context) {
        length = 2 + bounds.prepareToEncode(context);
        return 2 + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {

        coder.writeHeader(MovieTypes.DEFINE_SCALING_GRID, length);
        coder.writeI16(identifier);
        bounds.encode(coder, context);
    }
}
