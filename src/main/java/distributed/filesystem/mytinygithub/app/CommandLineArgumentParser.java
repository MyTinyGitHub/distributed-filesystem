package distributed.filesystem.mytinygithub.app;


import picocli.CommandLine.Command;

@Command(
        name = "",
        subcommands = {
                Server.class,
        }
)
public class CommandLineArgumentParser  {
}
