package com.flagstone.transform;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { AudioDataTest.class, VideoTest.class,
		VideoDataTest.class, VideoFrameTest.class, VideoMetaDataTest.class, })
@SuppressWarnings("PMD.AtLeastOneConstructor")
public final class AllVideoTests {
}
