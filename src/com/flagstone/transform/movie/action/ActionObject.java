/*
 * UnknownAction.java
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

package com.flagstone.transform.movie.action;

import java.util.Arrays;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.Strings;


public final class ActionObject implements Action
{
	private static final String FORMAT = "ActionObject: { type=%d; data[%s] }";

	private int type;
	private byte[] data;
	
	private transient int length;
	
	public ActionObject(int type) {
		this.type = type;
	}

	public ActionObject(int type, byte[] bytes) {
		this.type = type;
		setData(bytes);
	}

	public ActionObject(ActionObject object) {
		type = object.type;
		data = Arrays.copyOf(object.data, object.data.length);
	}
	
	public int getType() {
		return type;
	}

	/**
	 * Returns the encoded data for the action.
	 */
	public byte[] getData()
	{
		return data;
	}

	/**
	 * Sets the encoded data for the action.
	 * 
	 * @param bytes
	 *            the encoded data for the action. May be zero length but not null.
	 */
	public void setData(byte[] bytes)
	{
		if (bytes == null) {
			throw new IllegalArgumentException(Strings.DATA_CANNOT_BE_NULL);
		}
		data = bytes;
	}
	
	public void setData(int size) {
	    data = new byte[size];
	}

	@Override
	public ActionObject copy() 
	{
		return new ActionObject(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, type, (data != null ? String.valueOf(data.length) : data));
	}

	@Override
	public int prepareToEncode(final SWFEncoder coder)
	{
		length = 0;
		
		if (data != null) {
			length += data.length;
		}
		return ((type > 127) ? 3:1)+ length;
	}

	@Override
	public void encode(final SWFEncoder coder) throws CoderException
	{
		coder.writeByte(type);
		
		if (data != null) {
			coder.writeWord(data.length, 2);
			coder.writeBytes(data);		
		}
	}

	@Override
	public void decode(final SWFDecoder coder) throws CoderException
	{
		type = coder.readByte();
		
		if (type > 127) {
			length = coder.readWord(2, false);
			data = coder.readBytes(new byte[length]);			
		}
	}
}
