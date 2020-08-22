package net.manukagames.alfred.schema;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;

import com.google.inject.Guice;
import com.google.inject.Injector;

import net.manukagames.alfred.bundle.Bundle;
import net.manukagames.alfred.bundle.BundleConfiguration;

public final class TestFile {
  public static TestFile named(String name) {
    Objects.requireNonNull(name);
    return new TestFile(name);
  }

  private final String name;
  private final Injector injector = Guice.createInjector();

  private TestFile(String name) {
    this.name = name;
  }

  public Schema readSchema() {
    try {
      return SchemaConfiguration.withContent(readFileContents()).read(injector);
    } catch (IOException failure) {
      throw new RuntimeException(failure);
    }
  }

  public Bundle readBundle() {
    try {
      return BundleConfiguration.withContent(readFileContents()).read(injector);
    } catch (IOException failure) {
      throw new RuntimeException(failure);
    }
  }

  private String readFileContents() throws IOException {
    var resource = resolveResourceInPath();
    try (var input = new BufferedInputStream(resource)) {
      return new String(input.readAllBytes(), Charset.defaultCharset());
    }
  }

  private InputStream resolveResourceInPath() {
    return getClass().getClassLoader().getResourceAsStream(name);
  }
}