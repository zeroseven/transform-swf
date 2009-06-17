package tools;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import com.flagstone.transform.Video;
import com.flagstone.transform.coder.VideoTag;

public class VideoWriter {

    public void write(final Video video, final File file) throws IOException {
        final PrintWriter writer = new PrintWriter(file);
        write(video, writer);
        writer.close();
    }

    public void write(final Video video, final Writer writer)
            throws IOException {

        String str;

        int indent = 0;
        boolean start = false;

        for (final VideoTag tag : video.getObjects()) {

            str = tag.toString();

            for (final char c : str.toCharArray()) {

                if (c == '{') {
                    writer.append(c).append('\n');
                    indent++;
                    for (int i = 0; i < indent; i++) {
                        writer.append('\t');
                    }
                    start = true;
                } else if (c == '}') {
                    indent--;
                    writer.append('\n');
                    for (int i = 0; i < indent; i++) {
                        writer.append('\t');
                    }
                    writer.append(c);
                } else if (c == ';') {
                    writer.append(c).append('\n');
                    for (int i = 0; i < indent; i++) {
                        writer.append('\t');
                    }
                    start = true;
                } else if (c == '=') {
                    writer.append(' ').append('=').append(' ');
                } else if (c == ' ') {
                    if (!start) {
                        writer.append(c);
                    }
                } else {
                    writer.append(c);
                    start = false;
                }
            }
            writer.flush();
        }
    }
}
