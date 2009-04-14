package com.flagstone.transform.factory.movie;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.movie.Background;
import com.flagstone.transform.movie.DefineData;
import com.flagstone.transform.movie.DoABC;
import com.flagstone.transform.movie.DoAction;
import com.flagstone.transform.movie.FrameLabel;
import com.flagstone.transform.movie.Free;
import com.flagstone.transform.movie.LimitScript;
import com.flagstone.transform.movie.MovieObject;
import com.flagstone.transform.movie.MovieTag;
import com.flagstone.transform.movie.Place;
import com.flagstone.transform.movie.Place2;
import com.flagstone.transform.movie.Place3;
import com.flagstone.transform.movie.Remove;
import com.flagstone.transform.movie.Remove2;
import com.flagstone.transform.movie.ScalingGrid;
import com.flagstone.transform.movie.ShowFrame;
import com.flagstone.transform.movie.SymbolClass;
import com.flagstone.transform.movie.TabOrder;
import com.flagstone.transform.movie.Types;
import com.flagstone.transform.movie.button.ButtonColorTransform;
import com.flagstone.transform.movie.button.ButtonSound;
import com.flagstone.transform.movie.button.DefineButton;
import com.flagstone.transform.movie.button.DefineButton2;
import com.flagstone.transform.movie.font.DefineFont;
import com.flagstone.transform.movie.font.DefineFont2;
import com.flagstone.transform.movie.font.DefineFontName;
import com.flagstone.transform.movie.font.FontInfo;
import com.flagstone.transform.movie.font.FontInfo2;
import com.flagstone.transform.movie.image.DefineImage;
import com.flagstone.transform.movie.image.DefineImage2;
import com.flagstone.transform.movie.image.DefineJPEGImage;
import com.flagstone.transform.movie.image.DefineJPEGImage2;
import com.flagstone.transform.movie.image.DefineJPEGImage3;
import com.flagstone.transform.movie.image.JPEGEncodingTable;
import com.flagstone.transform.movie.meta.EnableDebugger;
import com.flagstone.transform.movie.meta.EnableDebugger2;
import com.flagstone.transform.movie.meta.Export;
import com.flagstone.transform.movie.meta.FileAttributes;
import com.flagstone.transform.movie.meta.Import;
import com.flagstone.transform.movie.meta.Import2;
import com.flagstone.transform.movie.meta.MovieMetaData;
import com.flagstone.transform.movie.meta.PathsArePostscript;
import com.flagstone.transform.movie.meta.Protect;
import com.flagstone.transform.movie.meta.ScenesAndLabels;
import com.flagstone.transform.movie.meta.SerialNumber;
import com.flagstone.transform.movie.movieclip.DefineMovieClip;
import com.flagstone.transform.movie.movieclip.InitializeMovieClip;
import com.flagstone.transform.movie.movieclip.QuicktimeMovie;
import com.flagstone.transform.movie.shape.DefineMorphShape;
import com.flagstone.transform.movie.shape.DefineShape;
import com.flagstone.transform.movie.shape.DefineShape2;
import com.flagstone.transform.movie.shape.DefineShape3;
import com.flagstone.transform.movie.sound.DefineSound;
import com.flagstone.transform.movie.sound.SoundStreamBlock;
import com.flagstone.transform.movie.sound.SoundStreamHead;
import com.flagstone.transform.movie.sound.SoundStreamHead2;
import com.flagstone.transform.movie.sound.StartSound;
import com.flagstone.transform.movie.sound.StartSound2;
import com.flagstone.transform.movie.text.DefineText;
import com.flagstone.transform.movie.text.DefineText2;
import com.flagstone.transform.movie.text.DefineTextField;
import com.flagstone.transform.movie.video.DefineVideo;
import com.flagstone.transform.movie.video.VideoFrame;

/**
 * Factory is the default implementation of an SWFFactory which used to create 
 * instances of Transform classes.
 */
@SuppressWarnings("PMD")
public final class MovieFactory implements SWFFactory<MovieTag> {

