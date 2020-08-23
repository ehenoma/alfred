package net.manukagames.alfred;

import com.google.inject.Guice;
import net.manukagames.alfred.generation.OutputPath;
import net.manukagames.alfred.schema.Schema;
import net.manukagames.alfred.schema.SchemaConfiguration;
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
    var outputPath = OutputPath.fromFile(outputDirectory);
    var generation = SchemaGeneration.of(readSchema(), outputPath);
    try {
      generation.run();
    } catch (IOException failure) {
      System.out.println("could not build schema: " + failure.getMessage());
    }
    return 0;
  }

  private Schema readSchema() {
    var file = SchemaConfiguration.ofFile(schemaFile);
    try {
      return file.read(Guice.createInjector());
    } catch (IOException exception) {
      throw new RuntimeException("failed to read schema", exception);
    }
  }
}
