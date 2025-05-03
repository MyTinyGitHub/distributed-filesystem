package distributed.filesystem.mytinygithub.app.server;

import distributed.filesystem.mytinygithub.app.server.request.HttpDecoder;
import distributed.filesystem.mytinygithub.app.server.request.HttpRequest;
import distributed.filesystem.mytinygithub.app.server.response.HttpResponse;
import distributed.filesystem.mytinygithub.app.server.response.ResponseWriter;

import java.io.*;
import java.util.Map;
import java.util.Optional;

public class HttpHandler {
    private final Map<String, RequestRunner> routes;

    public HttpHandler(Map<String, RequestRunner> routes) {
        this.routes = routes;
    }

    public void handleConnection(final InputStream inputStream, final OutputStream outputStream) throws IOException {
        final DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        Optional<HttpRequest> request = HttpDecoder.decode(inputStream);
        request.ifPresentOrElse((r) -> handleRequest(r, dataOutputStream), () -> handleInvalidRequest(dataOutputStream));

        outputStream.close();
        inputStream.close();
    }

    private void handleInvalidRequest(final DataOutputStream outputStream) {
        HttpResponse notFoundResponse = new HttpResponse();

        notFoundResponse.setStatusCode(400);
        notFoundResponse.setPayload("Bad Request...".getBytes());

        ResponseWriter.writeResponse(outputStream, notFoundResponse);
    }

    private void handleRequest(final HttpRequest request, final DataOutputStream bufferedWriter) {
        final String routeKey = request.getHeaders().getHttpMethod().name().concat(request.getUri().getRawPath());

        if (routes.containsKey(routeKey)) {
            HttpResponse response = new HttpResponse();

            routes.get(routeKey).run(request, response);

            ResponseWriter.writeResponse(bufferedWriter, response);
        } else {
            // Not found
            HttpResponse response = new HttpResponse();

            response.setStatusCode(404);
            response.setPayload("Route Not Found".getBytes());

            ResponseWriter.writeResponse(bufferedWriter, response);
        }
    }
}
