package com.flagstone.transform;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.flagstone.transform.action.AllActionTests;
import com.flagstone.transform.button.AllButtonTests;
import com.flagstone.transform.coder.AllCoderTests;
import com.flagstone.transform.datatype.AllDataTypeTests;
import com.flagstone.transform.fillstyle.AllFillStyleTests;
import com.flagstone.transform.filter.AllFilterTests;
import com.flagstone.transform.font.AllFontTests;
import com.flagstone.transform.image.AllImageTests;
import com.flagstone.transform.linestyle.AllLineStyleTests;
import com.flagstone.transform.movie.shape.AllShapeTests;
import com.flagstone.transform.movie.sound.AllSoundTests;
import com.flagstone.transform.movie.text.AllTextTests;
import com.flagstone.transform.movieclip.AllMovieClipTests;
import com.flagstone.transform.video.AllVideoDataTests;

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
	AllMovieClipTests.class,
	AllShapeTests.class,
	AllSoundTests.class,
	AllTextTests.class,
	AllVideoTests.class,
	AllVideoDataTests.class,
        })
public final class AllUnitTests {
}
