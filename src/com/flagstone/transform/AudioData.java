/*
 * AudioData.java
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

import java.util.Arrays;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.FLVDecoder;
import com.flagstone.transform.coder.FLVEncoder;
import com.flagstone.transform.coder.VideoTag;
import com.flagstone.transform.coder.VideoTypes;
import com.flagstone.transform.datatype.SoundFormat;

/**
 * AudioData is used to specify the audio track in Flash video files. It defines
 * the timestamp at which the sound is played, the format, playback rate,
 * whether the sound is stereo or mono and the size of each sound sample.
 *
 * <p>
 * Sounds may be either mono or stereo and encoded using either NATIVE_PCM,
 * ADPCM, MP3, NELLYMOSER or NELLYMOSER_8K formats and have sampling rates of
 * 5512, 11025, 22050 or 44100 Hertz. NELLYMOSER is a mono format and so the
 * number of channels must be set to 1. Similarly NELLYMOSER_8K is a special
 * 'fixed' format for a mono sound with an 8KHz sample rate so the values for
 * the playback rate and number of channels will be ignored.
 * </p>
 *
 * <p>
 * When playing the sound the timestamp is used exclusively. Any internal timing
 * information in the audio data is ignored.
 * </p>
 *
 * @see Video
 * @see VideoData
 */
//TODO(class)
public final class AudioData implements VideoTag {

    private static final String FORMAT = "AudioData: { timestamp=%d; format=%d;"
            + " rate=%d; channelCount=%d; sampleSize=%d; data=%d }";

    private int timestamp;
    private int format;
    private int rate;
    private int channelCount;
    private int sampleSize;
    private byte[] data;

    private transient int length;

    /**
     * Creates and initialises an AudioData object using values encoded
     * in the Flash Video binary format.
     *
     * @param coder
     *            an FLVDecoder object that contains the encoded Flash data.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public AudioData(final FLVDecoder coder) throws CoderException {
        final int start = coder.getPointer();
        coder.readByte();
        length = coder.readWord(3, false);
        final int end = coder.getPointer() + (length << 3);
        timestamp = coder.readWord(3, false);
        coder.readWord(4, false); // reserved
        unpack(coder.readByte());
        data = coder.readBytes(new byte[length - 1]);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }

    /** TODO(method). */
    public AudioData() {
        format = 0;
        rate = 5512;
        channelCount = 0;
        sampleSize = 1;
        data = new byte[0];
    }

    /**
     * Creates an AudioData object.
     *
     * @param timestamp
     *            time in milliseconds from the start of the file that the sound
     *            will be played.
     * @param format
     *            the encoding format for the sound, either
     *            Constants.NATIVE_PCM, Constants.ADPCM, Constants.MP3,
     *            Constants.NELLYMOSER_8K or Constants.NELLYMOSER.
     * @param rate
     *            the number of samples per second that the sound is played at,
     *            must be either 5512, 11025, 22050 or 44100. The rate is
     *            ignored if the format is Constants.NELLYMOSER_8K.
     * @param channels
     *            the number of channels in the sound, must be either 1 (Mono)
     *            or 2 (Stereo). If the format is NELLYMOSER the number of
     *            channels must be set to 1. The number of channels is ignored
     *            if the format is Constants.NELLYMOSER_8K.
     * @param size
     *            the size of an uncompressed sound sample in bytes, must be
     *            either 1 or 2. For NELLYMOSER and NELLYMOSER_8K formats the
     *            sample size must be 1.
     * @param bytes
     *            the sound data which cannot be null,
     */
    public AudioData(final int timestamp, final SoundFormat format,
            final int rate, final int channels, final int size,
            final byte[] bytes) {
        setTimestamp(timestamp);
        setFormat(format);
        setRate(rate);
        setChannelCount(channels);
        setSampleSize(size);
        setData(bytes);
    }

    /**
     * Creates and initialises an AudioData object using the values
     * copied from another AudioData object.
     *
     * @param object
     *            a AudioData object from which the values will be
     *            copied.
     */
    public AudioData(final AudioData object) {
        format = object.format;
        rate = object.rate;
        channelCount = object.channelCount;
        sampleSize = object.sampleSize;
        data = object.data;
    }

    /**
     * Returns the timestamp, in milliseconds, relative to the start of the
     * file, when the audio or video will be played.
     */
    public int getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp, in milliseconds, relative to the start of the file,
     * when the audio or video will be played.
     *
     * @param time
     *            the time in milliseconds relative to the start of the file.
     *            Must be in the range 0..16,777,215.
     */
    public void setTimestamp(final int time) {
        if ((time < 0) || (time > 16777215)) {
            throw new IllegalArgumentException(Strings.TIMESTAMP_RANGE);
        }
        timestamp = time;
    }

