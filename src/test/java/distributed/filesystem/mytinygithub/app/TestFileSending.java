package distributed.filesystem.mytinygithub.app;

import distributed.filesystem.mytinygithub.app.client.Client;
import distributed.filesystem.mytinygithub.app.server.ControlServer;
import distributed.filesystem.mytinygithub.app.server.Operations;
import distributed.filesystem.mytinygithub.app.server.WorkerServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TestFileSending {

    @Test
    public void testSingleFileAndSingleWorker() throws InterruptedException {
        int serverPort = 5002;

        var serverThread = createServerThread(serverPort);
        serverThread.start();

        Thread.sleep(2000);

        var worker = createWorkerNode("worker-node-1", serverPort);
        var workerThread = createAndStartWorkerThread(worker);

        Thread.sleep(2000);

        sendFile("test.dat", serverPort);

        Thread.sleep(10000);

        var paths = getDirectories(worker);

        Assertions.assertEquals(1, paths.size());
        Assertions.assertTrue(() -> paths.get(0).endsWith("test.dat-0"));

        retrieveFile("test.dat", serverPort);

        Thread.sleep(10000);

        workerThread.interrupt();
        serverThread.interrupt();
    }

    @Test
    public void testMultipleFilesSingleWorker() throws InterruptedException {
        int serverPort = 5001;

        var serverThread = createServerThread(serverPort);
        serverThread.start();

        Thread.sleep(2000);

        var worker = createWorkerNode("worker-node-1", serverPort);
        var workerThread = createAndStartWorkerThread(worker);

        Thread.sleep(5000);

        sendFile("test-bigger.dat", serverPort);


        Thread.sleep(10000);


        var paths = getDirectories(worker);

        Assertions.assertEquals(2, paths.size());
        Assertions.assertTrue(() -> paths.get(0).endsWith("test-bigger.dat-0"));

        retrieveFile("test-bigger.dat", serverPort);

        Thread.sleep(10000);

        serverThread.interrupt();
        workerThread.interrupt();
    }

    @Test
    public void testSingleFilesMultipleWorkers() throws InterruptedException {
        int serverPort = 5000;

        var serverThread = createServerThread(serverPort);
        serverThread.start();

        Thread.sleep(2000);

        var worker1 = createWorkerNode("worker-node-1", serverPort);
        var worker1Thread = createAndStartWorkerThread(worker1);

        var worker2 = createWorkerNode("worker-node-2", serverPort);
        var worker2Thread = createAndStartWorkerThread(worker2);

        var worker3 = createWorkerNode("worker-node-3", serverPort);
        var worker3Thread = createAndStartWorkerThread(worker3);

        Thread.sleep(2000);

        sendFile("test-bigger.dat", serverPort);

        Thread.sleep(10000);

        var paths1 = getDirectories(worker1);
        var paths2 = getDirectories(worker2);
        var paths3 = getDirectories(worker3);

        Assertions.assertEquals(0, paths1.size());
        Assertions.assertEquals(1, paths2.size());
        Assertions.assertEquals(1, paths3.size());

        Assertions.assertTrue(() -> paths2.get(0).endsWith("test-bigger.dat-1"));
        Assertions.assertTrue(() -> paths3.get(0).endsWith("test-bigger.dat-0"));

        retrieveFile("test-bigger.dat", serverPort);

        Thread.sleep(10000);

        worker1Thread.interrupt();
        worker2Thread.interrupt();
        worker3Thread.interrupt();

        serverThread.interrupt();
    }

    private WorkerServer createWorkerNode(String id, int serverPort) {
        WorkerServer workerServer = new WorkerServer();
        workerServer.setPort(serverPort);
        workerServer.setId(id);

        return workerServer;
    }

    private Thread createAndStartWorkerThread(WorkerServer workerServer) {
        var workerThread = new Thread(workerServer);
        workerThread.start();
        return workerThread;
    }

    private List<Path> getDirectories(WorkerServer worker) {
        Path tempPath = worker.getDirectory();
        List<Path> paths = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(tempPath)) {
            for (Path path : stream) {
                paths.add(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return paths;
    }

    private Thread createServerThread(int port) {
        return new Thread(() -> {
            Socket socket = null;
            try(ServerSocket server = new ServerSocket(port)) {
                var control = new ControlServer();

                while(true) {
                    socket = server.accept();
                    control.processRequest(socket);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void sendFile(String fileName, int serverPort) {
        Client client = new Client();
        var resource = Objects.requireNonNull(this.getClass().getResource(fileName));
        client.setFilename(resource.getFile());
        client.setOperation(Operations.POST);
        client.setPort(serverPort);
        client.run();
    }

    private void retrieveFile(String fileName, int serverPort) {
        Client client = new Client();
        client.setFilename(fileName);
        client.setOperation(Operations.GET);
        client.setPort(serverPort);
        client.run();
    }
}
