package distributed.filesystem.mytinygithub.app.request;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class ProcessFile {
    private FileOutputStream saveFile;

    public ProcessFile() {

    }

    public static FileOutputStream createFileOutputStream(Header header, Path directory) throws FileNotFoundException {
        String path = header.getValue("filename");

        var splitPath = path.split("/");
        var fileName = splitPath[splitPath.length - 1] + "-" + header.getValue("part");

        return new FileOutputStream(new File(directory.toFile(), fileName));
    }

    public static void processFile(InputStream in, OutputStream out, long processSize) throws IOException {
        var bufferSize = 1024;

        while (processSize > 0) {

            byte[] buffer = new byte[processSize > bufferSize ? bufferSize : (int) processSize];

            processSize -= in.read(buffer);

            out.write(buffer);
        }

    }
}
