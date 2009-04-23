/*
 * EnableDebugger2.java
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
 * EnableDebugger2 is an updated version or the EnableDebugger instruction 
 * which enables a movie to be debugged.
 * 
 * <p>In order to use the debugger a password must be supplied. When encrypted
 * using the MD5 algorithm it must match the value stored in the password
 * attribute.</p>
 * 
 * <p>The EnableDebugger2 2 data structure was introduced in Flash 6. It replaced 
 * EnableDebugger in Flash 5 with a different format to support internal changes 
 * in the Flash Player. The functionality was not changed.</p>
 * 
 * @see EnableDebugger
 */
public final class EnableDebugger2 implements MovieTag
{
	private static final String FORMAT = "EnableDebugger2: { password=%s }";

	private String password;
	
	private transient int length;

	//TODO(doc)
	public EnableDebugger2(final SWFDecoder coder, final Context context) throws CoderException
	{
		length = coder.readWord(2, false) & 0x3F;
		
		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}

		coder.readWord(2, false);
		password = coder.readString();
	}

	/**
	 * Creates a EnableDebugger2 object with an MD5 encrypted password.
	 * 
	 * @param password
	 *            the string defining the password. The string must not be 
	 *            empty or null.
	 */
	public EnableDebugger2(String password)
	{
		setPassword(password);
	}
	
	//TODO(doc)
	public EnableDebugger2(EnableDebugger2 object) {
		password = object.password;
	}

	/**
	 * Returns the MD5 encrypted password.
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Sets the MD5 encrypted password.
	 * 
	 * @param aString
	 *            the string defining the password. Must not be an empty string
	 *            or null.
	 */
	public void setPassword(String aString)
	{
		if (aString == null) {
			throw new IllegalArgumentException(Strings.STRING_CANNOT_BE_NULL);
		}
		if (aString.length() == 0) {
			throw new IllegalArgumentException(Strings.STRING_CANNOT_BE_EMPTY);
		}
		password = aString;
	}

	public EnableDebugger2 copy() {
		return new EnableDebugger2(this);	
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, password);
	}

	public int prepareToEncode(final SWFEncoder coder, final Context context)
	{
		length = 2 + coder.strlen(password);

		return (length > 62 ? 6:2) + length;
	}

	public void encode(final SWFEncoder coder, final Context context) throws CoderException
	{
		if (length > 62) {
			coder.writeWord((MovieTypes.ENABLE_DEBUGGER_2 << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((MovieTypes.ENABLE_DEBUGGER_2 << 6) | length, 2);
		}
		
		coder.writeWord(0, 2);
		coder.writeString(password);
	}
}
