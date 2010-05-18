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

import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.coder.FLVDecoder;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.sound.DefineSound;
import com.flagstone.transform.sound.SoundFormat;
import com.flagstone.transform.sound.SoundStreamBlock;
import com.flagstone.transform.sound.SoundStreamHead2;

/**
 * Decoder for MP3 sounds so they can be added to a flash file.
 */
public final class MP3Decoder implements SoundProvider, SoundDecoder {

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

    /** The sound format. */
    private transient SoundFormat format;
    /** The number of sound channels: 1 - mono, 2 - stereo. */
    private transient int numberOfChannels;
    /** The number of sound samples for each channel. */
    private transient int samplesPerChannel;
    /** The rate at which the sound will be played. */
    private transient int sampleRate;
    /** The number of bytes in each sample. */
    private transient int sampleSize;
    /** The sound samples. */
    private transient byte[] sound = null;

    /** Table to track the number of samples included in each stream block. */
    private transient int[][] frameTable = null;
    /** The number of sound samples in each frame. */
    private int samplesPerFrame = 0;

    /** {@inheritDoc} */
    public SoundDecoder newDecoder() {
        return new MP3Decoder();
    }

    /** {@inheritDoc} */
    public void read(final File file) throws IOException, DataFormatException {
        read(new FileInputStream(file), (int) file.length());
    }

    /** {@inheritDoc} */
    public void read(final URL url) throws IOException, DataFormatException {
        final URLConnection connection = url.openConnection();
        final int fileSize = connection.getContentLength();

        if (fileSize < 0) {
            throw new FileNotFoundException(url.getFile());
        }
        read(url.openStream(), fileSize);
    }

    /** {@inheritDoc} */
    public DefineSound defineSound(final int identifier) {
        final byte[] bytes = new byte[2 + sound.length];
        bytes[0] = 0;
        bytes[1] = 0;
        System.arraycopy(sound, 0, bytes, 2, sound.length);

        return new DefineSound(identifier, format, sampleRate,
                numberOfChannels, sampleSize, samplesPerChannel, bytes);
    }

    /** {@inheritDoc} */
    public void read(final InputStream stream, final int size)
            throws IOException, DataFormatException {

        final byte[] bytes = new byte[size];
        final BufferedInputStream buffer = new BufferedInputStream(stream);

        buffer.read(bytes);
        buffer.close();
        decodeMP3(bytes);
    }

    /** {@inheritDoc} */
    public List<MovieTag> streamSound(final int frameRate) {
        final ArrayList<MovieTag> array = new ArrayList<MovieTag>();

        final int samplesPerBlock = sampleRate / frameRate;
        final int numberOfBlocks = samplesPerChannel / samplesPerBlock;

        array.add(new SoundStreamHead2(format, sampleRate, numberOfChannels,
                sampleSize, sampleRate, numberOfChannels, sampleSize,
                samplesPerBlock));

        for (int i = 0; i < numberOfBlocks; i++) {
            array.add(streamBlock(i, samplesPerBlock));
        }
        return array;
    }

    /** {@inheritDoc} */
    public List<MovieTag> streamSound(final int frameRate, final int count) {
        final ArrayList<MovieTag> array = new ArrayList<MovieTag>();

        final int samplesPerBlock = sampleRate / frameRate;
        final int numberOfBlocks = Math.min(count,
                samplesPerChannel / samplesPerBlock);

        array.add(new SoundStreamHead2(format, sampleRate, numberOfChannels,
                sampleSize, sampleRate, numberOfChannels, sampleSize,
                samplesPerBlock));

        for (int i = 0; i < numberOfBlocks; i++) {
            array.add(streamBlock(i, samplesPerBlock));
        }
        return array;
    }

    /**
     * Get the nth block to be streamed.
     * @param blockNumber the number of the block to stream.
     * @param samplesPerBlock the number of samples to include.
     * @return a SoundStreamBlock containing the requested set of samples.
     */
    private SoundStreamBlock streamBlock(int blockNumber, int samplesPerBlock) {
//        int firstSample = 0;
//        int firstSampleOffset = 0;
//        int bytesPerBlock = 0;
//        int bytesRemaining = 0;
        int numberOfBytes = 0;

        int framesToSend = 0;
        int framesSent = 0;
        int frameCount = 0;
        int sampleCount = 0;
        int seek = 0;

        byte[] bytes = null;

        framesToSend = ((blockNumber + 1) * samplesPerBlock) / samplesPerFrame;
        framesSent = (blockNumber * samplesPerBlock) / samplesPerFrame;
        frameCount = framesToSend - framesSent;
        sampleCount = frameCount * samplesPerFrame;
        seek = (blockNumber * samplesPerBlock) - (framesSent * samplesPerFrame);

        numberOfBytes = 4;

        for (int i = 0, j = framesSent; i < frameCount; i++, j++) {
             numberOfBytes += frameTable[j][1];
        }
        bytes = new byte[numberOfBytes];

        bytes[0] = (byte) sampleCount;
        bytes[1] = (byte) (sampleCount >> Coder.BITS_PER_BYTE);
        bytes[2] = (byte) seek;
        bytes[3] = (byte) (seek >> Coder.BITS_PER_BYTE);

        int offset = 4;

        for (int i = 0, j = framesSent; i < frameCount; i++, j++) {
            System.arraycopy(sound, frameTable[j][0], bytes, offset,
                    frameTable[j][1]);
            offset += frameTable[j][1];
        }

        SoundStreamBlock block = null;

        if (bytes != null) {
            block = new SoundStreamBlock(bytes);
        }
        return block;
    }

