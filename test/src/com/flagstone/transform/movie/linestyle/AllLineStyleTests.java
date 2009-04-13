package com.flagstone.transform.movie.linestyle;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.flagstone.transform.coder.AllCoderTests;
import com.flagstone.transform.movie.AllMovieTests;
import com.flagstone.transform.movie.action.AllActionTests;
import com.flagstone.transform.movie.datatype.AllDataTypeTests;
import com.flagstone.transform.movie.font.AllFontTests;
import com.flagstone.transform.movie.meta.AllMetaTests;
import com.flagstone.transform.movie.movieclip.AllMovieClipTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	LineStyleTest.class,
	MorphLineStyleTest.class,
        })
public final class AllLineStyleTests {
}
