/*
 * FSLayerTest.java
 * Transform
 *
 * Copyright (c) 2001-2009 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.flagstone.transform.util.movie;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.flagstone.transform.Movie;
import com.flagstone.transform.Place2;
import com.flagstone.transform.Remove2;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.datatype.ColorTransform;
import com.flagstone.transform.datatype.CoordTransform;
import com.flagstone.transform.datatype.PlaceType;
import com.flagstone.transform.text.DefineTextField;

public final class LayerTest {
    private Layer layer;
    private DefineTextField obj;

    @Before
    public void setUp() {
        obj = new DefineTextField(1).setBounds(new Bounds(0, 0, 0, 0));

        layer = new Layer(1);
        layer.add(obj);
    }

    @Test
    public void merge() {
        final ArrayList<Layer> layers = new ArrayList<Layer>();

        final Layer one = new Layer(1);
        one.select(1);
        one.move(1, 1);
        one.show();

        layers.add(one);

        final Layer two = new Layer(2);
        two.select(2);
        two.move(2, 2);
        two.show();
        two.move(3, 3);
        two.show();

        layers.add(two);

        final Layer three = new Layer(3);
        three.select(3);
        three.move(3, 3);
        three.show();
        three.move(4, 4);
        three.show();
        three.move(5, 5);
        three.show();

        layers.add(three);

        final Movie movie = new Movie();
        movie.add(new Place2().show(1, 1, 1, 1));
        movie.add(new Place2().show(2, 2, 2, 2));
        movie.add(new Place2().show(3, 3, 3, 3));
        movie.add(ShowFrame.getInstance());
        movie.add(new Place2().move(2, 3, 3));
        movie.add(new Place2().move(3, 4, 4));
        movie.add(ShowFrame.getInstance());
        movie.add(new Place2().move(3, 5, 5));
        movie.add(ShowFrame.getInstance());

        assertEquals(Layer.merge(layers), movie.getObjects());
    }

    @Test
    public void add() {
        assertEquals(layer.getIdentifier(), 0);
        assertEquals(layer.getObjects().size(), 1);
    }

    @Test
    public void selectIdentifier() {
        layer.select(obj.getIdentifier());

        assertEquals(layer.getIdentifier(), obj.getIdentifier());
        assertEquals(layer.getObjects().size(), 2);
    }

    @Test
    public void selectObject() {
        layer.select(obj);

        assertEquals(layer.getIdentifier(), obj.getIdentifier());
        assertEquals(layer.getObjects().size(), 2);

        final Place2 place = (Place2) layer.getObjects().get(1);
        assertEquals(place.getType(), PlaceType.NEW);
    }

    @Test
    public void modify() {
        layer.show();
        layer.move(10, 10);

        final List<MovieTag> array = layer.getObjects();
        final Place2 place = (Place2) array.get(3);

        assertEquals(place.getType(), PlaceType.MODIFY);
    }

    @Test
    public void replace() {
        layer.show();
        layer.move(10, 10);
        layer.replace(obj.getIdentifier());

        final List<MovieTag> array = layer.getObjects();
        final Place2 place = (Place2) array.get(3);

        assertEquals(place.getType(), PlaceType.REPLACE);
    }

    @Test
    public void remove() {
        layer.show();
        layer.remove();

        assertEquals(layer.getObjects().get(3), new Remove2(layer.getLayer()));
    }

    @Test
    public void show() {
        layer.show();

        assertEquals(layer.getObjects().size(), 3);
        assertEquals(layer.getObjects().get(2), ShowFrame.getInstance());
    }

    @Test
    public void showMultipleFrames() {
        layer.show(5);

        assertEquals(layer.getObjects().size(), 7);
    }

    @Test
    public void move() {
        final int x = 10;
        final int y = 10;

        layer.move(-x, -y);
        layer.move(x, y);

        assertEquals(layer.getObjects().get(1), new Place2().show(
                obj.getIdentifier(), layer.getLayer(), x, y));
    }

    @Test
    public void color() {
        final int r = 0x33;
        final int g = 0x66;
        final int b = 0x99;

        layer.color(r, g, b);

        assertEquals(((Place2) layer.getObjects().get(1)).getColorTransform(),
                new ColorTransform(r, g, b, 255));
    }

    @Test
    public void transparentColor() {
        final int r = 0x33;
        final int g = 0x66;
        final int b = 0x99;
        final int a = 0xCC;

        layer.color(r, g, b, a);

        assertEquals(((Place2) layer.getObjects().get(1)).getColorTransform(),
                new ColorTransform(r, g, b, a));
    }

    @Test
    public void clip() {
        final int depth = 1;

        layer.clip(depth);

        assertEquals(((Place2) layer.getObjects().get(1)).getDepth(), depth);
    }

    @Test
    public void morph() {
        final float ratio = 0.25f;

        layer.morph(ratio);

        assertEquals(((Place2) layer.getObjects().get(1)).getRatio(), ratio,
                0.0f);
    }

    @Test
    public void name() {
        final String label = "test";

        layer.name(label);

        assertEquals(((Place2) layer.getObjects().get(1)).getName(), label);
    }

    @Test
    public void changeColor() {
        final int r = 0x33;
        final int g = 0x66;
        final int b = 0x99;
        final int a = 0xFF;

        layer.change(new ColorTransform(r, g, b, a));

        assertEquals(((Place2) layer.getObjects().get(1)).getColorTransform(),
                new ColorTransform(r, g, b, a));
    }

    @Test
    public void changeLocation() {
        final int x = 10;
        final int y = 10;

        layer.change(CoordTransform.translate(x, y));

        assertEquals(((Place2) layer.getObjects().get(1)).getTransform(),
                CoordTransform.translate(x, y));
    }

}
