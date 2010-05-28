/*
 * Movie.java
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

package com.flagstone.transform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.InflaterInputStream;

import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.DecoderRegistry;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.coder.SWFFactory;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.exception.IllegalArgumentRangeException;

/**
 * Movie is a container class for the objects that represents the data
 * structures in a Flash file.
 *
 * <p>
 * Movie is the core class of the Transform package. It is used to parse and
 * generate Flash files, translating the binary format of the Flash file into an
 * array objects that can be inspected and updated.
 * </p>
 *
 * <p>
 * A Movie object also contains the attributes that make up the header
 * information of the Flash file, identifying the version support, size of the
 * Flash Player screen, etc.
 * </p>
 *
 * <p>
 * Movie is also used to generate the unique identifiers that are used to
 * reference objects. Each call to newIdentifier() returns a unique number for
 * the current. The identifiers are generated using a simple counter. When a
 * movie is decoded this counter is updated each time an object definition is
 * decoded. This allows new objects to be added and ensures that the identifier
 * does not conflict with an existing object.
 * </p>
 */
public final class Movie {

    public static final int FWS = 0x465753;
    public static final int CWS = 0x435753;

    /** The version of Flash supported. */
    public static final int VERSION = 10;
    /** Format string used in toString() method. */
    private static final String FORMAT = "Movie: { compressed=%b; version=%d;"
            + " frameSize=%s; frameRate=%f; objects=%s }";

    /** The registry for the different types of decoder. */
    private transient DecoderRegistry registry;
    /** The character encoding used for strings. */
    private transient CharacterEncoding encoding;

    private boolean compressed;
    /** The Flash version number. */
    private int version;
    /** The Flash Player screen coordinates. */
    private Bounds frameSize;
    /** The frame rate of the movie. */
    private int frameRate;
    /** The list of objects that make up the movie. */
    private List<MovieTag> objects;

    /** The length of the object when it is encoded. */
    private transient int length;
    /** The number of frames in the movie when it is encoded. */
    private transient int frameCount;

    /**
     * Creates a new Movie.
     */
    public Movie() {
        registry = DecoderRegistry.getDefault();
        encoding = CharacterEncoding.UTF8;
        compressed = true;
        version = VERSION;
        frameSize = new Bounds(0, 0, 0, 0);
        frameRate = 0;
        objects = new ArrayList<MovieTag>();
    }

    /**
     * Creates a complete copy of this movie.
     *
     * @param movie the Movie to copy.
     */
    public Movie(final Movie movie) {

        if (movie.registry != null) {
            registry = movie.registry.copy();
        }

        compressed = movie.compressed;
        version = movie.version;
        frameSize = movie.frameSize;
        frameRate = movie.frameRate;
        encoding = movie.encoding;

        objects = new ArrayList<MovieTag>(movie.objects.size());

        for (final MovieTag tag : movie.objects) {
            objects.add(tag.copy());
        }
    }

    /**
     * Sets the registry containing the object used to decode the different
     * types of object found in a movie.
     *
     * @param decoderRegistry a central registry to decoders of different types
     * of object.
     */
    public void setRegistry(final DecoderRegistry decoderRegistry) {
        registry = decoderRegistry;
    }

