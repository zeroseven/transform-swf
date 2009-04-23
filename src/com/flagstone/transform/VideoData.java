/*
 * VideoData.java
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

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.FLVDecoder;
import com.flagstone.transform.coder.FLVEncoder;
import com.flagstone.transform.coder.VideoTag;
import com.flagstone.transform.coder.VideoTypes;
import com.flagstone.transform.datatype.VideoFormat;
import com.flagstone.transform.datatype.VideoFrameType;

/**
 * The VideoData class is used to store the data for a single frame in a Flash
 * Video file (.flv).
 * 
 * <p>VideoData contains information on the codec used to encode the video along
 * with information on  the frame - whether it is a key frame which must be 
 * displayed, a normal frame or an optional frame which may be discarded to 
 * maintain synchronisation with the audio track (Sorenson codec only).</p>
 */
public final class VideoData implements VideoTag
{
	private static final String FORMAT = "VideoData: { codec=%s; frameType=%s; data =%d }";
	
	private int timestamp;
	private VideoFormat format;
	private VideoFrameType frameType;
	private byte[] data;
	
	private transient int start;
	private transient int length;
	private transient int end;

	public VideoData(FLVDecoder coder) throws CoderException
	{
		start = coder.getPointer();
		coder.readWord(1, false);
		length = coder.readWord(3, false);
		end = coder.getPointer() + (length << 3);
		timestamp = coder.readWord(3, false);
		coder.readWord(4, false); // reserved
		unpack(coder.readByte());
		data = coder.readBytes(new byte[length-1]);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}
	
	/**
	 * Constructs a new VideoData object specifying the time which the video
	 * should be displayed, the video data and the format used to encode it and
	 * the type of frame that the video represents - either a key frame, regular
	 * frame or an optional frame which can be discarded (H263 format only).
	 * 
	 * @param timestamp
	 *            the time in milliseconds at which the data should be played.
	 * @param format
	 *            the format used to encode the video either Constants.H263 or
	 *            Constants.SCREEN_VIDEO.
	 * @param type
	 *            the type of frame being displayed, either Constants.KeyFrame,
	 *            Constants.Frame or Constants.Optional.
	 * @param data
	 *            an array of bytes containing the video encoded using the
	 *            format indicated in the codec attribute, either Constants.H263
	 *            or Constants.SCREEN_VIDEO.
	 */
	public VideoData(int timestamp, VideoFormat format, VideoFrameType type, byte[] data)
	{
		setTimestamp(timestamp);
		setFormat(format);
		setFrameType(type);
		setData(data);
	}

	public VideoData(VideoData object)
	{
		format = object.format;
		frameType = object.frameType;
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
	 * Returns the scheme used to encode the video data, either Constants.H263 for
	 * data that was encoded using the modified Sorenson H263 format or
	 * Constants.ScreenVideo for video that was encoded using Macromedia's
	 * ScreenVideo format.
	 * 
	 * @return the format used to encode the video either Constants.H263 or
	 *         Constants.SCREEN_VIDEO.
	 */
	public VideoFormat getFormat()
	{
		return format;
	}

	/**
	 * Sets the format used to encode the video data, either Constants.H263 for
	 * data that was encoded using the modified Sorenson H263 format or
	 * Constants.ScreenVideo for video that was encoded using Macromedia's
	 * ScreenVideo format.
	 * 
	 * @param format
	 *            the format used to encode the video either Constants.H263 or
	 *            Constants.SCREEN_VIDEO.
	 */
	public void setFormat(VideoFormat format)
	{
		this.format = format;
	}

	/**
	 * Returns the type of frame that will be displayed, either Constants.KeyFrame,
	 * Constants.Frame or Constants.Optional. The latter is used only to indicate
	 * disposable frame and is only used with the Sorenson modified H263 format.
	 * 
	 * @return the type of frame, either Constants.KeyFrame, Constants.Frame or
	 *         Constants.Optional.
	 */
	public VideoFrameType getFrameType()
	{
		return frameType;
	}

	/**
	 * Sets the type of frame type indicating whether it is a key frame
	 * (Constants.KeyFrame), a normal frame (Constants.Frame) displayed between key
	 * frames other whether display of the frame is optional (
	 * Constants.Optional). The latter is used only with video encoded using the
	 * Sorenson modified H263 format.
	 * 
	 * @param type
	 *            the type of frame being displayed, either Constants.KeyFrame,
	 *            Constants.Frame or Constants.Optional.
	 */
	public void setFrameType(VideoFrameType type)
	{
		frameType = type;
	}

	/**
	 * Get the encoded video data.
	 * 
	 * @return an array of bytes encoded using the format indicated in the codec
	 *         attribute, either Constants.H263 or Constants.SCREEN_VIDEO.
	 */
	public byte[] getData()
	{
		return data;
	}

	/**
	 * Sets the encoded video data for the frame.
	 * 
	 * @param data
	 *            an array of bytes containing the video encoded using the
	 *            format indicated in the codec attribute, either Constants.H263
	 *            or Constants.SCREEN_VIDEO.
	 */
	public void setData(byte[] data)
	{
		this.data = data;
	}

	/**
	 * Creates and returns a deep copy of this object.
	 */
	public VideoData copy() 
	{
		return new VideoData(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, format, frameType, data.length);
	}

	public int prepareToEncode()
	{
		length = 12 + data.length;

		return length;
	}

	public void encode(FLVEncoder coder) throws CoderException
	{
		start = coder.getPointer();

		coder.writeWord(VideoTypes.VIDEO_DATA, 1);
		coder.writeWord(length-11, 3);
		end = coder.getPointer() + (length << 3);
		coder.writeWord(timestamp, 3);
		coder.writeWord(0, 4);
		coder.writeByte(pack());
		coder.writeBytes(data);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}
	
	private byte pack() {
		byte value = 0;
		
		switch (format) {
		case H263:
			value |= 0x20;
			break;
		case SCREEN:
			value |= 0x30;
			break;
		}

		switch (frameType) {
		case KEY:
			value |= 1;
			break;
		case NORMAL:
			value |= 2;
			break;
		case OPTIONAL:
			value |= 3;
			break;
		}
		
		return value;
	}
	
	private void unpack(int value) {
		
		switch ((value >>> 4) & 0x0F0) {
		case 2:
			format = VideoFormat.H263;
			break;
		case 3:
			format = VideoFormat.SCREEN;
			break;
		}

		switch (value & 0x0F) {
		case 1:
			frameType = VideoFrameType.KEY;
			break;
		case 2:
			frameType = VideoFrameType.NORMAL;
			break;
		case 3:
			frameType = VideoFrameType.OPTIONAL;
			break;
		}
	}
}