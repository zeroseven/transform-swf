/*
 * SoundStreamHead.java
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


import java.io.IOException;

import com.flagstone.transform.MovieTag;
import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.exception.IllegalArgumentRangeException;
import com.flagstone.transform.exception.IllegalArgumentValueException;

/**
 * SoundStreamHead defines the format of a streaming sound, identifying the
 * encoding scheme, the rate at which the sound will be played and the size of
 * the decoded samples.
 *
 * <p>
 * Sounds may be either mono or stereo and encoded using either NATIVE_PCM,
 * ADPCM, MP3 or NELLYMOSER formats and have sampling rates of 5512, 11025,
 * 22050 or 44100 Hz.
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
 * @see SoundStreamHead2
 */
//TODO(class)
public final class SoundStreamHead implements MovieTag {

    /** Format string used in toString() method. */
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

    /** The length of the object, minus the header, when it is encoded. */
    private transient int length;

    /**
     * Creates and initialises a SoundStreamHead object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws IOException
     *             if an error occurs while decoding the data.
     */
    public SoundStreamHead(final SWFDecoder coder) throws IOException {
        length = coder.readUnsignedShort() & Coder.LENGTH_FIELD;
        if (length == Coder.IS_EXTENDED) {
            length = coder.readInt();
        }
        coder.mark();
        int info = coder.readByte();
        playRate = readRate((info & 0x0C) >> 2);
        playSampleSize = ((info & 0x02) >> 1) + 1;
        playChannels = (info & 0x01) + 1;

        info = coder.readByte();
        format = (info & 0x00F0) >> 4;
        streamRate = readRate((info & 0x0C) >> 2);
        streamSampleSize = ((info & 0x02) >> 1) + 1;
        streamChannels = (info & 0x01) + 1;
        streamSampleCount = coder.readUnsignedShort();

        if ((length == 6) && (format == 2)) {
            latency = coder.readSignedShort();
        }
        coder.unmark(length);
    }

    /**
     * Creates a SoundStreamHead object specifying all the parameters required
     * to define the sound.
     *
     * @param playbackRate
     *            the recommended rate for playing the sound, either 5512,
     *            11025, 22050 or 44100 Hz.
     * @param playbackChannels
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
     *            the sample size for the sound in bytes, either 1 or 2.
     * @param streamingCount
     *            the number of samples in each subsequent SoundStreamBlock
     *            object.
     */
    public SoundStreamHead(final int playbackRate, final int playbackChannels,
            final int playSize, final int streamingRate,
            final int streamingChannels, final int streamingSize,
            final int streamingCount) {
        setPlayRate(playbackRate);
        setPlayChannels(playbackChannels);
        setPlaySampleSize(playSize);

        setStreamRate(streamingRate);
        setStreamChannels(streamingChannels);
        setStreamSampleSize(streamingSize);
        setStreamSampleCount(streamingCount);
    }

