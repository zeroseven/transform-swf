/*
 * SoundStreamBlock.java
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

package com.flagstone.transform.sound;

import java.util.Arrays;


import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

/**
 * SoundStreamBlock contains the sound data being streamed to the Flash Player.
 *
 * <p>
 * Streaming sounds are played in tight synchronisation with one
 * SoundStreamBlock object defining the sound for each frame displayed in a
 * movie. When a streaming sound is played if the Flash Player cannot render the
 * frames fast enough to maintain synchronisation with the sound being played
 * then frames will be skipped. Normally the player will reduce the frame rate
 * so every frame of a movie is played.
 * </p>
 *
 * @see SoundStreamHead
 * @see SoundStreamHead2
 */
public final class SoundStreamBlock implements MovieTag {
    
    private static final String FORMAT = "SoundStreamBlock: { sound=%d }";

    private byte[] sound;

    private transient int length;

    /**
     * Creates and initialises a SoundStreamBlock object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public SoundStreamBlock(final SWFDecoder coder) throws CoderException {
        final int start = coder.getPointer();
        length = coder.readWord(2, false) & 0x3F;

        if (length == 0x3F) {
            length = coder.readWord(4, false);
        }
        final int end = coder.getPointer() + (length << 3);

        sound = coder.readBytes(new byte[length]);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }

    /**
     * Creates a SoundStreamBlock specifying the sound data in the format
     * defined by a preceding SoundStreamHead or SoundStreamHead2 object.
     *
     * @param bytes
     *            an array of bytes containing the sound data. Must not be null.
     */
    public SoundStreamBlock(final byte[] bytes) {
        setSound(bytes);
    }

    /**
     * Creates and initialises a SoundStreamBlock object using the values copied
     * from another SoundStreamBlock object.
     *
     * @param object
     *            a SoundStreamBlock object from which the values will be
     *            copied.
     */
    public SoundStreamBlock(final SoundStreamBlock object) {
        sound = object.sound;
    }

    /**
     * Returns a copy of the sound data in the format defined by a preceding
     * SoundStreamHead or SoundStreamHead2 object.
     */
    public byte[] getSound() {
        return Arrays.copyOf(sound, sound.length);
    }

    /**
     * Sets the sound data.
     *
     * @param bytes
     *            an array of bytes containing the sound data. Must not be null.
     */
    public void setSound(final byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException();
        }
        sound = Arrays.copyOf(bytes, bytes.length);
    }

    /** {@inheritDoc} */
    public SoundStreamBlock copy() {
        return new SoundStreamBlock(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, sound.length);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        length = sound.length;
        return (length > 62 ? 6 : 2) + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        final int start = coder.getPointer();

        if (length >= 63) {
            coder.writeWord((MovieTypes.SOUND_STREAM_BLOCK << 6) | 0x3F, 2);
            coder.writeWord(length, 4);
        } else {
            coder.writeWord((MovieTypes.SOUND_STREAM_BLOCK << 6) | length, 2);
        }
        final int end = coder.getPointer() + (length << 3);

        coder.writeBytes(sound);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }
}
