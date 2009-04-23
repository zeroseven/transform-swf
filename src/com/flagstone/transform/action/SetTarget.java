/*
 * SetTarget.java
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

package com.flagstone.transform.action;

import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;


//TODO(doc) Review
/**
 * SetTarget selects a movie clip to allow its time-line to be controlled. The 
 * action performs a "context switch". All following actions such as GotoFrame, 
 * Play, etc. will be applied to the specified object until another
 * <b>SetTarget</b> action is executed. Setting the target to be the empty
 * string ("") returns the target to the movie's main time-line.</p>
 * 
 */
public final class SetTarget implements Action
{
	private static final String FORMAT = "SetTarget: { target=%s }";
	
	private String target;
	
	private transient int length;

	//TODO(doc)
	public SetTarget(final SWFDecoder coder, final Context context) throws CoderException
	{
		coder.readByte();
		length = coder.readWord(2, false);
		target = coder.readString();
	}

	/**
	 * Creates a SetTarget action that changes the context to the
	 * specified target.
	 * 
	 * @param aString
	 *            the name of a movie clip. Must not be null or zero length string.
	 */
	public SetTarget(String aString)
	{
		setTarget(aString);
	}
	
	//TODO(doc)
	public SetTarget(SetTarget object) {
		target = object.target;
	}

	/**
	 * Returns the name of the target movie clip.
	 */
	public String getTarget()
	{
		return target;
	}

	/**
	 * Sets the name of the target movie clip.
	 * 
	 * @param aString
	 *            the name of a movie clip. Must not be null or zero length string.
	 */
	public void setTarget(String aString)
	{
		if (aString == null || aString.length() == 0) {
			throw new IllegalArgumentException(Strings.STRING_NOT_SET);
		}
		target = aString;
	}

	public SetTarget copy() {
		return new SetTarget(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, target);
	}

	public int prepareToEncode(final SWFEncoder coder, final Context context)
	{
		length = coder.strlen(target);

		return 3 + length;
	}

	public void encode(final SWFEncoder coder, final Context context) throws CoderException
	{
		coder.writeByte(ActionTypes.SET_TARGET);
		coder.writeWord(length, 2);
		coder.writeString(target);
	}
}