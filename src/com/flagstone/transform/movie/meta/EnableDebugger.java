/*
 * EnableDebugger.java
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

package com.flagstone.transform.movie.meta;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.MovieTag;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.Types;

/**
 * Enables a movie to be debugged when played using the Flash authoring tool,
 * allowing the variables defined in the arrays of actions specified in object
 * to be inspected. Note that the Flash Player does not support debugging.

 * <p>In order to use the debugger a password must be supplied. When encrypted
 * using the MD5 algorithm it must match the value stored in the password
 * attribute.</p>
 */
public final class EnableDebugger implements MovieTag
{
	private static final String FORMAT = "EnableDebugger: { password=%s }";
	
	private String password;
	
	private transient int length;

	//TODO(doc)
	public EnableDebugger(final SWFDecoder coder, final SWFContext context) throws CoderException
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
	 *            the string defining the password. Must not be an empty string
	 *            or null.
	 */
	public EnableDebugger(String password)
	{
		setPassword(password);
	}
	
	//TODO(doc)
	public EnableDebugger(EnableDebugger object) {
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

	public EnableDebugger copy() {
		return new EnableDebugger(this);	
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, password);
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context)
	{
		length = 2 + coder.strlen(password);

		return (length > 62 ? 6:2) + length;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
		if (length > 62) {
			coder.writeWord((Types.ENABLE_DEBUGGER << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((Types.ENABLE_DEBUGGER << 6) | length, 2);
		}
		
		coder.writeWord(0, 2);
		coder.writeString(password);
	}
}
