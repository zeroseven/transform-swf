package acceptance;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { BMPImageTest.class, JPGImageTest.class,
		PNGImageTest.class, })
public final class AllImageTests {
}
