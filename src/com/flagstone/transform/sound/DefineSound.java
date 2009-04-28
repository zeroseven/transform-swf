/*
 * DefineSound.java
 * Transform
 * 
 * Copyright (c) 2001-2009 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its contributors 
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.flagstone.transform.sound;

import java.util.Arrays;

import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.DefineTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.datatype.SoundFormat;

//TODO(doc) Review
/**
 * DefineSound is used to define a sound that will be played when a given event
 * occurs.
 * 
 * <p>
 * Three different types of object are used to play an event sound:
 * </p>
 * 
 * <ul>
 * <li>The DefineSound object that contains the sampled sound.</li>
 * <li>A Sound object that defines how the sound fades in and out, whether it
 * repeats and also defines an envelope for more sophisticated control over how
 * the sound is played.</li>
 * <li>A StartSound object that signals the Flash Player to begin playing the
 * sound.</li>
 * </ul>
 * 
 * <p>
 * Five encoded formats for the sound data are supported: NATIVE_PCM, PCM,
 * ADPCM, MP3 and NELLYMOSER.
 * </p>
 * 
 * @see SoundInfo
 * @see StartSound
 */
public final class DefineSound implements DefineTag {

    private static final String FORMAT = "DefineSound: { identifier=%d; format=%s; rate=%d; channelCount=%d; sampleSize=%d sampleCount=%d }";

    private SoundFormat format;
    private int rate;
    private int channelCount;
    private int sampleSize;
    private int sampleCount;
    private byte[] data;
    private int identifier;

    private transient int length;

    // TODO(doc)
    public DefineSound(final SWFDecoder coder) throws CoderException {

        final int start = coder.getPointer();
        length = coder.readWord(2, false) & 0x3F;

        if (length == 0x3F) {
            length = coder.readWord(4, false);
        }
        final int end = coder.getPointer() + (length << 3);

        identifier = coder.readWord(2, false);
        format = SoundFormat.fromInt(coder.readBits(4, false));

        switch (coder.readBits(2, false)) {
        case 0:
            rate = 5512;
            break;
        case 1:
            rate = 11025;
            break;
        case 2:
            rate = 22050;
            break;
        case 3:
            rate = 44100;
            break;
        default:
            rate = 0;
            break;
        }

        sampleSize = coder.readBits(1, false) + 1;
        channelCount = coder.readBits(1, false) + 1;
        sampleCount = coder.readWord(4, false);

        data = coder.readBytes(new byte[length - 7]);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }

    /**
     * Creates a DefineSound object specifying the unique identifier and all the
     * parameters required to describe the sound.
     * 
     * @param uid
     *            the unique identifier for this sound. Must be in the range
     *            1..65535.
     * @param aFormat
     *            the encoding format for the sound. For Flash 1 the formats may
     *            be one of the format: NATIVE_PCM, PCM or ADPCM. For Flash 4 or
     *            later include MP3 and Flash 6 or later include NELLYMOSER.
     * @param rate
     *            the number of samples per second that the sound is played at ,
     *            either 5512, 11025, 22050 or 44100.
     * @param channels
     *            the number of channels in the sound, must be either 1 (Mono)
     *            or 2 (Stereo).
     * @param sampleSize
     *            the size of an uncompressed sound sample in bits, must be
     *            either 8 or 16.
     * @param count
     *            the number of samples in the sound data.
     * @param bytes
     *            the sound data.
     */
    public DefineSound(final int uid, final SoundFormat aFormat,
            final int rate, final int channels, final int sampleSize,
            final int count, final byte[] bytes) {
        setIdentifier(uid);
        setFormat(aFormat);
        setRate(rate);
        setChannelCount(channels);
        setSampleSize(sampleSize);
        setSampleCount(count);
        setData(bytes);
    }

    // TODO(doc)
    public DefineSound(final DefineSound object) {
        format = object.format;
        rate = object.rate;
        channelCount = object.channelCount;
        sampleSize = object.sampleSize;
        sampleCount = object.sampleCount;
        data = Arrays.copyOf(object.data, object.data.length);
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final int uid) {
        if ((uid < 0) || (uid > 65535)) {
            throw new IllegalArgumentException(Strings.IDENTIFIER_RANGE);
        }
        identifier = uid;
    }

