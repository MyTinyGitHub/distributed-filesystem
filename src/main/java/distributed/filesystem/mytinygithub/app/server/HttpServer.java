package distributed.filesystem.mytinygithub.app.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {
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
