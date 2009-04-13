package acceptance;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	PlayEventSoundTest.class,
	PlayStreamingSoundTest.class
        })
public final class AllSoundTests
{
}

