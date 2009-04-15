/*
 * Envelope.java
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

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.Encodeable;
import com.flagstone.transform.movie.Strings;

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
public final class Envelope implements Encodeable {
	
	private static final String FORMAT = "Envelope: { mark=%d; left=%d; right=%d; }";
	
	protected int mark;
	protected int left;
	protected int right;

	public Envelope(final SWFDecoder coder) throws CoderException {
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
	public Envelope(int markValue, int leftValue, int rightValue) {
		setMark(markValue);
		setLeft(leftValue);
		setRight(rightValue);
	}

	public Envelope(Envelope object) {
		mark = object.mark;
		left = object.left;
		right = object.right;
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

	/**
	 * Sets the sample number in the 44.1KHz playback data stream where the
	 * levels for the channels is applied.
	 * 
	 * @param aNumber
	 *            the mark value.
	 */
	public void setMark(int aNumber) {
		mark = aNumber;
	}

	/**
	 * Sets the level for the left sound channel.
	 * 
	 * @param level
	 *            the level for the left sound channel. Must be in the range
	 *            0..65535.
	 */
	public void setLeft(int level) {
		if (level < 0 || level > 65535) {
			throw new IllegalArgumentException(
					Strings.UNSIGNED_VALUE_OUT_OF_RANGE);
		}
		left = level;
	}

	/**
	 * Sets the level for the right sound channel.
	 * 
	 * @param level
	 *            the level for the right sound channel. Must be in the range
	 *            0..65535.
	 */
	public void setRight(int level) {
		if (level < 0 || level > 65535) {
			throw new IllegalArgumentException(
					Strings.UNSIGNED_VALUE_OUT_OF_RANGE);
		}
		right = level;
	}

	public Envelope copy() {
		return new Envelope(this);
	}

	@Override
	public String toString() {
		return String.format(FORMAT, mark, left, right);
	}

	public int prepareToEncode(final SWFEncoder coder) {
		return 8;
	}

	public void encode(final SWFEncoder coder) throws CoderException {
		coder.writeWord(mark, 4);
		coder.writeWord(left, 2);
		coder.writeWord(right, 2);
	}
}
