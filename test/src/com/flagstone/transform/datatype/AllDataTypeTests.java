package com.flagstone.transform.datatype;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { BoundsTest.class, ColorTest.class,
		ColorTransformTest.class, CoordTransformTest.class, })
@SuppressWarnings("PMD.AtLeastOneConstructor")
public final class AllDataTypeTests {
}
