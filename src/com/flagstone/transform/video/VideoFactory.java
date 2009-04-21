package com.flagstone.transform.video;

import java.nio.ByteBuffer;

import com.flagstone.transform.coder.CoderException;

public final class VideoFactory implements FLVFactory<VideoTag> {

	public VideoTag getObject(final ByteBuffer coder) throws CoderException {

		VideoTag object;

		int type = coder.get();
		coder.position(coder.position()-1);
		
		switch (type)
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
		coder.getInt(); // previous length
		return object;
	}
}
