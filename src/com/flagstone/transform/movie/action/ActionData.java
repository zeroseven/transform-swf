/*
 * ActionData.java
 * Transform
 * 
 * Copyright (c) 2009 Flagstone Software Ltd. All rights reserved.
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

import java.util.Arrays;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.Strings;

/**
 * ActionData is used to store one or more actions which already have been
 * encoded for writing to a Flash file.
 * 
 * <p>
 * You can use this class to either selectively decode the actions in a movie,
 * so actions that are not of interest can be left encoded or to selectively
 * encode actions that will not change when generating Flash files from a
 * template.
 * </p>
 */
public final class ActionData implements Action {

	private static final String FORMAT = "ActionData: { data[%d] }";

	private byte[] data;

	/**
	 * Creates an uninitialised ActionData object.
	 */
	public ActionData() {
		data = new byte[0];
	}

	/**
	 * Creates an ActionData object initialised to hold a series of encoded
	 * actions.
	 * 
	 * @param size
	 *            the number of bytes to reserve for encoded actions.
	 */
	public ActionData(int size) {
		setData(size);
	}

	/**
	 * Creates an ActionData object initialised with a set of encoded actions.
	 * 
	 * @param bytes
	 *            the array of encoded actions.
	 */
	public ActionData(byte[] bytes) {
		setData(bytes);
	}

	/**
	 * Creates a copy of this ActionData object.
	 * 
	 * @param object
	 *            the ActionData object used to initialise this one.
	 */
	public ActionData(ActionData object) {
		data = Arrays.copyOf(object.data, object.data.length);
	}

	/**
	 * Returns the encoded actions.
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * Sets the encoded actions.
	 * 
	 * @param bytes
	 *            the encoded data for the action. May be zero length but not
	 *            null.
	 */
	public void setData(byte[] bytes) {
		if (bytes == null) {
			throw new IllegalArgumentException(Strings.DATA_CANNOT_BE_NULL);
		}
		data = bytes;
	}

	/**
	 * Sets the size of the buffer to reserve for encoded actions.
	 * 
	 * @param size
	 *            the number of bytes to reserve for storing encoded actions.
	 *            Must not be negative.
	 */
	public void setData(int size) {
		if (size < 0) {
			throw new IllegalArgumentException(Strings.NUMBER_CANNOT_BE_NEGATIVE);
		}
		data = new byte[size];
	}

	public ActionData copy() {
		return new ActionData(this);
	}

	@Override
	public String toString() {
		return String.format(FORMAT, data.length);
	}

	public int prepareToEncode(final SWFEncoder coder) {
		return data.length;
	}

	public void encode(final SWFEncoder coder) throws CoderException {
		coder.writeBytes(data);
	}

	public void decode(final SWFDecoder coder) throws CoderException {
		coder.readBytes(data);
	}
}
