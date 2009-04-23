/*
 *  ImageConstructor.java
 *  Transform Utilities
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

package com.flagstone.transform.coder;


/**
 */
public final class DecoderRegistry
{
	private SWFFactory<FillStyle> fillStyleDecoder;
	private SWFFactory<FillStyle> morphStyleDecoder;
	private SWFFactory<Action> actionDecoder;
	private SWFFactory<MovieTag> movieDecoder;
	private FLVFactory<VideoTag> videoDecoder;

	public SWFFactory<FillStyle> getFillStyleDecoder() {
		return fillStyleDecoder;
	}
	
	public void setFillStyleDecoder(SWFFactory<FillStyle> factory) {
	    fillStyleDecoder = factory;
    }

	public SWFFactory<FillStyle> getMorphFillStyleDecoder() {
		return morphStyleDecoder;
	}
	
	public void setMorphFillStyleDecoder(SWFFactory<FillStyle> factory) {
	    morphStyleDecoder = factory;
    }

	public SWFFactory<Action> getActionDecoder() {
		return actionDecoder;
	}
	
	public void setActionDecoder(SWFFactory<Action> factory) {
	    actionDecoder = factory;
    }

	public SWFFactory<MovieTag> getMovieDecoder() {
		return movieDecoder;
	}
	
	public void setMovieDecoder(SWFFactory<MovieTag> factory) {
	    movieDecoder = factory;
    }

	public FLVFactory<VideoTag> getVideoDecoder() {
		return videoDecoder;
	}
	
	public void setVideoDecoder(FLVFactory<VideoTag> factory) {
	    videoDecoder = factory;
    }
}
