/*
 * WAVDecoder.java
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

import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.sound.DefineSound;
import com.flagstone.transform.sound.SoundFormat;
import com.flagstone.transform.sound.SoundStreamBlock;
import com.flagstone.transform.sound.SoundStreamHead2;

/**
 * Decoder for WAV sounds so they can be added to a flash file.
 */
//TODO(class)
public final class WAVDecoder implements SoundProvider, SoundDecoder {

    private static final int[] RIFF = {82, 73, 70, 70};
    private static final int[] WAV = {87, 65, 86, 69};
    private static final int FMT = 0x20746d66;
    private static final int DATA = 0x61746164;

    private transient SoundFormat format;
    private transient int numberOfChannels;
    private transient int samplesPerChannel;
    private transient int sampleRate;
    private transient int sampleSize;
    private transient byte[] sound = null;


    public SoundDecoder newDecoder() {
        return new WAVDecoder();
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
        return new DefineSound(identifier, format, sampleRate,
                numberOfChannels, sampleSize, samplesPerChannel, sound);
    }

    /**
     * Generates all the objects required to generate a streaming sound from a
     * URL reference.
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

        int firstSample = 0;
        int firstSampleOffset = 0;
        int bytesPerBlock = 0;
        int bytesRemaining = 0;
        int numberOfBytes = 0;
        byte[] bytes = null;

        final int samplesPerBlock = sampleRate / frameRate;
        final int numberOfBlocks = samplesPerChannel / samplesPerBlock;

        array.add(new SoundStreamHead2(format, sampleRate, numberOfChannels,
                sampleSize, sampleRate, numberOfChannels, sampleSize,
                samplesPerBlock));

        for (int i = 0; i < numberOfBlocks; i++) {
            firstSample = i * samplesPerBlock;
            firstSampleOffset = firstSample * sampleSize * numberOfChannels;
            bytesPerBlock = samplesPerBlock * sampleSize * numberOfChannels;
            bytesRemaining = sound.length - firstSampleOffset;

            numberOfBytes = (bytesRemaining < bytesPerBlock) ? bytesRemaining
                    : bytesPerBlock;

            bytes = new byte[numberOfBytes];
            System.arraycopy(sound, firstSampleOffset, bytes, 0, numberOfBytes);

            array.add(new SoundStreamBlock(bytes));
        }
        return array;
    }


    public void read(final InputStream stream, final int size)
                    throws IOException, DataFormatException {

        final byte[] bytes = new byte[size];
        final BufferedInputStream buffer = new BufferedInputStream(stream);

        buffer.read(bytes);
        buffer.close();

        final SWFDecoder coder = new SWFDecoder(bytes);

        for (int i = 0; i < 4; i++) {
            if (coder.readByte() != RIFF[i]) {
                throw new DataFormatException("Unsupported format");
            }
        }

        coder.readUI32();

        for (int i = 0; i < 4; i++) {
            if (coder.readByte() != WAV[i]) {
                throw new DataFormatException("Unsupported format");
            }
        }

        int chunkType;
        int length;

        do {
            chunkType = coder.readUI32();
            length = coder.readUI32();

            final int blockStart = coder.getPointer();

            switch (chunkType) {
            case FMT:
                decodeFMT(coder);
                break;
            case DATA:
                decodeDATA(coder, length);
                break;
            default:
                coder.adjustPointer(length << 3);
                break;
            }

            final int nextBlock = blockStart + (length << 3);
            coder.setPointer(nextBlock);
        } while (!coder.eof());
    }

    private void decodeFMT(final SWFDecoder coder) throws DataFormatException {
        format = SoundFormat.PCM;

        if (coder.readUI16() != 1) {
            throw new DataFormatException("Unsupported format");
        }

        numberOfChannels = coder.readUI16();
        sampleRate = coder.readUI32();
        coder.readUI32(); // total data length
        coder.readUI16(); // total bytes per sample
        sampleSize = coder.readUI16() / 8;
    }

    private void decodeDATA(final SWFDecoder coder, final int length) {
        samplesPerChannel = length / (sampleSize * numberOfChannels);

        sound = coder.readBytes(new byte[length]);
    }
}
