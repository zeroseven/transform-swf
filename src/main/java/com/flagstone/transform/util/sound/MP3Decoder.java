/*
 * MP3Decoder.java
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

package com.flagstone.transform.util.sound;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.zip.DataFormatException;

import com.flagstone.transform.MovieTag;
import com.flagstone.transform.coder.BigDecoder;
import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.sound.DefineSound;
import com.flagstone.transform.sound.SoundFormat;
import com.flagstone.transform.sound.SoundStreamBlock;
import com.flagstone.transform.sound.SoundStreamHead2;

/**
 * Decoder for MP3 sounds so they can be added to a flash file.
 */
public final class MP3Decoder implements SoundProvider, SoundDecoder {

    private static final int ID3_MASK = 0xFFFFFF00;
    private static final int ID3_V1 = 0x54414700;
    private static final int ID3_V1_LENGTH = 128;
    private static final int ID3_V2 = 0x49443300;
    private static final int ID3_V2_FOOTER_LENGTH = 10;
    private static final int MP3_SYNC = 0xFFE00000;

    /** The version number of the MPEG sound format. In this case 3 for MP3. */
    private static final int MPEG1 = 3;
    /** The number of samples in each frame according to the MPEG version. */
    private static final int[] MP3_FRAME_SIZE = {576, 576, 576, 1152};
    /** The number of channels supported by each MP3 version. */
    private static final int[] CHANNEL_COUNT = {2, 2, 2, 1};
    /** The bit rates for the different MPEG sound versions. */
    private static final int[][] BIT_RATES = {
     // MPEG 2.5
        {-1, 8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112, 128, 144, 160, -1},
     // Reserved
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     // MPEG 2.0
        {-1, 8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112, 128, 144, 160, -1},
     // MPEG 3.0
        {-1, 32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320, -1},
    };

    /** The playback rates for the different MPEG sound versions. */
    private static final int[][] SAMPLE_RATES = {
        {11025, -1, -1, -1},
        {-1, -1, -1, -1},
        {22050, -1, -1, -1},
        // MPEG 3.0
        {44100, -1, -1, -1}
    };
    /** The number of bytes in each sample. */
    private static final int SAMPLE_SIZE = 2;

    private transient float movieRate;
    /** The number of sound channels: 1 - mono, 2 - stereo. */
    private transient int numberOfChannels;
    /** The number of sound samples for each channel. */
    private transient int samplesPerChannel;
    /** The rate at which the sound will be played. */
    private transient int sampleRate;
    /** The sound samples. */
    private transient byte[] sound;

    /** The decoder used to read the MP3 frames. */
    private transient BigDecoder coder;

    /** The number of sound samples in each frame. */
    private int samplesPerFrame = 0;
    /** The contents of the current MP3 frame. */
    private byte[] frame;

    private int actualSamples;
    private int expectedSamples;

    /** {@inheritDoc} */
    public SoundDecoder newDecoder() {
        return new MP3Decoder();
    }

