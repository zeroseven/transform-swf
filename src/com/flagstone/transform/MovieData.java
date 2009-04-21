/*
 * MovieData.java
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

import java.util.Arrays;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

/**
 * MovieData is used to store one or more MovieTags which already have been
 * encoded for writing to a Flash file.
 * 
 * <p>
 * You can use this class to either selectively decode the tags in a movie, so
 * tags that are not of interest can be left encoded or to selectively encode
 * tags that will not change when generating Flash files from a template.
 * </p>
 */
public final class MovieData implements MovieTag {
	
	private static final String FORMAT = "MovieData: { data[%d] }";

	private final byte[] data;
	
	//TODO(doc)
	public MovieData(final byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			throw new IllegalArgumentException(Strings.DATA_NOT_SET);
		}
		data = bytes;
	}

	//TODO(doc)
	public MovieData(final MovieData object) {
		data = Arrays.copyOf(object.data, object.data.length);
	}

	/**
	 * Returns the encoded MovieTag objects.
	 */
	public byte[] getData() {
		return data;
	}

	public MovieData copy() {
		return new MovieData(this);
	}

	@Override
	public String toString() {
		return String.format(FORMAT, data.length);
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context) {
		return data.length;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException {
		coder.writeBytes(data);
	}
}
