package net.manukagames.alfred;

import net.manukagames.alfred.schema.SchemaFile;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

@CommandLine.Command(
  name = "verify",
  mixinStandardHelpOptions = true
)
final class VerifySchemaCommand implements Callable<Integer> {
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
  public Integer call() {
    try {
      readSchema();
      return 0;
    } catch (Exception failure) {
      System.err.println(failure.getMessage());
      return 1;
    }
  }

  private void readSchema() {
    var file = SchemaFile.of(schemaFile);
    try {
      file.read();
    } catch (IOException exception) {
      throw new RuntimeException("failed to read schema", exception);
    }
  }
}