	public MovieTag getObject(final SWFDecoder coder) throws CoderException {

		MovieTag obj;

		switch (coder.scanByte()) {
		case Types.SHOW_FRAME:
			obj = ShowFrame.getInstance();
			break;
		case Types.DEFINE_SHAPE:
			obj = new DefineShape(coder);
			break;
		case Types.PLACE:
			obj = new Place(coder);
			break;
		case Types.REMOVE:
			obj = new Remove(coder);
			break;
		case Types.DEFINE_JPEG_IMAGE:
			obj = new DefineJPEGImage(coder);
			break;
		case Types.DEFINE_BUTTON:
			obj = new DefineButton(coder);
			break;
		case Types.JPEG_TABLES:
			obj = new JPEGEncodingTable(coder);
			break;
		case Types.SET_BACKGROUND_COLOR:
			obj = new Background(coder);
			break;
		case Types.DEFINE_FONT:
			obj = new DefineFont(coder);
			break;
		case Types.DEFINE_TEXT:
			obj = new DefineText(coder);
			break;
		case Types.DO_ACTION:
			obj = new DoAction(coder);
			break;
		case Types.FONT_INFO:
			obj = new FontInfo(coder);
			break;
		case Types.DEFINE_SOUND:
			obj = new DefineSound(coder);
			break;
		case Types.START_SOUND:
			obj = new StartSound(coder);
			break;
		case Types.SOUND_STREAM_HEAD:
			obj = new SoundStreamHead(coder);
			break;
		case Types.SOUND_STREAM_BLOCK:
			obj = new SoundStreamBlock(coder);
			break;
		case Types.BUTTON_SOUND:
			obj = new ButtonSound(coder);
			break;
		case Types.DEFINE_IMAGE:
			obj = new DefineImage(coder);
			break;
		case Types.DEFINE_JPEG_IMAGE_2:
			obj = new DefineJPEGImage2(coder);
			break;
		case Types.DEFINE_SHAPE_2:
			obj = new DefineShape2(coder);
			break;
		case Types.BUTTON_COLOR_TRANSFORM:
			obj = new ButtonColorTransform(coder);
			break;
		case Types.PROTECT:
			obj = new Protect(coder);
			break;
		case Types.FREE:
			obj = new Free(coder);
			break;
		case Types.PLACE_2:
			obj = new Place2(coder);
			break;
		case Types.REMOVE_2:
			obj = new Remove2(coder);
			break;
		case Types.DEFINE_SHAPE_3:
			obj = new DefineShape3(coder);
			break;
		case Types.DEFINE_TEXT_2:
			obj = new DefineText2(coder);
			break;
		case Types.DEFINE_BUTTON_2:
			obj = new DefineButton2(coder);
			break;
		case Types.DEFINE_JPEG_IMAGE_3:
			obj = new DefineJPEGImage3(coder);
			break;
		case Types.DEFINE_IMAGE_2:
			obj = new DefineImage2(coder);
			break;
		case Types.DEFINE_MOVIE_CLIP:
			obj = new DefineMovieClip(coder);
			break;
		case Types.FRAME_LABEL:
			obj = new FrameLabel(coder);
			break;
		case Types.SOUND_STREAM_HEAD_2:
			obj = new SoundStreamHead2(coder);
			break;
		case Types.DEFINE_MORPH_SHAPE:
			obj = new DefineMorphShape(coder);
			break;
		case Types.DEFINE_FONT_2:
			obj = new DefineFont2(coder);
			break;
		case Types.PATHS_ARE_POSTSCRIPT:
			obj = PathsArePostscript.getInstance();
			break;
		case Types.DEFINE_TEXT_FIELD:
			obj = new DefineTextField(coder);
			break;
		case Types.QUICKTIME_MOVIE:
			obj = new QuicktimeMovie(coder);
			break;
		case Types.SERIAL_NUMBER:
			obj = new SerialNumber(coder);
			break;
		case Types.ENABLE_DEBUGGER:
			obj = new EnableDebugger(coder);
			break;
		case Types.EXPORT:
			obj = new Export(coder);
			break;
		case Types.IMPORT:
			obj = new Import(coder);
			break;
		case Types.INITIALIZE:
			obj = new InitializeMovieClip(coder);
			break;
		case Types.DEFINE_VIDEO:
			obj = new DefineVideo(coder);
			break;
		case Types.VIDEO_FRAME:
			obj = new VideoFrame(coder);
			break;
		case Types.FONT_INFO_2:
			obj = new FontInfo2(coder);
			break;
		case Types.ENABLE_DEBUGGER_2:
			obj = new EnableDebugger2(coder);
			break;
		case Types.LIMIT_SCRIPT:
			obj = new LimitScript(coder);
			break;
		case Types.TAB_ORDER:
			obj = new TabOrder(coder);
			break;
		case Types.FILE_ATTRIBUTES:
			obj = new FileAttributes(coder);
			break;
		case Types.PLACE_3:
			obj = new Place3(coder);
			break;
		case Types.IMPORT_2:
			obj = new Import2(coder);
			break;
		case Types.SYMBOL:
			obj = new SymbolClass(coder);
			break;
		case Types.METADATA:
			obj = new MovieMetaData(coder);
			break;
		case Types.DEFINE_SCALING_GRID:
			obj = new ScalingGrid(coder);
			break;
		case Types.SCENES_AND_LABELS:
			obj = new ScenesAndLabels(coder);
			break;
		case Types.DO_ABC:
			obj = new DoABC(coder);
			break;
		case Types.DEFINE_BINARY_DATA:
			obj = new DefineData(coder);
			break;
		case Types.DEFINE_FONT_NAME:
			obj = new DefineFontName(coder);
			break;
		case Types.START_SOUND_2:
			obj = new StartSound2(coder);
			break;
		default:
			obj = new MovieObject(coder);
			break;
		}
		return obj;
	}
}
