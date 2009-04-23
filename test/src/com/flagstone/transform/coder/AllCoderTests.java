package com.flagstone.transform.coder;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	FLVDecoderTest.class,
	FLVEncoderTest.class,
	CoderTest.class,
	DecoderTest.class,
	EncoderTest.class,
	SWFDecoderTest.class,
	SWFEncoderTest.class,
        })
public final class AllCoderTests
{
}
