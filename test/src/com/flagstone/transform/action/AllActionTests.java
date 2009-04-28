package com.flagstone.transform.action;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { ActionDataTest.class, ActionObjectTest.class,
		BasicActionTest.class, CallTest.class, ExceptionHandlerTest.class,
		GetUrlTest.class, GetUrl2Test.class, GotoFrameTest.class,
		GotoFrame2Test.class, GotoLabelTest.class, IfTest.class,
		JumpTest.class, NewFunctionTest.class, NewFunction2Test.class,
		PropertyTest.class, PushTest.class, RegisterCopyTest.class,
		RegisterIndexTest.class, SetTargetTest.class, TableTest.class,
		TableIndexTest.class, WaitForFrameTest.class, WaitForFrame2Test.class,
		WithTest.class })
public final class AllActionTests {
}