    /**
     * Sets the encoding scheme for strings encoded and decoded from Flash
     * files.
     *
     * @param enc the character encoding used for strings.
     */
    public void setEncoding(final CharacterEncoding enc) {
        encoding = enc;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(final boolean compress) {
        compressed = compress;
    }

    /**
     * Get the number representing the version of Flash that the movie
     * represents.
     *
     * @return the version number of Flash that this movie contains.
     */
    public int getVersion() {
        return version;
    }

    /**
     * Sets the Flash version supported in this Movie. Note that there are no
     * restrictions on the objects that can be used in a coder. Using objects
     * that are not supported by an earlier version of the Flash file format may
     * cause the Player to not display the movie correctly or even crash the
     * Player.
     *
     * @param aNumber
     *            the version of the Flash file format that this movie utilises.
     */
    public void setVersion(final int aNumber) {
        if (aNumber < 0) {
            throw new IllegalArgumentRangeException(
                    0, Integer.MAX_VALUE, aNumber);
        }
        version = aNumber;
    }

    /**
     * Get the bounding rectangle that defines the size of the player
     * screen.
     *
     * @return the bounding box that defines the screen.
     */
    public Bounds getFrameSize() {
        return frameSize;
    }

    /**
     * Sets the bounding rectangle that defines the size of the player screen.
     * The coordinates of the bounding rectangle are also used to define the
     * coordinate range. For example if a 400 x 400 pixel rectangle is defined,
     * specifying the values for the x and y coordinates the range -200 to 200
     * sets the centre of the screen at (0,0), if the x and y coordinates are
     * specified in the range 0 to 400 then the centre of the screen will be at
     * (200, 200).
     *
     * @param aBounds
     *            the Bounds object that defines the frame size. Must not be
     *            null.
     */
    public void setFrameSize(final Bounds aBounds) {
        if (aBounds == null) {
            throw new IllegalArgumentException();
        }
        frameSize = aBounds;
    }

    /**
     * Get the number of frames played per second that the movie will be
     * displayed at.
     *
     * @return the movie frame rate.
     */
    public float getFrameRate() {
        return frameRate / 256.0f;
    }

    /**
     * Sets the number of frames played per second that the Player will display
     * the coder.
     *
     * @param rate
     *            the number of frames per second that the movie is played.
     */
    public void setFrameRate(final float rate) {
        frameRate = (int) (rate * 256);
    }

    /**
     * Get the array of objects contained in the Movie.
     *
     * @return the list of objects that make up the movie.
     */
    public List<MovieTag> getObjects() {
        return objects;
    }

    /**
     * Sets the array of objects contained in the Movie.
     *
     * @param anArray
     *            the array of objects that describe a coder. Must not be null.
     */
    public void setObjects(final List<MovieTag> anArray) {
        if (anArray == null) {
            throw new IllegalArgumentException();
        }
        objects = anArray;
    }

    /**
     * Adds the object to the Movie.
     *
     * @param anObject
     *            the object to be added to the movie. Must not be null.
     * @return this object.
     */
    public Movie add(final MovieTag anObject) {
        if (anObject == null) {
            throw new IllegalArgumentException();
        }
        objects.add(anObject);
        return this;
    }

    /** {@inheritDoc} */
    public Movie copy() {
        return new Movie(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, compressed, version, frameSize,
                getFrameRate(), objects);
    }

    /**
     * Decodes the contents of the specified file. An object for each tag
     * decoded from the file is placed in the Movie's object array in the order
     * they were decoded from the file. If an error occurs while reading and
     * parsing the file then an exception is thrown.
     *
     * @param file
     *            the Flash file that will be parsed.
     * @throws DataFormatException
     *             - if the file does not contain Flash data.
     * @throws IOException
     *             - if an I/O error occurs while reading the file.
     */
    public void decodeFromFile(final File file) throws DataFormatException,
            IOException {
        final FileInputStream stream = new FileInputStream(file);

        try {
            decodeFromStream(stream);
        } finally {
            stream.close();
        }
    }

    /**
     * Decodes the binary Flash data from an input stream. If an error occurs
     * while the data is being decoded an exception is thrown. The array of
     * objects in the Movie will contain the last tag successfully decoded.
     *
     * @param stream
     *            an InputStream from which the objects will be decoded.
     *
     * @throws DataFormatException
     *             if the file does not contain Flash data.
     * @throws IOException
     *             if an I/O error occurs while reading the file.
     */
    public void decodeFromStream(final InputStream stream)
            throws DataFormatException, IOException {

        int word = stream.read() << 16;
        word |= stream.read() << 8;
        word |= stream.read();

        if (word == 0x465753) {
            compressed = false;
        } else if (word == 0x435753) {
            compressed = true;
        } else {
            throw new DataFormatException();
        }

        version = stream.read();

        length = stream.read();
        length |= stream.read() << 8;
        length |= stream.read() << 16;
        length |= stream.read() << 24;

        final InputStream streamIn;

        if (compressed) {
            streamIn = new InflaterInputStream(stream);
        } else {
            streamIn = stream;
        }

        SWFDecoder decoder;

        /*
         * If the file is shorter than the default buffer size then set the
         * buffer size to be the file size - this gets around a bug in Java
         * where the end of ZLIB streams are not detected correctly.
         */

        if (length < SWFDecoder.BUFFER_SIZE) {
            decoder = new SWFDecoder(streamIn, length - 8);
        } else {
            decoder = new SWFDecoder(streamIn);
        }
//FIX        decoder.setEncoding(encoding.toString());

        frameSize = new Bounds(decoder);
        frameRate = decoder.readUnsignedShort();
        frameCount = decoder.readUnsignedShort();

        final Context context = new Context();
        context.setRegistry(registry);
        context.setEncoding(encoding.toString());
        context.put(Context.VERSION, version);

        objects.clear();

        final SWFFactory<MovieTag> factory = registry.getMovieDecoder();

        while (decoder.scanUnsignedShort() >>> 6 != MovieTypes.END) {
            objects.add(factory.getObject(decoder, context));
        }
    }

    /**
     * Encodes the array of objects and writes the data to the specified file.
     * If an error occurs while encoding the file then an exception is thrown.
     *
     * @param file
     *            the Flash file that the movie will be encoded to.
     *
     * @throws IOException
     *             - if an I/O error occurs while writing the file.
     * @throws DataFormatException
     *             if an error occurs when compressing the flash file.
     */
    /** {@inheritDoc} */
    public void encodeToFile(final File file) throws IOException,
            DataFormatException {
        final FileOutputStream stream = new FileOutputStream(file);

        try {
            encodeToStream(stream);
        } finally {
            stream.close();
        }
    }

    /**
     * Returns the encoded representation of the array of objects that this
     * Movie contains. If an error occurs while encoding the file then an
     * exception is thrown.
     *
     * @param stream
     *            the output stream that the video will be encoded to.
     * @throws IOException
     *             - if an I/O error occurs while encoding the file.
     * @throws DataFormatException
     *             if an error occurs when compressing the flash file.
     */
    public void encodeToStream(final OutputStream stream)
            throws DataFormatException, IOException {

        final Context context = new Context();
        context.setEncoding(encoding.toString());
        context.getVariables().put(Context.VERSION, version);

        frameCount = 0;

        length = 14; // Includes End
        length += frameSize.prepareToEncode(context);

        for (final MovieTag tag : objects) {
            length += tag.prepareToEncode(context);
            if (tag instanceof ShowFrame) {
                frameCount += 1;
            }
        }

        final SWFEncoder coder = new SWFEncoder(length);
        coder.setEncoding(encoding.toString());

        if (compressed) {
            coder.writeByte(0x43);
        } else {
            coder.writeByte(0x46);
        }
        coder.writeByte(0x57);
        coder.writeByte(0x53);
        coder.writeByte(version);
        coder.writeI32(length);
        frameSize.encode(coder, context);
        coder.writeI16(frameRate);
        coder.writeI16(frameCount);

        for (final MovieTag tag : objects) {
            tag.encode(coder, context);
        }
        coder.writeI16(0);

        byte[] data = new byte[length];

        if (compressed) {
            data = zip(coder.getData(), length);
        } else {
            data = coder.getData();
        }
        stream.write(data);
    }

    private byte[] zip(final byte[] bytes, final int len)
            throws DataFormatException {
        final Deflater deflater = new Deflater();
        final byte[] data = new byte[len];

        deflater.setInput(bytes, 8, len - 8);
        deflater.finish();

        final int bytesCompressed = deflater.deflate(data);
        final byte[] compressedData = new byte[8 + bytesCompressed];

        System.arraycopy(bytes, 0, compressedData, 0, 8);
        System.arraycopy(data, 0, compressedData, 8, bytesCompressed);

        return compressedData;
    }
}
