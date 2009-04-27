package com.flagstone.transform.util.image;

enum ImageEncoding {

	BMP("image/bmp", new BMPDecoder()), GIF("image/gif",
			new BufferedImageDecoder()), IFF("image/iff",
			new BufferedImageDecoder()), JPEG("image/jpeg", new JPGDecoder()), PBM(
			"image/x-portable-bitmap", new BufferedImageDecoder()), PCX(
			"image/pcx", new BufferedImageDecoder()), PGM(
			"image/x-portable-pixmap", new BufferedImageDecoder()), PNG(
			"image/png", new PNGDecoder()), PSD("image/psd",
			new BufferedImageDecoder()), RAS("image/ras",
			new BufferedImageDecoder());

	private final String mimeType;
	private final ImageProvider provider;

	private ImageEncoding(final String mimeType, final ImageProvider provider) {
		this.mimeType = mimeType;
		this.provider = provider;
	}

	public String getMimeType() {
		return mimeType;
	}

	public ImageProvider getProvider() {
		return provider;
	}
}
