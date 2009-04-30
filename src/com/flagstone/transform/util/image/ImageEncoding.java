package com.flagstone.transform.util.image;

public enum ImageEncoding {
    /** TODO(doc). */
    BMP("image/bmp", new BMPDecoder()),
    /** TODO(doc). */
    GIF("image/gif", new BufferedImageDecoder()),
    /** TODO(doc). */
    IFF("image/iff", new BufferedImageDecoder()),
    /** TODO(doc). */
    JPEG("image/jpeg", new JPGDecoder()),
    /** TODO(doc). */
    PBM("image/x-portable-bitmap", new BufferedImageDecoder()),
    /** TODO(doc). */
    PCX("image/pcx", new BufferedImageDecoder()),
    /** TODO(doc). */
    PGM("image/x-portable-pixmap", new BufferedImageDecoder()),
    /** TODO(doc). */
    PNG("image/png", new PNGDecoder()), 
    /** TODO(doc). */
    PSD("image/psd",new BufferedImageDecoder()),
    /** TODO(doc). */
    RAS("image/ras", new BufferedImageDecoder());

    private final String mimeType;
    private final ImageProvider provider;

    private ImageEncoding(final String mimeType, final ImageProvider provider) {
        this.mimeType = mimeType;
        this.provider = provider;
    }

    /** TODO(method). */
    public String getMimeType() {
        return mimeType;
    }

    /** TODO(method). */
    public ImageProvider getProvider() {
        return provider;
    }
}
