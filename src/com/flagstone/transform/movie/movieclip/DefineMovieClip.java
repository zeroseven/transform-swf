/*
 * DefineMovieClip.java
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

package com.flagstone.transform.movie.movieclip;

import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.DefineTag;
import com.flagstone.transform.movie.MovieTag;
import com.flagstone.transform.movie.ShowFrame;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.Types;

/**
 * DefineMovieClip defines a movie clip that animates shapes within a movie. It 
 * contains an array of movie objects that define the placement of shapes,
 * buttons, text and images and the order in which they are displayed through a
 * time-line that is separate from the parent movie.
 * 
 * <p>Although a movie clip contains the commands that instructs the Flash Player
 * on how to animate the clip it cannot contain any new definitions of objects.
 * All definitions must be in the main movie. All objects referred to by the
 * movie clip must be also defined in the main movie before they can be used.
 * </p>
 * 
 * <p>
 * When using the DefineMovieClip object can only contain objects from the
 * following classes: ShowFrame, PlaceObject, PlaceObject2,
 * RemoveObject, RemoveObject2, DoAction, StartSound, FrameLabel,
 * SoundStreamHead, SoundStreamHead2 or SoundStreamBlock. Other objects
 * are not allowed.
 * </p>
 */
public final class DefineMovieClip implements DefineTag
{
	private static final String FORMAT = "DefineMovieClip: { identifier=%d; objects=%s }";
			
	protected List<MovieTag> objects;
	
	private transient int frameCount;

	private int identifier;
	
	private transient int start;
	private transient int end;
	private transient int length;

	public DefineMovieClip(final SWFDecoder coder, final SWFContext context) throws CoderException
	{
		start = coder.getPointer();
		length = coder.readWord(2, false) & 0x3F;
		
		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}
		end = coder.getPointer() + (length << 3);

		identifier = coder.readWord(2, false);
		frameCount = coder.readWord(2, false);
		objects = new ArrayList<MovieTag>();
		
		int type;
		
		do {
			type = coder.scanUnsignedShort() >>> 6;
		
			if (type != 0){
				objects.add(context.movieOfType(coder, context));
			}
		} while (type != 0);

		coder.adjustPointer(16);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}

	/**
	 * Creates a DefineMovieClip object with the unique identifier and
	 * array of movie objects.
	 * 
	 * @param uid
	 *            a unique identifier for the movie clip. Must be in the range 
	 *            1..65535,
	 * @param anArray
	 *            the array of movie objects. Must not be null.
	 */
	public DefineMovieClip(int uid, List<MovieTag> anArray)
	{
		setIdentifier(uid);
		setObjects(anArray);
	}
	
	public DefineMovieClip(DefineMovieClip object) {
		identifier = object.identifier;
		objects = new ArrayList<MovieTag>(object.objects.size());
		for (MovieTag tag : object.objects) {
			objects.add(tag.copy());
		}
	}
	
	public int getIdentifier() {
		return identifier;
	}

	public void setIdentifier(final int uid) {
		if (uid < 1 || uid > 65535) {
			throw new IllegalArgumentException(Strings.IDENTIFIER_OUT_OF_RANGE);
		}
		identifier = uid;
	}

	/**
	 * Adds the movie object to the array of objects that update the display
	 * list. See description above for the list of acceptable types.
	 * 
	 * @param obj
	 *            a Movie object. Must not be null
	 */
	public DefineMovieClip add(MovieTag obj)
	{
		if (obj == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		objects.add(obj);
		return this;
	}

	/**
	 * Returns the array of movie objects.
	 */
	public List<MovieTag> getObjects()
	{
		return objects;
	}

	/**
	 * Sets the array of movie objects.
	 * 
	 * @param anArray
	 *            the array of movie objects. Must not be null.
	 */
	public void setObjects(List<MovieTag> anArray)
	{
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		objects = anArray;
	}

	/**
	 * Creates and returns a deep copy of this object.
	 */
	public DefineMovieClip copy() 
	{
		return new DefineMovieClip(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, identifier, objects);
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context){
		
		frameCount = 0;
		length = 6;
		
		for (MovieTag object : objects) {
			length += object.prepareToEncode(coder, context);

			if (object instanceof ShowFrame) {
				frameCount += 1;
			}
		}
		return (length > 62 ? 6:2) + length;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
		start = coder.getPointer();

		if (length >= 63) {
			coder.writeWord((Types.DEFINE_MOVIE_CLIP << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((Types.DEFINE_MOVIE_CLIP << 6) | length, 2);
		}
		end = coder.getPointer() + (length << 3);
		
		coder.writeWord(identifier, 2);
		coder.writeWord(frameCount, 2);

		for (MovieTag object : objects) {
			object.encode(coder, context);
		}

		coder.writeWord(0, 2);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}
}
