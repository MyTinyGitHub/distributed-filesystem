package distributed.filesystem.mytinygithub.app.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.LoggerContextFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {
    private static final Logger logger = LogManager.getLogger(HttpServer.class);

    private final Map<String, RequestRunner> routes;
    private final ServerSocket serverSocket;
    private HttpHandler handler;

    public HttpServer(int port) throws IOException {
        routes = new HashMap<>();
        serverSocket = new ServerSocket(port);
    }

    public void addRoute(HttpMethod opCode, String path, RequestRunner runner) {
        this.routes.put(opCode.name().concat(path), runner);
    }

    public void start() throws IOException {
        handler = new HttpHandler(routes);

        while (true) {
            logger.info("Listening for TCP requests");
            Socket clientConnection = serverSocket.accept();
            handleConnection(clientConnection);
        }
    }

    private void handleConnection(Socket clientConnection) {
        try {
            handler.handleConnection(clientConnection.getInputStream(), clientConnection.getOutputStream());
        } catch (IOException ignored) {
        }
    }
}
