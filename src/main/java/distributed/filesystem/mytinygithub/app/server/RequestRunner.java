package distributed.filesystem.mytinygithub.app.server;

import distributed.filesystem.mytinygithub.app.server.request.HttpRequest;
import distributed.filesystem.mytinygithub.app.server.response.HttpResponse;

public interface RequestRunner {
    void run(HttpRequest request, HttpResponse response);
}
