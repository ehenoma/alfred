package net.manukagames.alfred.schema.generation;

import net.manukagames.alfred.schema.TestFile;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

final class SchemaGenerationTest {
  @Test
  void testRun() throws IOException {
    var schema = TestFile.named("example_messages.yml").readSchema();

  }

  private Path selectOutputPath() {
    return Path.of(System.getProperty("user.dir")).resolve("tests/generation/src");
  }
}