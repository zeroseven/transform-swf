/*
 * GotoFrame.java
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

package com.flagstone.transform.action;

import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.Action;
import com.flagstone.transform.coder.ActionTypes;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

/**
 * The GotoFrame action instructs the player to move to the specified frame
 * number in the current movie's main time-line.
 *
 * <p>
 * GotoFrame is only used to control the main time-line of a movie. Controlling
 * how an individual movie clip is played is handled by a different mechanism.
 * From Flash 5 onward movie clips are defined as objects and the ExecuteMethod
 * action is used to execute the gotoAndPlay() or gotoAndStop() which start and
 * stop playing a movie clip.
 * </p>
 *
 * @see GotoFrame2
 */
public final class GotoFrame implements Action {
    
    private static final String FORMAT = "GotoFrame: { frameNumber=%d }";

    private int frameNumber;

    /**
     * Creates and initialises an GotoFrame action using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public GotoFrame(final SWFDecoder coder) throws CoderException {
        coder.readByte();
        coder.readWord(2, false);
        frameNumber = coder.readWord(2, false);
    }

    /**
     * Creates a GotoFrame with the specified frame number.
     *
     * @param aNumber
     *            the number of the frame. Must be in the range 1..65535.
     */
    public GotoFrame(final int aNumber) {
        setFrameNumber(aNumber);
    }

    /**
     * Creates and initialises a GotoFrame action using the values
     * copied from another GotoFrame action.
     *
     * @param object
     *            a GotoFrame action from which the values will be
     *            copied.
     */
    public GotoFrame(final GotoFrame object) {
        frameNumber = object.frameNumber;
    }

    /**
     * Returns the number of the frame to move the main time-line to.
     */
    public int getFrameNumber() {
        return frameNumber;
    }

    /**
     * Sets the number of the frame to move the main time-line to.
     *
     * @param aNumber
     *            the frame number. Must be in the range 1..65535.
     */
    public void setFrameNumber(final int aNumber) {
        if ((aNumber < 1) || (aNumber > 65535)) {
            throw new IllegalArgumentException(Strings.UNSIGNED_RANGE);
        }
        frameNumber = aNumber;
    }

    /** {@inheritDoc} */
    public GotoFrame copy() {
        return new GotoFrame(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, frameNumber);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        return 5;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        coder.writeByte(ActionTypes.GOTO_FRAME);
        coder.writeWord(2, 2);
        coder.writeWord(frameNumber, 2);
    }
}
