package net.manukagames.alfred;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import com.google.inject.Guice;
import com.google.inject.Injector;

import picocli.CommandLine;

import net.manukagames.alfred.bundle.Bundle;
import net.manukagames.alfred.bundle.BundleConfiguration;
import net.manukagames.alfred.bundle.BundleGeneration;
import net.manukagames.alfred.generation.OutputPath;
import net.manukagames.alfred.schema.Schema;
import net.manukagames.alfred.schema.SchemaConfiguration;
import net.manukagames.alfred.schema.generation.SchemaGeneration;

@CommandLine.Command(
  name = "build",
  mixinStandardHelpOptions = true
)
final class BuildBundleCommand implements Callable<Integer> {
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

  @CommandLine.Option(
    names = { "-s", "--schema" },
    description = "the source file"
  )
  File schemaFile;

  @CommandLine.Parameters(
    arity = "1",
    description = "the implementation file"
  )
  File implementationFile;

  private final Injector injector = Guice.createInjector();

  @Override
  public Integer call() {
    try {
      generate();
    } catch (Exception failure) {
      System.out.println("could not build bundle: " + failure.toString());
      return -1;
    }
    return 0;
  }

  private void generate() throws IOException {
    var schema = readSchema(injector);
    var bundle = readBundle(injector);
    generateSchema(schema);
    generateBundle(bundle, schema);
  }

  private void generateSchema(Schema schema) throws IOException {
    var path = OutputPath.fromFile(outputDirectory);
    var generation = SchemaGeneration.of(schema, path);
    generation.run();
  }

  private void generateBundle(Bundle bundle, Schema schema) throws IOException {
    var path = OutputPath.fromFile(outputDirectory);
    var generation = BundleGeneration.newBuilder()
      .withOutputPath(path)
      .withSchema(schema)
      .withBundle(bundle)
      .create();
    generation.run();
  }

  private Bundle readBundle(Injector injector) {
    var file = BundleConfiguration.of(implementationFile);
    try {
      return file.read(injector);
    } catch (IOException exception) {
      throw new RuntimeException(
        String.format("failed to read bundle %s", implementationFile.getName()),
        exception
      );
    }
  }

  private Schema readSchema(Injector injector) {
    var file = SchemaConfiguration.of(schemaFile);
    try {
      return file.read(injector);
    } catch (IOException exception) {
      throw new RuntimeException(
        String.format("failed to read schema %s", schemaFile.getName()),
        exception
      );
    }
  }
}
