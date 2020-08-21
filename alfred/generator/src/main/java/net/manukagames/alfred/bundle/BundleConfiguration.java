package net.manukagames.alfred.bundle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Injector;
import net.manukagames.alfred.schema.SchemaConfiguration;
import net.manukagames.alfred.schema.YamlReaders;
import org.yaml.snakeyaml.Yaml;

public final class BundleConfiguration {
  public static BundleConfiguration of(File file) {
    Objects.requireNonNull(file);
    return new BundleConfiguration(file.toPath());
  }

  public static BundleConfiguration at(Path path) {
    Objects.requireNonNull(path);
    return new BundleConfiguration(path);
  }

  private final Path path;

  private BundleConfiguration(Path path) {
    this.path = path;
  }

  public Bundle read(Injector injector) throws IOException {
    return read(injector, new Yaml());
  }

  public Bundle read(Injector injector, Yaml parser) throws IOException{
    var topLevelProperties = parseTopLevel(parser);
    var reading = new Reading(topLevelProperties, injector);
    try {
      return reading.read();
    } catch (SchemaConfiguration.InvalidFormatException invalidFormat) {
      throw new IOException(invalidFormat);
    }
  }

  private Map<?, ?> parseTopLevel(Yaml parser) throws IOException {
    try (var input = Files.newBufferedReader(path)) {
      return parser.load(input);
    }
  }

  public static final class Reading {
    private final Map<?, ?> topLevelProperties;
    private final Injector injector;
    private final Bundle.Builder config = Bundle.newBuilder();

    private Reading(Map<?, ?> topLevelProperties, Injector injector) {
      this.injector = injector;
      this.topLevelProperties = topLevelProperties;
    }

    public Bundle read() {
      readLocale();
      readMessages();
      readPreprocessors();
      return config.create();
    }

    private void readLocale() {
      String languageTag = YamlReaders.require("locale", topLevelProperties);
      var locale = Locale.forLanguageTag(languageTag);
      config.withLocale(locale);
    }

    private void readMessages() {
      Map<?, ?> messageProperties = YamlReaders.require(
        "messages", topLevelProperties);
      for (var entry : messageProperties.entrySet()) {
        config.addMessage(
          entry.getKey().toString(),
          entry.getValue().toString()
        );
      }
    }

    private void readPreprocessors() {
      if (!topLevelProperties.containsKey("preprocessors")) {
        return;
      }
      Collection<?> classes = YamlReaders.require("preprocessors", topLevelProperties);
      var classNames = classes.stream().map(String::valueOf).collect(Collectors.toList());
      config.withPreprocessor(loadPreprocessors(classNames));
    }

    public MessagePreprocessor loadPreprocessors(Collection<String> typeNames) {
      var preprocessors = typeNames.stream()
        .map(this::loadPreprocessorClass)
        .map(injector::getInstance)
        .collect(Collectors.toList());
      return MessagePreprocessors.combine(preprocessors);
    }

    @SuppressWarnings("unchecked")
    private Class<? extends MessagePreprocessor> loadPreprocessorClass(String name) {
      try {
        return (Class<? extends MessagePreprocessor>) Class.forName(name);
      } catch (ClassNotFoundException invalidClass) {
        throw new RuntimeException("could not find preprocessor", invalidClass);
      } catch (ClassCastException invalidType) {
        var error = String.format("%s is not a MessagePreprocessor", name);
        throw new RuntimeException(error, invalidType);
      }
    }

    @VisibleForTesting
    public static Reading withTopLevelProperties(Map<?, ?> properties, Injector injector) {
      Objects.requireNonNull(properties);
      Objects.requireNonNull(injector);
      return new Reading(properties, injector);
    }
  }
}
