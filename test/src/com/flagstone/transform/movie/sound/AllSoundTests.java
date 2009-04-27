package com.flagstone.transform.movie.sound;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { DefineSoundTest.class, EnvelopeTest.class,
		SoundInfoTest.class, SoundStreamBlockTest.class,
		SoundStreamHeadTest.class, SoundStreamHead2Test.class,
		StartSoundTest.class, StartSound2Test.class, })
@SuppressWarnings("PMD.AtLeastOneConstructor")
public final class AllSoundTests {
}
