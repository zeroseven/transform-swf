package com.flagstone.transform.util.sound;

enum SoundEncoding {

	MP3("audio/mpeg", new MP3Decoder()), 
	WAV("audio/x-wav", new WAVDecoder());

	private final String mimeType;
	private final SoundProvider provider;

	private SoundEncoding(final String mimeType, final SoundProvider provider) {
		this.mimeType = mimeType;
		this.provider = provider;
	}

	public String getMimeType() {
		return mimeType;
	}

	public SoundProvider getProvider() {
		return provider;
	}
}