    /**
     * Returns the compression format used, either NATIVE_PCM, PCM or ADPCM (all
     * Flash 1), MP3 (Flash 4+) or NELLYMOSER (Flash 6+).
     */
    public SoundFormat getFormat() {
        return format;
    }

    /**
     * Returns the rate at which the sound will be played, in Hz: 5512, 11025,
     * 22050 or 44100.
     */
    public int getRate() {
        return rate;
    }

    /**
     * Returns the number of sound channels, 1 (Mono) or 2 (Stereo).
     */
    public int getChannelCount() {
        return channelCount;
    }

    /**
     * Returns the size of an uncompressed sample in bytes.
     */
    public int getSampleSize() {
        return sampleSize;
    }

    /**
     * Returns the number of samples in the sound data.
     */
    public int getSampleCount() {
        return sampleCount;
    }

    /**
     * Returns the sound data.
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Sets the compression format used.Must be either Constants.NATIVE_PCM,
     * Constants.ADPCM or Constants.PCM from Flash 1 onwards, Constants.MP3 from
     * Flash 4 onwards, or Constants.NELLYMOSER from Flash 6 onwards.
     * 
     * @param encoding
     *            the format for the sound.
     */
    public void setFormat(final SoundFormat encoding) {
        format = encoding;
    }

    /**
     * Sets the sampling rate in Hertz.
     * 
     * @param rate
     *            the rate at which the sounds is played in Hz. Must be one of:
     *            5512, 11025, 22050 or 44100.
     */
    public void setRate(final int rate) {
        if ((rate != 5512) && (rate != 11025) && (rate != 22050)
                && (rate != 44100)) {
            throw new IllegalArgumentException(Strings.SOUND_RATE_RANGE);
        }
        this.rate = rate;
    }

    /**
     * Sets the number of channels defined in the sound.
     * 
     * @param channels
     *            the number of channels in the sound, must be either 1 (Mono)
     *            or 2 (Stereo).
     */
    public void setChannelCount(final int channels) {
        if ((channels < 1) || (channels > 2)) {
            throw new IllegalArgumentException(Strings.CHANNEL_RANGE);
        }
        channelCount = channels;
    }

    /**
     * Sets the sample size in bytes.
     * 
     * @param size
     *            the size of sound samples in bytes. Must be either 1 or 2.
     */
    public void setSampleSize(final int size) {
        if ((size < 1) || (size > 2)) {
            throw new IllegalArgumentException(Strings.SAMPLE_SIZE_RANGE);
        }
        sampleSize = size;
    }

    /**
     * Sets the number of samples in the sound data.
     * 
     * @param count
     *            the number of samples for the sound.
     */
    public void setSampleCount(final int count) {
        if (count < 1) {
            throw new IllegalArgumentException(Strings.NEGATIVE_NUMBER);
        }
        sampleCount = count;
    }

    /**
     * Sets the sound data.
     * 
     * @param bytes
     *            the sound data. Must not be null.
     */
    public void setData(final byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException(Strings.DATA_IS_NULL);
        }
        data = bytes;
    }

    public DefineSound copy() {
        return new DefineSound(this);
    }

    @Override
    public String toString() {
        return String.format(FORMAT, identifier, format, rate, channelCount,
                sampleSize, sampleCount);
    }

    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        length = 7;
        length += data.length;

        return (length > 62 ? 6 : 2) + length;
    }

    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {

        final int start = coder.getPointer();

        if (length >= 63) {
            coder.writeWord((MovieTypes.DEFINE_SOUND << 6) | 0x3F, 2);
            coder.writeWord(length, 4);
        } else {
            coder.writeWord((MovieTypes.DEFINE_SOUND << 6) | length, 2);
        }
        final int end = coder.getPointer() + (length << 3);

        coder.writeWord(identifier, 2);
        coder.writeBits(format.getValue(), 4);

        switch (rate) {
        case 5512:
            coder.writeBits(0, 2);
            break;
        case 11025:
            coder.writeBits(1, 2);
            break;
        case 22050:
            coder.writeBits(2, 2);
            break;
        case 44100:
            coder.writeBits(3, 2);
            break;
        default:
            break;
        }
        coder.writeBits(sampleSize - 1, 1);
        coder.writeBits(channelCount - 1, 1);
        coder.writeWord(sampleCount, 4);

        coder.writeBytes(data);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }
}