package com.flagstone.transform.filter;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

public final class BlurFilter implements Filter {

	public BlurFilter(final BlurFilter object) {
		//TODO Implement
	}

	public BlurFilter copy() {
		return new BlurFilter(this);
	}

	public int prepareToEncode(final SWFEncoder coder, final Context context) {
		return 0;
	}

	public void encode(final SWFEncoder coder, final Context context)
			throws CoderException {
		//TODO Implement
	}

	public void decode(final SWFDecoder coder, final Context context)
			throws CoderException {
		//TODO Implement
	}
}
