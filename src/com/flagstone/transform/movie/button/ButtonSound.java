/*
 * ButtonSOund.java
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

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.MovieTag;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.Types;
import com.flagstone.transform.movie.sound.SoundInfo;

/**
 * <p>ButtonSound defines the sounds that are played when an event occurs in a
 * button. Sounds are only played for the RollOver, RollOut, Press and Release
 * events.</p>
 * 
 * <p>For each event a {@link SoundInfo} object identifies the sound and 
 * controls how it is played. For events where no sound should be played simply 
 * specify a null value instead of a SoundInfo object.</p>
 * 
 * @see DefineButton
 * @see DefineButton2
 */
public final class ButtonSound implements MovieTag
{
	private static final String FORMAT = "ButtonSound: { identifier=%d; sound[0]=%s; sound[1]=%s; sound[2]=%s; sound[3]=%s }";
	
	protected int identifier;
	protected SoundInfo[] sound = new SoundInfo[] {null, null, null, null};
	
	private transient int start;
	private transient int end;
	private transient int length;

	public ButtonSound(final SWFDecoder coder, final SWFContext context) throws CoderException
	{
		start = coder.getPointer();
		length = coder.readWord(2, false) & 0x3F;
		
		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}
		end = coder.getPointer() + (length << 3);

		identifier = coder.readWord(2, false);

		for (int i=0; i<4; i++)
		{
			if (coder.readWord(2, false) > 0) 
			{
				coder.adjustPointer(-16);
				sound[i] = new SoundInfo(coder, context);
			}

			if (coder.getPointer() == end) {
				break;
			}
		}

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}

	/**
	 * Creates a ButtonSound object that defines the sound played for a
	 * single button event.
	 * 
	 * @param uid
	 *            the unique identifier of the DefineButton or DefineButton2 object
	 *            that defines the button. Must be in the range 1..65535.
	 * @param eventCode
	 *            the event that identifies when the sound id played, must be
	 *            either ButtonEvent.EventType.rollOver, ButtonEvent.EventType.rollOut,
	 *            ButtonEvent.EventType.press or ButtonEvent.EventType.release.
	 * @param aSound
	 *            an SoundInfo object that identifies a sound and controls how it
	 *            is played.
	 */
	public ButtonSound(int uid, ButtonEvent eventCode, SoundInfo aSound)
	{
		setIdentifier(uid);
		setSoundForEvent(eventCode, aSound);
	}
	
	public ButtonSound(ButtonSound object) {
		
		identifier = object.identifier;
		
		for (int i=0; i<sound.length; i++) {
			if (object.sound[i] != null) {
				sound[i] = object.sound[i].copy();
			}
		}
	}

	/**
	 * Returns the unique identifier of the button that this object applies to.
	 */
	public int getIdentifier()
	{
		return identifier;
	}

	/**
	 * Returns the SoundInfo object for the specified event. Null is returned if 
	 * there is no SoundInfo object defined for the event code.  
	 * 
	 * @param eventCode
	 *            the code representing the button event, must be either
	 *            ButtonEvent.EventType.RollOver, ButtonEvent.EventType.RollOut,
	 *            ButtonEvent.EventType.Press or ButtonEvent.EventType.Release.
	 * @return the SoundInfo that identifies and controls the sound that will be
	 *         played for the event.
	 */
	public SoundInfo getSoundForEvent(ButtonEvent eventCode)
	{
		SoundInfo aSound;

		if (eventCode == ButtonEvent.ROLL_OUT) {
			aSound = sound[0];
		}
		else if (eventCode == ButtonEvent.ROLL_OVER) {
			aSound = sound[1];
		}
		else if (eventCode == ButtonEvent.PRESS) {
			aSound = sound[2];
		}
		else {
			aSound = sound[3];
		}

		return aSound;
	}

	/**
	 * Sets the identifier of the button that this object applies to.
	 * 
	 * @param uid
	 *            the unique identifier of the button which this object applies to.
	 *            Must be in the range 1..65535.
	 */
	public void setIdentifier(int uid)
	{
		if (uid < 1 || uid > 65535) {
			throw new IllegalArgumentException(Strings.IDENTIFIER_OUT_OF_RANGE);
		}
		identifier = uid;
	}

	/**
	 * Sets the SoundInfo object for the specified button event. The argument 
	 * may be null allowing the SoundInfo object for a given event to be deleted.
	 * 
	 * @param eventCode
	 *            the code representing the button event, must be either
	 *            ButtonEvent.EventType.RollOver, ButtonEvent.EventType.RollOut,
	 *            ButtonEvent.EventType.Press or ButtonEvent.EventType.Release.
	 * @param aSound
	 *            an SoundInfo object that identifies and controls how the sound
	 *            is played.
	 */
	public void setSoundForEvent(ButtonEvent eventCode, SoundInfo aSound)
	{
		if (eventCode == ButtonEvent.ROLL_OUT) {
			sound[0] = aSound;
		}
		else if (eventCode == ButtonEvent.ROLL_OVER) {
			sound[1] = aSound;
		}
		else if (eventCode == ButtonEvent.PRESS) {
			sound[2] = aSound;
		}
		else {
			sound[3] = aSound;
		}
	}

	/**
	 * Creates and returns a deep copy of this object.
	 */
	public ButtonSound copy()
	{
		return new ButtonSound(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, identifier, sound[0], sound[1], sound[2], sound[3]);
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context)
	{
		length = 2;

		for (int i=0; i<4; i++)
		{
			if (sound[i] == null) {
				length += 2;
			}
			else {
				length += sound[i].prepareToEncode(coder, context);
			}
		}
		return (length > 62 ? 6:2) + length;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
		start = coder.getPointer();

		if (length > 62) {
			coder.writeWord((Types.BUTTON_SOUND << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((Types.BUTTON_SOUND << 6) | length, 2);
		}
		end = coder.getPointer() + (length << 3);
	
		coder.writeWord(identifier, 2);

		for (int i=0; i<4; i++)
		{
			if (sound[i] == null) {
				coder.writeWord(0, 2);
			}
			else {
				sound[i].encode(coder, context);
			}
		}

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}
}
