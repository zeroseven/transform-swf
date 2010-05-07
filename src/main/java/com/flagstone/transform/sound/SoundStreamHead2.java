/*
 * SoundStreamHead2.java
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

package com.flagstone.transform.sound;


import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.exception.IllegalArgumentRangeException;
import com.flagstone.transform.exception.IllegalArgumentValueException;

/**
 * SoundStreamHead2 defines the format of a streaming sound, identifying the
 * encoding scheme, the rate at which the sound will be played and the size of
 * the decoded samples.
 *
 * <p>
 * Sounds may be either mono or stereo and encoded using either NATIVE_PCM,
 * ADPCM, MP3 or NELLYMOSER formats and have sampling rates of 5512, 11025,
 * 22050 or 44100 Hertz.
 * </p>
 *
 * <p>
 * The actual sound is streamed used the SoundStreamBlock class which contains
 * the data for each frame in a movie.
 * </p>
 *
 * <p>
 * When a stream sound is played if the Flash Player cannot render the frames
 * fast enough to maintain synchronisation with the sound being played then
 * frames will be skipped. Normally the player will reduce the frame rate so
 * every frame of a movie is played. The different sets of attributes that
 * identify how the sound will be played compared to the way it was encoded
 * allows the Player more control over how the animation is rendered. Reducing
 * the resolution or playback rate can improve synchronisation with the frames
 * displayed.
 * </p>
 *
 * <p>
 * SoundStreamHead2 allows way the sound is played to differ from the way it is
 * encoded and streamed to the player. This allows the Player more control over
 * how the animation is rendered. Reducing the resolution or playback rate can
 * improve synchronisation with the frames displayed.
 * </p>
 *
 * @see SoundStreamBlock
 * @see SoundStreamHead
 */
//TODO(class)
public final class SoundStreamHead2 implements MovieTag {
    private static final String FORMAT = "SoundStreamHead: { format=%s; "
            + "playbackRate=%d; playbackChannels=%d; playbackSampleSize=%d; "
            + "streamRate=%d; streamChannels=%d; streamSampleSize=%d; "
            + "streamSampleCount=%d; latency=%d}";

    private int format;
    private int playRate;
    private int playChannels;
    private int playSampleSize;
    private int streamRate;
    private int streamChannels;
    private int streamSampleSize;
    private int streamSampleCount;
    private int latency;

    private transient int length;

    /*
     * The following variable is used to preserve the value of a reserved field
     * when decoding then encoding an existing Flash file. Macromedia's file
     * file format specification states that this field is always zero - it is
     * not, so this is used to preserve the value in case it is implementing an
     * undocumented feature.
     */
    private transient int reserved = 0;

    /**
     * Creates and initialises a SoundStreamHead2 object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public SoundStreamHead2(final SWFDecoder coder) throws CoderException {
        final int start = coder.getPointer();
        length = coder.readWord(2, false) & 0x3F;

        if (length == 0x3F) {
            length = coder.readWord(4, false);
        }
        final int end = coder.getPointer() + (length << 3);

        reserved = coder.readBits(4, false);
        switch (coder.readBits(2, false)) {
        case 0:
            playRate = 5512;
            break;
        case 1:
            playRate = 11025;
            break;
        case 2:
            playRate = 22050;
            break;
        case 3:
            playRate = 44100;
            break;
        default:
            playRate = 0;
            break;
        }
        playSampleSize = coder.readBits(1, false) + 1;
        playChannels = coder.readBits(1, false) + 1;

        format = coder.readBits(4, false);

        switch (coder.readBits(2, false)) {
        case 0:
            streamRate = 5512;
            break;
        case 1:
            streamRate = 11025;
            break;
        case 2:
            streamRate = 22050;
            break;
        case 3:
            streamRate = 44100;
            break;
        default:
            streamRate = 0;
            break;
        }
        streamSampleSize = coder.readBits(1, false) + 1;
        streamChannels = coder.readBits(1, false) + 1;
        streamSampleCount = coder.readWord(2, false);

        if ((length == 6) && (format == 2)) {
            latency = coder.readWord(2, true);
        }

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }

    /**
     * Creates a SoundStreamHead2 object specifying all the parameters required
     * to define the sound.
     *
     * @param encoding
     *            the compression format for the sound data, either
     *            DefineSound.NATIVE_PCM, DefineSound.ADPCM, DefineSound.MP3,
     *            DefineSound.PCM or DefineSound.NELLYMOSER (Flash 6+ only).
     * @param playRate
     *            the recommended rate for playing the sound, either 5512,
     *            11025, 22050 or 44100 Hz.
     * @param playChannels
     *            The recommended number of playback channels: 1 = mono or 2 =
     *            stereo.
     * @param playSize
     *            the recommended uncompressed sample size for playing the
     *            sound, either 1 or 2 bytes.
     * @param streamingRate
     *            the rate at which the sound was sampled, either 5512, 11025,
     *            22050 or 44100 Hz.
     * @param streamingChannels
     *            the number of channels: 1 = mono or 2 = stereo.
     * @param streamingSize
     *            the sample size for the sound, either 1 or 2 bytes.
     * @param streamingCount
     *            the number of samples in each subsequent SoundStreamBlock
     *            object.
     */
    public SoundStreamHead2(final SoundFormat encoding, final int playRate,
            final int playChannels, final int playSize,
            final int streamingRate, final int streamingChannels,
            final int streamingSize, final int streamingCount) {
        setFormat(encoding);
        setPlayRate(playRate);
        setPlayChannels(playChannels);
        setPlaySampleSize(playSize);
        setStreamRate(streamingRate);
        setStreamChannels(streamingChannels);
        setStreamSampleSize(streamingSize);
        setStreamSampleCount(streamingCount);
    }

