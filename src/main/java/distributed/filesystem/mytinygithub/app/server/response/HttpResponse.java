package distributed.filesystem.mytinygithub.app.server.response;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpResponse {
    private final Map<String, List<String>> responseHeaders;
    private int statusCode;
    private byte[] payload;

    /**
     * Headers should contain the following:
     * Date: < date >
     * Server: < my server >
     * Content-Type: text/plain, application/json etc...
     * Content-Length: size of payload
     */
    public HttpResponse() {
        this.responseHeaders = new HashMap<>();
        this.responseHeaders.put("Server", List.of("MyServer"));
        this.responseHeaders.put("Date", List.of(DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC))));
    }

    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    public void addHeader(final String name, final String value) {
        this.responseHeaders.put(name, List.of(value));
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Optional<byte[]> getPayload() {
        return Optional.ofNullable(payload);
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
}
