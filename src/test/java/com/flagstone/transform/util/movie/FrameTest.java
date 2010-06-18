/*
 * FrameTest.java
 * Transform
 *
 * Copyright (c) 2001-2010 Flagstone Software Ltd. All rights reserved.
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

package com.flagstone.transform.util.movie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.flagstone.transform.DoAction;
import com.flagstone.transform.FrameLabel;
import com.flagstone.transform.Movie;
import com.flagstone.transform.Place2;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.action.Action;
import com.flagstone.transform.action.GotoFrame2;
import com.flagstone.transform.action.Push;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.text.DefineTextField;

public final class FrameTest {
    private Movie movie;

    @Before
    public void setUp() {
        movie = new Movie();
//        MovieHeader header = new MovieHeader();
//        header.setFrameSize(new Bounds(0, 0, 100, 100));
//        header.setFrameRate(1.0f);
//        movie.add(header);
    }

    @Test
    public void frameLabel() throws IOException {
        final String label = "label";

        movie.add(new FrameLabel(label));
        movie.add(ShowFrame.getInstance());

        final List<Frame> frames = Frame.split(movie);

        assertEquals(frames.size(), 1);
        assertEquals(frames.get(0).getLabel(), label);
    }

    @Test
    public void frameWithNoLabel() throws IOException {
        movie.add(ShowFrame.getInstance());

        final List<Frame> frames = Frame.split(movie);

        assertEquals(frames.size(), 1);
        assertEquals(frames.get(0).getLabel(), null);
    }

    @Test
    public void frameWithCommand() throws IOException {
        final Place2 command = Place2.show(1, 1, 0, 0);

        movie.add(command);
        movie.add(ShowFrame.getInstance());

        final List<Frame> frames = Frame.split(movie);

        assertEquals(frames.size(), 1);
        assertEquals(frames.get(0).getCommands().size(), 1);
        assertEquals(frames.get(0).getCommands().get(0), command);
    }

    @Test
    public void frameWithNoCommand() throws IOException {
        movie.add(ShowFrame.getInstance());

        final List<Frame> frames = Frame.split(movie);

        assertEquals(frames.size(), 1);
        assertEquals(frames.get(0).getCommands().size(), 0);
    }

    @Test
    public void frameWithDefinition() throws IOException {
        final DefineTextField field = new DefineTextField(1)
                .setBounds(new Bounds(0, 0, 100, 100));

        movie.add(field);
        movie.add(ShowFrame.getInstance());

        final List<Frame> frames = Frame.split(movie);

        assertEquals(frames.size(), 1);
        assertEquals(frames.get(0).getDefinitions().size(), 1);
        assertEquals(frames.get(0).getDefinitions().get(0), field);
    }

    @Test
    public void frameWithNoDefinition() throws IOException {
        movie.add(ShowFrame.getInstance());

        final List<Frame> frames = Frame.split(movie);

        assertEquals(frames.size(), 1);
        assertEquals(frames.get(0).getDefinitions().size(), 0);
    }

    @Test
    public void frameWithActions() throws IOException {
        List<Object>values = new ArrayList<Object>();
        values.add("label");
        final Push push = new Push(values);
        final DoAction actions = new DoAction(new ArrayList<Action>());
        actions.add(push);
        actions.add(new GotoFrame2(true));

        movie.add(actions);
        movie.add(ShowFrame.getInstance());

        final List<Frame> frames = Frame.split(movie);

        assertEquals(frames.size(), 1);
        assertEquals(frames.get(0).getActions().size(), 2);
        assertEquals(frames.get(0).getActions().get(0), push);
        assertEquals(frames.get(0).getActions().get(1).toString(),
                new GotoFrame2(true).toString());
    }

    @Test
    public void frameWithNoActions() throws IOException {
        movie.add(ShowFrame.getInstance());

        final List<Frame> frames = Frame.split(movie);

        assertEquals(frames.size(), 1);
        assertEquals(frames.get(0).getActions().size(), 0);
    }

    @Test
    public void addFrameToMovie() throws IOException {
        final Frame frame = new Frame();
        frame.setLabel("label");
        frame.addDefinition(new DefineTextField(1).setBounds(new Bounds(0, 0,
                100, 100)));
        frame.addCommand(Place2.show(1, 1, 0, 0));
        frame.addToMovie(movie);

        final List<Frame> frames = Frame.split(movie);

        assertNotSame(frame, frames.get(0));
        assertEquals(frame.toString(), frames.get(0).toString());
    }

    @Test
    public void multipleFramesFromMovie() throws IOException {
        movie.add(ShowFrame.getInstance());
        movie.add(ShowFrame.getInstance());

        final List<Frame> frames = Frame.split(movie);

        assertEquals(frames.size(), 2);
    }
}
