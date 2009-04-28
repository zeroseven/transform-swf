/*
 * DefineMovieClip.java
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

package com.flagstone.transform.movieclip;

import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.DefineTag;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.coder.SWFFactory;

//TODO(doc) Review
/**
 * DefineMovieClip defines a movie clip that animates shapes within a movie. It
 * contains an array of movie objects that define the placement of shapes,
 * buttons, text and images and the order in which they are displayed through a
 * time-line that is separate from the parent movie.
 * 
 * <p>
 * Although a movie clip contains the commands that instructs the Flash Player
 * on how to animate the clip it cannot contain any new definitions of objects.
 * All definitions must be in the main movie. All objects referred to by the
 * movie clip must be also defined in the main movie before they can be used.
 * </p>
 * 
 * <p>
 * When using the DefineMovieClip object can only contain objects from the
 * following classes: ShowFrame, PlaceObject, PlaceObject2, RemoveObject,
 * RemoveObject2, DoAction, StartSound, FrameLabel, SoundStreamHead,
 * SoundStreamHead2 or SoundStreamBlock. Other objects are not allowed.
 * </p>
 */
public final class DefineMovieClip implements DefineTag {
    private static final String FORMAT = "DefineMovieClip: { identifier=%d; objects=%s }";

    private List<MovieTag> objects;

    private transient int frameCount;

    private int identifier;

    private transient int length;

    // TODO(doc)
    // TODO(optimise)
    public DefineMovieClip(final SWFDecoder coder, final Context context)
            throws CoderException {
        final int start = coder.getPointer();
        length = coder.readWord(2, false) & 0x3F;

        if (length == 0x3F) {
            length = coder.readWord(4, false);
        }
        final int end = coder.getPointer() + (length << 3);

        identifier = coder.readWord(2, false);
        frameCount = coder.readWord(2, false);
        objects = new ArrayList<MovieTag>();

        int type;
        final SWFFactory<MovieTag> decoder = context.getRegistry()
                .getMovieDecoder();

        do {
            type = coder.scanUnsignedShort() >>> 6;

            if (type != 0) {
                objects.add(decoder.getObject(coder, context));
            }
        } while (type != 0);

        coder.adjustPointer(16);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }

    /**
     * Creates a DefineMovieClip object with the unique identifier and array of
     * movie objects.
     * 
     * @param uid
     *            a unique identifier for the movie clip. Must be in the range
     *            1..65535,
     * @param anArray
     *            the array of movie objects. Must not be null.
     */
    public DefineMovieClip(final int uid, final List<MovieTag> anArray) {
        setIdentifier(uid);
        setObjects(anArray);
    }

    // TODO(doc)
    public DefineMovieClip(final DefineMovieClip object) {
        identifier = object.identifier;
        objects = new ArrayList<MovieTag>(object.objects.size());
        for (final MovieTag tag : object.objects) {
            objects.add(tag.copy());
        }
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final int uid) {
        if ((uid < 1) || (uid > 65535)) {
            throw new IllegalArgumentException(Strings.IDENTIFIER_RANGE);
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
    public DefineMovieClip add(final MovieTag obj) {
        if (obj == null) {
            throw new IllegalArgumentException(Strings.OBJECT_IS_NULL);
        }
        objects.add(obj);
        return this;
    }

    /**
     * Returns the array of movie objects.
     */
    public List<MovieTag> getObjects() {
        return objects;
    }

    /**
     * Sets the array of movie objects.
     * 
     * @param anArray
     *            the array of movie objects. Must not be null.
     */
    public void setObjects(final List<MovieTag> anArray) {
        if (anArray == null) {
            throw new IllegalArgumentException(Strings.ARRAY_IS_NULL);
        }
        objects = anArray;
    }

    /**
     * Creates and returns a deep copy of this object.
     */
    public DefineMovieClip copy() {
        return new DefineMovieClip(this);
    }

    @Override
    public String toString() {
        return String.format(FORMAT, identifier, objects);
    }

    public int prepareToEncode(final SWFEncoder coder, final Context context) {

        frameCount = 0;
        length = 6;

        for (final MovieTag object : objects) {
            length += object.prepareToEncode(coder, context);

            if (object instanceof ShowFrame) {
                frameCount += 1;
            }
        }
        return (length > 62 ? 6 : 2) + length;
    }

    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        final int start = coder.getPointer();

        if (length >= 63) {
            coder.writeWord((MovieTypes.DEFINE_MOVIE_CLIP << 6) | 0x3F, 2);
            coder.writeWord(length, 4);
        } else {
            coder.writeWord((MovieTypes.DEFINE_MOVIE_CLIP << 6) | length, 2);
        }
        final int end = coder.getPointer() + (length << 3);

        coder.writeWord(identifier, 2);
        coder.writeWord(frameCount, 2);

        for (final MovieTag object : objects) {
            object.encode(coder, context);
        }

        coder.writeWord(0, 2);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }
}
