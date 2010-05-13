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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import com.flagstone.transform.coder.FLVDecoder;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.sound.DefineSound;
import com.flagstone.transform.sound.SoundFormat;
import com.flagstone.transform.sound.SoundStreamBlock;
import com.flagstone.transform.sound.SoundStreamHead2;

/**
 * Decoder for MP3 sounds so they can be added to a flash file.
 */
public final class MP3Decoder implements SoundProvider, SoundDecoder {
    private static final int MPEG1 = 3;
    private static final int[] MP3_FRAME_SIZE = {576, 576, 576, 1152};
    private static final int[] CHANNEL_COUNT = {2, 2, 2, 1};

    private static final int[][] BIT_RATES = {
     // MPEG 2.5
        {-1, 8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112, 128, 144, 160, -1},
     // Reserved
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     // MPEG 2.0
        {-1, 8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112, 128, 144, 160, -1},
     // MPEG 1.0
        {-1, 32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320, -1},
    };

    private static final int[][] SAMPLE_RATES = {
        {11025, -1, -1, -1},
        {-1, -1, -1, -1},
        {22050, -1, -1, -1},
        {44100, -1, -1, -1}
    };

    private transient SoundFormat format;
    private transient int numberOfChannels;
    private transient int samplesPerChannel;
    private transient int sampleRate;
    private transient int sampleSize;
    private transient byte[] sound = null;


    public SoundDecoder newDecoder() {
        return new MP3Decoder();
    }


    public void read(final File file) throws IOException, DataFormatException {
        read(new FileInputStream(file), (int) file.length());
    }


    public void read(final URL url) throws IOException, DataFormatException {
        final URLConnection connection = url.openConnection();

        final int fileSize = connection.getContentLength();

        if (fileSize < 0) {
            throw new FileNotFoundException(url.getFile());
        }
        read(url.openStream(), fileSize);
    }

    /**
     * Create a definition for an event sound using the sound in the specified
     * file.
     *
     * @param identifier
     *            the unique identifier that will be used to refer to the sound
     *            in the Flash file.
     *
     * @return a sound definition that can be added to a Movie.
     */
    public DefineSound defineSound(final int identifier) {
        final byte[] bytes = new byte[2 + sound.length];
        bytes[0] = 0;
        bytes[1] = 0;
        System.arraycopy(sound, 0, bytes, 2, sound.length);

        return new DefineSound(identifier, format, sampleRate,
                numberOfChannels, sampleSize, samplesPerChannel, bytes);
    }

    /**
     * Generates all the objects required to stream a sound from a file.
     *
     * @param frameRate
     *            the rate at which the movie is played. Sound are streamed with
     *            one block of sound data per frame.
     *
     * @return an array where the first object is the SoundStreamHead2 object
     *         that defines the streaming sound, followed by SoundStreamBlock
     *         objects containing the sound samples that will be played in each
     *         frame.
     */
    public List<MovieTag> streamSound(final int frameRate) {
        final ArrayList<MovieTag> array = new ArrayList<MovieTag>();

        final int samplesPerBlock = sampleRate / frameRate;
        final int numberOfBlocks = samplesPerChannel / samplesPerBlock;

        int[][] frameTable = null;
        final int samplesPerFrame = 0;

        array.add(new SoundStreamHead2(format, sampleRate, numberOfChannels,
                sampleSize, sampleRate, numberOfChannels, sampleSize,
                samplesPerBlock));

        int numberOfBytes = 0;
        int framesToSend = 0;
        int framesSent = 0;
        int frameCount = 0;
        int sampleCount = 0;
        int seek = 0;

        final SWFDecoder coder = new SWFDecoder(sound);

        coder.findBits(0x7FF, 11, 8);

        final int frameStart = coder.getPointer();
        int numberOfFrames = 0;

        while (coder.findBits(0x7FF, 11, 8)) {
            coder.adjustPointer(frameSize(coder) << 3);
            numberOfFrames++;
        }

        frameTable = new int[numberOfFrames][2];

        coder.setPointer(frameStart);

        int frameNumber = 0;

        while (coder.findBits(0x7FF, 11, 8)) {
            frameTable[frameNumber][0] = (coder.getPointer()
                    - frameStart + 16) >> 3;

            coder.adjustPointer(11); // skip start of frame marker

            final int version = coder.readBits(2, false);

            coder.adjustPointer(3);

            final int bitRate = BIT_RATES[version][coder.readBits(4, false)];
            final int samplingRate = SAMPLE_RATES[version][coder.readBits(2,
                    false)];
            final int padding = coder.readBits(1, false);

            frameTable[frameNumber++][1] = 4 + (((version == MPEG1) ? 144 : 72)
                    * bitRate * 1000 / samplingRate + padding) - 4;

            coder.adjustPointer((frameSize(coder) << 3) - 23);
        }

        for (int i = 0; i < numberOfBlocks; i++) {
            framesToSend = ((i + 1) * samplesPerBlock) / samplesPerFrame;
            framesSent = (i * samplesPerBlock) / samplesPerFrame;
            frameCount = framesToSend - framesSent;
            sampleCount = frameCount * samplesPerFrame;
            seek = (i * samplesPerBlock) - (framesSent * samplesPerFrame);

            numberOfBytes = 4;

            for (int j = 0, k = framesSent; j < frameCount; j++, k++) {
                numberOfBytes += frameTable[k][1];
            }

            final byte[] bytes = new byte[numberOfBytes];

            bytes[0] = (byte) sampleCount;
            bytes[1] = (byte) (sampleCount >> 8);
            bytes[2] = (byte) seek;
            bytes[3] = (byte) (seek >> 8);

            int offset = 4;

            for (int j = 0, k = framesSent; j < frameCount; j++, k++) {
                System.arraycopy(sound, frameTable[k][0], bytes, offset,
                        frameTable[k][1]);
                offset += frameTable[k][1];
            }

            array.add(new SoundStreamBlock(bytes));
        }
        return array;
    }

