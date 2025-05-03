package distributed.filesystem.mytinygithub.app.server.request;

import distributed.filesystem.mytinygithub.app.server.HttpMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

public class HttpDecoder {

    private static Logger logger = LogManager.getLogger(HttpDecoder.class);

    public static Optional<HttpRequest> decode(final InputStream inputStream) {
        return readMessage(inputStream).flatMap(HttpDecoder::buildRequest);
    }

    private static Optional<List<String>> readMessage(final InputStream inputStream) {
        try {
            if (!(inputStream.available() > 0)) {
                return Optional.empty();
            }

            final char[] inBuffer = new char[inputStream.available()];
            final InputStreamReader inReader = new InputStreamReader(inputStream);
            final int read = inReader.read(inBuffer);

            List<String> message = new ArrayList<>();

            try (Scanner sc = new Scanner(new String(inBuffer))) {
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    message.add(line);
                }
            }

            return Optional.of(message);
        } catch (Exception e) {
            logger.error(e);

            return Optional.empty();
        }
    }

    private static Optional<HttpRequest> buildRequest(List<String> message) {
        if (message.isEmpty()) {
            return Optional.empty();
        }

        String firstLine = message.get(0);
        String[] httpInfo = firstLine.split(" ");

        if (httpInfo.length != 3) {
            return Optional.empty();
        }

        String protocolVersion = httpInfo[2];
        if (!protocolVersion.equals("HTTP/1.1")) {
            return Optional.empty();
        }

        try {
            HttpRequest request = new HttpRequest();

            request.getHeaders().setHttpMethod(HttpMethod.valueOf(httpInfo[0]));
            request.getHeaders().parse(message);

            request.setUri(new URI(httpInfo[1]));

            byte[] payload = message.stream()
                    .skip(request.getHeaders().getValues().size() + 2)
                    .collect(Collectors.joining("\n"))
                    .getBytes();

            request.setPayload(payload);

            return Optional.of(request);
        } catch (URISyntaxException | IllegalArgumentException e) {
            logger.error(e);

            return Optional.empty();
        }
    }
}