    /**
     * Creates and initialises a SoundStreamHead2 object using the values copied
     * from another SoundStreamHead2 object.
     *
     * @param object
     *            a SoundStreamHead2 object from which the values will be
     *            copied.
     */
    public SoundStreamHead2(final SoundStreamHead2 object) {
        format = object.format;
        playRate = object.playRate;
        playChannels = object.playChannels;
        playSampleSize = object.playSampleSize;
        streamRate = object.streamRate;
        streamChannels = object.streamChannels;
        streamSampleSize = object.streamSampleSize;
        streamSampleCount = object.streamSampleCount;
        latency = object.latency;
    }

    /**
     * Returns the streaming sound format.
     */
    public SoundFormat getFormat() {
        SoundFormat value;
        
        switch (format) {
        case 0: 
            value = SoundFormat.NATIVE_PCM;
            break;
        case 1: 
            value = SoundFormat.ADPCM;
            break;
        case 2: 
            value = SoundFormat.MP3;
            break;
        case 3: 
            value = SoundFormat.PCM;
            break;
        case 5: 
            value = SoundFormat.NELLYMOSER_8K;
            break;
        case 6: 
            value = SoundFormat.NELLYMOSER;
            break;
        case 11: 
            value = SoundFormat.SPEEX;
            break;
        default:
            throw new IllegalStateException("Unsupported sound format.");
        }
        return value;
    }

    /**
     * Sets the format for the streaming sound.
     *
     * @param encoding
     *            the compression format for the sound data, must be either
     *            NATIVE_PCM, ADPCM, MP3, PCM or NELLYMOSER from SoundFormat.
     */
    public void setFormat(final SoundFormat encoding) {
        switch (encoding) {
        case NATIVE_PCM: 
            format = 0;
            break;
        case ADPCM: 
            format = 1;
            break;
        case MP3: 
            format = 2;
            break;
        case PCM: 
            format = 3;
            break;
        case NELLYMOSER: 
            format = 6;
            break;
        case SPEEX: 
            format = 11;
            break;
        default:
            throw new IllegalArgumentException("Unsupported sound format.");
        }
    }

    /**
     * Returns the recommended playback rate: 5512, 11025, 22050 or 44100 Hertz.
     */
    public int getPlayRate() {
        return playRate;
    }

    /**
     * Returns the recommended number of playback channels = 1 = mono 2 =
     * stereo.
     */
    public int getPlayChannels() {
        return playChannels;
    }

    /**
     * Returns the recommended playback sample range in bytes: 1 or 2.
     */
    public int getPlaySampleSize() {
        return playSampleSize;
    }

    /**
     * Returns the sample rate: 5512, 11025, 22050 or 44100 Hz in the streaming
     * sound.
     */
    public float getStreamRate() {
        return streamRate;
    }

    /**
     * Returns the number of channels, 1 = mono 2 = stereo, in the streaming
     * sound.
     */
    public int getStreamChannels() {
        return streamChannels;
    }

    /**
     * Returns the sample size in bytes: 1 or 2 in the streaming sound.
     */
    public int getStreamSampleSize() {
        return streamSampleSize;
    }

    /**
     * Returns the average number of samples in each stream block following.
     */
    public int getStreamSampleCount() {
        return streamSampleCount;
    }

