/*
 * GotoFrame2.java
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
import com.flagstone.transform.coder.Action;
import com.flagstone.transform.coder.ActionTypes;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

//TODO(doc) Review
/**
 * The GotoFrame2 action instructs the player to go to the named or numbered
 * frame in the current movie's main time-line. It extends the functionality
 * provided by the GotoFrame action by allowing the name of a frame, previously
 * assigned using the FrameLabel object, to be specified.</p>
 * 
 * <p>
 * Up to Flash Version 4, movies contained a single sequence of 65536 frames. In
 * Flash 5 the concept of Scenes was added which allowed movies to contain
 * 'pages' of frames. GotoFrame2 contains a frameOffset attribute which allows
 * the frames in each scene to be referenced by its 'logical' number. The
 * frameOffset for a given scene is added to the frame number to generate the
 * 'physical' page number.
 * </p>
 * 
 * <p>
 * GotoFrame2 is a stack-based action. The name or number of the frame is pushed
 * onto the stack before the GotoFrame2 action is executed. If a frameOffset is
 * specified it is added to the number of the frame identified by the stack
 * arguments to give the final frame number. Whether the movie starts playing
 * the frame is controlled by the boolean attribute, <i>play</i>. When set to
 * true the movie starts playing the frame as soon as it has been loaded by the
 * Flash Player.
 * </p>
 * 
 * <p>
 * GotoFrame2 is only used to control the main time-line of a movie. Controlling
 * how an individual movie clip is played is handled by a different mechanism.
 * From Flash 5 onward movie clips are defined as objects. The ExecuteMethod
 * action is used to execute the gotoAndPlay() or gotoAndStop() methods that
 * control a movie clip's time-line.
 * </p>
 * 
 * @see GotoFrame
 */
public final class GotoFrame2 implements Action {
	private static final String FORMAT = "Gotoframe2: { playFrame=%s; frameOffset=%d }";

	private boolean play;
	private int frameOffset;

	private transient int length;
	private transient boolean hasOffset;

	// TODO(doc)
	public GotoFrame2(final SWFDecoder coder) throws CoderException {
		coder.readByte();
		length = coder.readWord(2, false);
		coder.readBits(6, false);
		hasOffset = coder.readBits(1, false) != 0;
		play = coder.readBits(1, false) != 0;

		if (hasOffset) {
			frameOffset = coder.readWord(2, false);
		}
	}

	/**
	 * Creates a GotoFrame2 object with the specified play flag setting.
	 * 
	 * @param aBool
	 *            true if the player should being playing the movie at the
	 *            specified frame. false if the player should stop playing the
	 *            movie.
	 */
	public GotoFrame2(final boolean aBool) {
		setPlay(aBool);
		frameOffset = 0;
	}

	/**
	 * Creates a GotoFrame2 object with the specified play flag setting and
	 * frame offset for a given scene.
	 * 
	 * @param offset
	 *            a number which will be added to the number of the frame popped
	 *            from the stack to give the final frame number. Must be in the
	 *            range 1..65535.
	 * @param aBool
	 *            true if the player should being playing the movie at the
	 *            specified frame, false if the player should stop playing the
	 *            movie.
	 */
	public GotoFrame2(final int offset, final boolean aBool) {
		setPlay(aBool);
		setFrameOffset(offset);
	}

	// TODO(doc)
	public GotoFrame2(final GotoFrame2 object) {
		play = object.play;
		frameOffset = object.frameOffset;
	}

	/**
	 * Returns the offset that will be added to the 'logical' frame number
	 * obtained from the stack to generate the 'physical' frame number.
	 */
	public int getFrameOffset() {
		return frameOffset;
	}

	/**
	 * Sets the offset that will be added to the 'logical' frame number obtained
	 * from the stack to generate the 'physical' frame number.
	 * 
	 * @param offset
	 *            a number that will be added to the frame number obtained form
	 *            the stack. Must be in the range 1..65535.
	 */
	public void setFrameOffset(final int offset) {
		if (offset < 0 || offset > 65535) {
			throw new IllegalArgumentException(Strings.UNSIGNED_RANGE);
		}
		frameOffset = offset;
	}

	/**
	 * Returns the play flag.
	 * 
	 * @return true if the player will being playing the movie at the specified
	 *         frame, false otherwise.
	 */
	public boolean isPlay() {
		return play;
	}

	/**
	 * Sets the play flag.
	 * 
	 * @param aBool
	 *            true if the player should being playing the movie at the
	 *            specified frame. false if the player should stop playing the
	 *            movie.
	 */
	public void setPlay(final boolean aBool) {
		play = aBool;
	}

	public GotoFrame2 copy() {
		return new GotoFrame2(this);
	}

	@Override
	public String toString() {
		return String.format(FORMAT, String.valueOf(play), frameOffset);
	}

	public int prepareToEncode(final SWFEncoder coder, final Context context) {
		hasOffset = frameOffset > 0;

		length = 1 + (hasOffset ? 2 : 0);

		return 3 + length;
	}

	public void encode(final SWFEncoder coder, final Context context)
			throws CoderException {
		coder.writeByte(ActionTypes.GOTO_FRAME_2);
		coder.writeWord(length, 2);
		coder.writeBits(0, 6);
		coder.writeBits(hasOffset ? 1 : 0, 1);
		coder.writeBits(play ? 1 : 0, 1);

		if (hasOffset) {
			coder.writeWord(frameOffset, 2);
		}
	}
}