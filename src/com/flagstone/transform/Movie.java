/*
 * Movie.java
 * Transform
 *
 * Copyright (c) 2001-2009 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.flagstone.transform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import com.flagstone.transform.coder.Action;
import com.flagstone.transform.coder.ActionDecoder;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.DecoderRegistry;
import com.flagstone.transform.coder.DefineTag;
import com.flagstone.transform.coder.FillStyle;
import com.flagstone.transform.coder.FillStyleDecoder;
import com.flagstone.transform.coder.MorphFillStyleDecoder;
import com.flagstone.transform.coder.MovieDecoder;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.coder.SWFFactory;
import com.flagstone.transform.datatype.Bounds;

/**
 * //TODO(doc) Review
 * //TODO(code) Refactor
 * Movie is a container class for the objects that represents the data
 * structures in a Flash file.
 * 
 * <p>Movie is the core class of the Transform package. It is used to parse and
 * generate Flash files, translating the binary format of the Flash file into an
 * array objects that can be inspected and updated.</p>
 * 
 * <p>A Movie object also contains the attributes that make up the header
 * information of the Flash file, identifying the version support, size of the
 * Flash Player screen, etc.</p>
 * 
 * <p>Movie is also used to generate the unique identifiers that are used to 
 * reference objects. Each call to newIdentifier() returns a unique number
 * for the current. The identifiers are generated using a simple counter. When
 * a movie is decoded this counter is updated each time an object definition
 * is decoded. This allows new objects to be added and ensures that the 
 * identifier does not conflict with an existing object.</p>
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class Movie implements Cloneable
{
	private static final String FORMAT = "Movie: { signature=%s; version=%d; frameSize=%s; frameRate=%f; objects=%s }";
	
	/**
	 * VERSION is used to identify the version of Flash that the edition
	 * supports.
	 */
	public static final int SWF_VERSION = 7;
	/**
	 * The string representing the name of the default character encoding 
	 * scheme when encoding and decoding strings.
	 */
	public static final String DEFAULT_ENCODING = "UTF-8";

	private boolean decodeActions = true;
	private boolean decodeShapes = true;
	private boolean decodeGlyphs = true;
	
	private int identifier = 0;
	private String encoding = Movie.DEFAULT_ENCODING;
	
	private String signature;
	private int version;
	private Bounds frameSize;
	private int length;
	private float frameRate;
	private int frameCount;
	private List<MovieTag> objects;

	/**
	 * Creates a Movie object with no objects. The Flash version defaults
	 * to the package constant Transform.VERSION. From VERSION = 6 onwards the
	 * signature attribute defaults to "CWS" indicating that the binary data
	 * generated when a movie is encoded movie will be compressed using the zlib
	 * algorithm.
	 */
	public Movie()
	{
		signature = Movie.SWF_VERSION > 5 ? "CWS" : "FWS";
		version = Movie.SWF_VERSION;
		frameSize = new Bounds(0,0,0,0);
		objects = new ArrayList<MovieTag>();
	}
	
	public Movie(Movie object) {

		signature = object.signature;
		version = object.version;
		frameSize = object.frameSize;
		frameRate = object.frameRate;
		
		objects = new ArrayList<MovieTag>(object.objects.size());

		for (MovieTag tag : object.objects) {
			objects.add(tag.copy());
		}
	}
	
	/**
	 * Returns a unique identifier for an object derived from the Definition
	 * class. In order to reference objects that define items such as shapes,
	 * sounds, etc. each must be assigned an identifier that is unique for a
	 * given Movie.
	 * 
	 * When binary data is decoded into a sequence of objects, the Movie class
	 * tracks each Define tag decoded, recording the highest value. If a new
	 * Define tag is added to the array of decoded objects the identifier
	 * assigned to the new tag will be guaranteed to be unique.
	 * 
	 * @return an unique identifier for objects that define shapes, sounds, etc.
	 *         in a Flash file.
	 */
	public int newIdentifier()
	{
		return ++identifier;
	}

	/**
	 * Returns the current value for the unique identifier that will be assigned to
	 * definition objects (classes derived from the Definition class). The
	 * value returned is the last value requested using the newIdentifier()
	 * method.
	 * 
	 * @return current value for the unique identifier.
	 */
	public int getIdentifier()
	{
		return identifier;
	}

	/**
	 * Sets the initial value for the unique identifier assigned to definition
	 * objects. When a new identifier is requested the identifier is incremented
	 * before being returned. For most situations the method argument will be
	 * zero. This method should be used with caution as generating definition
	 * object with duplicate identifiers will most probably crash the program
	 * displaying the Flash file generated.
	 * 
	 * @param aValue
	 *            the initial value for the unique identifier.
	 */
	public void setIdentifier(int aValue)
	{
		identifier = aValue;
	}

	/**
	 * Returns the encoding scheme for strings encoded and decoded from Flash
	 * files. The default encoding format is UTF-8 which provides backward
	 * compatibility for Flash files that contain ASCII encoded string. For
	 * files generated by internationalised versions of the Flash authoring tool
	 * any string recognised by the Java environment as identifying a character
	 * encoding scheme may be used. For example, "SJIS" may be used to decode
	 * files generated using the Japanese language version of Flash - Version 5
	 * or earlier.
	 * 
	 * With the introduction of Flash 6 support for Unicode strings was added so
	 * the default encoding of UTF8 will be useful for most applications.
	 * 
	 * @return the string identifying the format for strings. Any string
	 *         recognised by the Java environment may be used, "ASCII", "SJIS"
	 *         and UTF-8" will be the three most commonly used.
	 */
	public String getEncoding()
	{
		return encoding;
	}

	/**
	 * Sets the encoding scheme for strings encoded and decoded from Flash
	 * files. For files generated by internationalised versions of the Flash
	 * authoring tool any string recognised by the Java environment as
	 * identifying a character encoding scheme may be used. For example, "SJIS"
	 * may be used to decode files generated using the Japanese language version
	 * of Flash - Version 5 or earlier.
	 * 
	 * @param scheme
	 *            string identifying the format for strings. Any string
	 *            recognised by the Java environment may be used, "ASCII",
	 *            "SJIS" and UTF-8" will be the three most commonly used.
	 *            
	 * @throws UnsupportedEncodingException if the encoding scheme is not valid.
	 */
	public void setEncoding(String scheme) throws UnsupportedEncodingException
	{
		scheme.getBytes(scheme);		
		encoding = scheme;
	}

	/**
	 * Instructs the movie to fully decode all action objects (true) or leave 
	 * them as binary data (false).
	 * 
	 * @param decode true if the objects containing actions will be fully decoded
	 * false if the actions will be left as binary data.
	 */
	public void setDecodeActions(boolean decode)
	{
		decodeActions = decode;
	}

	/**
	 * Returns true if actions will be decoded, false if they will be left as 
	 * binary data.
	 *  
	 * @return true if actions will be decoded, false otherwise.
	 */
	public boolean willDecodeActions()
	{
		return decodeActions;
	}

	/**
	 * Instructs the movie to fully decode all shape objects (true) or leave 
	 * them as binary data (false).
	 * 
	 * @param decode true if the objects containing shapes will be fully decoded
	 * false if the shapes will be left as binary data.
	 */
	public void setDecodeShapes(boolean decode)
	{
		decodeShapes = decode;
	}

	/**
	 * Returns true if shapes will be decoded, false if they will be left as 
	 * binary data.
	 *  
	 * @return true if shapes will be decoded, false otherwise.
	 */
	public boolean willDecodeShapes()
	{
		return decodeShapes;
	}

	/**
	 * Instructs the movie to fully decode all glyph objects (true) or leave 
	 * them as binary data (false).
	 * 
	 * @param decode true if the font objects containing glyphs will be fully decoded
	 * false if the glyphs will be left as binary data.
	 */
	public void setDecodeGlyphs(boolean decode)
	{
		decodeGlyphs = decode;
	}

	/**
	 * Returns true if glyphs will be decoded, false if they will be left as 
	 * binary data.
	 *  
	 * @return true if glyphs will be decoded, false otherwise.
	 */
	public boolean willDecodeGlyphs()
	{
		return decodeGlyphs;
	}

	/**
	 * Returns the signature identifying that the movie contains Flash. Up to
	 * version 5 the string "FWS" identifies that data is encoded using the
	 * Flash file format. From Flash version 6 onwards Flash data could also be
	 * compressed using the zlib algorithm to reduce the file size. A signature
	 * of "CWS" is used to denote that a file contain compressed Flash data. The
	 * original signature "FWS" is still used to denote that a file contains
	 * uncompressed data.
	 * 
	 * When encoding a Movie object the signature also determines whether the
	 * data generated will be compressed.
	 * 
	 * @return the string identifying the format for the encoded Flash data.
	 */
	public String getSignature()
	{
		return signature;
	}

	/**
	 * Sets the signature for the Flash data when it is encoded. The signature
	 * is used to control whether the encoded data will be compressed. If the
	 * signature is "CWS" then the movie data will be compressed. If the
	 * signature is "FWS" then the data will not be compressed.
	 * 
	 * @param aString
	 *            the signature used for the encoded Flash file. Must be either
	 *            "FWS" or "CWS".
	 */
	public void setSignature(String aString)
	{
		if (!"FWS".equals(aString) && !"CWS".equals(aString)) {
			throw new IllegalArgumentException(Strings.INVALID_FLASH_SIGNATURE);
		}
		signature = aString;
	}

	/**
	 * Returns the number representing the version of Flash that the movie
	 * represents.
	 */
	public int getVersion()
	{
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
	public void setVersion(int aNumber)
	{
		if (aNumber < 0) {
			throw new IllegalArgumentException(Strings.NUMBER_CANNOT_BE_NEGATIVE);
		}
		version = aNumber;
	}

	/**
	 * Returns the bounding rectangle that defines the size of the player screen.
	 */
	public Bounds getFrameSize()
	{
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
	 *            the Bounds object that defines the frame size. Must not be null.
	 */
	public void setFrameSize(Bounds aBounds)
	{
		if (aBounds == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		frameSize = aBounds;
	}

	/**
	 * Returns the number of frames played per second that the movie will be
	 * displayed at.
	 */
	public float getFrameRate()
	{
		return frameRate;
	}

	/**
	 * Sets the number of frames played per second that the Player will display
	 * the coder.
	 * 
	 * @param aNumber
	 *            the number of frames per second that the movie is played.
	 */
	public void setFrameRate(float aNumber)
	{
		frameRate = aNumber;
	}

	/**
	 * Returns the array of objects contained in the Movie.
	 */
	public List<MovieTag> getObjects()
	{
		return objects;
	}

	/**
	 * Sets the array of objects contained in the Movie.
	 * 
	 * @param anArray
	 *            the array of objects that describe a coder. Must not be null.
	 */
	public void setObjects(List<MovieTag> anArray)
	{
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		objects = anArray;
	}

	/**
	 * Adds the object to the Movie.
	 * 
	 * @param anObject
	 *            the object to be added to the movie. Must not be null.
	 */
	public Movie add(MovieTag anObject)
	{
		if (anObject == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		objects.add(anObject);
		return this;
	}

	/**
	 * Adds all of the objects in the array to the Movie.
	 * 
	 * @param array
	 *            an array of MovieTags. Must not be null.
	 */
	public Movie add(List<MovieTag> array)
	{
		if (array == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		objects.addAll(array);
		return this;
	}

	/**
	 * Decodes the contents of the specified file. An object for each tag
	 * decoded from the file is placed in the Movie's object array in the order
	 * they were decoded from the file. If an error occurs while reading and
	 * parsing the file then an exception is thrown.
	 * 
	 * @param path
	 *            the path to the Flash file that will be parsed.
	 * @throws FileNotFoundException -
	 *             if an error occurs while reading the file.
	 * @throws DataFormatException -
	 *             if the file does not contain Flash data.
	 * @throws CoderException -
	 *             if an error occurs while decoding the file.
	 * @throws IOException -
	 *             if an I/O error occurs while reading the file.
	 */
	public void decodeFromFile(String path) throws FileNotFoundException, DataFormatException, IOException
	{
		decodeFromFile(new File(path));
	}

	/**
	 * Decodes the contents of the specified file. An object for each tag
	 * decoded from the file is placed in the Movie's object array in the order
	 * they were decoded from the file. If an error occurs while reading and
	 * parsing the file then an exception is thrown.
	 * 
	 * @param file
	 *            the Flash file that will be parsed.
	 * @throws FileNotFoundException -
	 *             if an error occurs while reading the file.
	 * @throws DataFormatException -
	 *             if the file does not contain Flash data.
	 * @throws CoderException -
	 *             if an error occurs while decoding the file.
	 * @throws IOException -
	 *             if an I/O error occurs while reading the file.
	 */
	public void decodeFromFile(File file) throws FileNotFoundException, DataFormatException, IOException
	{
		FileInputStream stream = null;
		byte[] data = new byte[(int) file.length()];

		try {
			stream = new FileInputStream(file);
			int bytesRead = stream.read(data);

			if (bytesRead != data.length) {
				throw new IOException(file.getAbsolutePath());
			}
		} finally {
			if (stream != null) {
				stream.close();
			}
		}

		decodeFromData(data);
	}

	/**
	 * Decodes the binary Flash data stored in the byte array. If an error
	 * occurs while the data is being decoded an exception is thrown. The array
	 * of objects in the Movie will contain the last tag successfully decoded.
	 * 
	 * @param bytes
	 *            an array of bytes that contain the encoded Flash objects.
	 * 
	 * @throws DataFormatException
	 *             if the file does not contain Flash data.
	 * @throws CoderException
	 *             if an error occurs while decoding the file.
	 * @throws IOException
	 *             if an I/O error occurs while reading the file.
	 */
	public void decodeFromData(byte[] bytes) throws CoderException, IOException, DataFormatException
	{
		if (bytes == null) {
			throw new DataFormatException(Strings.DATA_CANNOT_BE_NULL);
		}

		if (!((bytes[0] == 0x43 || bytes[0] == 0x46) && bytes[1] == 0x57 && bytes[2] == 0x53)) {
			throw new DataFormatException(Strings.INVALID_FLASH_SIGNATURE);
		}

		SWFDecoder coder;
		Context context = new Context();
		
		DecoderRegistry registry = new DecoderRegistry();
		registry.setFillStyleDecoder(new FillStyleDecoder());
		registry.setMorphFillStyleDecoder(new MorphFillStyleDecoder());
		registry.setActionDecoder(new ActionDecoder());
		registry.setMovieDecoder(new MovieDecoder());
		
		context.setRegistry(registry);

		if (bytes[0] == 0x43) {
			coder = new SWFDecoder(unzip(bytes));
		}
		else {
			coder = new SWFDecoder(bytes);
		}
		
		objects.clear();

		coder.setEncoding(encoding);
		
		signature = coder.readString(3, "UTF-8");
		version = coder.readByte();
		length = coder.readWord(4, false);

		frameSize = new Bounds(coder);

		frameRate = coder.readWord(2, true)/256.0f;
		frameCount = coder.readWord(2, false);

		Map<Integer,Integer>vars = context.getVariables();
		
		vars.put(Context.VERSION, version);
		if (decodeActions) {
			vars.put(Context.DECODE_ACTIONS, 1);
		}
		if (decodeShapes) {
			vars.put(Context.DECODE_SHAPES, 1);
		}
		if (decodeGlyphs) {
			vars.put(Context.DECODE_GLYPHS, 1);
		}

		SWFFactory<MovieTag> decoder = registry.getMovieDecoder();
		MovieTag object;
		int type;
		
		do {
			type = coder.scanUnsignedShort() >>> 6;
			
			if (type != 0) {
				object = decoder.getObject(coder, context);
				objects.add(object);
				
				if (object instanceof DefineTag) {
					identifier = ((DefineTag)object).getIdentifier();				
				}
			}
		} while (type != 0);
		
		coder.adjustPointer(16);
	}

	/**
	 * Encodes the array of objects and writes the data to the specified file.
	 * If an error occurs while encoding the file then an exception is thrown.
	 * 
	 * @param path
	 *            the path to the Flash file that the movie will be encoded to.
	 * 
	 * @throws FileNotFoundException -
	 *             if an error occurs while opening the file.
	 * @throws CoderException -
	 *             if an error occurs while encoding the file.
	 * @throws IOException -
	 *             if an I/O error occurs while writing the file.
	 * @throws DataFormatException 
	 *             if an error occurs when compressing the flash file.
	 */
	public void encodeToFile(String path) throws FileNotFoundException, CoderException, IOException, DataFormatException
	{
		FileOutputStream fileContents = new FileOutputStream(path);

		byte[] encodedData = encode();

		fileContents.write(encodedData);
		fileContents.close();
	}

	/**
	 * Encodes the array of objects and writes the data to the specified file.
	 * If an error occurs while encoding the file then an exception is thrown.
	 * 
	 * @param file
	 *            the Flash file that the movie will be encoded to.
	 * 
	 * @throws FileNotFoundException -
	 *             if an error occurs while opening the file.
	 * @throws CoderException -
	 *             if an error occurs while encoding the file.
	 * @throws IOException -
	 *             if an I/O error occurs while writing the file.
	 * @throws DataFormatException 
	 *             if an error occurs when compressing the flash file.
	 */
	public void encodeToFile(File file) throws FileNotFoundException, IOException, DataFormatException
	{
		FileOutputStream fileContents = new FileOutputStream(file);

		byte[] encodedData = encode();

		fileContents.write(encodedData);
		fileContents.close();
	}

	/**
	 * Returns the encoded representation of the array of objects that this
	 * Movie contains. If an error occurs while encoding the file then an
	 * exception is thrown.
	 * 
	 * @return the array of bytes representing the encoded objects.
	 * @throws CoderException -
	 *             if an error occurs while encoding the file.
	 * @throws IOException -
	 *             if an I/O error occurs while encoding the file.
	 * @throws DataFormatException 
	 *             if an error occurs when compressing the flash file.
	 */
	public byte[] encode() throws CoderException, IOException, DataFormatException
	{
		SWFEncoder coder = new SWFEncoder(0);
		Context context = new Context();
		
		coder.setEncoding(encoding);	
		context.getVariables().put(Context.VERSION, version);

		prepareToEncode(coder, context);

		coder.setData(length);

		coder.writeString(signature);
		coder.adjustPointer(-8);
		coder.writeByte(version);
		coder.writeWord(length, 4);
		frameSize.encode(coder, context);
		coder.writeWord((int)(frameRate*256), 2);
		coder.writeWord(frameCount, 2);
		
		for (MovieTag tag : objects) {
			tag.encode(coder, context);
		}
		coder.writeWord(0, 2);

		byte[] data = new byte[length];

		if (signature.charAt(0) == 'C') 
		{
			coder.getData();			
			data = zip(coder.getData(), length);
		} 
		else {
			data = coder.getData();			
		}
		return data;
	}

	/**
	 * Creates and returns a deep copy of this object.
	 */
	public Movie copy() 
	{
		return new Movie(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, signature, version, frameSize, frameRate, objects);
	}

	private int prepareToEncode(SWFEncoder coder, Context context)
	{
		frameCount = 0;

		length = 14; // Includes End
		length += frameSize.prepareToEncode(coder, context);

		for (MovieTag tag : objects) {
			length += tag.prepareToEncode(coder, context);
			if (tag instanceof ShowFrame) {
				frameCount += 1;
			}
		}

		return length;
	}

	private byte[] zip(byte[] bytes, int len) throws DataFormatException
	{
		Deflater deflater = new Deflater();
		byte[] data = new byte[len];

		deflater.setInput(bytes, 8, len - 8);
		deflater.finish();

		int bytesCompressed = deflater.deflate(data);
		byte[] compressedData = new byte[8 + bytesCompressed];

		System.arraycopy(bytes, 0, compressedData, 0, 8);
		System.arraycopy(data, 0, compressedData, 8, bytesCompressed);

		return compressedData;
	}

	private byte[] unzip(byte[] bytes) throws DataFormatException
	{
		int movieLength = 0;

		for (int i = 0; i < 4; i++) {
			movieLength += (bytes[i + 4] & 0x000000FF) << (i * 8);
		}

		byte[] data = new byte[movieLength];

		// copy the uncompressed signature, version and length
		System.arraycopy(bytes, 0, data, 0, 8);

		Inflater inflater = new Inflater();
		inflater.setInput(bytes, 8, bytes.length - 8);
		inflater.inflate(data, 8, movieLength - 8);

		return data;
	}
}
