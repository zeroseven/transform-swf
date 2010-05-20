/*
 * Frame.java
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

import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.DoAction;
import com.flagstone.transform.FrameLabel;
import com.flagstone.transform.Movie;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.action.Action;
import com.flagstone.transform.coder.DefineTag;
import com.flagstone.transform.coder.MovieTag;

/**
 * <p>
 * The Frame class is used to provide a higher level view of a movie. Rather
 * than viewing movies as a sequence of individual objects each representing a
 * given data structure in the encoded Flash file, objects can be grouped
 * together in frames which presents a more logical view of a movie and makes
 * movie manipulation and search for specific objects easier to handle.
 * </p>
 *
 * <p>
 * Each Frame object has the following attributes:<br/>
 *
 * <em>number</em> - The position in the movie when the frame will be displayed.
 * <br/>
 *
 * <em>label</em> - An optional name assigned to a frame. The GotoFrame2 object
 * can be used to move to a named frame when playing a movie or movie clip.<br/>
 *
 * <em>definitions</em> - An array containing objects that define items for
 * display in a movie. Definitions are sub-classes of the Definition class and
 * define shapes, fonts, images and sounds that are displayed or played by the
 * Flash Player.<br/>
 *
 * <em>commands</em> - An array containing objects that define commands that
 * affect the display list or the Flash Player directly.<br/>
 *
 * <em>actions</em> - An array that define actions that are executed when a
 * frame is displayed.
 * </p>
 *
 * <p>
 * Frame objects simplify the handling of movies. DoAction, FrameLabel and
 * ShowFrame classes can now "hidden" from view. They are generated
 * automatically by the Frame object when it is added to an Movie object.
 * </p>
 *
 * <p>
 * The framesFromMovie(Movie aMovie) method allows an existing movie to be
 * viewed as an array of Frame objects. Objects from the movie are copied into
 * each frame so changes made to the attributes of each object are reflected in
 * the movie. The frame objects are not synchronised with the movie, so any
 * objects added to a frame are not added to the Movie. The easiest way to do
 * this is to remove the existing objects from the movie and add all the frames.
 * </p>
 *
 * <pre>
 * ArrayList frames = Frame.framesFromMovie(aMovie);
 * ...
 * ...
 * aMovie.getObjects().clear();
 *
 * for (Iterator i = frames.iterator(); i.hasNext();) {
 *     ((Frame)i.next()).addToMovie(aMovie);
 * }
 *</pre>
 *
 * <p>
 * When the contents of an Frame object is added to a movie if a label defined
 * then an FrameLabel object will be added. Similarly if actions are defined
 * then an DoAction object will be added. An ShowFrame object which instructs
 * the Flash Player to update the display list with all the changes is added.
 * </p>
 *
 */
//TODO(class)
public final class Frame {
    /**
     * Create a frame based view of a movie. Objects from the movie are grouped
     * into Frame objects. Objects from the movie are added to the frame so any
     * changes made are reflected in the movie. However objects added or removed
     * from a frame are not reflected in the movie.
     *
     * @param aMovie
     *            an Movie object.
     * @return an array of Frame objects.
     */
    public static List<Frame> split(final Movie aMovie) {
        final ArrayList<Frame> frames = new ArrayList<Frame>();
        int index = 1;
        Frame currentFrame = new Frame();

        for (final MovieTag currentObject : aMovie.getObjects()) {
            if (currentObject instanceof DoAction) {
                currentFrame.actions = ((DoAction) currentObject).getActions();
            } else if (currentObject instanceof FrameLabel) {
                currentFrame.label = ((FrameLabel) currentObject).getLabel();
            } else if (currentObject instanceof DefineTag) {
                currentFrame.addDefinition(currentObject);
            } else if (currentObject instanceof ShowFrame) {
                currentFrame.setNumber(index++);
                frames.add(currentFrame);
                currentFrame = new Frame();
            } else {
                currentFrame.addCommand(currentObject);
            }
        }
        return frames;
    }

    private String label;
    private int number;
    private List<MovieTag> definitions;
    private List<MovieTag> commands;
    private List<Action> actions;

    /**
     * Creates a empty frame with no label defined and the definitions, commands
     * and actions arrays empty.
     */
    public Frame() {
        definitions = new ArrayList<MovieTag>();
        commands = new ArrayList<MovieTag>();
        actions = new ArrayList<Action>();
    }

