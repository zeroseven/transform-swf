package com.flagstone.transform.filter;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	BevelFilterTest.class,
	BlurFilterTest.class,
	ColorMatrixFilterTest.class,
	ConvolutionFilterTest.class,
	DropShadowFilterTest.class,
	GlowFilterTest.class,
	GradientBevelFilterTest.class,
	GradientGlowFilterTest.class
       })
public final class AllFilterTests {
}
