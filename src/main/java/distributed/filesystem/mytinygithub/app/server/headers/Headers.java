package distributed.filesystem.mytinygithub.app.server.headers;

import distributed.filesystem.mytinygithub.app.server.HttpMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Headers {
    private HttpMethod httpMethod;
    private Map<String, List<String>> values;

    public Headers() {
        this.values = new HashMap<>();
    }

    public HttpMethod getHttpMethod() {
        return this.httpMethod;
    }

    public void setHttpMethod(final HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Map<String, List<String>> getValues() {
        return this.values;
    }

    public void addValues(final String key, String value) {
        this.values.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    public void setValues(final Map<String, List<String>> requestHeaders) {
        this.values = requestHeaders;
    }

    public void parse(final List<String> message) {
        for (int i = 1; i < message.size(); i++) {
            String header = message.get(i);
            int colonIndex = header.indexOf(':');

            if (! (colonIndex > 0 && header.length() > colonIndex + 1)) {
                break;
            }

            String headerName = header.substring(0, colonIndex);
            String headerValue = header.substring(colonIndex + 1);

            addValues(headerName, headerValue);
        }
    }

}
