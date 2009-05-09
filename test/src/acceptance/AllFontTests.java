package acceptance;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { 
    TTFFontTest.class, 
    AWTFontTest.class 
})
public final class AllFontTests {
}
