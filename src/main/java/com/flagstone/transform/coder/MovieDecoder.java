package com.flagstone.transform.coder;

import com.flagstone.transform.Background;
import com.flagstone.transform.DefineData;
import com.flagstone.transform.DoABC;
import com.flagstone.transform.DoAction;
import com.flagstone.transform.EnableDebugger;
import com.flagstone.transform.EnableDebugger2;
import com.flagstone.transform.Export;
import com.flagstone.transform.MovieAttributes;
import com.flagstone.transform.FrameLabel;
import com.flagstone.transform.Free;
import com.flagstone.transform.Import;
import com.flagstone.transform.Import2;
import com.flagstone.transform.LimitScript;
import com.flagstone.transform.MovieMetaData;
import com.flagstone.transform.MovieObject;
import com.flagstone.transform.Place;
import com.flagstone.transform.Place2;
import com.flagstone.transform.Place3;
import com.flagstone.transform.Protect;
import com.flagstone.transform.Remove;
import com.flagstone.transform.Remove2;
import com.flagstone.transform.ScalingGrid;
import com.flagstone.transform.ScenesAndLabels;
import com.flagstone.transform.SerialNumber;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.SymbolClass;
import com.flagstone.transform.TabOrder;
import com.flagstone.transform.button.ButtonColorTransform;
import com.flagstone.transform.button.ButtonSound;
import com.flagstone.transform.button.DefineButton;
import com.flagstone.transform.button.DefineButton2;
import com.flagstone.transform.font.DefineFont;
import com.flagstone.transform.font.DefineFont2;
import com.flagstone.transform.font.DefineFontName;
import com.flagstone.transform.font.FontAlignment;
import com.flagstone.transform.font.FontInfo;
import com.flagstone.transform.font.FontInfo2;
import com.flagstone.transform.image.DefineImage;
import com.flagstone.transform.image.DefineImage2;
import com.flagstone.transform.image.DefineJPEGImage;
import com.flagstone.transform.image.DefineJPEGImage2;
import com.flagstone.transform.image.DefineJPEGImage3;
import com.flagstone.transform.image.JPEGEncodingTable;
import com.flagstone.transform.movieclip.DefineMovieClip;
import com.flagstone.transform.movieclip.InitializeMovieClip;
import com.flagstone.transform.movieclip.QuicktimeMovie;
import com.flagstone.transform.shape.DefineMorphShape;
import com.flagstone.transform.shape.DefineShape;
import com.flagstone.transform.shape.DefineShape2;
import com.flagstone.transform.shape.DefineShape3;
import com.flagstone.transform.shape.PathsArePostscript;
import com.flagstone.transform.sound.DefineSound;
import com.flagstone.transform.sound.SoundStreamBlock;
import com.flagstone.transform.sound.SoundStreamHead;
import com.flagstone.transform.sound.SoundStreamHead2;
import com.flagstone.transform.sound.StartSound;
import com.flagstone.transform.sound.StartSound2;
import com.flagstone.transform.text.DefineText;
import com.flagstone.transform.text.DefineText2;
import com.flagstone.transform.text.DefineTextField;
import com.flagstone.transform.text.TextSettings;
import com.flagstone.transform.video.DefineVideo;
import com.flagstone.transform.video.VideoFrame;

/**
 * Factory is the default implementation of an SWFFactory which used to create
 * instances of Transform classes.
 */
//TODO(class)
public final class MovieDecoder implements SWFFactory<MovieTag> {

    /** TODO(method). */
    public SWFFactory<MovieTag> copy() {
        return new MovieDecoder();
    }

