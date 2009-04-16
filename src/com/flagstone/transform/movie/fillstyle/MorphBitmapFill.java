/*
 * MorphBitmapFill.java
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

package com.flagstone.transform.movie.fillstyle;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.datatype.CoordTransform;
import com.flagstone.transform.movie.shape.DefineMorphShape;

/**
 * MorphBitmapFill uses a bitmap image to fill an area of a morphing shape. Four
 * types of bitmap fill are supported:
 * 
 * <ol>
 * <li>Clipped - If the image is larger than the shape then it will be clipped.
 * Conversely if the area to be filled is larger than the image the colour at
 * the edge of the image is used to fill the remainder of the shape.</li>
 * 
 * <li>Tiled - if the area to be filled is larger than the image then the image
 * is tiled to fill the area, otherwise as with the Clipped style the colour at
 * the edge of the image will be use to fill the space available.</li>
 * 
 * <li>Unsmoothed Clipped - Same as Clipped but if the image is smaller than the
 * shape the colour used to fill the space available is not smoothed. This style
 * was added to increase performance with few visible artifacts.</li>
 * </tr>
 * 
 * <li>Unsmoothed Tiled - Same as Tiled but no smoothing is applied if the
 * colour at the edge of the image is used to fill the space available. Again
 * this was introduced to increase performance.</li>
 * </ol>
 * 
 * <p>
 * Two coordinate transforms define the appearance of the image at the start and
 * end of the morphing process. The most common use of the coordinate transform
 * is to scale an image so it displayed at the correct resolution. When an image
 * is loaded its width and height default to twips rather than pixels. An image
 * 300 x 200 pixels will be displayed as 300 x 200 twips (15 x 10 pixels).
 * Scaling the image by 20 (20 twips = 1 pixel) using the CoordTransform object
 * will restore it to its original size.
 * </p>
 * 
 * <p>
 * The coordinate transform is also used to control the image registration. An
 * image is drawn with the top left corner placed at the origin (0, 0) of the
 * shape being filled. The transform can be used to apply different translations
 * to the image so its position can be adjusted relative to the origin of the
 * enclosing shape.
 * </p>
 * 
 * @see DefineMorphShape
 */
public final class MorphBitmapFill implements FillStyle {

	private static final String FORMAT = "MorphBitmapFill: { identifier=%d; start=%s; end=%s }";

	private int type;
	private int identifier;
	private CoordTransform startTransform;
	private CoordTransform endTransform;

	public MorphBitmapFill(final SWFDecoder coder, final SWFContext context) throws CoderException {
		type = coder.readByte();
		identifier = coder.readWord(2, false);
		startTransform = new CoordTransform(coder);
		endTransform = new CoordTransform(coder);
	}

	/**
	 * Creates a MorphBitmapFill specifying the type, bitmap image and
	 * coordinate transforms for the image at the start and end of the morphing
	 * process.
	 * 
	 * @param type
	 *            the type of bitmap fill, must be one of the constants defined
	 *            in the FillStyle class, either TILED, CLIPPED,
	 *            UNSMOOTHED_TILED or UNSMOOTHED_CLIPPED.
	 * @param uid
	 *            the unique identifier for the image. Must be in the range
	 *            1..65535.
	 * @param start
	 *            the coordinate transform defining the appearance of the image
	 *            at the start of the morphing process.
	 * @param end
	 *            the coordinate transform defining the appearance of the image
	 *            at the end of the morphing process.
	 */
	public MorphBitmapFill(final int type, final int uid,
			final CoordTransform start, final CoordTransform end) {
		this.type = type;
		setIdentifier(uid);
		setStartTransform(start);
		setEndTransform(end);
	}
	
	public MorphBitmapFill(MorphBitmapFill object) {
		type = object.type;
		identifier = object.identifier;
		startTransform = object.startTransform;
		endTransform = object.endTransform;
	}

	/**
	 * Returns the unique identifier of the bitmap image.
	 */
	public int getIdentifier() {
		return identifier;
	}

	/**
	 * Returns the coordinate transform defining the appearance of the image at
	 * the start of the morphing process.
	 */
	public CoordTransform getStartTransform() {
		return startTransform;
	}

	/**
	 * Returns the coordinate transform defining the appearance of the image at
	 * the end of the morphing process.
	 */
	public CoordTransform getEndTransform() {
		return endTransform;
	}

	/**
	 * Sets the identifier of the bitmap image to be used in the morphing
	 * process.
	 * 
	 * @param uid
	 *            the unique identifier of the bitmap image. Must be in the
	 *            range 1..65535.
	 */
	public void setIdentifier(final int uid) {
		if ((uid < 1) || (uid > 65535)) {
			throw new IllegalArgumentException(Strings.IDENTIFIER_OUT_OF_RANGE);
		}
		identifier = uid;
	}

	/**
	 * Sets the coordinate transform defining the appearance of the image at the
	 * start of the morphing process.
	 * 
	 * @param aTransform
	 *            the starting coordinate transform. Must not be null.
	 */
	public void setStartTransform(final CoordTransform aTransform) {
		if (aTransform == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		startTransform = aTransform;
	}

	/**
	 * Sets the coordinate transform defining the appearance of the image at the
	 * end of the morphing process.
	 * 
	 * @param aTransform
	 *            the ending coordinate transform. Must not be null.
	 */
	public void setEndTransform(final CoordTransform aTransform) {
		if (aTransform == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		endTransform = aTransform;
	}

	public MorphBitmapFill copy() {
		return new MorphBitmapFill(this);
	}

	@Override
	public String toString() {
		return String.format(FORMAT, identifier, startTransform, endTransform);
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context) {
		return 3 + startTransform.prepareToEncode(coder, context) + endTransform.prepareToEncode(coder, context);
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException {
		coder.writeByte(type);
		coder.writeWord(identifier, 2);
		startTransform.encode(coder, context);
		endTransform.encode(coder, context);
	}
}
