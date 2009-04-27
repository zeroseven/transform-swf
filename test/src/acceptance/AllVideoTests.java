package acceptance;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { VideoDecodeTest.class, VideoEncodeTest.class,
		VideoCopyTest.class })
@SuppressWarnings("PMD.AtLeastOneConstructor")
public final class AllVideoTests {
}
