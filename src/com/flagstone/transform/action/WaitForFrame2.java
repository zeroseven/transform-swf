/*
 * WaitForFrame2.java
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

import com.flagstone.transform.coder.Action;
import com.flagstone.transform.coder.ActionTypes;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;


//TODO(doc) Review
/**
 * The WaitForFrame2 action instructs the player to wait until the specified
 * frame number or named frame has been loaded.
 * 
 * <p>If the frame has been loaded then the following <i>n</i> actions are
 * executed. The WaitForFrame2 action extends the WaitForFrame action by
 * allowing the name of a frame to be specified.</p>
 * 
 * <p>WaitForFrame2 is a stack-based action. The frame number or frame name which
 * should be loaded to trigger execution of the following actions is popped from
 * the Flash Player's stack. Note however that this method of waiting until a 
 * frame has been loaded is considered obsolete. Determining the number of frames 
 * loaded using the FramesLoaded property of the Flash player in combination with 
 * an If action is now preferred.</p>
 * 
 * @see Push
 * @see If
 */
public final class WaitForFrame2 implements Action
{
	private static final String FORMAT = "WaitForFrame2: { actionCount=%d }";
	
	private int actionCount;

	//TODO(doc)
	public WaitForFrame2(final SWFDecoder coder, final Context context) throws CoderException
	{
		coder.readByte();
		coder.readWord(2, false);
		actionCount = coder.readByte();
	}

	/**
	 * Creates a WaitForFrame2 object with the number of actions to
	 * execute if the frame has been loaded.
	 * 
	 * @param aNumber
	 *            the number of actions to execute. Must be in the range 
	 *            0..255.
	 */
	public WaitForFrame2(int aNumber)
	{
		setActionCount(aNumber);
	}

	//TODO(doc)
	public WaitForFrame2(WaitForFrame2 object) {
		actionCount = object.actionCount;
	}

	/**
	 * Returns the number of actions to execute.
	 */
	public int getActionCount()
	{
		return actionCount;
	}

	/**
	 * Sets the number of actions to execute if the frame has been loaded.
	 * Unlike other actions it is the number of actions that are specified not
	 * the number of bytes in memory they occupy.
	 * 
	 * @param aNumber
	 *            the number of actions to execute. Must be in the range 
	 *            0..255.
	 */
	public void setActionCount(int aNumber)
	{
		if (aNumber < 0 || aNumber > 255) {
			throw new IllegalArgumentException("Number of actions must be in the range 0..255.");
		}
		actionCount = aNumber;
	}
	
	public WaitForFrame2 copy() {
		return new WaitForFrame2(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, actionCount);
	}

	public int prepareToEncode(final SWFEncoder coder, final Context context)
	{
		return 4;
	}

	public void encode(final SWFEncoder coder, final Context context) throws CoderException
	{
		coder.writeByte(ActionTypes.WAIT_FOR_FRAME_2);
		coder.writeWord(1, 2);
		coder.writeWord(actionCount, 1);
	}
}
