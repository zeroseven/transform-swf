package com.flagstone.transform;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.flagstone.transform.coder.AllCoderTests;
import com.flagstone.transform.movie.AllMovieTests;
import com.flagstone.transform.movie.action.AllActionTests;
import com.flagstone.transform.movie.datatype.AllDataTypeTests;
import com.flagstone.transform.movie.fillstyle.AllFillStyleTests;
import com.flagstone.transform.movie.font.AllFontTests;
import com.flagstone.transform.movie.linestyle.AllLineStyleTests;
import com.flagstone.transform.movie.meta.AllMetaTests;
import com.flagstone.transform.movie.movieclip.AllMovieClipTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	AllActionTests.class,
	AllCoderTests.class,
	AllDataTypeTests.class,
	AllFillStyleTests.class,
	AllFontTests.class,
	AllLineStyleTests.class,
	AllMovieTests.class,
	AllMetaTests.class,
	AllMovieClipTests.class
        })
public final class AllUnitTests {
}
