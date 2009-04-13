package com.flagstone.transform.coder;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	BigEndianDecoderTest.class,
	BigEndianEncoderTest.class,
	CoderTest.class,
	DecoderTest.class,
	EncoderTest.class,
	LittleEndianDecoderTest.class,
	LittleEndianEncoderTest.class,
        })
public final class AllCoderTests
{
}
