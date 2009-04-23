/*
 * ShowFrame.java
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
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

//TODO(doc) Review
/**
 * ShowFrame is used to instruct the Flash Player to display a single frame in a
 * movie or movie clip.
 * 
 * <p>
 * When a frame is displayed the Flash Player performs the following:
 * <ul>
 * <li>Any actions defined using a DoAction object are executed.</li>
 * <li>The contents of the Flash Player's display list are drawn on the screen.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * The scope of a frame is delineated by successive ShowFrame objects. All the
 * commands that affect change the state of the display list or define actions
 * to be executed take effect when the Flash Player displays the frame. All the
 * objects displayed in a frame must be defined before they can be displayed.
 * </p>
 * 
 * @see Place
 * @see Place2
 * @see Remove
 * @see Remove2
 * @see DoAction
 * 
 */
public final class ShowFrame implements MovieTag {
	
	private static final String FORMAT = "ShowFrame";

	private static final ShowFrame instance = new ShowFrame();

	/**
	 * Returns a shared ShowFrame object.
	 * 
	 * @return an object that can safely be shared among objects.
	 */
	public static ShowFrame getInstance() {
		return instance;
	}

	private ShowFrame() {
	}

	//TODO(doc)
	public ShowFrame copy() {
		return this;
	}

	@Override
	public String toString() {
		return FORMAT;
	}

	public int prepareToEncode(final SWFEncoder coder, final Context context) {
		return 2;
	}

	public void encode(final SWFEncoder coder, final Context context) throws CoderException {
		coder.writeWord((MovieTypes.SHOW_FRAME << 6) | 0, 2);
	}

	public void decode(final SWFDecoder coder, final Context context) throws CoderException {
		if ((coder.readWord(2, false) & 0x3F) == 0x3F) {
			coder.readWord(4, false);
		}
	}
}
