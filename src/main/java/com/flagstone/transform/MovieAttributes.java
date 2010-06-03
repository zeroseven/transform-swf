/*
 * MovieAttributes.java
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

package com.flagstone.transform;

import java.io.IOException;

import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.exception.IllegalArgumentRangeException;

/** TODO(class). */
public final class MovieAttributes implements MovieTag {

    /** Format string used in toString() method. */
    private static final String FORMAT = "MovieAttributes: { version=%d; "
    		+ " frameSize=%s; frameRate=%f; frameCount=%d; "
    		+ " compressed=%b;  metadata=%b;  as3=%b;  network=%b;}";
    /** The set of encoded attributes. */
    private int attributes;

    /** The Flash version number. */
    private int version;
    /** The Flash Player screen coordinates. */
    private Bounds frameSize;
    /** The frame rate of the movie. */
    private int frameRate;
    /** The number of frames in the movie. */
    private int frameCount;
    private boolean compressed;
    private boolean metadata;
    private boolean actionscript3;
    private boolean network;

    public MovieAttributes() {
        version = SWF.VERSION;
    }

    /**
     * Creates and initialises a MovieAttributes object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws IOException
     *             if an error occurs while decoding the data.
     */
    public MovieAttributes(final SWFDecoder coder, Context context) throws IOException {
        version = context.get(Context.VERSION);
        frameSize = new Bounds(coder);
        frameRate = coder.readUnsignedShort();
        frameCount = coder.readUnsignedShort();
        if (version > 7) {
            coder.readUnsignedShort();
            int flags = coder.readByte();
            network = (flags & 1) != 0;
            actionscript3 = (flags & 8) != 0;
            metadata = (flags & 16) != 0;
            coder.skip(3);
        }
    }

    /**
     * Creates and initialises a MovieAttributes object using the values copied
     * from another MovieAttributes object.
     *
     * @param object
     *            a MovieAttributes object from which the values will be
     *            copied.
     */
    public MovieAttributes(final MovieAttributes object) {
        attributes = object.attributes;
    }

    /**
     * Get the number representing the version of Flash that the movie
     * represents.
     *
     * @return the version number of Flash that this movie contains.
     */
    public int getVersion() {
        return version;
    }

    /**
     * Sets the Flash version supported in this Movie. Note that there are no
     * restrictions on the objects that can be used in a coder. Using objects
     * that are not supported by an earlier version of the Flash file format may
     * cause the Player to not display the movie correctly or even crash the
     * Player.
     *
     * @param aNumber
     *            the version of the Flash file format that this movie utilises.
     */
    public void setVersion(final int aNumber) {
        if (aNumber < 0) {
            throw new IllegalArgumentRangeException(
                    0, Integer.MAX_VALUE, aNumber);
        }
        version = aNumber;
    }

    /**
     * Get the bounding rectangle that defines the size of the player
     * screen.
     *
     * @return the bounding box that defines the screen.
     */
    public Bounds getFrameSize() {
        return frameSize;
    }

    /**
     * Sets the bounding rectangle that defines the size of the player screen.
     * The coordinates of the bounding rectangle are also used to define the
     * coordinate range. For example if a 400 x 400 pixel rectangle is defined,
     * specifying the values for the x and y coordinates the range -200 to 200
     * sets the centre of the screen at (0,0), if the x and y coordinates are
     * specified in the range 0 to 400 then the centre of the screen will be at
     * (200, 200).
     *
     * @param aBounds
     *            the Bounds object that defines the frame size. Must not be
     *            null.
     */
    public void setFrameSize(final Bounds aBounds) {
        if (aBounds == null) {
            throw new IllegalArgumentException();
        }
        frameSize = aBounds;
    }

    /**
     * Get the number of frames played per second that the movie will be
     * displayed at.
     *
     * @return the movie frame rate.
     */
    public float getFrameRate() {
        return frameRate / 256.0f;
    }

    /**
     * Sets the number of frames played per second that the Player will display
     * the coder.
     *
     * @param rate
     *            the number of frames per second that the movie is played.
     */
    public void setFrameRate(final float rate) {
        frameRate = (int) (rate * 256);
    }

    public float getFrameCount() {
        return frameCount;
    }

    public void setFrameCount(final int count) {
        frameCount = count;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(final boolean compress) {
        compressed = compress;
    }

    public boolean hasMetaData() {
        return metadata;
    }

    public void setMetaData(final boolean hasMeta) {
        metadata = hasMeta;
    }

    public boolean hasActionscript3() {
        return actionscript3;
    }

    public void setActionscript3(final boolean hasActionscript) {
        actionscript3 = hasActionscript;
    }

    public boolean usesNetwork() {
        return network;
    }

    public void setUsesNetwork(final boolean usesNetwork) {
        network = usesNetwork;
    }

    /** {@inheritDoc} */
    public MovieAttributes copy() {
        return new MovieAttributes(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, version, frameSize, getFrameRate(),
                frameCount, compressed, metadata, actionscript3, network);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final Context context) {
        int length = 4 + frameSize.prepareToEncode(context);
        if (version > 7) {
            length += 6;
        }
        return length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws IOException {
        frameSize.encode(coder, context);
        coder.writeShort(frameRate);
        coder.writeShort(frameCount);

        if (version > 7) {
            coder.writeShort((MovieTypes.FILE_ATTRIBUTES
                    << Coder.LENGTH_FIELD_SIZE) | 4);
            int flags = 0;
            flags |= network ? 1 : 0;
            flags |= actionscript3 ? 8 : 0;
            flags |= metadata ? 16 : 0;
            coder.writeByte(flags);
            coder.writeByte(0);
            coder.writeByte(0);
            coder.writeByte(0);
        }
    }
}
