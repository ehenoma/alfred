package net.manukagames.alfred;

import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(
  name = "build",
  mixinStandardHelpOptions = true
)
final class BuildSchemaCommand implements Callable<Integer> {
  @CommandLine.Option(
    names = {"-o", "--output"},
    description = "target directory for output sources"
  )
  File outputDirectory;

  @CommandLine.Option(
    names = {"-v", "--verbose"},
    description = "flag to toggle verbose console output"
  )
  boolean verbose;

  @CommandLine.Parameters(
    arity = "1",
    description = "the source file"
  )
  File schemaFile;

  @Override
  public Integer call() throws Exception {
    return 0;
  }
}
