package net.manukagames.alfred;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.manukagames.alfred.bundle.Bundle;
import net.manukagames.alfred.bundle.BundleConfiguration;
import net.manukagames.alfred.bundle.BundleGeneration;
import net.manukagames.alfred.schema.Schema;
import net.manukagames.alfred.schema.SchemaConfiguration;
import net.manukagames.alfred.schema.generation.SchemaGeneration;
import org.yaml.snakeyaml.Yaml;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.Callable;

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
    var injector = Guice.createInjector();
    var schema = readSchema(injector);
    var generation = SchemaGeneration.of(schema, outputDirectory.toPath());
    generation.run();
    var bundleConfig = readBundle(injector);
    var bundleGeneration = BundleGeneration.newBundleGenerationBuilder()
      .withOutputDirectory(outputDirectory.toPath())
      .withSchema(schema)
      .withConfig(bundleConfig)
      .create();
    bundleGeneration.generate();
  }

  private Bundle readBundle(Injector injector) {
    var properties = readTopLevelProperties();
    var reading = BundleConfiguration.Reading.withTopLevelProperties(properties, injector);
    return reading.read();
  }

  private Map<?, ?> readTopLevelProperties() {
    var yaml = new Yaml();
    return (Map<?, ?>) yaml.load(readFileContents());
  }

  private String readFileContents() {
    try {
      return Files.readString(implementationFile.toPath());
    } catch (IOException failedRead) {
      throw new RuntimeException(failedRead);
    }
  }


  private Schema readSchema(Injector injector) {
    var file = SchemaConfiguration.of(schemaFile);
    try {
      return file.read(injector);
    } catch (IOException exception) {
      throw new RuntimeException("failed to read schema", exception);
    }
  }
}
