/*
 * Export.java
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

import java.util.LinkedHashMap;
import java.util.Map;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

/**
 * Export is used to export one or more shapes and other objects so they can be 
 * used in another Flash file.
 * 
 * <p>Since the identifier for an object is only unique within a given Flash file,
 * each object exported must be given a name so it can referenced when it is
 * imported.</p>
 */
public final class Export implements MovieTag
{
	private static final String FORMAT = "Export: { objects=%s }";
	
	private Map<Integer,String> objects;
	
	private transient int length;

	//TODO(doc)
	public Export(final SWFDecoder coder, final Context context) throws CoderException
	{
		length = coder.readWord(2, false) & 0x3F;
		
		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}

		int count = coder.readWord(2, false);
		objects = new LinkedHashMap<Integer,String>();

		for (int i = 0; i < count; i++)
		{
			int identifier = coder.readWord(2, false);
			String var = coder.readString();

			add(identifier, var);
		}
	}

	/**
	 * Creates an Export object with an empty array.
	 * 
	 * @param map
	 *            the table containing identifier/name pairs for the objects 
	 *            that will be exported from the movie.
	 */
	public Export(Map<Integer,String> map)
	{
		objects = map;
	}

	/**
	 * Creates an Export object that exports the object with the specified
	 * identifier. The exported object is assigned the specified name to allow
	 * it to be referenced in files importing the object.
	 * 
	 * @param uid
	 *            the identifier of the object to be exported. Must be in the 
	 *            range 1..65535.
	 * @param aString
	 *            the name of the exported object to allow it to be referenced.
	 *            Must not be an empty string or null.
	 */
	public Export(int uid, String aString)
	{
		objects = new LinkedHashMap<Integer,String>();
		add(uid, aString);
	}
	
	//TODO(doc)
	public Export(Export object) {
		objects = new LinkedHashMap<Integer,String>(object.objects.size());
		objects.putAll(object.objects);
	}

	/**
	 * Adds the identifier and name to the list of objects to be exported.
	 * 
	 * @param uid
	 *            the identifier of the object to be exported.
	 * @param aString
	 *            the name of the exported object to allow it to be referenced.
	 *            The name must not be null or an empty string.
	 */
	public final void add(int uid, String aString)
	{
		if (uid < 1 || uid > 65535) {
			throw new IllegalArgumentException(Strings.IDENTIFIER_OUT_OF_RANGE);
		}
		if (aString == null || aString.length() == 0) {
			throw new IllegalArgumentException(Strings.STRING_NOT_SET);
		}
			
		objects.put(uid, aString);
	}

	/**
	 * Returns the table of objects to be exported.
	 */
	public Map<Integer,String> getObjects()
	{
		return objects;
	}

	/**
	 * Sets the table of objects to be exported.
	 * 
	 * @param aTable
	 *            the table of objects being imported. Must not be null.
	 */
	public void setObjects(Map<Integer,String> aTable)
	{
		if (aTable == null) {
			throw new IllegalArgumentException(Strings.TABLE_CANNOT_BE_NULL);
		}
		objects = aTable;
	}


	/**
	 * Creates and returns a deep copy of this object.
	 */
	public Export copy() 
	{
		return new Export(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, objects);
	}

	public int prepareToEncode(final SWFEncoder coder, final Context context)
	{
		length = 2;

		for (Integer identifier : objects.keySet())
		{
			String var = (objects.get(identifier));

			length += 2;
			length += coder.strlen(var);
		}
		return (length > 62 ? 6:2) + length;
	}

	public void encode(final SWFEncoder coder, final Context context) throws CoderException
	{
		if (length > 62) {
			coder.writeWord((MovieTypes.EXPORT << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((MovieTypes.EXPORT << 6) | length, 2);
		}
		
		coder.writeWord(objects.size(), 2);

		for (Integer identifier : objects.keySet())
		{
			String var = (objects.get(identifier));

			coder.writeWord(identifier.intValue(), 2);
			coder.writeString(var);
		}
	}

	public void decode(final SWFDecoder coder, final Context context) throws CoderException
	{
		length = coder.readWord(2, false) & 0x3F;
		
		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}

		int count = coder.readWord(2, false);

		for (int i = 0; i < count; i++)
		{
			int identifier = coder.readWord(2, false);
			String var = coder.readString();

			add(identifier, var);
		}
	}
}
