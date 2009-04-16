/*
 * GetUrl.java
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

package com.flagstone.transform.movie.action;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.Types;

/**
 * GetUrl is used to display a web page or load a movie clip into the Flash
 * Player.
 * 
 * <p>In addition to the URL to be loaded, GetUrl also contains a target which 
 * is either a level in the Flash Player where the movie clip will be loaded or 
 * frame or window in the browser where the web page will be displayed.</p>
 * 
 * <p>The following reserved words may be used to identify a specific frame or 
 * window in a web browser:</p>
 * 
 * <table class="datasheet">
 * 
 * <tr>
 * <td valign="top"><code>_blank</code></td>
 * <td>opens the new page in a new window.</td>
 * </tr>
 * 
 * <tr>
 * <td valign="top"><code>_self</code></td>
 * <td>opens the new page in the current window.</td>
 * </tr>
 * 
 * <tr>
 * <td valign="top"><code>_top</code></td>
 * <td>opens the new page in the top level frame of the current window.</td>
 * </tr>
 * 
 * <tr>
 * <td valign="top"><code>_parent</code></td>
 * <td>opens the new page in the parent frame of the frame where the Flash
 * Player id displayed.</td>
 * </tr>
 * 
 * <tr>
 * <td valign="top"><code>""</code></td>
 * <td>(blank string) opens the new page in the current frame or window.</td>
 * </tr>
 * 
 * </table>
 * 
 * <p>To load a movie clip into the currently playing movie then the target is a
 * string literal of the form "_level<i>n</i>". The Flash Player supports the
 * concept of virtual layers (analogous to the layers in the Display List).
 * Higher levels are displayed in front of lower levels. The background of each
 * level is transparent allowing movie clips on lower levels to be visible in
 * areas not filled by the movie clip on a given level. The main movie is loaded
 * into _level0. Movie clips are loaded into any level above this (1, 2, 3, ...).
 * If a movie clip is loaded into a level that already contains a movie
 * clip then the existing clip is replaced by the new one.</p>
 * 
 * @see GetUrl2
 */
public final class GetUrl implements Action
{
	private static final String FORMAT = "GetUrl: { url=%s; target=%s }";
	
	private String url;
	private String target;
	
	private transient int length;

	public GetUrl(final SWFDecoder coder, final SWFContext context) throws CoderException
	{
		coder.readByte();
		length = coder.readWord(2, false);
		url = coder.readString();
		target = coder.readString();
	}


	/**
	 * Creates a GetUrl with the specified url and target frame.
	 * 
	 * @param urlString
	 *            a fully qualified URL. Must not be null or an empty string.
	 * @param targetString
	 *            the location (in the Flash Player or web browser) where the
	 *            contents of file retrieved via the url will be displayed.
	 *            Must not be null.
	 */
	public GetUrl(String urlString, String targetString)
	{
		setUrl(urlString);
		setTarget(targetString);
	}

	/**
	 * Creates a GetUrl with the specified url. The target defaults to the
	 * current window.
	 * 
	 * @param urlString
	 *            a fully qualified URL. Must not be null or an empty string.
	 */
	public GetUrl(String urlString)
	{
		setUrl(urlString);
		target = "";
	}

	public GetUrl(GetUrl object)
	{
		url = object.url;
		target = object.target;
	}

	/**
	 * Returns the URL.
	 */
	public String getUrl()
	{
		return url;
	}
	
	/**
	 * Returns the name of the target frame.
	 */
	public String getTarget()
	{
		return target;
	}

	/**
	 * Sets the URL of the file to be retrieved.
	 * 
	 * @param aString
	 *            a fully qualified URL. Must not be null or an empty string.
	 */
	public void setUrl(String aString)
	{
		if (aString == null) {
			throw new IllegalArgumentException(Strings.STRING_CANNOT_BE_NULL);
		}
		if (aString.length() == 0) {
			throw new IllegalArgumentException(Strings.STRING_CANNOT_BE_EMPTY);
		}
		url = aString;
	}

	/**
	 * Sets the name of the Target where the URL will be displayed. The target
	 * may be a frame or window in a web browser when displaying a web page or a
	 * level in the current movie when loading a movie clip.
	 * 
	 * @param aString
	 *            the name of the location (in the Flash Player or web browser)
	 *            where contents of file retrieved via the url will be
	 *            displayed. Must not be null.
	 */
	public void setTarget(String aString)
	{
		if (aString == null) {
			throw new IllegalArgumentException(Strings.STRING_CANNOT_BE_NULL);
		}
		target = aString;
	}

	public GetUrl copy() {
		return new GetUrl(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, url, target);
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context)
	{
		length = coder.strlen(url);
		length += coder.strlen(target);

		return 3+length;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
		coder.writeByte(Types.GET_URL);
		coder.writeWord(length, 2);
		coder.writeString(url);
		coder.writeString(target);
	}
}
