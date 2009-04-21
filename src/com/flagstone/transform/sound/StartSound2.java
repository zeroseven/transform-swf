/*
 * StartSound.java
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

package com.flagstone.transform.sound;

import com.flagstone.transform.MovieTag;
import com.flagstone.transform.MovieTypes;
import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

//TODO(doc) 
/**
 * StartSound2 instructs the player to start or stop playing a sound defined
 * using the DefineSound class.
 * 
 * <p>StartSound contains a SoundInfo object that defines how the sound fades in 
 * and out, whether it is repeated as well as specifying an envelope that 
 * provides a finer degree of control over the levels at which the sound is 
 * played.</p>
 * 
 * @see DefineSound
 * @see SoundInfo
 */
public final class StartSound2 implements MovieTag
{
	private static final String FORMAT = "StartSound2: { sound=%s }";

	private String soundClass;
	private SoundInfo sound;
	
	private transient int start;
	private transient int end;
	private transient int length;

	//TODO(doc) 
	public StartSound2(final SWFDecoder coder, final SWFContext context) throws CoderException
	{
		start = coder.getPointer();
		length = coder.readWord(2, false) & 0x3F;
		
		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}
		end = coder.getPointer() + (length << 3);

		soundClass = coder.readString();
		sound = new SoundInfo(coder, context);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}

	/**
	 * Creates a StartSound object with an Sound object that identifies
	 * the sound and controls how it is played.
	 * 
	 * @param aSound
	 *            the Sound object. Must not be null.
	 */
	public StartSound2(SoundInfo aSound)
	{
		setSound(aSound);
	}
	
	//TODO(doc) 
	public StartSound2(StartSound2 object)
	{
		soundClass = object.soundClass;
		sound = object.sound.copy();
	}

	//TODO(doc) 
	public String getSoundClass() {
		return soundClass;
	}

	/**
	 * Returns the Sound object describing how the sound will be played.
	 */
	public SoundInfo getSound()
	{
		return sound;
	}
	
	//TODO(doc) 
	public void setSoundClass(String className) {
		soundClass = className;
	}

	/**
	 * Sets the Sound object that describes how the sound will be played.
	 * 
	 * @param aSound
	 *            the Sound object that controls how the sound is played.
	 *            Must not be null.
	 */
	public void setSound(SoundInfo aSound)
	{
		if (aSound == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		sound = aSound;
	}

	public StartSound2 copy() 
	{
		return new StartSound2(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, sound);
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context)
	{
		length = soundClass.length() + sound.prepareToEncode(coder, context);
		return (length > 62 ? 6:2) + length;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
		start = coder.getPointer();

		if (length >= 63) {
			coder.writeWord((MovieTypes.START_SOUND_2 << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((MovieTypes.START_SOUND_2 << 6) | length, 2);
		}
		end = coder.getPointer() + (length << 3);

		coder.writeString(soundClass);
		sound.encode(coder, context);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}
}