    /**
     * Sets the recommended playback rate in Hz. Must be either: 5512, 11025,
     * 22050 or 44100.
     *
     * @param rate
     *            the recommended rate for playing the sound.
     */
    public void setPlayRate(final int rate) {
        if ((rate != 5512) && (rate != 11025) && (rate != 22050)
                && (rate != 44100)) {
            throw new IllegalArgumentValueException(new int[] {5512, 11025, 22050, 44100}, rate);
        }
        playRate = rate;
    }

    /**
     * Sets the recommended number of playback channels = 1 = mono 2 = stereo.
     *
     * @param channels
     *            the recommended number of playback channels.
     */
    public void setPlayChannels(final int channels) {
        if ((channels < 1) || (channels > 2)) {
            throw new IllegalArgumentRangeException(1, 2, channels);
        }
        playChannels = channels;
    }

    /**
     * Sets the recommended playback sample size in bytes. Must be wither 1 or
     * 2.
     *
     * @param playSize
     *            the recommended sample size for playing the sound.
     */
    public void setPlaySampleSize(final int playSize) {
        if ((playSize < 1) || (playSize > 2)) {
            throw new IllegalArgumentRangeException(1, 2, playSize);
        }
        playSampleSize = playSize;
    }

    /**
     * Sets the sample rate in Hz for the streaming sound. Must be either: 5512,
     * 11025, 22050 or 44100.
     *
     * @param rate
     *            the rate at which the streaming sound was sampled.
     */
    public void setStreamRate(final int rate) {
        if ((rate != 5512) && (rate != 11025) && (rate != 22050)
                && (rate != 44100)) {
            throw new IllegalArgumentValueException(new int[] {5512, 11025, 22050, 44100}, rate);
        }
        streamRate = rate;
    }

    /**
     * Sets the number of channels in the streaming sound: 1 = mono 2 = stereo.
     *
     * @param channels
     *            the number of channels in the streaming sound.
     */
    public void setStreamChannels(final int channels) {
        if ((channels < 1) || (channels > 2)) {
            throw new IllegalArgumentRangeException(1, 2, channels);
        }
        streamChannels = channels;
    }

    /**
     * Sets the sample size in bytes for the streaming sound. Must be 1 or 2.
     *
     * @param size
     *            the sample size for the sound.
     */
    public void setStreamSampleSize(final int size) {
        if ((size < 1) || (size > 2)) {
            throw new IllegalArgumentRangeException(1, 2, size);
        }
        streamSampleSize = size;
    }

    /**
     * Sets the number of samples in each stream block.
     *
     * @param count
     *            the number of samples in each subsequent SoundStreamBlock
     *            object.
     */
    public void setStreamSampleCount(final int count) {
        if (count < 0) {
            throw new IllegalArgumentRangeException(0, Integer.MAX_VALUE, count);
        }
        streamSampleCount = count;
    }

    /**
     * For MP3 encoded sounds, returns the number of samples to skip when
     * starting to play a sound.
     *
     * @return the number of samples skipped in an MP3 encoded sound Returns 0
     *         for other sound formats.
     */
    public int getLatency() {
        return latency;
    }

    /**
     * Set the number of samples to skip when starting to play an MP3 encoded
     * sound.
     *
     * @param latency
     *            the number of samples to be skipped in an MP3 encoded sound
     *            should be 0 for other sound formats.
     */
    public void setLatency(final int latency) {
        this.latency = latency;
    }

    /** TODO(method). */
    public SoundStreamHead2 copy() {
        return new SoundStreamHead2(this);
    }

    @Override
    public String toString() {
        return String.format(FORMAT, format, playRate, playChannels,
                playSampleSize, streamRate, streamChannels, streamSampleSize,
                streamSampleCount, latency);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        length = 4;

        if ((format == 2) && (latency > 0)) {
            length += 2;
        }
        return (length > 62 ? 6 : 2) + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        final int start = coder.getPointer();

        if (length >= 63) {
            coder.writeWord((MovieTypes.SOUND_STREAM_HEAD_2 << 6) | 0x3F, 2);
            coder.writeWord(length, 4);
        } else {
            coder.writeWord((MovieTypes.SOUND_STREAM_HEAD_2 << 6) | length, 2);
        }
        final int end = coder.getPointer() + (length << 3);

        coder.writeBits(reserved, 4);

        switch (playRate) {
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
        coder.writeBits(playSampleSize - 1, 1);
        coder.writeBits(playChannels - 1, 1);

        coder.writeBits(format, 4);

        switch (streamRate) {
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
        coder.writeBits(streamSampleSize - 1, 1);
        coder.writeBits(streamChannels - 1, 1);
        coder.writeWord(streamSampleCount, 2);

        if ((format == 2) && (latency > 0)) {
            coder.writeWord(latency, 2);
        }

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }
}
