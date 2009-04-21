/*
 * VideoData.java
 * Transform
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
package com.flagstone.transform.video;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.movie.Strings;

/**
 * The VideoMetaData class is used to store information on how the video
 * stream should be displayed.
 * 
 * <p>Although meta-data can be found in all flash Video files there is no
 * documentation published by Adobe that describes the data structure. As a
 * result the information is decoded as a simple block of binary data.</p>
 */
public final class VideoMetaData implements VideoTag
{
	private static final String FORMAT = "VideoMetaData: { data=%d }";
	
	private int timestamp;
	private byte[] data;
	
	private transient int start;
	private transient int length;
	private transient int end;

	public VideoMetaData(ByteBuffer coder) throws CoderException
	{
		start = coder.position();

		coder.get();
		length = coder.getInt() >>> 8;
		coder.position(coder.position()-1);
		end = coder.position() + (length << 3);
		timestamp = coder.getInt() >>> 8;
		coder.position(coder.position()-1);
		coder.getInt(); // reserved
		data = new byte[length];
		coder.get(data);

		if (coder.position() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.position() - end) >> 3);
		}
	}

	/**
	 * Constructs a new VideoMetaData object with the encoded data).
	 * 
	 * @param data
	 *            an array of bytes containing the encoded meta-data. Must not
	 *            be null.
	 */
	public VideoMetaData(int timestamp, byte[] data)
	{
		setTimestamp(timestamp);
		setData(data);
	}

	public VideoMetaData(VideoMetaData object)
	{
		timestamp = object.timestamp;
		data = Arrays.copyOf(object.data, object.data.length);
	}

	/**
	 * Returns the timestamp, in milliseconds, relative to the start of the file,
	 * when the audio or video will be played.
	 */
	public int getTimestamp()
	{
		return timestamp;
	}

	/**
	 * Sets the timestamp, in milliseconds, relative to the start of the file,
	 * when the audio or video will be played.
	 * 
	 * @param time the time in milliseconds relative to the start of the file.
	 * Must be in the range 0..16,777,215.
	 */
	public void setTimestamp(int time)
	{
		if (time < 0 || time > 16777215) {
			throw new IllegalArgumentException(Strings.TIMESTAMP_OUT_OF_RANGE);
		}
		timestamp = time;
	}
	/**
	 * Get the encoded meta data that describes how the video stream should be
	 * played.
	 */
	public byte[] getData()
	{
		return data;
	}

	/**
	 * Sets the encoded meta data that describes how the video stream should be
	 * played.
	 * 
	 * @param data
	 *            an array of bytes containing the encoded meta-data. Must not
	 *            be null.
	 */
	public void setData(byte[] data)
	{
		if (data == null) {
			throw new IllegalArgumentException(Strings.DATA_CANNOT_BE_NULL);
		}
		this.data = data;
	}

	/**
	 * Creates and returns a deep copy of this object.
	 */
	public VideoMetaData copy() 
	{
		return new VideoMetaData(this);
	}

	/**
	 * Returns a short description of this action.
	 */
	@Override
	public String toString()
	{
		return String.format(FORMAT, data.length);
	}

	public int prepareToEncode()
	{
		length = 11 + data.length;

		return length;
	}

	public void encode(ByteBuffer coder) throws CoderException
	{
		start = coder.position();

		coder.put((byte)VideoTypes.META_DATA);
		coder.putInt(length-11);
		coder.position(coder.position()-1);
		end = coder.position() + (length << 3);
		coder.putInt(timestamp);
		coder.position(coder.position()-1);
		coder.putInt(0);
		coder.put(data);

		if (coder.position() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.position() - end) >> 3);
		}
	}
}