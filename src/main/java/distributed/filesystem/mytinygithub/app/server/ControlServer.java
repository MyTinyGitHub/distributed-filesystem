package distributed.filesystem.mytinygithub.app.server;

import distributed.filesystem.mytinygithub.app.request.Header;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ControlServer {

    private final ConcurrentHashMap<String, Socket> workerNodes;
    private final FileUploader uploader;
    private final FileDownloader retriever;

    final public static int FILE_SIZE = 200;

    public ControlServer() {
        this.workerNodes = new ConcurrentHashMap<>();
        this.uploader = new FileUploader(this.workerNodes);
        this.retriever = new FileDownloader(this.workerNodes);
    }

    public void processRequest(Socket client) throws IOException {
        InputStream in = client.getInputStream();

        Header header = Header.extractHeader(in);
        var operation = Operations.valueOf(header.getValue("action").toUpperCase());

        switch (operation) {
            case CONNECT -> this.connectWorker(header, client);
            case POST -> this.uploader.upload(header, client);
            case GET -> this.retriever.download(header, client);
        }

    };

    private void connectWorker(Header header, Socket client) {
        String workerId = header.getValue("worker-id");
        System.out.println("[ Connecting worker " + workerId + " ]");
        workerNodes.put(workerId, client);
    }

}
