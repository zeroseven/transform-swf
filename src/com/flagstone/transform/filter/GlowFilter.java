package com.flagstone.transform.filter;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

public final class GlowFilter implements Filter {

	public GlowFilter(GlowFilter object) {
		
	}
	
	public GlowFilter copy() {
		return new GlowFilter(this);	}

	public int prepareToEncode(final SWFEncoder coder, final Context context)
	{
		return 0;
	}

	public void encode(final SWFEncoder coder, final Context context) throws CoderException
	{
	}

	public void decode(final SWFDecoder coder, final Context context) throws CoderException
	{
	}
}
