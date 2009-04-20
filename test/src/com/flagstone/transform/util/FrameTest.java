/*
 * FSFrameTest.java
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

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.movie.DoAction;
import com.flagstone.transform.movie.FrameLabel;
import com.flagstone.transform.movie.Movie;
import com.flagstone.transform.movie.Place2;
import com.flagstone.transform.movie.ShowFrame;
import com.flagstone.transform.movie.action.Action;
import com.flagstone.transform.movie.action.GotoFrame2;
import com.flagstone.transform.movie.action.Push;
import com.flagstone.transform.movie.datatype.Bounds;
import com.flagstone.transform.movie.text.DefineTextField;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public final class FrameTest
{
	private Movie movie;
	
	@Before
	public void setUp()
	{
		movie = new Movie();
		movie.setSignature("FWS");
		movie.setFrameSize(new Bounds(0,0,100,100));
		movie.setFrameRate(1.0f);
	}
	
	@Test
	public void frameLabel() throws CoderException
	{
		String label ="label";
		
		movie.add(new FrameLabel(label));
		movie.add(ShowFrame.getInstance());
		
		List<Frame> frames = Frame.framesFromMovie(movie);
		
		assertEquals(frames.size(), 1);
		assertEquals(frames.get(0).getLabel(), label);
	}
	
	@Test
	public void frameWithNoLabel() throws CoderException
	{
		movie.add(ShowFrame.getInstance());
		
		List<Frame> frames = Frame.framesFromMovie(movie);
		
		assertEquals(frames.size(), 1);
		assertEquals(frames.get(0).getLabel(), null);
	}
	
	@Test
	public void frameWithCommand() throws CoderException
	{
		Place2 command = Place2.show(1, 1, 0, 0);
		
		movie.add(command);
		movie.add(ShowFrame.getInstance());
		
		List<Frame> frames = Frame.framesFromMovie(movie);
		
		assertEquals(frames.size(), 1);
		assertEquals(frames.get(0).getCommands().size(), 1);
		assertEquals(frames.get(0).getCommands().get(0), command);
	}
	
	@Test
	public void frameWithNoCommand() throws CoderException
	{
		movie.add(ShowFrame.getInstance());
		
		List<Frame> frames = Frame.framesFromMovie(movie);
		
		assertEquals(frames.size(), 1);
		assertEquals(frames.get(0).getCommands().size(), 0);
	}
	
	@Test
	public void frameWithDefinition() throws CoderException
	{
		DefineTextField field = new DefineTextField(1).setBounds(new Bounds(0,0,100,100));
		
		movie.add(field);
		movie.add(ShowFrame.getInstance());
		
		List<Frame> frames = Frame.framesFromMovie(movie);
		
		assertEquals(frames.size(), 1);
		assertEquals(frames.get(0).getDefinitions().size(), 1);
		assertEquals(frames.get(0).getDefinitions().get(0), field);
	}
	
	@Test
	public void frameWithNoDefinition() throws CoderException
	{
		movie.add(ShowFrame.getInstance());
		
		List<Frame> frames = Frame.framesFromMovie(movie);
		
		assertEquals(frames.size(), 1);
		assertEquals(frames.get(0).getDefinitions().size(), 0);
	}
	
	@Test
	public void frameWithActions() throws CoderException
	{
		Push push = new Push(new ArrayList<Object>());
		push.add("label");
		
		DoAction actions = new DoAction(new ArrayList<Action>());		
		actions.add(push);
		actions.add(new GotoFrame2(true));
		
		movie.add(actions);
		movie.add(ShowFrame.getInstance());
		
		List<Frame> frames = Frame.framesFromMovie(movie);
		
		assertEquals(frames.size(), 1);
		assertEquals(frames.get(0).getActions().size(), 2);
		assertEquals(frames.get(0).getActions().get(0), push);
		assertEquals(frames.get(0).getActions().get(1), new GotoFrame2(true));
	}
	
	@Test
	public void frameWithNoActions() throws CoderException
	{
		movie.add(ShowFrame.getInstance());
		
		List<Frame> frames = Frame.framesFromMovie(movie);
		
		assertEquals(frames.size(), 1);
		assertEquals(frames.get(0).getActions().size(), 0);
	}
	
	@Test
	public void addFrameToMovie() throws CoderException
	{
		Frame frame = new Frame();
		frame.setLabel("label");
		frame.addDefinition(new DefineTextField(1).setBounds(new Bounds(0,0,100,100)));
		frame.addCommand(Place2.show(1, 1, 0, 0));
		frame.addToMovie(movie);
		
		List<Frame> frames = Frame.framesFromMovie(movie);
		
		assertNotSame(frame, frames.get(0));
		assertEquals(frame, frames.get(0));
	}

	@Test
	public void multipleFramesFromMovie() throws CoderException
	{
		movie.add(ShowFrame.getInstance());
		movie.add(ShowFrame.getInstance());

		List<Frame> frames = Frame.framesFromMovie(movie);
		
		assertEquals(frames.size(), 2);
	}
}

