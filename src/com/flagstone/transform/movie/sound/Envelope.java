/*
 * SoundInfo.java
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

package com.flagstone.transform.movie.sound;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.Encodeable;
import com.flagstone.transform.movie.Strings;

/**
 */
public final class Envelope implements Encodeable
{
	private List<SoundLevel> envelopes;
	
	private transient int count;

	public Envelope(final SWFDecoder coder, final SWFContext context) throws CoderException
	{
		count = coder.readByte();
		
		envelopes = new ArrayList<SoundLevel>(count);
		
		for (int i = 0; i < count; i++) {
			envelopes.add(new SoundLevel(coder, context));
		}
	}

	public Envelope(Envelope object)
	{
		envelopes = new ArrayList<SoundLevel>(object.envelopes);
	}

	/**
	 * Add a Envelope object to the array of envelope objects.
	 * 
	 * @param level
	 *            a SoundLevel object. Must not be null.
	 */
	public Envelope add(SoundLevel level)
	{
		if (level == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		envelopes.add(level);
		return this;
	}

	/**
	 * Returns the array of SoundLevels that control the volume of the sound.
	 */
	public List<SoundLevel> getEnvelopes()
	{
		return envelopes;
	}

	/**
	 * Sets the array of SoundLevel objects that define the levels at which a
	 * sound is played over the duration of the sound. May be set to null if no
	 * envelope is defined.
	 * 
	 * @param anArray
	 *            an array of Envelope objects. Must not be null.
	 */
	public void setEnvelopes(List<SoundLevel> anArray)
	{
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		envelopes = anArray;
	}

	/**
	 * Creates and returns a deep copy of this object.
	 */
	public Envelope copy()
	{
		return new Envelope(this);
	}

	@Override
	public String toString()
	{
		return envelopes.toString();
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context)
	{
		count = envelopes.size();		
		return 1 + (count << 3);
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
		coder.writeByte(count);

		for (SoundLevel level : envelopes) {
			level.encode(coder, context);
		}
	}
}
