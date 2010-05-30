/*
 * StartSound2.java
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

package com.flagstone.transform.sound;


import java.io.IOException;

import com.flagstone.transform.MovieTag;
import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

//TODO(doc)
/**
 * StartSound2 instructs the player to start or stop playing a sound defined
 * using the DefineSound class.
 *
 * <p>
 * StartSound contains a SoundInfo object that defines how the sound fades in
 * and out, whether it is repeated as well as specifying an envelope that
 * provides a finer degree of control over the levels at which the sound is
 * played.
 * </p>
 *
 * @see DefineSound
 * @see SoundInfo
 */
//TODO(class)
public final class StartSound2 implements MovieTag {

    /** Format string used in toString() method. */
    private static final String FORMAT = "StartSound2: { sound=%s }";

    private String soundClass;
    private SoundInfo sound;

    /** The length of the object, minus the header, when it is encoded. */
    private transient int length;

    /**
     * Creates and initialises a StartSound2 object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws IOException
     *             if an error occurs while decoding the data.
     */
    public StartSound2(final SWFDecoder coder) throws IOException {
        length = coder.readUnsignedShort() & Coder.LENGTH_FIELD;
        if (length == Coder.IS_EXTENDED) {
            length = coder.readInt();
        }
        coder.mark();
        soundClass = coder.readString();
        sound = new SoundInfo(coder.readUnsignedShort(), coder);
        coder.unmark(length);
    }

    /**
     * Creates a StartSound object with an Sound object that identifies the
     * sound and controls how it is played.
     *
     * @param aSound
     *            the Sound object. Must not be null.
     */
    public StartSound2(final SoundInfo aSound) {
        setSound(aSound);
    }

    /**
     * Creates and initialises a StartSound2 object using the values copied
     * from another StartSound2 object.
     *
     * @param object
     *            a StartSound2 object from which the values will be
     *            copied.
     */
    public StartSound2(final StartSound2 object) {
        soundClass = object.soundClass;
        sound = object.sound.copy();
    }


    public String getSoundClass() {
        return soundClass;
    }

    /**
     * Get the Sound object describing how the sound will be played.
     *
     * @return the SoundInfo object that controls the playback.
     */
    public SoundInfo getSound() {
        return sound;
    }


    public void setSoundClass(final String className) {
        soundClass = className;
    }

    /**
     * Sets the Sound object that describes how the sound will be played.
     *
     * @param aSound
     *            the Sound object that controls how the sound is played. Must
     *            not be null.
     */
    public void setSound(final SoundInfo aSound) {
        if (aSound == null) {
            throw new IllegalArgumentException();
        }
        sound = aSound;
    }

    /** {@inheritDoc} */
    public StartSound2 copy() {
        return new StartSound2(this);
    }

    @Override
    public String toString() {
        return String.format(FORMAT, sound);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final Context context) {
        length = soundClass.length() + sound.prepareToEncode(context);
        return (length > SWFEncoder.STD_LIMIT ? SWFEncoder.EXT_LENGTH
                : SWFEncoder.STD_LENGTH) + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws IOException {

        coder.writeHeader(MovieTypes.START_SOUND_2, length);
        coder.mark();
        coder.writeString(soundClass);
        sound.encode(coder, context);
        coder.unmark(length);
    }
}