    /** TODO(method). */
    public MovieTag getObject(final SWFDecoder coder, final Context context)
            throws CoderException {

        MovieTag obj;

        switch (coder.scanUnsignedShort() >>> 6) {
        case MovieTypes.SHOW_FRAME:
            obj = ShowFrame.getInstance();
            coder.adjustPointer(16);
            break;
        case MovieTypes.DEFINE_SHAPE:
            obj = new DefineShape(coder, context);
            break;
        case MovieTypes.PLACE:
            obj = new Place(coder, context);
            break;
        case MovieTypes.REMOVE:
            obj = new Remove(coder);
            break;
        case MovieTypes.DEFINE_JPEG_IMAGE:
            obj = new DefineJPEGImage(coder);
            break;
        case MovieTypes.DEFINE_BUTTON:
            obj = new DefineButton(coder, context);
            break;
        case MovieTypes.JPEG_TABLES:
            obj = new JPEGEncodingTable(coder);
            break;
        case MovieTypes.SET_BACKGROUND_COLOR:
            obj = new Background(coder, context);
            break;
        case MovieTypes.DEFINE_FONT:
            obj = new DefineFont(coder);
            break;
        case MovieTypes.DEFINE_TEXT:
            obj = new DefineText(coder, context);
            break;
        case MovieTypes.DO_ACTION:
            obj = new DoAction(coder, context);
            break;
        case MovieTypes.FONT_INFO:
            obj = new FontInfo(coder);
            break;
        case MovieTypes.DEFINE_SOUND:
            obj = new DefineSound(coder);
            break;
        case MovieTypes.START_SOUND:
            obj = new StartSound(coder);
            break;
        case MovieTypes.SOUND_STREAM_HEAD:
            obj = new SoundStreamHead(coder);
            break;
        case MovieTypes.SOUND_STREAM_BLOCK:
            obj = new SoundStreamBlock(coder);
            break;
        case MovieTypes.BUTTON_SOUND:
            obj = new ButtonSound(coder);
            break;
        case MovieTypes.DEFINE_IMAGE:
            obj = new DefineImage(coder);
            break;
        case MovieTypes.DEFINE_JPEG_IMAGE_2:
            obj = new DefineJPEGImage2(coder);
            break;
        case MovieTypes.DEFINE_SHAPE_2:
            obj = new DefineShape2(coder, context);
            break;
        case MovieTypes.BUTTON_COLOR_TRANSFORM:
            obj = new ButtonColorTransform(coder, context);
            break;
        case MovieTypes.PROTECT:
            obj = new Protect(coder);
            break;
        case MovieTypes.FREE:
            obj = new Free(coder);
            break;
        case MovieTypes.PLACE_2:
            obj = new Place2(coder, context);
            break;
        case MovieTypes.REMOVE_2:
            obj = new Remove2(coder);
            break;
        case MovieTypes.DEFINE_SHAPE_3:
            obj = new DefineShape3(coder, context);
            break;
        case MovieTypes.DEFINE_TEXT_2:
            obj = new DefineText2(coder, context);
            break;
        case MovieTypes.DEFINE_BUTTON_2:
            obj = new DefineButton2(coder, context);
            break;
        case MovieTypes.DEFINE_JPEG_IMAGE_3:
            obj = new DefineJPEGImage3(coder);
            break;
        case MovieTypes.DEFINE_IMAGE_2:
            obj = new DefineImage2(coder);
            break;
        case MovieTypes.DEFINE_MOVIE_CLIP:
            obj = new DefineMovieClip(coder, context);
            break;
        case MovieTypes.FRAME_LABEL:
            obj = new FrameLabel(coder);
            break;
        case MovieTypes.SOUND_STREAM_HEAD_2:
            obj = new SoundStreamHead2(coder);
            break;
        case MovieTypes.DEFINE_MORPH_SHAPE:
            obj = new DefineMorphShape(coder, context);
            break;
        case MovieTypes.DEFINE_FONT_2:
            obj = new DefineFont2(coder, context);
            break;
        case MovieTypes.PATHS_ARE_POSTSCRIPT:
            obj = PathsArePostscript.getInstance();
            if ((coder.readWord(2, false) & 0x3F) == 0x3F) {
                coder.readWord(4, false);
            }
            break;
        case MovieTypes.DEFINE_TEXT_FIELD:
            obj = new DefineTextField(coder, context);
            break;
        case MovieTypes.QUICKTIME_MOVIE:
            obj = new QuicktimeMovie(coder);
            break;
        case MovieTypes.SERIAL_NUMBER:
            obj = new SerialNumber(coder);
            break;
        case MovieTypes.ENABLE_DEBUGGER:
            obj = new EnableDebugger(coder);
            break;
        case MovieTypes.EXPORT:
            obj = new Export(coder);
            break;
        case MovieTypes.IMPORT:
            obj = new Import(coder);
            break;
        case MovieTypes.INITIALIZE:
            obj = new InitializeMovieClip(coder, context);
            break;
        case MovieTypes.DEFINE_VIDEO:
            obj = new DefineVideo(coder);
            break;
        case MovieTypes.VIDEO_FRAME:
            obj = new VideoFrame(coder);
            break;
        case MovieTypes.FONT_INFO_2:
            obj = new FontInfo2(coder);
            break;
        case MovieTypes.ENABLE_DEBUGGER_2:
            obj = new EnableDebugger2(coder);
            break;
        case MovieTypes.LIMIT_SCRIPT:
            obj = new LimitScript(coder);
            break;
        case MovieTypes.TAB_ORDER:
            obj = new TabOrder(coder);
            break;
        case MovieTypes.FILE_ATTRIBUTES:
            obj = new MovieAttributes(coder);
            break;
        case MovieTypes.PLACE_3:
            obj = new Place3(coder, context);
            break;
        case MovieTypes.IMPORT_2:
            obj = new Import2(coder);
            break;
        case MovieTypes.FONT_ALIGNMENT:
            obj = new FontAlignment(coder, context);
            break;
        case MovieTypes.TEXT_SETTINGS:
            obj = new TextSettings(coder);
            break;
        case MovieTypes.SYMBOL:
            obj = new SymbolClass(coder);
            break;
        case MovieTypes.METADATA:
            obj = new MovieMetaData(coder);
            break;
        case MovieTypes.DEFINE_SCALING_GRID:
            obj = new ScalingGrid(coder);
            break;
        case MovieTypes.SCENES_AND_LABELS:
            obj = new ScenesAndLabels(coder);
            break;
        case MovieTypes.DO_ABC:
            obj = new DoABC(coder);
            break;
        case MovieTypes.DEFINE_BINARY_DATA:
            obj = new DefineData(coder);
            break;
        case MovieTypes.DEFINE_FONT_NAME:
            obj = new DefineFontName(coder);
            break;
        case MovieTypes.START_SOUND_2:
            obj = new StartSound2(coder);
            break;
        default:
            obj = new MovieObject(coder);
            break;
        }
        return obj;
    }
}
