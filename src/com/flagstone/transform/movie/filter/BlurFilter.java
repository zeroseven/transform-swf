package com.flagstone.transform.movie.filter;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

public final class BlurFilter implements Filter {

	public BlurFilter(BlurFilter object) {

	}

	public BlurFilter copy() {
		return new BlurFilter(this);
	}

	public int prepareToEncode(final SWFEncoder coder) {
		return 0;
	}

	public void encode(final SWFEncoder coder) throws CoderException {
	}

	public void decode(final SWFDecoder coder) throws CoderException {
	}
}
