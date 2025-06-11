package distributed.filesystem.mytinygithub.app.server;

import distributed.filesystem.mytinygithub.app.request.Header;
import distributed.filesystem.mytinygithub.app.request.ProcessFile;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class FileDownloader {

    private final Map<String, Socket> workerNodes;

    public FileDownloader(Map<String, Socket> workerNodes) {
        this.workerNodes = workerNodes;
    }


    public void download(Header header, Socket socket) throws IOException {
        var filename = header.getValue("filename");

        Header reqHeader = new Header();
        reqHeader.addValue("action", Operations.LIST.toString());
        reqHeader.addValue("filename", filename);


        List<ImmutableTriple<Socket, String, Integer>> fileInfo = new ArrayList<>();
        for (var nodes : workerNodes.entrySet()) {
            var worker = nodes.getValue();

            DataInputStream in = new DataInputStream(worker.getInputStream());
            DataOutputStream out = new DataOutputStream(worker.getOutputStream());

            Header.write(reqHeader, out);
            var resHeader = Header.extractHeader(in);

            byte[] unprocessedParts = in.readNBytes(Integer.parseInt(resHeader.getValue("size")));
            String[] partsArray = new String(unprocessedParts).split(",");
            System.out.println(Arrays.toString(partsArray));

            for(var p : partsArray) {

                String[] parts = p.split(":");
                var part = parts[0];
                int size = Integer.parseInt(parts[1]);

                fileInfo.add(new ImmutableTriple<>(worker, part, size));

            }
        }

        int finalFileSize = fileInfo.stream().mapToInt(ImmutableTriple::getRight).sum();

        Header resHeader = new Header();
        resHeader.addValue("size", String.valueOf(finalFileSize));
        resHeader.addValue("filename", "result.dat");

        DataOutputStream consumerOut = new DataOutputStream(socket.getOutputStream());
        Header.write(resHeader, consumerOut);

        fileInfo.stream()
                .sorted(Comparator.comparing(ImmutableTriple::getMiddle))
                .forEachOrdered(pair -> this.downloadFile(filename, pair.getMiddle(), pair.left, socket));

    }

    public void downloadFile(String filename, String part, Socket provider, Socket consumer)    {
        Header header = new Header();
        header.addValue("action", Operations.GET.getAction());
        header.addValue("filename", filename);
        header.addValue("part", part);

        try {
            DataOutputStream providerOut = new DataOutputStream(provider.getOutputStream());
            DataInputStream providerIn = new DataInputStream(provider.getInputStream());

            Header.write(header, providerOut);

            DataOutputStream consumerOut = new DataOutputStream(consumer.getOutputStream());
            ProcessFile.processFile(providerIn, consumerOut, ControlServer.FILE_SIZE);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
