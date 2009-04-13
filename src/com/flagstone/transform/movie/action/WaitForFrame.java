/*
 * WaitForFrame.java
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

package com.flagstone.transform.movie.action;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.Types;


/**
 * The WaitForFrame action instructs the player to wait until the specified
 * frame number has been loaded.
 * 
 * <p>If the frame has been loaded then the actions in the following <i>n</i>
 * actions are executed. This action is most often used to execute a short
 * animation loop that plays until the main part of a movie has been loaded.
 * </p>
 * 
 * <p>This method of waiting until a frame has been loaded is considered obsolete.
 * Determining the number of frames loaded using the FramesLoaded property of
 * the Flash player in combination with an If action is now the preferred
 * mechanism.</p>
 * 
 * @see Push
 * @see If
 */
public final class WaitForFrame implements Action
{
	private static final String FORMAT = "WaitForFrame: { frameNumber=%d; actionCount=%d }";

	private int frameNumber;
	private int actionCount;

	public WaitForFrame()
	{
		frameNumber = 1;
		actionCount = 0;
	}

	/**
	 * Creates a WaitForFrame object with the specified frame number and
	 * the number of actions that will be executed when the frame is loaded.
	 * 
	 * @param aFrameNumber
	 *            the number of the frame to wait for. Must be in the range 
	 *            1..65535.
	 * @param anActionCount
	 *            the number (not bytes) of actions to execute. Must be in the range 
	 *            0..255.
	 */
	public WaitForFrame(int aFrameNumber, int anActionCount)
	{
		setFrameNumber(aFrameNumber);
		setActionCount(anActionCount);
	}
	
	public WaitForFrame(WaitForFrame object) {
		frameNumber = object.frameNumber;
		actionCount = object.actionCount;
	}

	/**
	 * Returns the frame number.
	 */
	public int getFrameNumber()
	{
		return frameNumber;
	}

	/**
	 * Returns the number of actions that will be executed when the specified frame
	 * is loaded.
	 */
	public int getActionCount()
	{
		return actionCount;
	}

	/**
	 * Sets the frame number.
	 * 
	 * @param aNumber
	 *            the number of the frame to wait for. Must be in the range 
	 *            1..65535.
	 */
	public void setFrameNumber(int aNumber)
	{
		if (aNumber < 1 || aNumber > 65535) {
			throw new IllegalArgumentException(Strings.FRAME_OUT_OF_RANGE);
		}
		frameNumber = aNumber;
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

	public WaitForFrame copy() {
		return new WaitForFrame(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, frameNumber, actionCount);
	}

	public int prepareToEncode(final SWFEncoder coder)
	{
		return 6;
	}

	public void encode(final SWFEncoder coder) throws CoderException
	{
		coder.writeByte(Types.WAIT_FOR_FRAME);
		coder.writeWord(3, 2);
		coder.writeWord(frameNumber, 2);
		coder.writeWord(actionCount, 1);
	}

	public void decode(final SWFDecoder coder) throws CoderException
	{
		coder.readByte();
		coder.readWord(2, false);
		frameNumber = coder.readWord(2, false);
		actionCount = coder.readByte();
	}
}
