/*
 * CanvasTest.java
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

package integration;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.flagstone.transform.Background;
import com.flagstone.transform.Movie;
import com.flagstone.transform.MovieHeader;
import com.flagstone.transform.Place2;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.datatype.WebPalette;
import com.flagstone.transform.fillstyle.SolidFill;
import com.flagstone.transform.linestyle.LineStyle;
import com.flagstone.transform.shape.DefineShape2;
import com.flagstone.transform.util.shape.Canvas;

public final class CanvasIT {
    private static File resultDir;
    private static Canvas path;

    private static int width;
    private static int height;

    @BeforeClass
    public static void initialize() {
        path = new Canvas(true);

        resultDir = new File("target/integration-results/Canvas");

        if (!resultDir.exists() && !resultDir.mkdirs()) {
            fail();
        }

        width = 150;
        height = 100;
    }

    @Before
    public void setUp() throws DataFormatException, IOException {
        path.clear();
        path.setLineStyle(new LineStyle(20, WebPalette.BLACK.color()));
        path.setFillStyle(new SolidFill(WebPalette.RED.color()));
    }

    @Test
    public void rpolyline() throws DataFormatException, IOException {
        final File destFile = new File(resultDir, "rpolyline.swf");

        final int[] points = new int[] {0, -100, 10, 0, 0, 90, 90, 0, 0, 20,
                -90, 0, 0, 90, -20, 0, 0, -90, -90, 0, 0, -20, 90, 0, 0, -90,
                10, 0 };

        path.rpolygon(points);
        showShape(path.defineShape(1), destFile);
    }

    @Test
    public void curve() throws DataFormatException, IOException {
        final File destFile = new File(resultDir, "curve.swf");
        path.curve(0, -100, 150, -100, 150, 0);
        path.close();
        showShape(path.defineShape(1), destFile);
    }

    private void showShape(final DefineShape2 shape, final File file)
            throws DataFormatException, IOException {
        final Movie movie = new Movie();
        MovieHeader attrs = new MovieHeader();
        attrs.setFrameRate(1.0f);
        attrs.setFrameSize(shape.getBounds());

        movie.add(attrs);
        movie.add(new Background(WebPalette.LIGHT_BLUE.color()));

        movie.add(shape);
        movie.add(Place2.show(shape.getIdentifier(), 1, 0, 0));
        movie.add(ShowFrame.getInstance());

        movie.encodeToFile(file);
    }
}
