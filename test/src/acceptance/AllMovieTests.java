package acceptance;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { MovieDecodeTest.class, MovieEncodeTest.class,
        MovieCopyTest.class })
public final class AllMovieTests {
}
