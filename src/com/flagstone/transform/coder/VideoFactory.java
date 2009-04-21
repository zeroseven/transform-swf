package com.flagstone.transform.coder;

import java.nio.ByteBuffer;

import com.flagstone.transform.AudioData;
import com.flagstone.transform.VideoData;
import com.flagstone.transform.VideoMetaData;
import com.flagstone.transform.VideoTag;
import com.flagstone.transform.VideoTypes;

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
