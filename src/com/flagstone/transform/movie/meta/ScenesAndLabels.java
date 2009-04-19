/*
 * Symbol.java
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

package com.flagstone.transform.movie.meta;

import java.util.LinkedHashMap;
import java.util.Map;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.LittleEndianEncoder;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.MovieTag;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.MovieTypes;

//TODO(doc) Description and methods
public final class ScenesAndLabels implements MovieTag {
	
	private static final String FORMAT = "ScenesAndLabels: { scenes=%s; labels=%s }";

	private Map<Integer, String> scenes;
	private Map<Integer, String> labels;

	private transient int start;
	private transient int end;
	private transient int length;

	public ScenesAndLabels(final SWFDecoder coder, final SWFContext context) throws CoderException {
		
		start = coder.getPointer();

		length = coder.readWord(2, false) & 0x3F;

		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}
		end = coder.getPointer() + (length << 3);

		int count = coder.readVariableU32();

		scenes = new LinkedHashMap<Integer, String>();
		labels = new LinkedHashMap<Integer, String>();
		for (int i=0; i < count; i++) {
			scenes.put(coder.readVariableU32(), coder.readString());
		}
		
		count = coder.readVariableU32();

		for (int i=0; i < count; i++) {
			labels.put(coder.readVariableU32(), coder.readString());
		}

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}

	public ScenesAndLabels(Map<Integer, String> scenes, Map<Integer, String>labels) {
		this.scenes = scenes;
		this.labels = labels;
	}

	public ScenesAndLabels(ScenesAndLabels object) {
		scenes = new LinkedHashMap<Integer, String>(object.scenes);
		labels = new LinkedHashMap<Integer, String>(object.labels);
	}

	public final void addScene(int offset, String name) {
		if (offset < 0 || offset > 65535) {
			throw new IllegalArgumentException(Strings.FRAME_OUT_OF_RANGE);
		}
		if (name == null || name.length() == 0) {
			throw new IllegalArgumentException(Strings.STRING_NOT_SET);
		}
		scenes.put(offset, name);
	}

	public Map<Integer, String> getScenes() {
		return scenes;
	}

	public void setScenes(Map<Integer, String> map) {
		if (map == null) {
			throw new IllegalArgumentException(Strings.TABLE_CANNOT_BE_NULL);
		}
		scenes = map;
	}

	public final void addLabel(int offset, String name) {
		if (offset < 0 || offset > 65535) {
			throw new IllegalArgumentException(Strings.FRAME_OUT_OF_RANGE);
		}
		if (name == null || name.length() == 0) {
			throw new IllegalArgumentException(Strings.STRING_NOT_SET);
		}
		labels.put(offset, name);
	}

	public Map<Integer, String> getLabels() {
		return labels;
	}

	public void setLabels(Map<Integer, String> map) {
		if (map == null) {
			throw new IllegalArgumentException(Strings.TABLE_CANNOT_BE_NULL);
		}
		labels = map;
	}

	public ScenesAndLabels copy() {
		return new ScenesAndLabels(this);
	}

	@Override
	public String toString() {
		return String.format(FORMAT, scenes, labels);
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context) {
		
		length = LittleEndianEncoder.sizeVariableU32(scenes.size());
		
		for (Integer offset : scenes.keySet()) {
			length += LittleEndianEncoder.sizeVariableU32(offset) + coder.strlen(scenes.get(offset));
		}

		length += LittleEndianEncoder.sizeVariableU32(labels.size());

		for (Integer offset : labels.keySet()) {
			length += LittleEndianEncoder.sizeVariableU32(offset) + coder.strlen(labels.get(offset));
		}

		return (length > 62 ? 6:2) + length;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException {
		
		start = coder.getPointer();

		if (length > 62) {
			coder.writeWord((MovieTypes.SCENES_AND_LABELS << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((MovieTypes.SCENES_AND_LABELS << 6) | length, 2);
		}
		end = coder.getPointer() + (length << 3);

		coder.writeVariableU32(scenes.size());

		for (Integer identifier : scenes.keySet()) {
			coder.writeVariableU32(identifier.intValue());
			coder.writeString(scenes.get(identifier));
		}
		
		coder.writeVariableU32(labels.size());

		for (Integer identifier : labels.keySet()) {
			coder.writeVariableU32(identifier.intValue());
			coder.writeString(labels.get(identifier));
		}

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}
}
