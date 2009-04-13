/*
 * FSLayerTest.java
 * Transform
 *
 * Copyright (c) 2001-2008 Flagstone Software Ltd. All rights reserved.
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
package com.flagstone.transform.util;

import java.util.List;
import java.util.ArrayList;

import com.flagstone.transform.movie.Movie;
import com.flagstone.transform.movie.MovieTag;
import com.flagstone.transform.movie.Place2;
import com.flagstone.transform.movie.Remove2;
import com.flagstone.transform.movie.ShowFrame;
import com.flagstone.transform.movie.datatype.Bounds;
import com.flagstone.transform.movie.datatype.ColorTransform;
import com.flagstone.transform.movie.datatype.CoordTransform;
import com.flagstone.transform.movie.text.DefineTextField;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class LayerTest
{
	private Layer layer;
	private DefineTextField obj;
	
	@Before
	public void setUp()
	{
		obj = new DefineTextField(1, new Bounds(0,0,0,0), "", "");	

		layer = new Layer(1);
		layer.add(obj);		
	}
	
	@Test
	public void merge()
	{
		ArrayList<Layer> layers = new ArrayList<Layer>();
		
		Layer one = new Layer(1);
		one.select(1);
		one.move(1, 1);
		one.show();

		layers.add(one);
		
		Layer two = new Layer(2);	
		two.select(2);
		two.move(2, 2);
		two.show();
		two.move(3, 3);
		two.show();
		
		layers.add(two);
		
		com.flagstone.transform.util.Layer three = new Layer(3);	
		three.select(3);
		three.move(3, 3);
		three.show();
		three.move(4, 4);
		three.show();
		three.move(5, 5);
		three.show();
		
		layers.add(three);
		
		Movie movie = new Movie();
		movie.add(new Place2(1, 1, 1, 1));
		movie.add(new Place2(2, 2, 2, 2));
		movie.add(new Place2(3, 3, 3, 3));
		movie.add(ShowFrame.getInstance());
		movie.add(new Place2(2, 3, 3));
		movie.add(new Place2(3, 4, 4));
		movie.add(ShowFrame.getInstance());
		movie.add(new Place2(3, 5, 5));
		movie.add(ShowFrame.getInstance());
				
		assertEquals(Layer.merge(layers), movie.getObjects());
	}
		
	@Test
	public void add()
	{
	    assertEquals(layer.getIdentifier(), 0);
	    assertEquals(layer.getObjects().size(), 1);
	}
	
	@Test
	public void selectIdentifier()
	{
		layer.select(obj.getIdentifier());
		
	    assertEquals(layer.getIdentifier(), obj.getIdentifier());
	    assertEquals(layer.getObjects().size(), 2);
	}
	
	@Test
	public void selectObject()
	{
		layer.select(obj);
		
	    assertEquals(layer.getIdentifier(), obj.getIdentifier());
	    assertEquals(layer.getObjects().size(), 2);

		Place2 place = (Place2)layer.getObjects().get(1);
	    assertEquals(place.getMode(), Place2.Mode.NEW);
	}
	
	@Test
	public void modify()
	{
		layer.show();
		layer.move(10, 10);
		
		List<MovieTag> array = layer.getObjects();
		Place2 place = (Place2)array.get(3);
		
	    assertEquals(place.getMode(), Place2.Mode.MODIFY);
	}
	
	@Test
	public void replace()
	{
		layer.show();
		layer.move(10, 10);
		layer.replace(obj.getIdentifier());
		
		List<MovieTag> array = layer.getObjects();
		Place2 place = (Place2)array.get(3);
		
	    assertEquals(place.getMode(), Place2.Mode.REPLACE);
	}
	
	@Test
	public void remove()
	{
		layer.show();
		layer.remove();
		
	    assertEquals(layer.getObjects().get(3), new Remove2(layer.getLayer()));
	}
	
	@Test
	public void show()
	{
		layer.show();
		
	    assertEquals(layer.getObjects().size(), 3);
	    assertEquals(layer.getObjects().get(2), ShowFrame.getInstance());
	}
	
	@Test
	public void showMultipleFrames()
	{
	    layer.show(5);
		
	    assertEquals(layer.getObjects().size(), 7);
	}
	
	@Test
	public void move()
	{
		int x = 10;
		int y = 10;
		
		layer.move(-x,-y);
		layer.move(x,y);
	    
	    assertEquals(layer.getObjects().get(1), new Place2(obj.getIdentifier(), layer.getLayer(), x, y));
	}
	
	@Test
	public void color()
	{
		int r = 0x33;
		int g = 0x66;
		int b = 0x99;
		
		layer.color(r,g,b);
	    
	    assertEquals(((Place2)layer.getObjects().get(1)).getColorTransform(), new ColorTransform(r,g,b,255));
	}
	
	@Test
	public void transparentColor()
	{
		int r = 0x33;
		int g = 0x66;
		int b = 0x99;
		int a = 0xCC;
		
		layer.color(r,g,b,a);
	    
	    assertEquals(((Place2)layer.getObjects().get(1)).getColorTransform(), new ColorTransform(r,g,b,a));
	}
	
	@Test
	public void clip()
	{
		int depth = 1;
		
		layer.clip(depth);

	    assertEquals(((Place2)layer.getObjects().get(1)).getClippingDepth(), depth);
	}
	
	@Test
	public void morph()
	{
		float ratio = 0.25f;
		
		layer.morph(ratio);

	    assertEquals(((Place2)layer.getObjects().get(1)).getRatio(), ratio, 0.0f);
	}
	
	@Test
	public void name()
	{
		String label = "test";
		
		layer.name(label);

	    assertEquals(((Place2)layer.getObjects().get(1)).getName(), label);
	}
	
	@Test
	public void changeColor()
	{
		int r = 0x33;
		int g = 0x66;
		int b = 0x99;
		int a = 0xFF;
		
		layer.change(new ColorTransform(r,g,b,a));
	    
	    assertEquals(((Place2)layer.getObjects().get(1)).getColorTransform(), new ColorTransform(r,g,b,a));
	}
	
	@Test
	public void changeLocation()
	{
		int x = 10;
		int y = 10;
		
		layer.change(CoordTransform.translate(x,y));
	    
	    assertEquals(((Place2)layer.getObjects().get(1)).getTransform(), CoordTransform.translate(x,y));
	}
	
}

