package distributed.filesystem.mytinygithub.app.server.response;

import distributed.filesystem.mytinygithub.app.server.HttpStatusCode;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ResponseWriter {

    /**
     * Write a HTTPResponse to an outputstream
     *
     * @param outputStream - the outputstream
     * @param response     - the HTTPResponse
     */
    public static void writeResponse(final DataOutputStream outputStream, final HttpResponse response) {
        try {
            final int statusCode = response.getStatusCode();
            final String statusCodeMeaning = HttpStatusCode.getMessage(statusCode).orElse("Unknown");
            final List<String> responseHeaders = buildHeaderStrings(response.getResponseHeaders());

            outputStream.writeBytes("HTTP/1.1 " + statusCode + " " + statusCodeMeaning + "\r\n");

            for (String header : responseHeaders) {
                outputStream.writeBytes(header);
            }

            final Optional<byte[]> payload = response.getPayload();

            if (payload.isPresent()) {
                outputStream.writeBytes("Content-Length: " + payload.get().length + "\r\n");
                outputStream.writeBytes("\r\n");
                outputStream.write(payload.get());
            } else {
                outputStream.writeBytes("\r\n");
            }

        } catch (Exception ignored) {
        }
    }

    private static List<String> buildHeaderStrings(final Map<String, List<String>> responseHeaders) {
        final List<String> responseHeadersList = new ArrayList<>();

        responseHeaders.forEach((name, values) -> {
            final StringBuilder valuesCombined = new StringBuilder();
            values.forEach(valuesCombined::append);
            valuesCombined.append(";");

            responseHeadersList.add(name + ": " + valuesCombined + "\r\n");
        });

        return responseHeadersList;
    }
}
