package net.manukagames.alfred;

import com.google.inject.Guice;
import net.manukagames.alfred.bundle.BundleConfig;
import net.manukagames.alfred.bundle.BundleConfigFile;
import net.manukagames.alfred.bundle.BundleGeneration;
import net.manukagames.alfred.schema.Schema;
import net.manukagames.alfred.schema.SchemaFile;
import net.manukagames.alfred.schema.generation.SchemaGeneration;
import org.yaml.snakeyaml.Yaml;
import picocli.CommandLine;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
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
    var schema = readSchema();
    var injector = Guice.createInjector();
    var generation = SchemaGeneration.of(schema, outputDirectory.toPath());
    generation.run();
    var bundleConfig = readBundle();
    var bundleGeneration = BundleGeneration.newBundleGenerationBuilder()
      .withBase(generation.createGeneration(outputDirectory.toPath()))
      .withOutputDirectory(outputDirectory.toPath())
      .withSchema(schema)
      .withInjector(injector)
      .withConfig(bundleConfig)
      .create();
    bundleGeneration.generate();
  }

  private BundleConfig readBundle() {
    var properties = readTopLevelProperties();
    var reading = BundleConfigFile.Reading.withTopLevelProperties(properties);
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


  private Schema readSchema() {
    var file = SchemaFile.of(schemaFile);
    try {
      return file.read();
    } catch (IOException exception) {
      throw new RuntimeException("failed to read schema", exception);
    }
  }
}
