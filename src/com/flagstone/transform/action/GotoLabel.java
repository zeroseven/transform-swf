/*
 * GotoLabel.java
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

import com.flagstone.transform.FrameLabel;
import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;



/**
 * The GotoLabel action instructs the player to move to the frame in the
 * current movie with the specified label - previously assigned using a FrameLabel
 * object.
 *
 * @see FrameLabel
 */
public final class GotoLabel implements Action
{
	private static final String FORMAT = "GotoLabel: { label=%s }";
	
	private String label;

	private transient int length;
	
	//TODO(doc)
	public GotoLabel(final SWFDecoder coder, final SWFContext context) throws CoderException
	{
		coder.readByte();
		length = coder.readWord(2, false);
		label = coder.readString();
	}

	/**
	 * Creates a GotoLabel action with the specified frame label.
	 * 
	 * @param aString
	 *            the label assigned a particular frame in the movie. Must not
	 *            be null or an empty string.
	 */
	public GotoLabel(String aString)
	{
		setLabel(aString);
	}
	
	//TODO(doc)
	public GotoLabel(GotoLabel object) {
		label = object.label;
	}

	/**
	 * Returns the frame label.
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Sets the frame label.
	 * 
	 * @param aString
	 *            the label assigned a particular frame in the movie. Must not
	 *            be null or an empty string.
	 */
	public void setLabel(String aString)
	{
		if (aString == null) {
			throw new IllegalArgumentException(Strings.STRING_NOT_SET);
		}
		label = aString;
	}

	public GotoLabel copy() {
		return new GotoLabel(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, label);
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context)
	{
		length = coder.strlen(label);

		return 3 + length;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
		coder.writeByte(ActionTypes.GOTO_LABEL);
		coder.writeWord(length, 2);
		coder.writeString(label);
	}
}
