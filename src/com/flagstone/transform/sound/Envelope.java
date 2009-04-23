/*
 * SoundInfo.java
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

package com.flagstone.transform.sound;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFEncodeable;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.font.Kerning;

/**
 */
//TODO(doc)
public final class Envelope implements SWFEncodeable
{
	/**
	 * Envelope is used to define an envelope which controls how a particular sound
	 * is played over time.
	 * 
	 * <p>
	 * Each Envelope object contains a sample number in the audio <b>when it is
	 * played</b> where the envelope will be applied along with the sound levels for
	 * the left and right channels.
	 * </p>
	 * 
	 * <p>
	 * The Flash Player plays sounds at a fixed rate of 44.1KHz, therefore sounds
	 * sampled at a lower frequency are interpolated with each sample repeated to
	 * generated the 44.1Khz playback rate. For example each sample in a sound
	 * sampled at 22KHz is played twice to generated the 44.1Khz playback rate.
	 * </p>
	 * 
	 * <p>
	 * The envelope defines the sample number (and hence the time) in the playback
	 * data stream where the level information applies and <b>not</b> the sample
	 * number in the original sound data. For example to set the level 0.1 seconds
	 * into a sound that plays for 1 second the value for the mark attribute in the
	 * envelope object would be 44100 * 0.1/1.0 = 4410.
	 * </p>
	 * 
	 * @see SoundInfo
	 */
	public static class Level implements SWFEncodeable {
		
		private static final String FORMAT = "Envelope: { mark=%d; left=%d; right=%d; }";
		
		private final transient int mark;
		private final transient int left;
		private final transient int right;

		public Level(final SWFDecoder coder, final Context context) throws CoderException {
			mark = coder.readWord(4, false);
			left = coder.readWord(2, false);
			right = coder.readWord(2, false);
		}

		/**
		 * Creates a envelope specifying the mark, left and right sound levels.
		 * 
		 * @param markValue
		 *            the sample number in the 44.1KHz playback data stream where
		 *            the levels for the channels is applied.
		 * @param leftValue
		 *            the level for the left sound channel, in the range 0..65535.
		 * @param rightValue
		 *            the level for the right sound channel, in the range 0..65535.
		 */
		public Level(int markValue, int leftValue, int rightValue) {

			mark = markValue;
			
			if (leftValue < 0 || leftValue > 65535) {
				throw new IllegalArgumentException(Strings.UNSIGNED_VALUE_OUT_OF_RANGE);
			}
			left = leftValue;
			
			if (rightValue < 0 || rightValue > 65535) {
				throw new IllegalArgumentException(
						Strings.UNSIGNED_VALUE_OUT_OF_RANGE);
			}
			right = rightValue;
		}

		/**
		 * Returns the sample number in the 44.1KHz playback data stream where the
		 * level information is applied.
		 */
		public int getMark() {
			return mark;
		}

		/**
		 * Returns the level of the sound played in the left channel.
		 */
		public int getLeft() {
			return left;
		}

		/**
		 * Returns the level of the sound played in the right channel.
		 */
		public int getRight() {
			return right;
		}

		@Override
		public String toString() {
			return String.format(FORMAT, mark, left, right);
		}
		
		@Override
		public boolean equals(final Object object) {
			boolean result;
			Level level;
			
			if (object == null) {
				result = false;
			} else if (object == this) {
				result = true;
			} else if (object instanceof Level) {
				level = (Level)object;
				result = mark == level.mark && left == level.left && 
					right == level.right;
			} else {
				result = false;
			}
			return result;
		}
		
		@Override
		public int hashCode() {
			return ((mark*31) + left)* 31 + right;
		}

		public int prepareToEncode(final SWFEncoder coder, final Context context) {
			return 8;
		}

		public void encode(final SWFEncoder coder, final Context context) throws CoderException {
			coder.writeWord(mark, 4);
			coder.writeWord(left, 2);
			coder.writeWord(right, 2);
		}
	}

	private List<Level> levels;
	
	private transient int count;

	//TODO(doc)
	public Envelope(final SWFDecoder coder, final Context context) throws CoderException
	{
		count = coder.readByte();
		
		levels = new ArrayList<Level>(count);
		
		for (int i = 0; i < count; i++) {
			levels.add(new Level(coder, context));
		}
	}

	//TODO(doc)
	public Envelope(Envelope object)
	{
		levels = new ArrayList<Level>(object.levels);
	}

	/**
	 * Add a Envelope object to the array of envelope objects.
	 * 
	 * @param level
	 *            a SoundLevel object. Must not be null.
	 */
	public Envelope add(Level level)
	{
		if (level == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		levels.add(level);
		return this;
	}

	/**
	 * Returns the array of SoundLevels that control the volume of the sound.
	 */
	public List<Level> getLevels()
	{
		return levels;
	}

	/**
	 * Sets the array of SoundLevel objects that define the levels at which a
	 * sound is played over the duration of the sound. May be set to null if no
	 * envelope is defined.
	 * 
	 * @param anArray
	 *            an array of Envelope objects. Must not be null.
	 */
	public void setLevels(List<Level> anArray)
	{
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		levels = anArray;
	}

	public Envelope copy()
	{
		return new Envelope(this);
	}

	@Override
	public String toString()
	{
		return levels.toString();
	}

	public int prepareToEncode(final SWFEncoder coder, final Context context)
	{
		count = levels.size();		
		return 1 + (count << 3);
	}

	public void encode(final SWFEncoder coder, final Context context) throws CoderException
	{
		coder.writeByte(count);

		for (Level level : levels) {
			level.encode(coder, context);
		}
	}
}
