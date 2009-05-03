package com.flagstone.transform.util.font;

/** TODO(class). */
enum FontEncoding {

    /** TODO(method). */
    SWF("swf", new SWFFontDecoder()),
    /** TODO(method). */
    TTF("ttf", new TTFDecoder());

    private final String mimeType;
    private final FontProvider provider;

    private FontEncoding(final String mimeType, final FontProvider provider) {
        this.mimeType = mimeType;
        this.provider = provider;
    }

    /** TODO(method). */
    public String getMimeType() {
        return mimeType;
    }

    /** TODO(method). */
    public FontProvider getProvider() {
        return provider;
    }
}
