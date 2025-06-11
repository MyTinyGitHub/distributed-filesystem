package distributed.filesystem.mytinygithub.app.server;

import distributed.filesystem.mytinygithub.app.request.Header;
import distributed.filesystem.mytinygithub.app.request.ProcessFile;
import picocli.CommandLine;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CommandLine.Command(
        name = "worker-server",
        description = "starts a worker server to store files"
)
public class WorkerServer implements Runnable {

    private Path directory;

    @CommandLine.Option(
            names = "port",
            required = true
    )
    private int port;

    @CommandLine.Option(
            names = "id",
            required = true
    )
    private String id;

    private final List<SavedFileStruct> savedFiles;

    public WorkerServer() {
        savedFiles = new ArrayList<>();
    }

    public void run() {
        try {
            this.directory = Files.createTempDirectory("");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (Socket socket = new Socket()) {

            this.connectToMaster(socket);

            var in = new DataInputStream(socket.getInputStream());
            var out = new DataOutputStream(socket.getOutputStream());

            while (socket.isConnected()) {
                Header header = Header.extractHeader(in);
                var action = Operations.valueOf(header.getValue("action"));

                switch (action) {
                    case POST -> processFileUpload(header, in);
                    case LIST -> processInfoParts(header, out);
                    case GET -> processFileRetrieve(header, out);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void processFileRetrieve(Header header, DataOutputStream out) throws IOException {
        String fileName = header.getValue("filename");
        String part = header.getValue("part");

        savedFiles.stream()
                .filter(sfs -> sfs.filename().endsWith(fileName))
                .filter(sfs -> sfs.part().equals(part))
                .forEach(sfs -> uploadFileToServer(sfs, out));
    }

    public void uploadFileToServer(SavedFileStruct sfs, DataOutputStream out) {
        String fileName = sfs.filename();
        try (var file = new FileInputStream(fileName)) {

            Header header = new Header();
            header.addValue("size", String.valueOf(file.available()));
            header.addValue("filename", fileName);

            Header.write(header, out);
            out.write(file.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void processInfoParts(Header header, DataOutputStream out) throws IOException {
        String fileName = header.getValue("filename");

        String payload = savedFiles.stream()
                .filter(sfs -> sfs.filename().endsWith(fileName))
                .map(sfs -> String.format("%s:%s", sfs.part(), sfs.size()))
                .collect(Collectors.joining(","));

        header = new Header();
        header.addValue("size", String.valueOf(payload.getBytes().length));
        header.addValue("filename", fileName);

        Header.write(header, out);

        out.write(payload.getBytes());

    }

    public void processFileUpload(Header header, DataInputStream in) throws IOException {
        try (var outFile = ProcessFile.createFileOutputStream(header, directory)) {
            ProcessFile.processFile(in, outFile, Long.parseLong(header.getValue("size")));

            String part = header.getValue("part");
            String filename = header.getValue("filename");

            int size = Integer.parseInt(header.getValue("size"));

            savedFiles.add(new SavedFileStruct(filename, part, size));
        }
    }

    public void connectToMaster(Socket socket) throws IOException {
        socket.connect(new InetSocketAddress(port));

        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        Header header = new Header();
        header.addValue("action", Operations.CONNECT.getAction());
        header.addValue("worker-id", id);

        Header.write(header, out);
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Path getDirectory() {
        return directory;
    }
}
