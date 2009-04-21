package com.flagstone.transform;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	EnableDebuggerTest.class,
	EnableDebugger2Test.class,
	ExportTest.class,
	FileAttributesTest.class,
	ImportTest.class,
	Import2Test.class,
	MovieMetaDataTest.class,
	PathsArePostscriptTest.class,
	ProtectTest.class,
	ScenesAndLabelsTest.class,
	SerialNumberTest.class
        })
public final class AllMetaTests {
}
