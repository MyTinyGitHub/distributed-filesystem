package distributed.filesystem.mytinygithub.app.server;

import distributed.filesystem.mytinygithub.app.request.Header;
import distributed.filesystem.mytinygithub.app.request.ProcessFile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static distributed.filesystem.mytinygithub.app.server.ControlServer.FILE_SIZE;

public class FileUploader {

    private final Map<String, Socket> workerNodes;

    public FileUploader(Map<String, Socket> workerNodes) {
        this.workerNodes = workerNodes;
    }

    public void upload(Header header, Socket client) throws IOException {
        int processed = 0;
        int toProcess = client.getInputStream().available();
        while (processed < toProcess) {
            processed = distributePayload(header, client, processed, toProcess);
        }
    }

    private int distributePayload(Header header, Socket client, int processed, int toProcess) throws IOException {
        int from = processed;
        for (var worker : workerNodes.values()) {
            if (from >= toProcess) {
                return from;
            }

            int to = Math.min(from + FILE_SIZE, toProcess);

            processWork(header, worker, client, from, to, from / FILE_SIZE);

            from = to;
        }

        return from;
    }

    private void processWork(Header header, Socket worker, Socket client, long from, long to, int part) throws IOException {

        var out = new DataOutputStream(worker.getOutputStream());
        var in = new DataInputStream(client.getInputStream());

        header.replaceValue("part", String.valueOf(part));
        header.replaceValue("size", String.valueOf(to - from));

        Header.write(header, out);

        ProcessFile.processFile(in, out, to - from);
    }
}
