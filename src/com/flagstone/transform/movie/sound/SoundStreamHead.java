/*
 * SoundStreamHead.java
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

package com.flagstone.transform.movie.sound;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.MovieTag;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.Types;
import com.flagstone.transform.video.SoundFormat;

/**
 * SoundStreamHead defines the format of a streaming sound, identifying the
 * encoding scheme, the rate at which the sound will be played and the size of
 * the decoded samples.
 * 
 * <p>Sounds may be either mono or stereo and encoded using either NATIVE_PCM, 
 * ADPCM, MP3 or NELLYMOSER formats and have sampling rates of  5512, 11025, 
 * 22050 or 44100 Hertz.</p>

 * <p>The actual sound is streamed used the SoundStreamBlock class which contains
 * the data for each frame in a movie.</p>
 * 
 * <p>When a stream sound is played if the Flash Player cannot render the frames
 * fast enough to maintain synchronisation with the sound being played then
 * frames will be skipped. Normally the player will reduce the frame rate so
 * every frame of a movie is played. The different sets of attributes that
 * identify how the sound will be played compared to the way it was encoded
 * allows the Player more control over how the animation is rendered. Reducing
 * the resolution or playback rate can improve synchronisation with the frames
 * displayed.
 * </p>
 * 
 * <p>SoundStreamHead2 allows way the sound is played to differ from the way it
 * is encoded and streamed to the player. This allows the Player more control
 * over how the animation is rendered. Reducing the resolution or playback rate
 * can improve synchronisation with the frames displayed.</p>
 * 
 *  @see SoundStreamBlock
 *  @see SoundStreamHead2
 */
public final class SoundStreamHead implements MovieTag
{
	private static final String FORMAT = "SoundStreamHead: { format=%s; " +
			"playbackRate=%d; playbackChannels=%d; playbackSampleSize=%d; " +
			"streamRate=%d; streamChannels=%d; streamSampleSize=%d; " +
			"streamSampleCount=%d; latency=%d}";
	
	protected SoundFormat format;
	protected int playRate;
	protected int playChannels;
	protected int playSampleSize;
	protected int streamRate;
	protected int streamChannels;
	protected int streamSampleSize;
	protected int streamSampleCount;
	protected int latency;
	
	private transient int start;
	private transient int end;
	private transient int length;

	public SoundStreamHead(final SWFDecoder coder, final SWFContext context) throws CoderException
	{
		start = coder.getPointer();
		length = coder.readWord(2, false) & 0x3F;
		
		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}
		end = coder.getPointer() + (length << 3);

		coder.readBits(4, false);
		switch (coder.readBits(2, false))
		{
			case 0:
				playRate = 5512;
				break;
			case 1:
				playRate = 11025;
				break;
			case 2:
				playRate = 22050;
				break;
			case 3:
				playRate = 44100;
				break;
			default:
				playRate = 0;
				break;
		}
		playSampleSize = coder.readBits(1, false) + 1;
		playChannels = coder.readBits(1, false) + 1;

		format = SoundFormat.fromInt(coder.readBits(4, false));

		switch (coder.readBits(2, false))
		{
			case 0:
				streamRate = 5512;
				break;
			case 1:
				streamRate = 11025;
				break;
			case 2:
				streamRate = 22050;
				break;
			case 3:
				streamRate = 44100;
				break;
			default:
				streamRate = 0;
				break;
		}
		streamSampleSize = coder.readBits(1, false) + 1;
		streamChannels = coder.readBits(1, false) + 1;
		streamSampleCount = coder.readWord(2, false);

