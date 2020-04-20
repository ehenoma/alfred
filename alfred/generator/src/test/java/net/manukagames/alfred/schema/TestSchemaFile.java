package net.manukagames.alfred.schema;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;

import org.yaml.snakeyaml.Yaml;

public final class TestSchemaFile {
  private final String name;

  private TestSchemaFile(String name) {
    this.name = name;
  }

  public Schema read() {
    var yaml = new Yaml();
    var properties = (Map<?, ?>) yaml.load(readFileContents());
    var reading = new SchemaFile.Reading(properties);
    return reading.read();
  }

  private String readFileContents() {
    var loader = getClass().getClassLoader();
    var resource = loader.getResourceAsStream(name);
    Objects.requireNonNull(resource);
    try (var input = new BufferedInputStream(resource)) {
      return new String(input.readAllBytes(), Charset.defaultCharset());
    } catch (IOException failedRead) {
      throw new RuntimeException(failedRead);
    }
  }

  public static TestSchemaFile named(String name) {
    Objects.requireNonNull(name);
    return new TestSchemaFile(name);
  }
}
