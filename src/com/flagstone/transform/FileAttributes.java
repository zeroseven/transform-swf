/*
 * Free.java
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

//TODO(doc) Description and methods
public final class FileAttributes implements MovieTag {

	private static final String FORMAT = "FileAttributes: { metadata=%d; actionscript3=%d; userNetwork=%d }";

	private int hasMetaData;
	private int hasActionscript;
	private int useNetwork;

	public FileAttributes(final SWFDecoder coder) throws CoderException {

		if ((coder.readWord(2, false) & 0x3F) == 0x3F) {
			coder.readWord(4, false);
		}

		final int value = coder.readByte();
		hasMetaData = value & 16;
		hasActionscript = value & 8;
		useNetwork = value & 1;

		coder.adjustPointer(24);
	}

	public FileAttributes(final boolean metadata, final boolean actionscript,
			final boolean network) {
		setHasMetaData(metadata);
		setHasActionscript(actionscript);
		setUseNetwork(network);
	}

	public FileAttributes(final FileAttributes object) {
		hasMetaData = object.hasMetaData;
		hasActionscript = object.hasActionscript;
		useNetwork = object.useNetwork;
	}

	public boolean hasMetaData() {
		return (hasMetaData & 16) != 0;
	}

	public void setHasMetaData(final boolean metadata) {
		hasMetaData = metadata ? 16 : 0;
	}

	public boolean hasActionscript() {
		return (hasActionscript & 8) != 0;
	}

	public void setHasActionscript(final boolean actionscript) {
		hasActionscript = actionscript ? 8 : 0;
	}

	public boolean useNetwork() {
		return (useNetwork & 1) != 0;
	}

	public void setUseNetwork(final boolean network) {
		useNetwork = network ? 1 : 0;
	}

	public FileAttributes copy() {
		return new FileAttributes(this);
	}

	@Override
	public String toString() {
		return String.format(FORMAT, hasMetaData >> 4, hasActionscript >> 2,
				useNetwork);
	}

	public int prepareToEncode(final SWFEncoder coder, final Context context) {
		return 6;
	}

	public void encode(final SWFEncoder coder, final Context context)
			throws CoderException {
		coder.writeWord((MovieTypes.FILE_ATTRIBUTES << 6) | 4, 2);
		coder.writeByte(hasMetaData | hasActionscript | useNetwork);
		coder.writeWord(0, 3);
	}
}
