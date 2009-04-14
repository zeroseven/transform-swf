/*
 * DoABC.java
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

package com.flagstone.transform.movie;

import java.util.Arrays;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

/**
 * DoABC is used to define scripts containing Actionscript 3.0 byte-codes.
 * 
 * <p>
 * Execution of the script may be deferred until it is explicitly called using
 * the assigned name.
 * </p>
 */
public final class DoABC implements MovieTag {

	private static final String FORMAT = "DoABC: { name=%s; deferred=%d; actions=data[%d] {...} }";

	private String name;
	private int deferred;
	private byte[] data;

	private transient int start;
	private transient int end;
	private transient int length;

	public DoABC(final SWFDecoder coder) throws CoderException {

		start = coder.getPointer();
		length = coder.readWord(2, false) & 0x3F;

		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}
		end = coder.getPointer() + (length << 3);

		deferred = coder.readBits(32, false);
		name = coder.readString();
		data = coder.readBytes(new byte[(end-coder.getPointer()) >>> 3]);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}

	/**
	 * Creates a DoABC object with the name and compiled Actionscript 3.0
	 * byte-codes. 
	 * 
	 * @param name
	 *            the name used to identify the script.
	 * @param defer
	 *            whether execution of the script is deferred.
	 * @param script
	 *            the compiled Actionscript 3.0 byte-codes.
	 */
	public DoABC(final String name, final boolean defer, final byte[] script) {
		setName(name);
		setDeferred(defer);
		setData(script);
	}

	/**
	 * Creates a DoABC initialize with a copy of the data from another object.
	 * 
	 * @param object
	 *            a DoABC object used to initialize this one.
	 */
	public DoABC(final DoABC object) {
		name = object.name;
		deferred = object.deferred;
		data = Arrays.copyOf(object.data, object.data.length);
	}

	/**
	 * Returns the name of the script.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the script.
	 * 
	 * @param name
	 *            the name assigned to the script so it can be referred to. Must
	 *            not be null or an empty string.
	 */
	public void setName(final String name) {
		if (name == null || name.length() == 0) {
			throw new IllegalArgumentException(Strings.STRING_NOT_SET);
		}
		this.name = name;
	}

	/**
	 * Returns true is the script is loaded but not executed or false if it is
	 * loaded and executed.
	 */
	public boolean isDeferred() {
		return (deferred & 1) != 0;
	}

	/**
	 * Sets whether execution of the script is deferred.
	 * 
	 * @param defer
	 *            execution of the script is deferred (true) or executed
	 *            immediately (false).
	 */
	public void setDeferred(final boolean defer) {
		this.deferred = defer ? 1 : 0;
	}

	/**
	 * Returns the array of Actionscript byte-codes.
	 */
	@SuppressWarnings("PMD.MethodReturnsInternalArray")
	public byte[] getData() {
		return data;
	}

	/**
	 * Sets the script containing compiled Actionscript 3.0 byte-codes.
	 * 
	 * @param bytes
	 *            an array of byte-codes. Must not be null.
	 */
	@SuppressWarnings("PMD.ArrayIsStoredDirectly")
	public void setData(final byte[] bytes) {
		if (bytes == null) {
			throw new IllegalArgumentException(Strings.DATA_CANNOT_BE_NULL);
		} else if (bytes.length == 0) {
			throw new IllegalArgumentException(Strings.DATA_CANNOT_BE_EMPTY);
		}
		data = bytes;
	}

	public DoABC copy() {
		return new DoABC(this);
	}

	@Override
	public String toString() {
		return String.format(FORMAT, name, deferred, data.length);
	}

	public int prepareToEncode(final SWFEncoder coder) {
		length = 4 + coder.strlen(name) + data.length;

		return (length > 62 ? 6 : 2) + length;
	}

	public void encode(final SWFEncoder coder) throws CoderException {

		start = coder.getPointer();

		if (length > 62) {
			coder.writeWord((Types.DO_ABC << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((Types.DO_ABC << 6) | length, 2);
		}
		end = coder.getPointer() + (length << 3);

		coder.writeBits(deferred, 32);
		coder.writeString(name);
		coder.writeBytes(data);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}
}
