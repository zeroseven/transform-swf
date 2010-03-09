package com.flagstone.transform;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.flagstone.transform.video.DefineVideoTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { BackgroundTest.class, BackgroundCodingTest.class,
        DefineDataTest.class, DefineDataCodingTest.class,
        DoABCTest.class, DoABCCodingTest.class,
        DoActionTest.class, DoActionCodingTest.class,
        EnableDebuggerTest.class, EnableDebuggerCodingTest.class,
        EnableDebugger2Test.class, EnableDebugger2CodingTest.class, 
        ExportTest.class, ExportCodingTest.class,
        FrameLabelTest.class, FreeTest.class,
        LimitScriptTest.class, MovieTest.class, MovieDataTest.class,
        MovieObjectTest.class, PlaceTest.class, Place2Test.class,
        Place3Test.class, RemoveTest.class, Remove2Test.class,
        ScalingGridTest.class, ShowFrameTest.class, SymbolClassTest.class,
        TabOrderTest.class, ExportTest.class, MovieAttributesTest.class,
        ImportTest.class, Import2Test.class, MovieMetaDataTest.class,
        PathsArePostscriptTest.class, ProtectTest.class,
        ScenesAndLabelsTest.class, SerialNumberTest.class })
public final class AllMovieTests {
}
