package com.flagstone.transform.video;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

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
