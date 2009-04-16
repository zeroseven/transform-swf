package com.flagstone.transform;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.flagstone.transform.coder.AllCoderTests;
import com.flagstone.transform.movie.AllMovieTests;
import com.flagstone.transform.movie.action.AllActionTests;
import com.flagstone.transform.movie.button.AllButtonTests;
import com.flagstone.transform.movie.datatype.AllDataTypeTests;
import com.flagstone.transform.movie.fillstyle.AllFillStyleTests;
import com.flagstone.transform.movie.filter.AllFilterTests;
import com.flagstone.transform.movie.font.AllFontTests;
import com.flagstone.transform.movie.image.AllImageTests;
import com.flagstone.transform.movie.linestyle.AllLineStyleTests;
import com.flagstone.transform.movie.meta.AllMetaTests;
import com.flagstone.transform.movie.movieclip.AllMovieClipTests;
import com.flagstone.transform.movie.shape.AllShapeTests;
import com.flagstone.transform.movie.sound.AllSoundTests;
import com.flagstone.transform.movie.text.AllTextTests;
import com.flagstone.transform.movie.video.AllVideoTests;
import com.flagstone.transform.video.AllFlashVideoTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	AllMovieTests.class,
	AllActionTests.class,
	AllButtonTests.class,
	AllCoderTests.class,
	AllDataTypeTests.class,
	AllFillStyleTests.class,
	AllFilterTests.class,
	AllFontTests.class,
	AllImageTests.class,
	AllLineStyleTests.class,
	AllMetaTests.class,
	AllMovieClipTests.class,
	AllShapeTests.class,
	AllSoundTests.class,
	AllTextTests.class,
	AllVideoTests.class,
	AllFlashVideoTests.class,
        })
public final class AllUnitTests {
}
