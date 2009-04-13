package com.flagstone.transform.util;

/*
 * Header.java
 * 
 * Copyright (c) 2001-2008 Flagstone Software Ltd. All rights reserved.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import com.flagstone.transform.coder.LittleEndianDecoder;
import com.flagstone.transform.movie.Strings;

/**
 * <p>The Header class can be used to quickly decode the header information in a
 * flash file to get information about the number of frames, screen size etc.
 * Only the first few bytes are decoded so this avoid the cost of using the
 * Movie class which decodes all the data structures. However if a file is
 * compressed then the entire contents will have to be unzipped first in order
 * to decode the header information.</p>
 * 
 * <p>Note: The coordinate system used for the screen extends from the TOP LEFT
 * corner with the positive x-axis extending to the left and the positive y-axis
 * extending DOWN, unlike "traditional" Cartesian systems. The (minX, minY) and
 * (maxX, maxY) coordinates specify the range of values used for the x and y
 * axes. (minX, minY) is often (0,0) but may contain any positive or negative
 * values.</p>
 */
public final class Header
{
	private String signature;
	private int version;
	private int minX;
	private int minY;
	private int maxX;
	private int maxY;
	private int length;
	private float frameRate;
	private int frameCount;

	/**
	 * Return the signature used to identify the file type: "FWS" ("SWF" in
	 * reverse) for regular Flash files or "CWS" for compressed (zipped) Flash
	 * files.
	 * 
	 * @return the signature used to identify the file as a Flash file.
	 */
	public String getSignature()
	{
		return signature;
	}

	/**
	 * Return the version number of Flash format used n the file.
	 * 
	 * @return the Flash version number
	 */
	public int getVersion()
	{
		return version;
	}

	/**
	 * Return the length in bytes of the file when uncompressed.
	 * 
	 * @return the length of the (uncompressed) file in bytes.
	 */
	public int getLength()
	{
		return length;
	}

	/**
	 * Return the x-coordinate of the top left corner of the screen.
	 * 
	 * @return the x-coordinate of the top left corner
	 */
	public int getMinX()
	{
		return minX;
	}

	/**
	 * Return the x-coordinate of the bottom right corner of the screen.
	 * 
	 * @return the x-coordinate of the bottom right corner
	 */
	public int getMaxX()
	{
		return maxX;
	}

	/**
	 * The y-coordinate of the top left corner of the screen.
	 * 
	 * @return the y-coordinate of the top left corner
	 */
	public int getMinY()
	{
		return minY;
	}

	/**
	 * The y-coordinate of the bottom right corner of the screen.
	 * 
	 * @return the y-coordinate of the bottom right corner
	 */
	public int getMaxY()
	{
		return maxY;
	}

	/**
	 * Return the number of frames per second at which the Flash Player will
	 * attempt to display the file.
	 * 
	 * @return the frame rate in frames per second.
	 */
	public float getFrameRate()
	{
		return frameRate;
	}

	/**
	 * Return the number of frames in the movie.
	 * 
	 * @return the number of frames in the movie
	 */
	public int getNumberOfFrames()
	{
		return frameCount;
	}

	/**
	 * Initialize the Header object with the specified flash file.
	 * 
	 * @param fileName
	 *            the path to a file.
	 * 
	 * @throws IllegalArgumentException
	 *             if the path is null.

	 * @throws FileNotFoundException
	 *             if the file referenced by the path does not exist or refers
	 *             to a directory.
	 * 
	 * @throws IOException
	 *             if there is an error reading the file.
	 * 
	 * @throws DataFormatException
	 *             if the file is not a flash file or there is an error
	 *             unzipping a compressed flash file.
	 */
	public void decodeFromFile(String fileName) throws FileNotFoundException, IOException, DataFormatException
	{
		decodeFromFile(new File(fileName));
	}

	/**
	 * Initialize the Header object with the specified flash file.
	 * 
	 * @param file
	 *            a File containing the abstract path to a file.
	 * 
	 * @throws IllegalArgumentException
	 *             if the file is null.

	 * @throws FileNotFoundException
	 *             if the file referenced by the path does not exist or refers
	 *             to a directory.
	 * 
	 * @throws IOException
	 *             if there is an error reading the file.
	 * 
	 * @throws DataFormatException
	 *             if the file is not a flash file or there is an error
	 *             unzipping a compressed flash file.
	 */
	public void decodeFromFile(File file) throws FileNotFoundException, IOException, DataFormatException
	{
		FileInputStream fileContents = new FileInputStream(file);

		int fileLength = (int) file.length();
		byte[] contents = new byte[fileLength];

		fileContents.read(contents);
		fileContents.close();

		decodeFromData(contents);
	}

	/**
	 * Initialize the Header object with binary flash data.
	 * 
	 * @param bytes
	 *            the encoded binary data from a Flash file.
	 * 
	 * @throws DataFormatException
	 *             if the data is not in the flash format or there is an error
	 *             unzipping the data if it is compressed.
	 */
	public void decodeFromData(byte[] bytes) throws DataFormatException
	{
		LittleEndianDecoder coder;

		isFlash(bytes);

		if (bytes[0] == 0x43) {
			coder = new LittleEndianDecoder(unzip(bytes));
		}
		else {
			coder = new LittleEndianDecoder(bytes);
		}

		signature = coder.readString(3, "UTF8");
		version = coder.readByte();
		length = coder.readWord(4, false);

		coder.alignToByte();

		int fieldSize = coder.readBits(5, false);

		minX = coder.readBits(fieldSize, true);
		maxX = coder.readBits(fieldSize, true);
		minY = coder.readBits(fieldSize, true);
		maxY = coder.readBits(fieldSize, true);

		coder.alignToByte();

		frameRate = coder.readWord(1, true)/256.0f;
		frameCount = coder.readWord(2, false);
	}

	private void isFlash(byte[] bytes) throws DataFormatException
	{
		if (bytes == null || bytes.length < 8) {
			throw new DataFormatException(Strings.DATA_CANNOT_BE_NULL);
		}

		boolean isFlash = (bytes[0] == 0x43 || bytes[0] == 0x46)
						&& bytes[1] == 0x57 && bytes[2] == 0x53;

		if (!isFlash) {
			throw new DataFormatException(Strings.INVALID_FLASH_SIGNATURE);
		}
	}

	private byte[] unzip(byte[] bytes) throws DataFormatException
	{
		int movieLength = 0;

		for (int i = 0; i < 4; i++) {
			movieLength += (bytes[i + 4] & 0x000000FF) << (i * 8);
		}

		byte[] data = new byte[movieLength];

		System.arraycopy(bytes, 0, data, 0, 8);
		
		Inflater inflater = new Inflater();
		inflater.setInput(bytes, 8, bytes.length - 8);
		inflater.inflate(data, 8, movieLength - 8);

		return data;
	}
}
