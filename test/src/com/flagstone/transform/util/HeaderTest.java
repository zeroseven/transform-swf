/*
 * HeaderTest.java
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
package com.flagstone.transform.util;

import java.io.IOException;

import java.util.zip.DataFormatException;

import com.flagstone.transform.movie.Movie;
import com.flagstone.transform.movie.ShowFrame;
import com.flagstone.transform.movie.datatype.Bounds;

import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class HeaderTest
{
	private static Movie movie = null;
	
	private Header fixture;
	
	@BeforeClass
	public static void setupMovie()
	{
    	movie.setSignature("FWS");
    	movie.setVersion(5);
    	movie.setFrameSize(new Bounds(100,200,1000,2000));
    	movie.setFrameRate(12.0f);
    	movie.add(ShowFrame.getInstance());
	}
	
	@Before
	public static void setup()
	{
		
 	}
	
	@Test
    public void constructorWithData() throws IOException, DataFormatException
    {
		movie.setSignature("FWS");
    	fixture = new Header();
    	fixture.decodeFromData(movie.encode());

    	compare();
    }
	
	@Test
    public void constructorWithCompressedData() throws IOException, DataFormatException
    {
		movie.setSignature("CWS");
    	fixture = new Header();
    	fixture.decodeFromData(movie.encode());

    	compare();
    }
    
    private void compare()
    {
    	assertEquals(fixture.getSignature(), movie.getSignature());
    	assertEquals(fixture.getVersion(), movie.getVersion());
    	assertEquals(fixture.getMinX(), movie.getFrameSize().getMinX());
    	assertEquals(fixture.getMinY(), movie.getFrameSize().getMinY());
    	assertEquals(fixture.getMaxX(), movie.getFrameSize().getMaxX());
    	assertEquals(fixture.getMaxY(), movie.getFrameSize().getMaxY());
    	assertEquals(fixture.getFrameRate(), movie.getFrameRate(), 0.0f);
    	assertEquals(fixture.getNumberOfFrames(), 1);
    }
}

