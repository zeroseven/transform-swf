/*
 * ImageDecoder.java
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

package com.flagstone.transform.util.image;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.DataFormatException;

import com.flagstone.transform.coder.ImageTag;

/**
 * ImageDecoder is an interface that classes used to decode different image
 * formats should implement in order to be registered with the ImageRegistry.
 */
public interface ImageDecoder {
    /**
     * Read an image from a file.
     * @param file the path to the file.
     * @throws IOException if there is an error reading the image data.
     * @throws DataFormatException if the file contains an unsupported format.
     */
    void read(File file) throws IOException, DataFormatException;
    /**
     * Read an image from a file referenced by a URL.
     * @param url the reference to the file.
     * @throws IOException if there is an error reading the image data.
     * @throws DataFormatException if the file contains an unsupported format.
     */
    void read(URL url) throws IOException, DataFormatException;
    /**
     * Read an image from an input stream.
     * @param stream the stream used to read the image data.
     * @param size the length of the stream in bytes.
     * @throws IOException if there is an error reading the image data.
     * @throws DataFormatException if the file contains an unsupported format.
     */
    void read(InputStream stream, int size)
        throws IOException, DataFormatException;
    /**
     * Get the width of the image.
     * @return the width of the image in pixels.
     */
    int getWidth();
    /**
     * Get the height of the image.
     * @return the height of the image in pixels.
     */
    int getHeight();
    /**
     * Get the array of bytes that make up the image. This method is used by
     * the ImageFactory to generate a list of blocks for encoding an image as
     * ScreenVideo.
     *
     * @return the array of bytes representing the image.
     */
    byte[] getImage();
    /**
     * Create the image definition so it can be added to a movie.
     * @param identifier the unique identifier used to refer to the image.
     * @return the image definition.
     */
    ImageTag defineImage(int identifier);
}
