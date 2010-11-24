/*
 * AllMovieTests.java
 * Transform
 *
 * Copyright (c) 2009-2010 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.flagstone.transform;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    BackgroundTest.class, BackgroundCodingTest.class,
    DefineDataTest.class, DefineDataCodingTest.class,
    DoABCTest.class, DoABCCodingTest.class,
    DoActionTest.class, DoActionCodingTest.class,
    EnableDebuggerTest.class, EnableDebuggerCodingTest.class,
    EnableDebugger2Test.class, EnableDebugger2CodingTest.class,
    ExportTest.class, ExportCodingTest.class,
    FrameLabelTest.class, FrameLabelCodingTest.class,
    FreeTest.class, FreeCodingTest.class,
    ImportTest.class, ImportCodingTest.class,
    Import2Test.class, Import2CodingTest.class,
    LimitScriptTest.class, LimitScriptCodingTest.class,
    MovieAttributesCodingTest.class,
    MovieDataTest.class, MovieDataCodingTest.class,
    MovieMetaDataTest.class, MovieMetaDataCodingTest.class,
    MovieObjectTest.class, MovieObjectCodingTest.class,
    PathsArePostscriptTest.class, PathsArePostscriptCodingTest.class,
    PlaceTest.class, PlaceCodingTest.class,
    Place2Test.class, Place2CodingTest.class,
    Place3Test.class, Place3CodingTest.class,
    ProtectTest.class, ProtectCodingTest.class,
    RemoveTest.class, RemoveCodingTest.class,
    Remove2Test.class, Remove2CodingTest.class,
    ScalingGridTest.class, ScalingGridCodingTest.class,
    ScenesAndLabelsTest.class, ScenesAndLabelsCodingTest.class,
    SerialNumberTest.class, SerialNumberCodingTest.class,
    ShowFrameTest.class, ShowFrameCodingTest.class,
    SymbolClassTest.class, SymbolClassCodingTest.class,
    TabOrderTest.class, TabOrderCodingTest.class
    })
public final class AllMovieTests { //NOPMD class for defining test suite
}
