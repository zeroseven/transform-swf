package com.flagstone.transform.image;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	DefineImageTest.class,
	DefineImage2Test.class,
	DefineJPEGImageTest.class,
	DefineJPEGImage2Test.class,
	DefineJPEGImage3Test.class,
	ImageBlockTest.class,
	JPEGEncodingTableTest.class
        })
public final class AllImageTests {
}
