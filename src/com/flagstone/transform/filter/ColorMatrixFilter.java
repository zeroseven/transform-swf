package com.flagstone.transform.filter;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

public final class ColorMatrixFilter implements Filter {

	public ColorMatrixFilter(ColorMatrixFilter object) {
		
	}
	
	public ColorMatrixFilter copy() {
		return new ColorMatrixFilter(this);	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context)
	{
		return 0;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
	}

	public void decode(final SWFDecoder coder, final SWFContext context) throws CoderException
	{
	}
}
