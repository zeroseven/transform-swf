/*
 * ScenesAndLabelsTest.java
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;


public final class ScenesAndLabelsTest {

    private static Map<Integer, String> scenes =
        new LinkedHashMap<Integer, String>();

    private static Map<Integer, String> labels =
        new LinkedHashMap<Integer, String>();

    static {
        scenes.put(1, "A");
        scenes.put(2, "B");
        scenes.put(3, "C");

        labels.put(4, "D");
        labels.put(5, "E");
        labels.put(6, "F");
    }

    private transient ScenesAndLabels fixture;

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessorForSceneIdentifierWithLowerBound() {
        fixture = new ScenesAndLabels(scenes, labels);
        fixture.addScene(-1, "A");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessorForSceneIdentifierWithUpperBound() {
        fixture = new ScenesAndLabels(scenes, labels);
        fixture.addScene(65536, "A");
    }

    @Test(expected = NullPointerException.class)
    public void checkAccessorForSceneNameWithNull() {
        fixture = new ScenesAndLabels(scenes, labels);
        fixture.addScene(1, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessorForSceneNameWithEmpty() {
        fixture = new ScenesAndLabels(scenes, labels);
        fixture.addScene(1, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessorForLabelIdentifierWithLowerBound() {
        fixture = new ScenesAndLabels(scenes, labels);
        fixture.addLabel(-1, "A");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessorForLabelIdentifierWithUpperBound() {
        fixture = new ScenesAndLabels(scenes, labels);
        fixture.addLabel(65536, "A");
    }

    @Test(expected = NullPointerException.class)
    public void checkAccessorForLabelNameWithNull() {
        fixture = new ScenesAndLabels(scenes, labels);
        fixture.addLabel(1, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessorForLabelNameWithEmpty() {
        fixture = new ScenesAndLabels(scenes, labels);
        fixture.addLabel(1, "");
    }

    @Test
    public void checkCopy() {
        fixture = new ScenesAndLabels(scenes, labels);
        final ScenesAndLabels copy = fixture.copy();

        assertNotSame(fixture.getScenes(), copy.getScenes());
        assertNotSame(fixture.getLabels(), copy.getLabels());
        assertEquals(fixture.toString(), copy.toString());
    }
}
