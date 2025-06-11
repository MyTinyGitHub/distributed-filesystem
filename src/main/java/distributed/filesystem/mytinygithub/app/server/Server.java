package distributed.filesystem.mytinygithub.app.server;


import picocli.CommandLine.Command;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

@Command(
        name="server",
        description="Starts a TCP Server"
)
public class Server implements Runnable {

    private final ControlServer controlServer;

    public Server() {
        controlServer = new ControlServer();
    }


    @Override
    public void run() {
        try(ServerSocket serverSocket = new ServerSocket(5000)) {
            while(true) {
                Socket client = serverSocket.accept();
                controlServer.processRequest(client);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