    /** {@inheritDoc} */
    public void read(final File file) throws IOException, DataFormatException {
        FileInputStream stream = new FileInputStream(file);
        try {
            read(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    /** {@inheritDoc} */
    public void read(final URL url) throws IOException, DataFormatException {
        final URLConnection connection = url.openConnection();
        final int fileSize = connection.getContentLength();

        if (fileSize < 0) {
            throw new FileNotFoundException(url.getFile());
        }

        InputStream stream = url.openStream();

        try {
            read(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    /** {@inheritDoc} */
    public void read(final InputStream stream)
            throws IOException, DataFormatException {
        coder = new BigDecoder(stream);
        readFrame();
        actualSamples += samplesPerFrame;
    }

    /** {@inheritDoc} */
    public DefineSound defineSound(final int identifier)
            throws IOException, DataFormatException {

        int length;
        sound = new byte[2];

        do {
            length = sound.length;
            sound = Arrays.copyOf(sound, length + frame.length);
            System.arraycopy(frame, 0, sound, length, frame.length);
        } while (readFrame());

        return new DefineSound(identifier, SoundFormat.MP3, sampleRate,
                numberOfChannels, SAMPLE_SIZE, samplesPerChannel, sound);
    }

    /** {@inheritDoc} */
    public DefineSound defineSound(final int identifier, final float duration)
            throws IOException, DataFormatException {

        sound = new byte[2];
        float played = 0;
        int length;

        while (played < duration) {
            length = sound.length;
            sound = Arrays.copyOf(sound, length + frame.length);
            System.arraycopy(frame, 0, sound, length, frame.length);
            played += (float) samplesPerFrame / (float) sampleRate;
            if (!readFrame()) {
                break;
            }
        }

        return new DefineSound(identifier, SoundFormat.MP3, sampleRate,
                numberOfChannels, SAMPLE_SIZE, samplesPerChannel, sound);
    }

    /** {@inheritDoc} */
    public MovieTag streamHeader(final float frameRate) {
        movieRate = frameRate;
        return new SoundStreamHead2(SoundFormat.MP3, sampleRate,
                numberOfChannels, SAMPLE_SIZE, sampleRate, numberOfChannels,
                SAMPLE_SIZE, (int) (sampleRate / frameRate));
    }

    /** {@inheritDoc} */
    public MovieTag streamSound() throws IOException, DataFormatException {
        int seek = expectedSamples > 0 ?  actualSamples - expectedSamples : 0;

        expectedSamples += sampleRate / movieRate;
        sound = new byte[4];
        int sampleCount = 0;
        boolean hasFrames = true;
        do {
            int length = sound.length;
            sound = Arrays.copyOf(sound, length + frame.length);
            System.arraycopy(frame, 0, sound, length, frame.length);
            sampleCount += samplesPerFrame;
            hasFrames = readFrame();
            actualSamples += samplesPerFrame;
       } while (hasFrames && (actualSamples < expectedSamples));

        SoundStreamBlock block = null;

        if (hasFrames) {
            sound[0] = (byte) sampleCount;
            sound[1] = (byte) (sampleCount >> Coder.TO_LOWER_BYTE);
            sound[2] = (byte) seek;
            sound[3] = (byte) (seek >> Coder.TO_LOWER_BYTE);

            if (sound != null) {
                block = new SoundStreamBlock(sound);
            }
        }
        return block;
    }

    private boolean readFrame() throws IOException, DataFormatException {
        boolean frameRead = false;
        while ((!coder.eof()) && !frameRead) {
            int header = coder.scanInt();
            if (header == -1) {
                coder.readUnsignedShort();
            } else if ((header & ID3_MASK) == ID3_V1) {
                readID3V1();
            } else if ((header & ID3_MASK) == ID3_V2) {
                readID3V2();
            } else if ((header & MP3_SYNC) == MP3_SYNC) {
                readFrame(header);
                frameRead = true;
            } else {
                coder.readUnsignedShort();
            }
        }
        return !coder.eof();
    }

    private void readID3V1()
            throws IOException, DataFormatException {
        coder.skip(ID3_V1_LENGTH);
    }

    private void readID3V2()
            throws IOException, DataFormatException {
        coder.readByte(); // I
        coder.readByte(); // D
        coder.readByte(); // 3
        coder.readByte(); // major version
        coder.readByte(); // minor version

        int length;
        int flags = coder.readByte();

        if ((flags & Coder.BIT4) != 0) {
            length = ID3_V2_FOOTER_LENGTH;
        } else  {
            length = 0;
        }
        length += coder.readByte() << 21;
        length += coder.readByte() << 14;
        length += coder.readByte() << 7;
        length += coder.readByte();
        coder.skip(length);
    }

    private void readFrame(final int header)
            throws IOException, DataFormatException {

        int version = (header & 0x180000) >> 19;
        int layer = (header & 0x060000) >> 17;
        //boolean hasCRC = (header & 0x010000) != 0;
        samplesPerFrame = MP3_FRAME_SIZE[version];
        int bitRate = BIT_RATES[version][(header & Coder.NIB3)
                                         >> Coder.ALIGN_NIB3];
        sampleRate = SAMPLE_RATES[version][(header & 0x0C00) >> 10];
        int padding = (header & 0x0200) >> 9;
        //int reserved = (header & 0x0100) >> 8;

        if (layer != 1) {
            throw new DataFormatException("Flash only supports MPEG Layer 3");
        }

        if (bitRate == -1) {
            throw new DataFormatException("Unsupported Bit-rate");
        }

        if (sampleRate == -1) {
            throw new DataFormatException("Unsupported Sampling-rate");
        }

        numberOfChannels = CHANNEL_COUNT[(header & Coder.PAIR3) >> 6];
        samplesPerChannel += samplesPerFrame;

        int frameSize = 4 + (((version == MPEG1) ? 144 : 72)
                * bitRate * 1000 / sampleRate + padding) - 4;

        frame = coder.readBytes(new byte[frameSize]);
    }
}
