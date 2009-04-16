package com.flagstone.transform.movie.fillstyle;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	BitmapFillTest.class,
	FocalGradientFillTest.class,
	GradientTest.class,
	GradientFillTest.class,
	MorphBitmapFillTest.class,
	MorphGradientTest.class,
	MorphGradientFillTest.class,
	MorphSolidFillTest.class,
	SolidFillTest.class
        })
public final class AllFillStyleTests {
}