    /**
     * Creates and initialises a SoundStreamHead object using the values copied
     * from another SoundStreamHead object.
     *
     * @param object
     *            a SoundStreamHead object from which the values will be
     *            copied.
     */
    public SoundStreamHead(final SoundStreamHead object) {
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
     * Get the compression format used.
     *
     * @return the format for the sound data.
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
     *            ADPCM or MP3 from SoundFormat.
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
        case NELLYMOSER_8K:
            format = 5;
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
     * Returns the recommended playback rate: 5512, 11025, 22050 or 44100 Hz.
     *
     * @return the playback rate in Hertz.
     */
    public int getPlayRate() {
        return playRate;
    }

    /**
     * Get the recommended number of playback channels = 1 = mono 2 =
     * stereo.
     *
     * @return the number of channels.
     */
    public int getPlayChannels() {
        return playChannels;
    }

    /**
     * Get the recommended playback sample range in bytes: 1 or 2.
     *
     * @return the number of bytes in each sample.
     */
    public int getPlaySampleSize() {
        return playSampleSize;
    }

    /**
     * Get the sample rate: 5512, 11025, 22050 or 44100 Hz in the streaming
     * sound.
     *
     * @return the stream rate in Hertz.
     */
    public float getStreamRate() {
        return streamRate;
    }

    /**
     * Get the number of channels, 1 = mono 2 = stereo, in the streaming
     * sound.
     *
     * @return the number of channels defined in the streaming sound.
     */
    public int getStreamChannels() {
        return streamChannels;
    }

    /**
     * Get the sample size in bytes: 1 or 2 in the streaming sound.
     *
     * @return the number of bytes per sample in the streaming sound.
     */
    public int getStreamSampleSize() {
        return streamSampleSize;
    }

    /**
     * Get the average number of samples in each stream block following.
     *
     * @return the number of sample in each stream block.
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
        if ((rate != SoundRate.KHZ_5K) && (rate != SoundRate.KHZ_11K)
                && (rate != SoundRate.KHZ_22K) && (rate != SoundRate.KHZ_44K)) {
            throw new IllegalArgumentValueException(
                    new int[] {SoundRate.KHZ_5K, SoundRate.KHZ_11K,
                            SoundRate.KHZ_22K, SoundRate.KHZ_44K}, rate);
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
        if ((rate != SoundRate.KHZ_5K) && (rate != SoundRate.KHZ_11K)
                && (rate != SoundRate.KHZ_22K) && (rate != SoundRate.KHZ_44K)) {
            throw new IllegalArgumentValueException(
                    new int[] {SoundRate.KHZ_5K, SoundRate.KHZ_11K,
                            SoundRate.KHZ_22K, SoundRate.KHZ_44K}, rate);
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
            throw new IllegalArgumentRangeException(0,
                    Integer.MAX_VALUE, count);
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
     * @param delay
     *            the number of samples to be skipped in an MP3 encoded sound
     *            should be 0 for other sound formats.
     */
    public void setLatency(final int delay) {
        latency = delay;
    }

    /** {@inheritDoc} */
    public SoundStreamHead copy() {
        return new SoundStreamHead(this);
    }

    @Override
    public String toString() {
        return String.format(FORMAT, format, playRate, playChannels,
                playSampleSize, streamRate, streamChannels, streamSampleSize,
                streamSampleCount, latency);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final Context context) {
        length = 4;

        if ((format == 2) && (latency > 0)) {
            length += 2;
        }
        return (length > Coder.SHORT_HEADER_LIMIT ? Coder.LONG_HEADER
                : Coder.SHORT_HEADER) + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws IOException {
        if (length > Coder.SHORT_HEADER_LIMIT) {
            coder.writeShort((MovieTypes.SOUND_STREAM_HEAD
                    << Coder.LENGTH_FIELD_SIZE) | Coder.IS_EXTENDED);
            coder.writeInt(length);
        } else {
            coder.writeShort((MovieTypes.SOUND_STREAM_HEAD
                    << Coder.LENGTH_FIELD_SIZE) | length);
        }
        coder.mark();
        int bits = writeRate(playRate);
        bits |= (playSampleSize - 1) << 1;
        bits |= playChannels - 1;
        coder.writeByte(bits);

        bits = format << 4;
        bits |= writeRate(streamRate);
        bits |= (streamSampleSize - 1) << 1;
        bits |= streamChannels - 1;
        coder.writeByte(bits);

        coder.writeShort(streamSampleCount);

        if ((format == 2) && (latency > 0)) {
            coder.writeShort(latency);
        }
        coder.unmark(length);
    }

    private int readRate(final int value) {
        final int rate;
        switch (value) {
        case 0:
            rate = SoundRate.KHZ_5K;
            break;
        case 1:
            rate = SoundRate.KHZ_11K;
            break;
        case 2:
            rate = SoundRate.KHZ_22K;
            break;
        case 3:
            rate = SoundRate.KHZ_44K;
            break;
        default:
            rate = 0;
            break;
        }
        return rate;
    }

    private int writeRate(final int rate) {
        int value;
        switch (rate) {
        case SoundRate.KHZ_11K:
            value = 4;
            break;
        case SoundRate.KHZ_22K:
            value = 8;
            break;
        case SoundRate.KHZ_44K:
            value = 12;
            break;
        default:
            value = 0;
            break;
        }
        return value;
    }
}
