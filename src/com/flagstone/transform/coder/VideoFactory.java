package com.flagstone.transform.coder;

import java.nio.ByteBuffer;

import com.flagstone.transform.AudioData;
import com.flagstone.transform.VideoData;
import com.flagstone.transform.VideoMetaData;

public final class VideoFactory implements FLVFactory<VideoTag> {

	public VideoTag getObject(final FLVDecoder coder) throws CoderException {

		VideoTag object;

		switch (coder.scanByte())
		{
			case VideoTypes.AUDIO_DATA:
				object = new AudioData(coder);
				break;
			case VideoTypes.VIDEO_DATA:
				object = new VideoData(coder);
				break;
			case VideoTypes.META_DATA:
				object = new VideoMetaData(coder);
				break;
			default:
				throw new AssertionError();
		}
		coder.readWord(4, false); // previous length
		return object;
	}
}
