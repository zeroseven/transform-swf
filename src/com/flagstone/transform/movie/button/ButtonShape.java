/*
 * Button.java
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

package com.flagstone.transform.movie.button;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.Encodeable;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.Types;
import com.flagstone.transform.movie.datatype.Blend;
import com.flagstone.transform.movie.datatype.ColorTransform;
import com.flagstone.transform.movie.datatype.CoordTransform;
import com.flagstone.transform.movie.filter.Filter;

/**
 * <p>ButtonShape identifies the shape that is drawn when a button is in a 
 * particular state. Shapes can be drawn for each of three button states, Over,
 * Up and Down allowing simple animations to be created when a button is 
 * clicked.</p> 
 * 
 * <p>A shape is also used to define active area of the button. When defining 
 * the active area the outline of the shape defines the boundary of the area,
 * the shape itself is not displayed. The button will only respond to mouse 
 * events when the cursor is placed inside the active area.</p>
 * 
 * <p>An shape can be used more than one state. Multiple states can be defined 
 * by bitwise Or-ing individual state codes together:
 * </p>
 * 
 * <pre>
 * int buttonState = Button.Up | Button.Over;
 * </pre>
 * 
 * <p>The order in which shapes are displayed is controlled by the layer number.
 * As with the Flash Player's display list shapes on a layer with a higher number 
 * are displayed in front of ones on a layer with a lower number. A coordinate
 * and color transform can also be applied to each shape to change its 
 * appearance when it is displayed when the button enters the specified state.</p>
 * 
 * @see DefineButton
 * @see DefineButton2
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class ButtonShape implements Encodeable
{
	private static final String FORMAT = "ButtonShape: { state=%d; identifier=%d; layer=%d; transform=%s; colorTransform=%s }";
		
	private int state;
	private int identifier;
	private int layer;
	private CoordTransform transform;
	private ColorTransform colorTransform;
	private List<Filter> filters;
	private Blend blendMode;

	protected ButtonShape(final SWFDecoder coder) throws CoderException
	{
		coder.readBits(4, false);

		state = coder.readBits(4, false);
		identifier = coder.readWord(2, false);
		layer = coder.readWord(2, false);
		transform = new CoordTransform(coder);

		if (coder.getContext().getType() == Types.DEFINE_BUTTON_2) {
			colorTransform = new ColorTransform(coder);
		}
	}

	/**
	 * Creates an ButtonShape object with a coordinate transform that will be
	 * applied to the shape drawn for the button states.
	 * 
	 * @param aState
	 *            the state of the button when the shape is drawn. The compound
	 *            state code must be in the range 1..15. 
	 * @param uid
	 *            the unique identifier of an DefineShape, DefineShape2 or
	 *            DefineShape3 object. Must be in the range 1..65535.
	 * @param aLayer
	 *            the layer in the display list on which the shape is drawn.
	 *            Must be in the range 1..65535.
	 * @param aTransform
	 *            a coordinate transform that changes the appearance of the
	 *            shape when it is drawn.
	 */
	public ButtonShape(Set<Button.State> states, int uid, int aLayer, CoordTransform aTransform)
	{
		setState(states);
		setIdentifier(uid);
		setLayer(aLayer);
		setTransform(aTransform);
	}

	/**
	 * Creates a ButtonShape object with a coordinate and colour transform that
	 * will be applied to the shape drawn for the button states.
	 * 
	 * IMPORTANT: Only DefineButton2 makes use of the ColorTransform. The colour
	 * transform will not be encoded if the ButtonShape is used for a DefineButton
	 * object.
	 * 
	 * @param states
	 *            the state of the button when the shape is drawn. The compound
	 *            state code must be in the range 1..15. 
	 * @param uid
	 *            the unique identifier of an DefineShape, DefineShape2 or
	 *            DefineShape3 object. Must be in the range 1..65535.
	 * @param aLayer
	 *            the layer in the display list on which the shape is drawn.
	 *            Must be in the range 1..65353.
	 * @param aTransform
	 *            an CoordTransform object that changes the appearance of the
	 *            shape when it is drawn. Must not be null.
	 * @param aColorTransform
	 *            an ColorTransform object that changes the colour of the
	 *            shape when it is drawn. Must not be null.
	 */
	public ButtonShape(Set<Button.State>states, int uid, int aLayer, CoordTransform aTransform, ColorTransform aColorTransform)
	{
		setState(states);
		setIdentifier(uid);
		setLayer(aLayer);
		setTransform(aTransform);
		setColorTransform(aColorTransform);
	}
	
	public ButtonShape(ButtonShape object) {
		state = object.state;
		identifier = object.identifier;
		layer = object.layer;
		transform = object.transform.copy();
		colorTransform = object.colorTransform.copy();
	}

	public Set<Button.State> getState()
	{
		Set<Button.State>set = EnumSet.allOf(Button.State.class);
		
		for (Iterator<Button.State>iter = set.iterator(); iter.hasNext();) {
			if ((state & iter.next().getValue()) == 0) {
				iter.remove();
			}
		}
		return set;
	}

	public void setState(Set<Button.State>states)
	{
		for (Button.State state : states) {
			this.state |= state.getValue();
		}
	}

	/**
	 * Return the unique identifier of the shape that this Button applies to.
	 */
	public int getIdentifier()
	{
		return identifier;
	}

	/**
	 * Sets the unique identifier of the DefineShape, DefineShape2 or
	 * DefineShape3 object that defines the appearance of the button when it
	 * is in the specified state(s).
	 * 
	 * @param uid
	 *            the unique identifier of the shape object that defines the shape's
	 *            appearance. Must be in the range 1..65535.
	 */
	public void setIdentifier(int uid)
	{
		if (uid < 1 || uid > 65535) {
			throw new IllegalArgumentException(Strings.IDENTIFIER_OUT_OF_RANGE);
		}
		identifier = uid;
	}

	/**
	 * Returns the layer that the button will be displayed on.
	 */
	public int getLayer()
	{
		return layer;
	}

	/**
	 * Sets the layer in the display list that the shape will be displayed on.
	 * 
	 * @param aNumber
	 *            the number of the layer in the display list where the shape is
	 *            drawn. Must be in the range 1..65535.
	 */
	public void setLayer(int aNumber)
	{
		if (aNumber < 0 || aNumber > 65535) {
			throw new IllegalArgumentException(Strings.LAYER_OUT_OF_RANGE);
		}
		layer = aNumber;
	}

	/**
	 * Returns the coordinate transform that will be applied to the button.
	 */
	public CoordTransform getTransform()
	{
		return transform;
	}

	/**
	 * Sets the coordinate transform that will be applied to the shape to change
	 * it's appearance.
	 * 
	 * @param aTransform
	 *            an CoordTransform object that will be applied to the shape.
	 *            Must not be null.
	 */
	public void setTransform(CoordTransform aTransform)
	{
		if (aTransform == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		transform = aTransform;
	}

	/**
	 * Returns the colour transform that will be applied to the button.
	 * 
	 * Note that the colour transform will only be used if the ButtonShape is 
	 * added to a DefineButton2 object.
	 */
	public ColorTransform getColorTransform()
	{
		return colorTransform;
	}

	/**
	 * Sets the colour transform that will be applied to the shape to change
	 * it's colour.
	 * 
	 * IMPORTANT: The colour transform is only used in DefineButton2 objects.
	 * 
	 * @param aTransform
	 *            an ColorTransform object that will be applied to the shape.
	 *            Must not be null, even if the ButtonShape will be added to a 
	 *            DefineButton object.
	 */
	public void setColorTransform(ColorTransform aTransform)
	{
		if (aTransform == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		colorTransform = aTransform;
	}

	public void add(Filter filter)
	{
		if (filter == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		filters.add(filter);
	}

	public List<Filter> getFilters()
	{
		return filters;
	}

	public void setFilters(List<Filter> array)
	{
		if (array == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		filters = array;
	}

	public Blend getBlendMode()
	{
		return blendMode;
	}

	public void setBlendMode(Blend blend)
	{
		blendMode = blend;
	}

	/**
	 * Creates and returns a deep copy of this object.
	 */
	public ButtonShape copy()
	{
		return new ButtonShape(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, state, identifier, layer, transform, colorTransform);
	}

	public int prepareToEncode(final SWFEncoder coder)
	{
		int length = 5 + transform.prepareToEncode(coder);

		if (coder.getContext().getType() == Types.DEFINE_BUTTON_2) {
			length += colorTransform.prepareToEncode(coder);
		}

		return length;
	}

	public void encode(final SWFEncoder coder) throws CoderException
	{
		coder.writeBits(0, 4);
		coder.writeBits(state, 4);
		coder.writeWord(identifier, 2);
		coder.writeWord(layer, 2);
		transform.encode(coder);

		if (coder.getContext().getType() == Types.DEFINE_BUTTON_2) {
			colorTransform.encode(coder);
		}
	}
}
