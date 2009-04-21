package com.flagstone.transform.util.font;

enum FontEncoding {
	
	SWF("swf", new SWFDecoder()),
	TTF("ttf", new TTFDecoder());
	
	private final String mimeType;
	private final FontProvider provider;
	
	private FontEncoding(String mimeType, FontProvider provider) {
		this.mimeType = mimeType;
		this.provider = provider;
	}
	
	public String getMimeType() {
		return mimeType;
	}
	
	public FontProvider getProvider() {
		return provider;
	}
}
