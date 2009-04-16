/*
 * Layer.java
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

import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.movie.MovieTag;
import com.flagstone.transform.movie.Place2;
import com.flagstone.transform.movie.Remove2;
import com.flagstone.transform.movie.ShowFrame;
import com.flagstone.transform.movie.datatype.ColorTransform;
import com.flagstone.transform.movie.datatype.CoordTransform;

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
public final class Layer
{
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
	public static List<MovieTag> merge(List<Layer> layers)
	{
		ArrayList<MovieTag> objects = new ArrayList<MovieTag>();

		int[] idx = new int[layers.size()];

		for (int i = 0; i < idx.length; i++) {
			idx[i] = 0;
		}

		while (true)
		{
			List<MovieTag> array = null;
			MovieTag object;

			for (int i = 0; i < idx.length; i++)
			{
				array = (layers.get(i)).getObjects();

				while (idx[i] < array.size())
				{
					object = array.get(idx[i]++);

					if (object instanceof ShowFrame)
					{
						break;
					}

					objects.add(object);
				}

				if (idx[i] == array.size())
				{
					array = null;  // NOPMD
				}
			}
			objects.add(ShowFrame.getInstance());

			if (array == null)
			{
				break;
			}
		}

		return objects;
	}

	private final int layerNumber;
	private int identifier;
	private Place2 place;
	private final List<MovieTag> displayList;

	/**
	 * Create a new Layer object. Layers are used to define the order in which
	 * objects are displayed. Objects placed on a high layer number are
	 * displayed in front of those on a lower layer.
	 * 
	 * @param number
	 *            the layer number on the display list.
	 */
	public Layer(int number)
	{
		layerNumber = number;
		identifier = 0;
		displayList = new ArrayList<MovieTag>();
	}

	/**
	 * Return the layer number. The Flash Player assumes that there is only one
	 * object placed on each layer and so each must have a unique number.
	 * 
	 * @return the layer number.
	 */
	public int getLayer()
	{
		return layerNumber;
	}

	/**
	 * Return the identifier of the object on the layer. Note: it is possible to
	 * place more than one object on a layer however only one should be visible
	 * on the display list at a given time.
	 * 
	 * @return the unique identifier used to reference the object.
	 */
	public int getIdentifier()
	{
		return identifier;
	}

	/**
	 * Return the array of commands used to manipulate the object on the display
	 * list.
	 * 
	 * @return an array of MovieTag containing the definition of the object
	 *         (shape, text, etc) to be displayed and the associated commands
	 *         that update the position of the object on the screen.
	 */
	public List<MovieTag> getObjects()
	{
		return displayList;
	}

	/**
	 * Add an object to the layer. The object may be selected later for display.
	 * 
	 * @param definition
	 *            an object defining an image, shape, text, sound or video.
	 */
	public Layer add(MovieTag definition)
	{
		displayList.add(definition);
		return this;
	}

	/**
	 * Select the object previously added to the layer to be added on the
	 * display list. The object will be placed at the default coordinates (0,0).
	 * 
	 * IMPORTANT: You only need to use this method when adding an object to the
	 * display list for the first time or when re-adding it after it has been
	 * deleted. Since the display list has one layer per object then the layer
	 * number can be used to identify which object to update.
	 * 
	 * @param uid
	 *            an object definition, DefineShape, DefineImage, etc. that
	 *            will be added to the display list.
	 */
	public void select(int uid)
	{
		identifier = uid;
		place = new Place2(uid, layerNumber, 0, 0);
		displayList.add(place);
	}

	/**
	 * Add an object to the later and select it to be added on the display list.
	 * 
	 * @param definition
	 *            an object definition, DefineShape, DefineImage, etc. that
	 *            will be added to the display list.
	 */
	public void select(MovieTag definition)
	{
		add(definition);
		//TODO select(definition.getIdentifier());
	}

	/**
	 * Set the coordinates where the object will be displayed.
	 * 
	 * @param xCoord
	 *            the x-coordinate, expressed in twips.
	 * @param yCoord
	 *            the y-coordinate, expressed in twips.
	 */
	public void move(int xCoord, int yCoord)
	{
		if (place == null)
		{
			place = new Place2(layerNumber, xCoord, yCoord);
			displayList.add(place);
		} else
		{
			place.setTransform(CoordTransform.translate(xCoord, yCoord));
		}
	}

	/**
	 * Change the colour of the object to the values (r,g,b). The colour will be
	 * opaque so you cannot use this method to change only the colour of a
	 * transparent object.
	 * 
	 * @param red
	 *            the red component of the colour.
	 * @param green
	 *            the green component of the colour.
	 * @param blue
	 *            the blue component of the colour.
	 */
	public void color(int red, int green, int blue)
	{
		color(red, green, blue, 255);
	}

	/**
	 * Change the colour of the object to the values (r,g,b,a).
	 * 
	 * @param red
	 *            the red component of the colour.
	 * @param green
	 *            the green component of the colour.
	 * @param blue
	 *            the blue component of the colour.
	 * @param alpha
	 *            the transparency component.
	 */
	public void color(int red, int green, int blue, int alpha)
	{
		ColorTransform transform = new ColorTransform(red, green, blue, alpha);
		
		if (place == null)
		{
			Place2.Builder builder = new Place2.Builder();
			builder.layer(layerNumber).colorTransform(transform);
			displayList.add(builder.build());
		} else
		{
			place.setColorTransform(transform);
		}
	}

	/**
	 * Set the number of layers that the outline of the object will clip when
	 * placed on the display list.
	 * 
	 * @param depth
	 *            the number of layers that will be clipped.
	 */
	public void clip(int depth)
	{
		if (place == null)
		{
			Place2.Builder builder = new Place2.Builder();
			builder.layer(layerNumber).clippingDepth(depth);
			displayList.add(builder.build());
		} else
		{
			place.setClippingDepth(depth);
		}
	}

	/**
	 * Set the point in the morphing process for a morph shape in the range 0.0
	 * to 1.0.
	 * 
	 * @param ratio
	 *            the ratio between the starting shape and the end shape.
	 */
	public void morph(float ratio)
	{
		if (place == null)
		{
			Place2.Builder builder = new Place2.Builder();
			builder.layer(layerNumber).ratio((int)(ratio*65535.0f));
			displayList.add(builder.build());
		} else
		{
			place.setRatio((int)(ratio*65535.0f));
		}
	}

	/**
	 * Assign a name to the object.
	 * 
	 * @param name
	 *            a string that can be used to reference the object.
	 */
	public void name(String name)
	{
		if (place == null)
		{
			Place2.Builder builder = new Place2.Builder();
			builder.layer(layerNumber).name(name);
			displayList.add(builder.build());
		} else
		{
			place.setName(name);
		}
	}

	/**
	 * Remove the object from the display list.
	 */
	public void remove()
	{
		if (place != null) {
			place = null; // NOPMD
		}

		displayList.add(new Remove2(layerNumber));
	}

	/**
	 * Replace the object on the display list.
	 * 
	 * @param uid the unique identifier used to reference the object.
	 */
	public void replace(int uid)
	{
		identifier = uid;

		if (place == null)
		{
			Place2.Builder builder = new Place2.Builder();
			builder.layer(layerNumber).mode(Place2.Mode.REPLACE);
			displayList.add(builder.build());
		} else
		{
			place.setIdentifier(uid);
			place.setPlaceType(Place2.Mode.REPLACE);
		}
	}

	/**
	 * Change the colour of the object by applying a color transform.
	 * 
	 * @param color
	 *            the color transform to apply to the shape.
	 */
	public void change(ColorTransform color)
	{
		if (place == null)
		{
			Place2.Builder builder = new Place2.Builder();
			builder.layer(layerNumber).colorTransform(color);
			displayList.add(builder.build());
		} else
		{
			place.setColorTransform(color);
		}
	}

	/**
	 * Change the position, orientation and scaling of the object by applying a
	 * 2D coordinate transform.
	 * 
	 * Use this method to apply more complex coordinate transforms to an object
	 * for example that composite move, scaling and rotation operations in a
	 * single step.
	 * 
	 * @param coord
	 *            the coordinate transform to apply to the object.
	 */
	public void change(CoordTransform coord)
	{
		if (place == null)
		{
			Place2.Builder builder = new Place2.Builder();
			builder.layer(layerNumber).transform(coord);
			displayList.add(builder.build());
		} else
		{
			place.setTransform(coord);
		}
	}

	/**
	 * Instruct the Flash Player to display a frame and render the contents of
	 * the display list, applying the commands previously set. You must use this
	 * method for any of the other operations to take effect.
	 * 
	 */
	public void show()
	{
		show(1);
	}

	/**
	 * Display one or more frames. The state (position, orientation, visibility,
	 * etc.) of the object will remain unchanged for the duration of the added
	 * frames.
	 * 
	 * @param count
	 *            the number of frames to be displayed.
	 * @throws IllegalArgumentException
	 *             is the number of frames is less than 1.
	 */
	public void show(int count)
	{
		if (count < 1)
		{
			throw new IllegalArgumentException("Must show at least one frame");
		}

		if (place != null) {
			place = null; // NOPMD
		}

		for (int i = 0; i < count; i++)
		{
			displayList.add(ShowFrame.getInstance());
		}
	}
}
