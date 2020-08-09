package net.manukagames.alfred;

import net.manukagames.alfred.schema.Schema;
import net.manukagames.alfred.schema.SchemaFile;
import net.manukagames.alfred.schema.generation.SchemaGeneration;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
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
  public Integer call() {
    SchemaGeneration generation = SchemaGeneration.of(
      readSchema(),
      outputDirectory.toPath()
    );
    generation.run();
    return 0;
  }

  private Schema readSchema() {
    var file = SchemaFile.of(schemaFile);
    try {
      return file.read();
    } catch (IOException exception) {
      throw new RuntimeException("failed to read schema", exception);
    }
  }
}
