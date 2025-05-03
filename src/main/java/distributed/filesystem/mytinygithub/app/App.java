package distributed.filesystem.mytinygithub.app;

import picocli.CommandLine;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new CommandLineArgumentParser()).execute(args);
        System.exit(exitCode);
    }
}
