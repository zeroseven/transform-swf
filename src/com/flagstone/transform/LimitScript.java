/*
 * LimitScript.java
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

package com.flagstone.transform;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

/**
 * The LimitScript is used to define the execution environment of the Flash
 * Player, limiting the resources available when executing actions and improving
 * performance.
 * 
 * <p>
 * LimitScript can be used to limit the maximum recursion depth and limit the
 * time a sequence of actions can execute for. This provides a rudimentary
 * mechanism for people viewing a movie to regain control of the Flash Player
 * should a script fail.
 * </p>
 */
public final class LimitScript implements MovieTag {

	private static final String FORMAT = "LimitScript: { depth=%d; timeout=%d }";

	private int depth;
	private int timeout;

	// TODO(doc)
	public LimitScript(final SWFDecoder coder) throws CoderException {

		if ((coder.readWord(2, false) & 0x3F) == 0x3F) {
			coder.readWord(4, false);
		}

		depth = coder.readWord(2, false);
		timeout = coder.readWord(2, false);
	}

	/**
	 * Creates a LimitScript object that limits the recursion depth to
	 * <em>depth</em> levels and specifies that any sequence of actions will
	 * timeout after <em>timeout</em> seconds.
	 * 
	 * @param depth
	 *            the maximum depth a sequence of actions can recurse to. Must
	 *            be in the range 0..65535.
	 * @param timeout
	 *            the time in seconds that a sequence of actions is allowed to
	 *            execute before the Flash Player displays a dialog box asking
	 *            whether the script should be terminated. Must be in the range
	 *            0..65535.
	 */
	public LimitScript(final int depth, final int timeout) {
		setDepth(depth);
		setTimeout(timeout);
	}

	public LimitScript(final LimitScript object) {
		depth = object.depth;
		timeout = object.timeout;
	}

	/**
	 * Returns the maximum recursion level.
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * Sets the maximum recursion level.
	 * 
	 * @param depth
	 *            the maximum depth a sequence of actions can recurse to. Must
	 *            be in the range 0..65535.
	 */
	public void setDepth(final int depth) {
		if (depth < 0 || depth > 65535) {
			throw new IllegalArgumentException(Strings.UNSIGNED_RANGE);
		}
		this.depth = depth;
	}

	/**
	 * Returns the maximum time a sequence of actions will execute before the
	 * Flash Player present a dialog box asking whether the script should be
	 * terminated.
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * Sets the maximum time a sequence of actions will execute before the Flash
	 * Player present a dialog box asking whether the script should be
	 * terminated.
	 * 
	 * @param time
	 *            the time in seconds that a sequence of actions is allowed to
	 *            execute. Must be in the range 0..65535.
	 */
	public void setTimeout(final int time) {
		if (time < 0 || time > 65535) {
			throw new IllegalArgumentException(Strings.UNSIGNED_RANGE);
		}
		timeout = time;
	}

	public LimitScript copy() {
		return new LimitScript(this);
	}

	@Override
	public String toString() {
		return String.format(FORMAT, depth, timeout);
	}

	public int prepareToEncode(final SWFEncoder coder, final Context context) {
		return 6;
	}

	public void encode(final SWFEncoder coder, final Context context)
			throws CoderException {
		coder.writeWord((MovieTypes.LIMIT_SCRIPT << 6) | 4, 2);
		coder.writeWord(depth, 2);
		coder.writeWord(timeout, 2);
	}
}
