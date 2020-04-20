package net.manukagames.alfred.bundle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import net.manukagames.alfred.schema.SchemaFile;
import net.manukagames.alfred.schema.YamlReaders;
import org.yaml.snakeyaml.Yaml;

public final class BundleConfigFile {
  private final Path path;

  private BundleConfigFile(Path path) {
    this.path = path;
  }

  public BundleConfig read() throws IOException {
    return read(new Yaml());
  }

  public BundleConfig read(Yaml parser) throws IOException{
    var topLevelProperties = parseTopLevel(parser);
    var reading = new Reading(topLevelProperties);
    try {
      return reading.read();
    } catch (SchemaFile.InvalidFormatException invalidFormat) {
      throw new IOException(invalidFormat);
    }
  }

  private Map<?, ?> parseTopLevel(Yaml parser) throws IOException {
    try (var input = Files.newBufferedReader(path)) {
      return parser.load(input);
    }
  }

  public static BundleConfigFile of(File file) {
    Objects.requireNonNull(file);
    return new BundleConfigFile(file.toPath());
  }

  public static BundleConfigFile at(Path path) {
    Objects.requireNonNull(path);
    return new BundleConfigFile(path);
  }

  public static final class Reading {
    private final Map<?, ?> topLevelProperties;
    private final BundleConfig.Builder config = BundleConfig.newBuilder();

    private Reading(Map<?, ?> topLevelProperties) {
      this.topLevelProperties = topLevelProperties;
    }

    public BundleConfig read() {
      Map<?, ?> messageProperties = YamlReaders.require(
        "messages", topLevelProperties);
      readLocale();
      readMessages(messageProperties);
      return config.create();
    }

    private void readLocale() {
      String languageTag = YamlReaders.require("locale", topLevelProperties);
      var locale = Locale.forLanguageTag(languageTag);
      config.withLocale(locale);
    }

    private void readMessages(Map<?, ?> properties) {
      for (var entry : properties.entrySet()) {
        config.addMessage(
          entry.getKey().toString(),
          entry.getValue().toString()
        );
      }
    }
  }
}
