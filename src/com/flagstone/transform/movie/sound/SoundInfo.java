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
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.Codeable;
import com.flagstone.transform.movie.Strings;

/**
 * SoundInfo identifies a sound (previously defined using The
 * DefineSound class) and controls how it is played.
 * 
 * <p>SoundInfo defines how the sound fades in and out, whether it is repeated as
 * well as specifying an envelope that provides a finer degree of control over
 * the levels at which the sound is played.</p>
 * 
 * <p>The in and out point specify the sample number which marks the point in 
 * time at which the sound stops increasing or starts decreasing in volume 
 * respectively. Sounds are played by the Flash player at 44.1KHz so the sample
 * number also indicates the time when the total number of samples in the sound
 * is taken into account.</p>
 * 
 * <p>Not all the attributes are required to play a sound. Only the identifier and
 * the mode is required. The other attributes are optional and may be added as a
 * greater degree of control is required. The inPoint and outPoint attributes
 * may be set to zero if the sound does not fade in or out respectively. The
 * loopCount may be set to zero if a sound is being stopped. The envelopes array
 * may be left empty if no envelope is defined for the sound. The class provides
 * different constructors to specify different sets of attributes.
 * </p>
 * 
 * @see DefineSound
 */
public final class SoundInfo implements Codeable
{
	private static final String FORMAT = "SoundInfo: { identifier=%d; mode=%s; inPoint=%d; outPoint=%d; loopCount=%d; envelopes=%s; }";
	
	public enum Mode {
		/** Start playing the sound. */
		START(0),
		/**  Start playing the sound or continues if it is already playing. */
		CONTINUE(1),
		/** Stop playing the sound. */
		STOP(2);

		private static final Map<Integer,Mode>table 
			= new LinkedHashMap<Integer,Mode>();

		static {
			for (Mode encoding : values()) {
				table.put(encoding.value, encoding);
			}
		}
		
		public static Mode fromInt(int type) {
			return table.get(type);
		}

		private final int value;

		private Mode(int value) {
			this.value = value;
		}
		
		public int getValue()
		{
			return value;
		}
	}

	protected int identifier;
	protected Mode mode;
	protected int inPoint;
	protected int outPoint;
	protected int loopCount;
	protected List<Envelope> envelopes;

	public SoundInfo(final SWFDecoder coder) throws CoderException
	{
		identifier = coder.readWord(2, false);
		mode = Mode.fromInt(coder.readBits(4, false));
		boolean hasEnvelopes = coder.readBits(1, false) != 0;
		boolean hasLoopCount = coder.readBits(1, false) != 0;
		boolean hasOutPoint = coder.readBits(1, false) != 0;
		boolean hasInPoint = coder.readBits(1, false) != 0;

		if (hasInPoint) {
			inPoint = coder.readWord(4, false);
		}

		if (hasOutPoint) {
			outPoint = coder.readWord(4, false);
		}

		if (hasLoopCount) {
			loopCount = coder.readWord(2, false);
		}

		envelopes = new ArrayList<Envelope>();
		if (hasEnvelopes)
		{
			int envelopeCount = coder.readByte();
			
			for (int i = 0; i < envelopeCount; i++) {
				envelopes.add(new Envelope(coder));
			}
		}
	}

	/**
	 * Creates ad Sound object specifying how the sound is played and the
	 * number of times the sound is repeated.
	 * 
	 * @param uid
	 *            the unique identifier of the object that contains the sound
	 *            data.
	 * @param aMode
	 *            how the sound is synchronised when the frames are displayed:
	 *            Play - do not play the sound if it is already playing and Stop -
	 *            stop playing the sound.
	 * @param aCount
	 *            the number of times the sound is repeated. May be set to zero
	 *            if the sound will not be repeated.
	 */
	public SoundInfo(int uid, Mode aMode, int aCount, List<Envelope> array)
	{
		setIdentifier(uid);
		setMode(aMode);
		setLoopCount(aCount);
		setEnvelopes(array);
	}

	public SoundInfo(SoundInfo object)
	{
		identifier = object.identifier;
		mode = object.mode;
		loopCount = object.loopCount;
		inPoint = object.inPoint;
		outPoint = object.outPoint;
		
		envelopes = new ArrayList<Envelope>(object.envelopes.size());
		
		for (Envelope envelope : object.envelopes) {
			envelopes.add(envelope.copy());
		}
	}

	/**
	 * Add a Envelope object to the array of envelope objects.
	 * 
	 * @param anEnvelope
	 *            an Envelope object. Must not be null.
	 */
	public void add(Envelope anEnvelope)
	{
		envelopes.add(anEnvelope);
	}