    /**
     * Decode the MP3 sound.
     * @param bytes the encoded MP3 sound.
     * @throws DataFormatException if the sound data is not in MP3 format.
     */
    private void decodeMP3(byte[] bytes) throws DataFormatException {
        FLVDecoder coder = new FLVDecoder(bytes);
        int numberOfFrames = 0;
        int frameStart = 0;

        format = SoundFormat.MP3;
        sampleSize = 2;

        while (!coder.eof()) {
            int header = coder.readWord(3, false);
            coder.adjustPointer(-24); // ID3 signature

            if (header == 0x494433) {// ID3
                coder.adjustPointer(24); // ID3 signature
                coder.adjustPointer(8); // version number
                coder.adjustPointer(8); // revision number

                coder.adjustPointer(1); // unsynchronized
                coder.adjustPointer(1); // extendedHeader
                coder.adjustPointer(1); // experimental
                int hasFooter = coder.readBits(1, false);

                coder.adjustPointer(4);

                int totalLength = (hasFooter == 1) ? 10 : 0;

                totalLength += coder.readWord(1, false) << 21;
                totalLength += coder.readWord(1, false) << 14;
                totalLength += coder.readWord(1, false) << 7;
                totalLength += coder.readWord(1, false);

                coder.adjustPointer(totalLength << 3);
            } else if (header == 0x544147) {// ID3 V1
                coder.adjustPointer(128 << 3);
            } else if ((header & 0x00FFFFFF) >>> 13 == 0x7FF) {// MP3 frame
                if (numberOfFrames == 0) {
                    frameStart = coder.getPointer();
                }
                coder.adjustPointer(frameSize(coder) << 3);
                numberOfFrames++;
            } else {
                /*
                 * If we get here it means we jumped into the middle of either
                 * a frame or tag information. This appears to be a common
                 * occurrence. Goto the end of the file so we can keep the
                 * frames found so far.
                 */
                coder.setPointer(bytes.length << Coder.BITS_TO_BYTES);
            }
        }

        int dataLength = bytes.length - (frameStart >> 3);

        sound = new byte[dataLength];

        System.arraycopy(bytes, frameStart >> Coder.BITS_TO_BYTES,
                sound, 0, dataLength);

        frameTable = new int[numberOfFrames][2];

        for (int i = 0; i < numberOfFrames; i++) {
            frameTable[i][0] = -1;
            frameTable[i][1] = 0;
        }

        coder.setPointer(frameStart);

        int frameNumber = 0;

        while (coder.findBits(0x7FF, 11, 8)) {
            frameTable[frameNumber][0] = (coder.getPointer()
                    - frameStart) >> Coder.BITS_TO_BYTES;

            coder.adjustPointer(11); // skip start of frame marker

            int version = coder.readBits(2, false);

            samplesPerFrame = MP3_FRAME_SIZE[version];

            if (coder.readBits(2, false) != 1) {
                throw new DataFormatException(
                        "Flash only supports MPEG Layer 3");
            }
            coder.readBits(1, false); // crc follows header

            int bitRate = BIT_RATES[version][coder.readBits(4, false)];

            if (bitRate == -1) {
                throw new DataFormatException("Unsupported Bit-rate");
            }
            sampleRate = SAMPLE_RATES[version][coder.readBits(2, false)];

            if (sampleRate == -1) {
                throw new DataFormatException("Unsupported Sampling-rate");
            }
            int padding = coder.readBits(1, false);
            coder.readBits(1, false); // reserved

            numberOfChannels = CHANNEL_COUNT[coder.readBits(2, false)];
            // skip modeExtension, copyright, original and emphasis
            coder.adjustPointer(6);

            samplesPerChannel += samplesPerFrame;

            int frameSize = (((version == MPEG1) ? 144 : 72)
                    * bitRate * 1000 / sampleRate + padding) - 4;

            frameTable[frameNumber++][1] = 4 + frameSize;

            coder.adjustPointer(frameSize << Coder.BYTES_TO_BITS);
        }
    }

    /**
     * Get the size of the next frame.
     *
     * @param coder the decoder containing the sound data.
     * @return the length of the frame in bytes.
     */
    private int frameSize(FLVDecoder coder) {
        int frameSize = 4;

        coder.adjustPointer(11);

        int version = coder.readBits(2, false);

        coder.adjustPointer(3);

        int bitRate = BIT_RATES[version][coder.readBits(4, false)];
        int samplingRate = SAMPLE_RATES[version][coder.readBits(2, false)];
           int padding = coder.readBits(1, false);

        coder.adjustPointer(-23);

        frameSize += (((version == MPEG1) ? 144 : 72)
                * bitRate * 1000 / samplingRate + padding) - 4;

        return frameSize;
    }
}
