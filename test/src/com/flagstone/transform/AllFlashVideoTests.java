package com.flagstone.transform;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.flagstone.transform.video.ScreenPacketTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	AudioDataTest.class,
	ScreenPacketTest.class,
	VideoTest.class,
	VideoDataTest.class,
	VideoFrameTest.class,
	VideoMetaDataTest.class,
       })
public final class AllFlashVideoTests {
}