	/**
	 * Returns the identifier of the sound to the played.
	 */
	public int getIdentifier()
	{
		return identifier;
	}

	/**
	 * Returns the synchronisation mode: START - start playing the sound, 
	 * CONTINUE - do not play the sound if it is already playing and STOP - stop 
	 * playing the sound.
	 */
	public Mode getMode()
	{
		return mode;
	}

	/**
	 * Returns the sample number at which the sound reaches full volume when 
	 * fading in.
	 */
	public int getInPoint()
	{
		return inPoint;
	}

	/**
	 * Returns the sample number at which the sound starts to fade.
	 */
	public int getOutPoint()
	{
		return outPoint;
	}

	/**
	 * Returns the number of times the sound will be repeated.
	 */
	public int getLoopCount()
	{
		return loopCount;
	}

	/**
	 * Returns the array of Envelope objects that control the levels the sound is
	 * played.
	 */
	public List<Envelope> getEnvelopes()
	{
		return envelopes;
	}

	/**
	 * Sets the identifier of the sound to the played.
	 * 
	 * @param uid
	 *            the identifier for the sound to be played. Must be in the 
	 *            range 1..65535.
	 */
	public void setIdentifier(int uid)
	{
		if (uid < 0 || uid > 65535) {
			throw new IllegalArgumentException(Strings.IDENTIFIER_OUT_OF_RANGE);
		}
		identifier = uid;
	}

	/**
	 * Sets how the sound is synchronised when the frames are displayed: START -
	 * start playing the sound, CONTINUE - do not play the sound if it is
	 * already playing and STOP - stop playing the sound.
	 * 
	 * @param aMode
	 *            how the sound is played.
	 */
	public void setMode(Mode mode)
	{
		this.mode = mode;
	}

	/**
	 * Sets the sample number at which the sound reaches full volume when fading
	 * in. May be set to zero if the sound does not fade in.
	 * 
	 * @param aNumber
	 *            the sample number which the sound fades in to.
	 */
	public void setInPoint(int aNumber)
	{
		if (aNumber < 0 || aNumber > 65535) {
			throw new IllegalArgumentException(Strings.UNSIGNED_VALUE_OUT_OF_RANGE);
		}
		inPoint = aNumber;
	}

	/**
	 * Sets the sample number at which the sound starts to fade. May be set to
	 * zero if the sound does not fade out.
	 * 
	 * @param aNumber
	 *            the sample number at which the sound starts to fade.
	 */
	public void setOutPoint(int aNumber)
	{
		outPoint = aNumber;
	}

	/**
	 * Sets the number of times the sound is repeated. May be set to zero if the
	 * sound will not be repeated.
	 * 
	 * @param aNumber
	 *            the number of times the sound is repeated.
	 */
	public void setLoopCount(int aNumber)
	{
		loopCount = aNumber;
	}

	/**
	 * Sets the array of Envelope objects that define the levels at which a
	 * sound is played over the duration of the sound. May be set to null if no
	 * envelope is defined.
	 * 
	 * @param anArray
	 *            an array of Envelope objects. Must not be null.
	 */
	public void setEnvelopes(List<Envelope> anArray)
	{
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		envelopes = anArray;
	}

	/**
	 * Creates and returns a deep copy of this object.
	 */
	public SoundInfo copy()
	{
		return new SoundInfo(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, identifier, mode, inPoint, outPoint, loopCount, envelopes);
	}

	public int prepareToEncode(final SWFEncoder coder)
	{
		int length = 3;

		length += (inPoint == 0) ? 0 : 4;
		length += (outPoint == 0) ? 0 : 4;
		length += (loopCount == 0) ? 0 : 2;
		
		if (!envelopes.isEmpty()) {
			length += 1 + envelopes.size() * 8;
		}

		return length;
	}

	public void encode(final SWFEncoder coder) throws CoderException
	{
		coder.writeWord(identifier, 2);
		coder.writeBits(mode.getValue(), 4);
		coder.writeBits(envelopes.isEmpty() ? 0 : 1, 1);
		coder.writeBits(loopCount == 0 ? 0 : 1, 1);
		coder.writeBits(outPoint == 0 ? 0 : 1, 1);
		coder.writeBits(inPoint == 0 ? 0 : 1, 1);

		if (inPoint != 0) {
			coder.writeWord(inPoint, 4);
		}
		if (outPoint != 0) {
			coder.writeWord(outPoint, 4);
		}
		if (loopCount != 0) {
			coder.writeWord(loopCount, 2);
		}
		if (!envelopes.isEmpty())
		{
			coder.writeWord(envelopes.size(), 1);

			for (int i=0; i<envelopes.size(); i++) {
				envelopes.get(i).encode(coder);
			}
		}
	}
}
