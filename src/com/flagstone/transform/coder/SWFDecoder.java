/*
 * SWFDecoder.java
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
package com.flagstone.transform.coder;

import com.flagstone.transform.factory.movie.ActionFactory;
import com.flagstone.transform.factory.movie.FillStyleFactory;
import com.flagstone.transform.factory.movie.MorphFillStyleFactory;
import com.flagstone.transform.factory.movie.MovieFactory;
import com.flagstone.transform.factory.movie.SWFFactory;
import com.flagstone.transform.movie.MovieTag;
import com.flagstone.transform.movie.action.Action;
import com.flagstone.transform.movie.datatype.Color;
import com.flagstone.transform.movie.fillstyle.FillStyle;

/**
 * SWFDecoder extends LittleEndianDecoder by adding a context used to pass 
 * information between classes during decoding and a factory class for 
 * generating instances of objects.
 */
public final class SWFDecoder extends LittleEndianDecoder {

	private SWFFactory<FillStyle> fillStyleFactory;
	private SWFFactory<FillStyle> morphStyleFactory;
	private SWFFactory<Action> actionFactory;
	private SWFFactory<MovieTag> movieFactory;

	private SWFContext context;

	/**
	 * Creates a SWFDecoder object initialised with the data to be decoded.
	 * 
	 * @param data
	 *            an array of bytes to be decoded.
	 */
	public SWFDecoder(final byte[] data) {
		super(data);
		
		fillStyleFactory = new FillStyleFactory();
		morphStyleFactory = new MorphFillStyleFactory();
		actionFactory = new ActionFactory();
		movieFactory = new MovieFactory();
		
		context = new SWFContext();
	}

	/**
	 * Set the Context object used to share information between objects.
	 */
	public void setContext(final SWFContext context) {
		this.context = context;
	}

	/**
	 * Returns the Context object used by this decoder.
	 */
	public SWFContext getContext() {
		return context;
	}

	public void setFillStyleFactory(SWFFactory<FillStyle> factory) {
	    fillStyleFactory = factory;
    }
	
	public void setMorphFillStyleFactory(SWFFactory<FillStyle> factory) {
	    morphStyleFactory = factory;
    }
	
	public void setActionFactory(SWFFactory<Action> factory) {
	    actionFactory = factory;
    }
	
	public void setMovieFactory(SWFFactory<MovieTag> factory) {
	    movieFactory = factory;
    }
	
	public FillStyle fillStyleOfType(SWFDecoder coder) throws CoderException {
		return fillStyleFactory.getObject(coder);
	}
	
	public FillStyle morphFillStyleOfType(SWFDecoder coder) throws CoderException {
		return morphStyleFactory.getObject(coder);
	}
	
	public Action actionOfType(SWFDecoder coder) throws CoderException {
		return actionFactory.getObject(coder);
	}
	
	public MovieTag movieOfType(SWFDecoder coder) throws CoderException {
		return movieFactory.getObject(coder);
	}
}
