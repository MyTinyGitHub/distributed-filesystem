package distributed.filesystem.mytinygithub.app.server.request;

import distributed.filesystem.mytinygithub.app.server.headers.Headers;

import java.net.URI;

public class HttpRequest {
    private URI uri;
    private Headers headers;
    private byte[] payload;

    public HttpRequest() {
        this.headers = new Headers();
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public byte[] getPayload() {
        return this.payload;
    }

}