    /**
     * Creates a empty frame with no label defined and the definitions, commands
     * and actions arrays empty.
     *
     * @param frame the frame number.
     */
    public Frame(final int frame) {
        setNumber(frame);
        definitions = new ArrayList<MovieTag>();
        commands = new ArrayList<MovieTag>();
        actions = new ArrayList<Action>();
    }

    /**
     * Adds the action object to the frame.
     *
     * @param anObject
     *            the action object to be added to the frame. Must not be null.
     */
    public void addAction(final Action anObject) {
        if (anObject == null) {
            throw new IllegalArgumentException();
        }
        actions.add(anObject);
    }

    /**
     * Adds an object to the frame that defines an object to be displayed in the
     * movie.
     *
     * @param anObject
     *            a sub-class of Definition. Must not be null.
     */
    public void addDefinition(final MovieTag anObject) {
        if (anObject == null) {
            throw new IllegalArgumentException();
        }
        definitions.add(anObject);
    }

    /**
     * Adds the display list command to the frame.
     *
     * @param anObject
     *            an MovieTag the manipulates the display list. Must not be
     *            null.
     */
    public void addCommand(final MovieTag anObject) {
        if (anObject == null) {
            throw new IllegalArgumentException();
        }
        commands.add(anObject);
    }

    /**
     * Get the number of the frame.
     *
     * @return the frame number.
     */
    public int getNumber() {
        return number;
    }

    /**
     * Returns the label assigned to the frame.
     *
     * @return the label. The string will be empty if no label is defined.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the array of definition objects contained in the frame.
     *
     * @return the array of definitions.
     */
    public List<MovieTag> getDefinitions() {
        return definitions;
    }

    /**
     * Returns the array of commands that update the display list.
     *
     * @return the array of commands objects.
     */
    public List<MovieTag> getCommands() {
        return commands;
    }

    /**
     * Returns the array of action objects that will be execute when the frame
     * is displayed.
     *
     * @return the array of actions defined for the frame.
     */
    public List<Action> getActions() {
        return actions;
    }

    /**
     * Sets the number for the frame.
     *
     * @param frameNumber
     *            the frame number.
     */
    public void setNumber(final int frameNumber) {
        number = frameNumber;
    }

    /**
     * Sets the label for the frame.
     *
     * @param aString
     *            the label.
     */
    public void setLabel(final String aString) {
        label = aString;
    }

    /**
     * Sets the array of action objects for the frame.
     *
     * @param anArray
     *            the array of actions. Must not be null.
     */
    public void setDefinitions(final List<MovieTag> anArray) {
        if (anArray == null) {
            throw new IllegalArgumentException();
        }
        definitions = anArray;
    }

    /**
     * Sets the array of commands that updated the display list for the frame.
     * The changes are visible when the frame is displayed.
     *
     * @param anArray
     *            the array of command objects. Must not be null.
     */
    public void setCommands(final List<MovieTag> anArray) {
        if (anArray == null) {
            throw new IllegalArgumentException();
        }
        commands = anArray;
    }

    /**
     * Sets the array of action objects for the frame.
     *
     * @param anArray
     *            the array of actions. Must not be null.
     */
    public void setActions(final List<Action> anArray) {
        if (anArray == null) {
            throw new IllegalArgumentException();
        }
        actions = anArray;
    }

    /**
     * Add the objects in the frame to the movie. The contents of the
     * definitions and commands arrays are added to the movie. If a label is
     * assigned to the frame then an FrameLabel object is added to the movie. If
     * actions are defined then an DoAction object is added containing the
     * actions defined in the frame.
     *
     * @param aMovie
     *            an Movie object. Must not be null.
     */
    public void addToMovie(final Movie aMovie) {
        if (!definitions.isEmpty()) {
            for (final MovieTag object : definitions) {
                aMovie.add(object);
            }
        }

        if (label.length() > 0) {
            aMovie.add(new FrameLabel(label));
        }

        if (!actions.isEmpty()) {
            aMovie.add(new DoAction(actions));
        }

        for (final MovieTag object : commands) {
            aMovie.add(object);
        }

        aMovie.add(ShowFrame.getInstance());
    }
}
