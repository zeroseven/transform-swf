package acceptance;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { BMPImageTest.class, JPGImageTest.class,
		PNGImageTest.class, })
@SuppressWarnings("PMD.AtLeastOneConstructor")
public final class AllImageTests {
}