		if (length == 6 && format == SoundFormat.MP3) {
			latency = coder.readWord(2, true);
		}

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}

	/**
	 * Creates a SoundStreamHead object specifying all the parameters
	 * required to define the sound.
	 * 
	 * @param playRate
	 *            the recommended rate for playing the sound, either 5512,
	 *            11025, 22050 or 44100 Hz.
	 * @param playChannels
	 *            The recommended number of playback channels: 1 = mono or 2 =
	 *            stereo.
	 * @param playSize
	 *            the recommended uncompressed sample size for playing the
	 *            sound, either 1 or 2 bytes.
	 * @param streamingRate
	 *            the rate at which the sound was sampled, either 5512, 11025,
	 *            22050 or 44100 Hz.
	 * @param streamingChannels
	 *            the number of channels: 1 = mono or 2 = stereo.
	 * @param streamingSize
	 *            the sample size for the sound in bytes, either 1 or 2.
	 * @param streamingCount
	 *            the number of samples in each subsequent SoundStreamBlock
	 *            object.
	 */
	public SoundStreamHead(int playRate, int playChannels, int playSize,
								int streamingRate, int streamingChannels,
								int streamingSize, int streamingCount)
	{
		setPlayRate(playRate);
		setPlayChannels(playChannels);
		setPlaySampleSize(playSize);

		setStreamRate(streamingRate);
		setStreamChannels(streamingChannels);
		setStreamSampleSize(streamingSize);
		setStreamSampleCount(streamingCount);
	}

	public SoundStreamHead(SoundStreamHead object)
	{
		format = object.format;
		playRate = object.playRate;
		playChannels = object.playChannels;
		playSampleSize = object.playSampleSize;
		streamRate = object.streamRate;
		streamChannels = object.streamChannels;
		streamSampleSize = object.streamSampleSize;
		streamSampleCount = object.streamSampleCount;
		latency = object.latency;
	}

	/**
	 * Returns the streaming sound format. For the SoundStreamHead class supports
	 * ADPCM or MP3 encoded sound data.
	 */
	public SoundFormat getFormat()
	{
		return format;
	}

	/**
	 * Sets the format for the streaming sound.
	 * 
	 * @param encoding
	 *            the compression format for the sound data, either
	 *            DefineSound.ADPCM or DefineSound.MP3.
	 */
	public void setFormat(SoundFormat encoding)
	{
		format = encoding;
	}

	/**
	 * Returns the recommended playback rate: 5512, 11025, 22050 or 44100 Hertz.
	 */
	public int getPlayRate()
	{
		return playRate;
	}

	/**
	 * Returns the recommended number of playback channels = 1 = mono 2 = stereo.
	 */
	public int getPlayChannels()
	{
		return playChannels;
	}

	/**
	 * Returns the recommended playback sample range in bytes: 1 or 2.
	 */
	public int getPlaySampleSize()
	{
		return playSampleSize;
	}

	/**
	 * Returns the sample rate: 5512, 11025, 22050 or 44100 Hz in the streaming
	 * sound.
	 */
	public float getStreamRate()
	{
		return streamRate;
	}

	/**
	 * Returns the number of channels, 1 = mono 2 = stereo, in the streaming sound.
	 */
	public int getStreamChannels()
	{
		return streamChannels;
	}

	/**
	 * Returns the sample size in bytes: 1 or 2 in the streaming sound.
	 */
	public int getStreamSampleSize()
	{
		return streamSampleSize;
	}

	/**
	 * Returns the average number of samples in each stream block following.
	 */
	public int getStreamSampleCount()
	{
		return streamSampleCount;
	}

	/**
	 * Sets the recommended playback rate in Hz. Must be either: 5512, 11025,
	 * 22050 or 44100.
	 * 
	 * @param rate
	 *            the recommended rate for playing the sound.
	 */
	public void setPlayRate(int rate)
	{
		if (rate != 5512 && rate != 11025 && rate != 22050 && rate != 44100) {
			throw new IllegalArgumentException(Strings.SOUND_RATE_OUT_OF_RANGE);
		}
		playRate = rate;
	}

	/**
	 * Sets the recommended number of playback channels = 1 = mono 2 = stereo.
	 * 
	 * @param channels
	 *            the recommended number of playback channels.
	 */
	public void setPlayChannels(int channels)
	{
		if (channels < 1 || channels > 2) {
			throw new IllegalArgumentException(Strings.CHANNEL_COUNT_OUT_OF_RANGE);
		}
		playChannels = channels;
	}

	/**
	 * Sets the recommended playback sample size in bytes. Must be wither 1 or
	 * 2.
	 * 
	 * @param playSize
	 *            the recommended sample size for playing the sound.
	 */
	public void setPlaySampleSize(int playSize)
	{
		if (playSize < 1 || playSize > 2) {
			throw new IllegalArgumentException(Strings.SAMPLE_SIZE_OUT_OF_RANGE);
		}
		playSampleSize = playSize;
	}

	/**
	 * Sets the sample rate in Hz for the streaming sound. Must be either: 5512,
	 * 11025, 22050 or 44100.
	 * 
	 * @param rate
	 *            the rate at which the streaming sound was sampled.
	 */
	public void setStreamRate(int rate)
	{
		if (rate != 5512 && rate != 11025 && rate != 22050 && rate != 44100) {
			throw new IllegalArgumentException(Strings.SOUND_RATE_OUT_OF_RANGE);
		}
		streamRate = rate;
	}

	/**
	 * Sets the number of channels in the streaming sound: 1 = mono 2 = stereo.
	 * 
	 * @param channels
	 *            the number of channels in the streaming sound.
	 */
	public void setStreamChannels(int channels)
	{
		if (channels < 1 || channels > 2) {
			throw new IllegalArgumentException(Strings.CHANNEL_COUNT_OUT_OF_RANGE);
		}
		streamChannels = channels;
	}

	/**
	 * Sets the sample size in bytes for the streaming sound. Must be 1 or 2.
	 * 
	 * @param size
	 *            the sample size for the sound.
	 */
	public void setStreamSampleSize(int size)
	{
		if (size < 1 || size > 2) {
			throw new IllegalArgumentException(Strings.SAMPLE_SIZE_OUT_OF_RANGE);
		}
		streamSampleSize = size;
	}

	/**
	 * Sets the number of samples in each stream block.
	 * 
	 * @param count
	 *            the number of samples in each subsequent SoundStreamBlock
	 *            object.
	 */
	public void setStreamSampleCount(int count)
	{
		if (count < 0) {
			throw new IllegalArgumentException(Strings.NUMBER_CANNOT_BE_NEGATIVE);
		}
		streamSampleCount = count;
	}

	/**
	 * For MP3 encoded sounds, returns the number of samples to skip when
	 * starting to play a sound.
	 * 
	 * @return the number of samples skipped in an MP3 encoded sound Returns 0
	 *         for other sound formats.
	 */
	public int getLatency()
	{
		return latency;
	}

	/**
	 * Set the number of samples to skip when starting to play an MP3 encoded
	 * sound.
	 * 
	 * @param latency
	 *            the number of samples to be skipped in an MP3 encoded sound
	 *            should be 0 for other sound formats.
	 */
	public void setLatency(int latency)
	{
		this.latency = latency;
	}

	public SoundStreamHead copy() {
		return new SoundStreamHead(this);	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, format, playRate, playChannels, playSampleSize,
				streamRate, streamChannels, streamSampleSize, streamSampleCount,
				latency);
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context)
	{
		length = 4;

		if (format == SoundFormat.MP3 && latency > 0) {
			length += 2;
		}
		return (length > 62 ? 6:2) + length;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
		start = coder.getPointer();

		if (length >= 63) {
			coder.writeWord((Types.SOUND_STREAM_HEAD << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((Types.SOUND_STREAM_HEAD << 6) | length, 2);
		}
		end = coder.getPointer() + (length << 3);
		
		coder.writeBits(0, 4);

		switch (playRate)
		{
			case 5512:
				coder.writeBits(0, 2);
				break;
			case 11025:
				coder.writeBits(1, 2);
				break;
			case 22050:
				coder.writeBits(2, 2);
				break;
			case 44100:
				coder.writeBits(3, 2);
				break;
			default:
				break;
		}
		coder.writeBits(playSampleSize - 1, 1);
		coder.writeBits(playChannels - 1, 1);

		coder.writeBits(format.getValue(), 4);

		switch (streamRate)
		{
			case 5512:
				coder.writeBits(0, 2);
				break;
			case 11025:
				coder.writeBits(1, 2);
				break;
			case 22050:
				coder.writeBits(2, 2);
				break;
			case 44100:
				coder.writeBits(3, 2);
				break;
			default:
				break;
		}
		coder.writeBits(streamSampleSize - 1, 1);
		coder.writeBits(streamChannels - 1, 1);
		coder.writeWord(streamSampleCount, 2);

		if (format == SoundFormat.MP3 && latency > 0) {
			coder.writeWord(latency, 2);
		}

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}
}
