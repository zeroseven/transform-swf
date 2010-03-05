package metrics;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { MovieMemoryTest.class, MovieTimingTest.class })
public final class AllMetricTests {
}
