/*
 * Video.java
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
package com.flagstone.transform.video;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import com.flagstone.transform.coder.FLVDecoder;
import com.flagstone.transform.coder.FLVEncoder;
import com.flagstone.transform.coder.VideoDecoder;
import com.flagstone.transform.coder.VideoTag;

/**
 * Video is a container class for the objects that represents the data
 * structures in a Flash video file. It is very similar to the Movie class for
 * flash (.swf) files and provides a simple API for decoding and encoding files
 * and accessing the objects that represent the different data structures used
 * for audio and video data.
 */
//TODO(class)
public final class Video {

    private static final String FORMAT = "Video: { signature=%s; version=%d;"
            + " objects=%s }";

    private int version;
    private List<VideoTag> objects;

    private transient String signature;

    /**
     * Creates a Video object with no objects.
     */
    public Video() {
        signature = "FLV";
        version = 1;
        objects = new ArrayList<VideoTag>();
    }


    public Video(final Video object) {
        signature = object.signature;
        version = object.version;
        objects = new ArrayList<VideoTag>(object.objects.size());
        for (final VideoTag tag : object.objects) {
            objects.add(tag.copy());
        }
    }

    /**
     * Returns the number representing the version of Flash Video that the video
     * represents.
     */
    public int getVersion() {
        return version;
    }

    /**
     * Sets the Flash Video version.
     *
     * @param aNumber
     *            the version of the Flash Video file format that this object
     *            utilises.
     */
    public void setVersion(final int aNumber) {
        version = aNumber;
    }

    /**
     * Returns the array of video objects.
     */
    public List<VideoTag> getObjects() {
        return objects;
    }

    /**
     * Sets the array of objects contained in the Video.
     *
     * @param anArray
     *            the array of objects that describe a coder. Must not be null.
     */
    public void setObjects(final List<VideoTag> anArray) {
        if (anArray == null) {
            throw new IllegalArgumentException();
        }
        objects = anArray;
    }

    /**
     * Adds the object to the Movie.
     *
     * @param anObject
     *            the object to be added to the coder. Must not be null.
     */
    public Video add(final VideoTag anObject) {
        if (anObject == null) {
            throw new IllegalArgumentException();
        }
        objects.add(anObject);
        return this;
    }

    /** {@inheritDoc} */
    public Video copy() {
        return new Video(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, signature, version, objects);
    }

    /**
     * Decodes the contents of the specified Flash Video file.
     *
     * @param file
     *            the Flash Video file that will be decoded.
     * @throws DataFormatException
     *             - if the file does not contain Flash Video data.
     * @throws IOException
     *             - if an error occurs while reading and decoding the file.
     */
    public void decodeFromFile(final File file) throws IOException,
            DataFormatException {
        final FileInputStream stream = new FileInputStream(file);

        try {
            decodeFromStream(stream, (int) file.length());
        } finally {
            stream.close();
        }
    }

    /**
     * Decodes the contents of the flash video file referenced by the URL.
     *
     * @param url
     *            the path to the Flash Video file.
     * @throws DataFormatException
     *             - if the file does not contain Flash Video data.
     * @throws IOException
     *             - if an error occurs while reading and decoding the file.
     */
    public void decodeFromURL(final URL url)
                throws IOException, DataFormatException {

        final URLConnection connection = url.openConnection();
        final int length = connection.getContentLength();
        if (length < 0) {
            throw new FileNotFoundException(url.getFile());
        }

        final InputStream stream = url.openStream();

        try {
            decodeFromStream(stream, length);
        } finally {
            stream.close();
        }
    }

    private void decodeFromStream(final InputStream stream, final int length)
            throws IOException, DataFormatException {

        final byte[] data = new byte[length];
        stream.read(data);

        final FLVDecoder coder = new FLVDecoder(data);

        signature = coder.readString(3);
        if (!"FLV".equals(signature)) {
            throw new DataFormatException("Not FLV format");
        }

        version = coder.readByte();
        coder.readByte(); // audio & video flags
        coder.readUI32(); // header length always 9
        coder.readUI32(); // previous length

        objects.clear();

        final VideoDecoder decoder = new VideoDecoder();

        do {
            objects.add(decoder.getObject(coder));
            coder.readUI32(); // previous length

        } while (!coder.eof());
    }

    /**
     * Encodes the Video and writes the data to the specified file.
     *
     * @param file
     *            the file that the video will be encoded to.
     * @throws IOException
     *            if the file cannot be found or if an error occurs while
     *            encoding and writing the file.
     */
    public void encodeToFile(final File file) throws IOException {
        final FileOutputStream stream = new FileOutputStream(file);

        try {
            encodeToStream(stream);
        } finally {
            stream.close();
        }
    }

    /**
     * Encodes the Video and writes the data to the specified stream.
     *
     * @param stream
     *            the output stream that the video will be encoded to.
     * @throws IOException
     *            if an error occurs while encoding and writing the file.
     */
    public void encodeToStream(final OutputStream stream) throws IOException {

        int fileLength = 13;

        for (final VideoTag object : objects) {
            fileLength += 4 + object.prepareToEncode();
        }

        final FLVEncoder coder = new FLVEncoder(fileLength);

        int flags = 0;

        for (final VideoTag object : objects) {
            if (object instanceof AudioData) {
                flags |= 4;
            } else if (object instanceof VideoData) {
                flags |= 1;
            }
        }

        coder.writeBytes(signature.getBytes("UTF8"));
        coder.writeByte((byte) version);
        coder.writeByte((byte) flags);
        coder.writeWord(9, 4);
        coder.writeWord(0, 4);

        for (final VideoTag object : objects) {
            object.encode(coder);
        }

        stream.write(coder.getData());
    }
}
