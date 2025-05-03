package distributed.filesystem.mytinygithub.app;


import distributed.filesystem.mytinygithub.app.server.HttpMethod;
import distributed.filesystem.mytinygithub.app.server.HttpServer;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(
        name="server",
        description="Starts a TCP Server"
)
public class Server implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {

        HttpServer server = new HttpServer(5000);

        server.addRoute(HttpMethod.POST, "/test",
                ( request, response ) -> {
                    response.addHeader("Content-Type", "text/plain");
                    response.setStatusCode(200);
                    response.setPayload(request.getPayload());
                }
        );

        server.start();

        return 0;
    }
}
