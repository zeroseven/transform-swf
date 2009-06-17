package acceptance;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { 
    MP3EventSoundTest.class, 
    MP3StreamingSoundTest.class,
    WAVEventSoundTest.class, 
    WAVStreamingSoundTest.class 
})
public final class AllSoundTests {
}
