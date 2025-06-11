package distributed.filesystem.mytinygithub.app.client;

import distributed.filesystem.mytinygithub.app.request.Header;
import distributed.filesystem.mytinygithub.app.server.Operations;
import picocli.CommandLine;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

@CommandLine.Command(
        name="client",
        description="Starts a TCP client"
)
public class Client implements Runnable {

    @CommandLine.Option(
            names = "filename",
            required = true
    )
    private String filename;

    @CommandLine.Option(
            names = "action",
            required = true
    )
    private Operations operation;

    private Integer port;

    public Client() {}

    public void run() {
        switch (operation) {
            case POST -> uploadFile();
            case GET -> downloadFile();
        }
    }

    private void downloadFile() {

        try(var socket = new Socket()) {
            socket.connect(new InetSocketAddress(port));

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            Header header = new Header();
            header.addValue("filename", filename);
            header.addValue("action", operation.toString());

            Header.write(header, out);

            var headers = Header.extractHeader(in);
            


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void uploadFile() {
        try(var socket = new Socket()) {
            var file = new File(filename);

            socket.connect(new InetSocketAddress(port));

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            Header header = new Header();
            header.addValue("filename", filename);
            header.addValue("action", operation.toString());
            header.addValue("size", String.valueOf(file.length()));

            Header.write(header, out);

            try(var fos = new FileInputStream(file)) {
                out.write(fos.readAllBytes());
            }

            out.flush();
            out.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setOperation(Operations operation) {
        this.operation = operation;
    }

    public Operations getOperation() {
        return operation;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
