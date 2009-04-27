package acceptance;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { PlayEventSoundTest.class, PlayStreamingSoundTest.class })
@SuppressWarnings("PMD.AtLeastOneConstructor")
public final class AllSoundTests {
}
