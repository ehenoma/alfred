package net.manukagames.alfred.schema;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;

import com.google.inject.Guice;
import net.manukagames.alfred.bundle.Bundle;
import net.manukagames.alfred.bundle.BundleConfiguration;
import net.manukagames.alfred.bundle.BundleConfiguration.Reading;
import org.yaml.snakeyaml.Yaml;

public final class TestFile {
  private final String name;

  private TestFile(String name) {
    this.name = name;
  }

  public Schema readSchema() {
    var properties = readTopLevelProperties();
    return new SchemaConfiguration.Reading(properties, Guice.createInjector()).read();
  }

  public Bundle readBundle() {
    var properties = readTopLevelProperties();
    var reading =
      Reading.withTopLevelProperties(properties, Guice.createInjector());
    return reading.read();
  }

  private Map<?, ?> readTopLevelProperties() {
    var yaml = new Yaml();
    return (Map<?, ?>) yaml.load(readFileContents());
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

  public static TestFile named(String name) {
    Objects.requireNonNull(name);
    return new TestFile(name);
  }
}
