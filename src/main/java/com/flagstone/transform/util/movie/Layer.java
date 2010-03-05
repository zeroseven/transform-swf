/*
 * Layer.java
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

import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.Place2;
import com.flagstone.transform.PlaceType;
import com.flagstone.transform.Remove2;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.coder.DefineTag;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.datatype.ColorTransform;
import com.flagstone.transform.datatype.CoordTransform;

/**
 * The Layer class can be used to simplify the creation of movies. It provides a
 * series of methods that can be used to control how an object is displayed and
 * provides an API that is easier to use when compared to creating the commands
 * (PlaceObject, RemoveObject, etc.) used to manipulate the Flash Player's
 * display list directly. The following code:
 *
 * <pre>
 * Layer layer = new Layer(1);
 *
 * layer.select(shape);
 * layer.move(x1, y1);
 * layer.show();
 * layer.move(x2, y2);
 * layer.show();
 *
 * movie.add(layer.getObjects());
 * </pre>
 *
 * is equivalent to:
 *
 * <pre>
 * movie.add(shape);
 * movie.add(new PlaceObject2(shape.getIdentifier, 1, x1, y1));
 * movie.add(ShowFrame.getInstance());
 * movie.add(new PlaceObject2(1, x2, y2));
 * movie.add(ShowFrame.getInstance());
 * </pre>
 *
 * After each set of commands the display list is updated by executing the
 * show() method - this adds a ShowFrame instruction to the final movie which
 * tells the Flash Player to render the display list on the screen.
 *
 * The select() method is only used when displaying an object for the first time
 * or re-displaying it after it was deleted from the display list.
 *
 * The commands that manipulate the display list can also be combined to apply
 * several operations at once:
 *
 * <pre>
 * layer.select(shape);
 * layer.move(x, y);
 * layer.morph(0.9);
 * layer.color(r, g, b);
 * layer.show();
 * </pre>
 *
 * is equivalent to:
 *
 * <pre>
 * CoordTransform coord = new CoordTransform(x, y);
 * ColorTransform color = new ColorTransform(r, g, b);
 * PlaceObject2 place = new PlaceObject2(shape.getIdentifier, 1, coord, color)
 * place.setRatio(0.9);
 *
 * movie.add(shape);
 * movie.add(place);
 * movie.add(ShowFrame.getInstance());
 * </pre>
 *
 * An operation is use to set the attributes on either an PlaceObject2 or
 * RemoveObject2 object so operations of the same type cannot be combined to
 * create a cumulative effect. For example:
 *
 * <pre>
 * layer.move(x1, y1);
 * layer.move(x2, y2);
 * </pre>
 *
 * is the same as:
 *
 * <pre>
 * layer.move(x2, y2);
 * </pre>
 *
 * and not:
 *
 * <pre>
 * layer.move(x1 + x2, y1 + y2);
 * </pre>
 *
 * The most obvious benefit is code that is easier to write and read however the
 * benefits of using layers come to the fore when creating movies with multiple
 * objects. Currently the movie object represents the main time-line and the
 * commands to control and display each object must be interleaved together.
 * This quickly becomes unwieldy and error prone if several objects are
 * involved. With layers, each can be regarded as the time-line for a single
 * object. The object can then be manipulated more easily and the final set of
 * Layers merged together to create a single time-line. The only limitation in
 * the merging process is that all the Layers must start at the same point in
 * time.
 *
 * Each Layer object created must be assigned a unique number. In Flash an
 * object to be displayed is assigned to a given layer with (typically) only one
 * object displayed on a given layer. The layer number is used to control the
 * order in which the objects are displayed. Objects placed on a higher layer
 * number are displayed in front of object placed on a lower layer number.
 *
 */
//TODO(class)
public final class Layer {
    /**
     * Merge layers together to create a single time-line. Each layer is assumed
     * to start at the same point in time. The process steps through each of the
     * layers, frame by frame, adding all the commands used to manipulate the
     * Flash Player's display list into a single group.
     *
     * @param layers
     *            and array of Layer objects.
     *
     * @return an array of all the objects contained in each layer. This array
     *         can then be added to the movie.
     */
    public static List<Frame> merge(final List<Layer> layers) {
        
        int lastFrame = 0;
        
        for (Layer layer : layers) {
            for (Frame frame : layer.getFrames()) {
                if (frame.getNumber() > lastFrame) {
                    lastFrame = frame.getNumber();
                }
            }
        }
         
        final ArrayList<Frame> merged = new ArrayList<Frame>(lastFrame);
        
        for (int i=1; i<=lastFrame; i++) {
            merged.add(new Frame(i));
        }
        
        Frame selected;
        
        for (Layer layer : layers) {
            for (Frame frame : layer.getFrames()) {
                selected = merged.get(frame.getNumber()-1);
                
                selected.setActions(frame.getActions());
                selected.setCommands(frame.getCommands());
                selected.setDefinitions(frame.getDefinitions());
                
                if (frame.getLabel() != null) {
                    selected.setLabel(frame.getLabel());
                }
            }
        }
        return merged;
    }

    private final int layerNumber;
    private final List<Frame> frames;

    /**
     * Create a new Layer object. Layers are used to define the order in which
     * objects are displayed. Objects placed on a high layer number are
     * displayed in front of those on a lower layer.
     *
     * @param number
     *            the layer number on the display list.
     */
    public Layer(final int number) {
        layerNumber = number;
        frames = new ArrayList<Frame>();
    }

    /**
     * Return the layer number. The Flash Player assumes that there is only one
     * object placed on each layer and so each must have a unique number.
     *
     * @return the layer number.
     */
    public int getLayer() {
        return layerNumber;
    }

    /**
     * TODO(method).
     */
    public List<Frame> getFrames() {
        return frames;
    }

    /**
     * Add a frame to the layer. The object may be selected later for display.
     *
     * @param frame
     *            a Frame object.
     */
    public Layer add(final Frame frame) {
        frames.add(frame);
        frame.setNumber(frames.size());
        return this;
    }
}
