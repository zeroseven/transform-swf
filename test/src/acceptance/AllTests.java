package acceptance;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { AllMovieTests.class, AllVideoTests.class,
        AllShapeTests.class, AllImageTests.class, AllSoundTests.class,
        AllFontTests.class })
public final class AllTests {
}
