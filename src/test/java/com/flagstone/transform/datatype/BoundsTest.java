/*
 * BoundsTest.java
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
package com.flagstone.transform.datatype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class BoundsTest {

    private static final int XMIN = 1;
    private static final int YMIN = 2;
    private static final int XMAX = 3;
    private static final int YMAX = 4;

    @Test
    public void checkWidth() {
        assertEquals(XMAX - XMIN, new Bounds(XMIN, 0, XMAX, 0).getWidth());
    }

    @Test
    public void checkHeight() {
        assertEquals(YMAX - YMIN, new Bounds(0, YMIN, 0, YMAX).getHeight());
    }

    @Test
    public void checkObjectIsNotEqual() {
        assertFalse(new Bounds(XMIN, YMIN, XMAX, YMAX).equals(new Object()));
    }

    @Test
    public void checkSameIsEqual() {
        final Bounds fixture = new Bounds(XMIN, YMIN, XMAX, YMAX);
        assertEquals(fixture, fixture);
    }

    @Test
    public void checkDifferentIsNotEqual() {
        final Bounds fixture = new Bounds(XMIN, YMIN, XMAX, YMAX);
        assertFalse(fixture.equals(new Bounds(YMAX, XMAX, YMIN, XMIN)));
    }

    @Test
    public void checkOtherIsEqual() {
        assertTrue(new Bounds(XMIN, YMIN, XMAX, YMAX).equals(
                new Bounds(XMIN, YMIN, XMAX, YMAX)));
    }
}
