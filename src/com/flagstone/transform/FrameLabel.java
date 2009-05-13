/*
 * FrameLabel.java
 * Transform
 *
 * Copyright (c) 2001-2009 Flagstone Software Ltd. All rights reserved.
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
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

/**
 * FrameLabel defines a name for the current frame in a movie or movie clip.
 *
 * <p>
 * The name can be referenced from other objects such as GotoFrame2 to simplify
 * the creation of scripts to control movies by using a predefined name rather
 * than the frame number. The label assigned to a particular frame should be
 * unique. A frame cannot be referenced within a movie before the Player has
 * loaded and displayed the frame that contains the corresponding FrameLabel
 * object.
 * </p>
 *
 * <p>
 * If a frame is defined as an anchor it may also be referenced externally when
 * specifying the movie to play using a URL - similar to the way names links are
 * used in HTML. When the Flash Player loads a movie it will begin playing at
 * the frame specified in the URL.
 * </p>
 *
 * @see GetUrl
 * @see GetUrl2
 */
public final class FrameLabel implements MovieTag {

    private static final String FORMAT = "FrameLabel: { label=%s; anchor=%s}";

    private String label;
    private boolean anchor;

    private transient int length;

    /**
     * Creates and initialises a FrameLabel object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public FrameLabel(final SWFDecoder coder) throws CoderException {

        length = coder.readWord(2, false) & 0x3F;

        if (length == 0x3F) {
            length = coder.readWord(4, false);
        }
        final int end = coder.getPointer() + (length << 3);

        label = coder.readString();

        if (coder.getPointer() < end) {
            anchor = coder.readByte() != 0;
        }
    }

    /**
     * Creates a FrameLabel object with the specified name.
     *
     * @param aString
     *            the string that defines the label that will be assigned to the
     *            current frame. Must not be null or an empty string.
     */
    public FrameLabel(final String aString) {
        setLabel(aString);
    }

    /**
     * Creates a FrameLabel object with the specified name. If the isAnchor flag
     * is true then the frame can be directly addressed by a URL and the Flash
     * Player will begin playing the movie at the specified frame.
     *
     * @param aString
     *            the string that defines the label that will be assigned to the
     *            current frame. Must not be null or an empty string.
     * @param isAnchor
     *            if true the name will be used as an anchor when referencing
     *            the frame in a URL.
     */
    public FrameLabel(final String aString, final boolean isAnchor) {
        setLabel(aString);
        anchor = isAnchor;
    }

    /**
     * Creates a FrameLabel object with a copy of the label and anchor from
     * another FrameLabel object.
     *
     * @param object
     *            a FrameLabel object to copy.
     */
    public FrameLabel(final FrameLabel object) {
        label = object.label;
        anchor = object.anchor;
    }

    /**
     * Returns the label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label.
     *
     * @param label
     *            the string that defines the label that will be assigned to the
     *            current frame. Must not be null or an empty string.
     */
    public void setLabel(final String label) {
        if ((label == null) || (label.length() == 0)) {
            throw new IllegalArgumentException(Strings.STRING_NOT_SET);
        }
        this.label = label;
    }

    /**
     * Returns the flag indicating whether the frame name is also used as an
     * anchor so the frame can be referenced from outside of the movie.
     */
    public boolean isAnchor() {
        return anchor;
    }

    /**
     * Sets the flag indicating whether the frame name is also used as an anchor
     * so the frame can be referenced from outside of the movie.
     *
     * @param anchored
     *            true if the frame is an anchor frame, false otherwise.
     */
    public void setAnchor(final boolean anchored) {
        anchor = anchored;
    }

    /** {@inheritDoc} */
    public FrameLabel copy() {
        return new FrameLabel(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, label, String.valueOf(anchor));
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {

        length = coder.strlen(label);
        length += anchor ? 1 : 0;

        return (length > 62 ? 6 : 2) + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {

        if (length > 62) {
            coder.writeWord((MovieTypes.FRAME_LABEL << 6) | 0x3F, 2);
            coder.writeWord(length, 4);
        } else {
            coder.writeWord((MovieTypes.FRAME_LABEL << 6) | length, 2);
        }

        coder.writeString(label);

        if (anchor) {
            coder.writeWord(1, 1);
        }
    }
}