    /**
     * Returns the format used to encode the sound.
     *
     * @return the format used to compress the sound, either
     *         Constants.NATIVE_PCM, Constants.ADPCM, Constants.MP3,
     *         Constants.NELLYMOSER or Constants.NELLYMOSER_8K.
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
        default:
            throw new IllegalStateException("Unsupported sound format.");
        }
        return value;
    }

    /**
     * Sets the encoding format used to encode the sound.
     *
     * @param encoding
     *            the format for the sound, either Constants.NATIVE_PCM,
     *            Constants.ADPCM, Constants.MP3, Constants.NELLYMOSER or
     *            Constants.NELLYMOSER_8K.
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
        default:
            throw new IllegalArgumentException("Unsupported sound format.");
        }
    }

    /**
     * Returns the rate at which the sound will be played, in Hertz.
     *
     * @return the rate at which the sound was sampled. either: 5512, 11025,
     *         22050 or 44100.
     */
    public int getRate() {
        return rate;
    }

    /**
     * Sets the rate at which the sound was sampled in Hertz. The playback rate
     * for NELLYMOSER_8K encoded audio is fixed at 8KHz so setting the rate has
     * no effect.
     *
     * @param rate
     *            the rate at which the sounds is played in Hz, MUST either
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
     * Returns the number of channels in the sound, either 1 (Mono) or 2
     * (Stereo).
     */
    public int getChannelCount() {
        return channelCount;
    }

    /**
     * Sets the number of channels in the sound. Both the NELLYMOSER and
     * NELLYMOSER_8K are mono format. For NELLYMOSER the number of channels must
     * be set to 1. For NELLYMOSER_8K the setting for the number of channels has
     * no effect.
     *
     * @param count
     *            the number of channels in the sound. Must be either 1 (Mono)
     *            or 2 (Stereo).
     */
    public void setChannelCount(final int count) {
        if ((count < 1) || (count > 2)) {
            throw new IllegalArgumentException(Strings.CHANNEL_RANGE);
        }
        channelCount = count;
    }

    /**
     * Returns the size of an uncompressed sample in bytes, either 1 (8-bit) or
     * 2 (16.-bit).
     */
    public int getSampleSize() {
        return sampleSize;
    }

    /**
     * Sets the sample size in bytes.
     *
     * @param size
     *            the size of sound samples in bytes. Must be either 1 or 2. For
     *            NELLYMOSER and NELLYMOSER_8K formats the sample size must be
     *            1.
     */
    public void setSampleSize(final int size) {
        if ((size < 1) || (size > 2)) {
            throw new IllegalArgumentException(Strings.SAMPLE_SIZE_RANGE);
        }
        sampleSize = size;
    }

    /**
     * Returns a copy of the sound data.
     */
    public byte[] getData() {
        return Arrays.copyOf(data, data.length);
    }

    /**
     * Sets the sound data.
     *
     * @param bytes
     *            the sound data. Can be zero length but must not be null.
     */
    public void setData(final byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException(Strings.DATA_IS_NULL);
        }
        data = Arrays.copyOf(bytes, bytes.length);
    }

    /** {@inheritDoc} */
    public AudioData copy() {
        return new AudioData(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, timestamp, format, rate, channelCount,
                sampleSize, data.length);
    }

    /** {@inheritDoc} */
    public int prepareToEncode() {
        length = 12 + data.length;

        return length;
    }

    /** {@inheritDoc} */
    public void encode(final FLVEncoder coder) throws CoderException {
        final int start = coder.getPointer();

        coder.writeWord(VideoTypes.VIDEO_DATA, 1);
        coder.writeWord(length - 11, 3);
        final int end = coder.getPointer() + (length << 3);
        coder.writeWord(timestamp, 3);
        coder.writeWord(0, 4);
        coder.writeByte(pack());
        coder.writeBytes(data);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }

    private byte pack() {
        byte value = (byte) (format << 4);

        switch (rate) {
        case 5512:
            break;
        case 11025:
            value |= 4;
            break;
        case 22050:
            value |= 8;
            break;
        case 44100:
            value |= 12;
            break;
        default:
            break;
        }
        value |= sampleSize == 2 ? 2 : 0;
        value |= channelCount == 2 ? 1 : 0;

        return value;
    }

    private void unpack(final int value) {
        format = (value >> 4) & 0x0F;

        switch (value & 0x0C) {
        case 0:
            rate = 5512;
            break;
        case 4:
            rate = 11025;
            break;
        case 8:
            rate = 22050;
            break;
        case 12:
            rate = 44100;
            break;
        default:
            break;
        }
        sampleSize = (value & 0x02) == 0 ? 1 : 2;
        channelCount = (value & 0x01) == 0 ? 1 : 2;
    }

}