    private int frameSize(final SWFDecoder coder) {
        int frameSize = 4;

        coder.adjustPointer(11);

        final int version = coder.readBits(2, false);

        coder.adjustPointer(3);

        final int bitRate = BIT_RATES[version][coder.readBits(4, false)];
        final int samplingRate = SAMPLE_RATES[version]
                                              [coder.readBits(2, false)];
        final int padding = coder.readBits(1, false);

        coder.adjustPointer(-23);

        frameSize += (((version == MPEG1) ? 144 : 72) * bitRate * 1000
                / samplingRate + padding) - 4;

        return frameSize;
    }


    public void read(final InputStream stream, final int size)
                    throws IOException, DataFormatException {

        final byte[] bytes = new byte[size];
        final BufferedInputStream buffer = new BufferedInputStream(stream);

        buffer.read(bytes);
        buffer.close();

        final FLVDecoder coder = new FLVDecoder(bytes);

        int numberOfFrames = 0;
        int frameStart = 0;
        int[][] frameTable = null;

        format = SoundFormat.MP3;
        sampleSize = 2;

        int marker;

        while (!coder.eof()) {
            marker = coder.readWord(3, false);
            coder.adjustPointer(-24);

            if (marker == 0x494433) {
                coder.adjustPointer(24); // ID3 signature
                coder.adjustPointer(8); // version number
                coder.adjustPointer(8); // revision number

                coder.adjustPointer(1); // unsynchronized
                coder.adjustPointer(1); // extendedHeader
                coder.adjustPointer(1); // experimental
                final int hasFooter = coder.readBits(1, false);

                coder.adjustPointer(4);

                int totalLength = (hasFooter == 1) ? 10 : 0;

                totalLength += coder.readByte() << 21;
                totalLength += coder.readByte() << 14;
                totalLength += coder.readByte() << 7;
                totalLength += coder.readByte();

                coder.adjustPointer(totalLength << 3);
            } else if (marker == 0x544147) {
                // ID3 V1
                coder.adjustPointer(128 << 3);
            } else if (coder.readBits(11, false) == 0x7FF) {
                // MP3 frame
                coder.adjustPointer(-11);

                if (numberOfFrames == 0) {
                    frameStart = coder.getPointer();
                }

                coder.adjustPointer(frameSize(coder) << 3);
                numberOfFrames++;
            } else {
                /*
                 * If we get here it means we jumped into the middle of either a
                 * frame or tag information. This appears to be a common
                 * occurrence. Goto the end of the file so we can keep the
                 * frames found so far.
                 */
                coder.setPointer(coder.getData().length << 3);
            }
        }

        final int dataLength = coder.getData().length - (frameStart >> 3);

        sound = new byte[dataLength];

        System
                .arraycopy(coder.getData(), frameStart >> 3, sound, 0,
                        dataLength);

        frameTable = new int[numberOfFrames][2];

        for (int i = 0; i < numberOfFrames; i++) {
            frameTable[i][0] = -1;
            frameTable[i][1] = 0;
        }

        coder.setPointer(frameStart);

        int frameNumber = 0;
        int samplesPerFrame;
        int version;

        while (coder.findBits(0x7FF, 11, 8)) {
            frameTable[frameNumber][0] = (coder.getPointer() - frameStart) >> 3;

            coder.adjustPointer(11); // skip start of frame marker

            version = coder.readBits(2, false);

            samplesPerFrame = MP3_FRAME_SIZE[version];

            if (coder.readBits(2, false) != 1) {
                throw new DataFormatException(
                        "Flash only supports MPEG Layer 3");
            }

            coder.readBits(1, false); // crc follows header

            final int bitRate = BIT_RATES[version][coder.readBits(4, false)];

            if (bitRate == -1) {
                throw new DataFormatException("Unsupported Bit-rate");
            }

            sampleRate = SAMPLE_RATES[version][coder.readBits(2, false)];

            if (sampleRate == -1) {
                throw new DataFormatException("Unsupported Sampling-rate");
            }

            final int padding = coder.readBits(1, false);
            coder.readBits(1, false); // reserved

            numberOfChannels = CHANNEL_COUNT[coder.readBits(2, false)];

            coder.adjustPointer(6); // skip modeExtension, copyright, original
            // and emphasis

            samplesPerChannel += samplesPerFrame;

            final int frameSize = (((version == MPEG1) ? 144 : 72) * bitRate
                    * 1000 / sampleRate + padding) - 4;

            frameTable[frameNumber++][1] = 4 + frameSize;

            coder.adjustPointer(frameSize << 3);
        }
    }

    private int frameSize(final FLVDecoder coder) {
        int frameSize = 4;

        coder.adjustPointer(11);

        final int version = coder.readBits(2, false);

        coder.adjustPointer(3);

        final int bitRate = BIT_RATES[version][coder.readBits(4, false)];
        final int samplingRate = SAMPLE_RATES[version]
                                              [coder.readBits(2, false)];
        final int padding = coder.readBits(1, false);

        coder.adjustPointer(-23);

        frameSize += (((version == MPEG1) ? 144 : 72) * bitRate * 1000
                / samplingRate + padding) - 4;

        return frameSize;
    }
}
