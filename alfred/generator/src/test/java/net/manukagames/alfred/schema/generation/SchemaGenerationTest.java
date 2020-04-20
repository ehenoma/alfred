package net.manukagames.alfred.schema.generation;

import com.google.inject.Guice;

import net.manukagames.alfred.schema.TestSchemaFile;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

final class SchemaGenerationTest {
  @Test
  void testRun() {
    var schema = TestSchemaFile.named("example_messages.yml").read();
    var injector = Guice.createInjector();
    var outputPath = selectOutputPath();
    System.out.println("writing generated files to: " + outputPath);
    var generation = new SchemaGeneration(injector, schema, outputPath);
    generation.run();
  }

  private Path selectOutputPath() {
    return Path.of(System.getProperty("user.dir")).resolve("tests/generation/src");
  }
}