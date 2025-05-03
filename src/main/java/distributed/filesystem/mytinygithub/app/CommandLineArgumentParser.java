package distributed.filesystem.mytinygithub.app;


import picocli.CommandLine;

@CommandLine.Command(
        name = "",
        subcommands = {
                Server.class,
        }
)
public class CommandLineArgumentParser  {
}
