package com.flagstone.transform.coder;

import com.flagstone.transform.video.AudioData;
import com.flagstone.transform.video.VideoData;
import com.flagstone.transform.video.VideoMetaData;
import com.flagstone.transform.video.VideoTag;

public class FLVDecoder extends BigEndianDecoder 
{
	public FLVDecoder(byte[] data)
	{
		super(data);
	}

	/**
	 * Decode a video object. 
	 * 
	 * If an error occurs while decoding it will be recored in the coder.getContext() When
	 * control returns to the parent video an exception will be thrown.
	 * 
	 * @param coder the Coder containing the binary representation of the styles.
	 * 
	 * @param context the context created by the parent movie to allow information
	 * to be exchanged between objects indirectly.
	 */
	public VideoTag decodeVideoTag() throws CoderException
	{
		VideoTag obj;
	
		int start = getPointer();
	
		int type = readByte();
		int length = readWord(3, false) & 0x00FFFFFF;
		int next = start + ((11 + length) << 3);
	
		setPointer(start);
	
		switch (type)
		{
			case VideoTag.AUDIO_DATA:
				obj = new AudioData();
				obj.decode(this);
				break;
			case VideoTag.VIDEO_DATA:
				obj = new VideoData();
				obj.decode(this);
				break;
			case VideoTag.META_DATA:
				obj = new VideoMetaData();
				obj.decode(this);
				break;
			default:
				obj = null; // NOPMD
				break;
		}
	
		int delta = next - getPointer();

		if (delta != 0)
		{
			throw new CoderException(
					String.valueOf(type),
					start >>> 3,
					length+3,
					delta);
		}
		
		setPointer(next);
	
		return obj;
	}	
}
