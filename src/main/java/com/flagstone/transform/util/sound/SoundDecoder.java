/*
 * SoundDecoder.java
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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.DataFormatException;

import com.flagstone.transform.MovieTag;
import com.flagstone.transform.sound.DefineSound;

/**
 * SoundDecoder is an interface that classes used to decode different sound
 * formats should implement in order to be registered with the SoundRegistry.
 */
public interface SoundDecoder {
    /**
     * Read a sound from a file.
     * @param file the path to the file.
     * @throws IOException if there is an error reading the sound data.
     * @throws DataFormatException if the file contains an unsupported format.
     */
    void read(File file) throws IOException, DataFormatException;
    /**
     * Read a sound from a file referenced by a URL.
     * @param url the reference to the file.
     * @throws IOException if there is an error reading the sound data.
     * @throws DataFormatException if the file contains an unsupported format.
     */
    void read(URL url) throws IOException, DataFormatException;
    /**
     * Read a sound from an input stream.
     * @param stream the stream used to read the sound data.
     * @throws IOException if there is an error reading the sound data.
     * @throws DataFormatException if the file contains an unsupported format.
     */
    void read(InputStream stream)
            throws IOException, DataFormatException;
    /**
     * Define an event sound.
     * @param identifier he unique identifier that will be used to reference the
     * sound in a Movie.
     * @return the definition used to add the sound to a Movie.
     * @throws IOException if there is an error reading the sound data.
     * @throws DataFormatException if the file contains an unsupported format.
     */
    DefineSound defineSound(int identifier)
            throws IOException, DataFormatException;
    /**
     * Define an event sound.
     * @param identifier the unique identifier that will be used to reference
     * the sound in a Movie.
     * @param duration the number of seconds to play the sound for.
     * @return the definition used to add the sound to a Movie.
     * @throws IOException if there is an error reading the sound data.
     * @throws DataFormatException if the file contains an unsupported format.
     */
    DefineSound defineSound(int identifier, float duration)
            throws IOException, DataFormatException;
    /**
     * Generate the header for a streaming sound.
     *
     * @param frameRate the frame rate for the movie so the sound can be divided
     * into sets of samples that can be played with each frame.
     *
     * @return the stream header object that contains information about the
     * sound.
     */
    MovieTag streamHeader(float frameRate);

    /**
     * Generate a SoundStreamBlock with next set of sound samples.
     *
     * @return a SoundStreamBlock containing the sound samples or null if there
     * are no more samples to available.
     * @throws IOException if there is an error reading the sound data.
     * @throws DataFormatException if the file contains an unsupported format.
     */
    MovieTag streamSound() throws IOException, DataFormatException;
}
