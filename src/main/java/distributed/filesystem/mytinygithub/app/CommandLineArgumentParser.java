package distributed.filesystem.mytinygithub.app;


import distributed.filesystem.mytinygithub.app.client.Client;
import distributed.filesystem.mytinygithub.app.server.Server;
import distributed.filesystem.mytinygithub.app.server.WorkerServer;
import picocli.CommandLine.Command;

@Command(
        name = "",
        subcommands = {
                Client.class,
                Server.class,
                WorkerServer.class,
        }
)
public class CommandLineArgumentParser  {
}
