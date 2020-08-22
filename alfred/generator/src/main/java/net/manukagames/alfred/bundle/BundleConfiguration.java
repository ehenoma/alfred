package net.manukagames.alfred.bundle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import com.google.inject.Injector;

import org.yaml.snakeyaml.Yaml;

import net.manukagames.alfred.yaml.MoreFiles;
import net.manukagames.alfred.yaml.YamlReaders;

public final class BundleConfiguration {
  public static BundleConfiguration withContent(String content) {
    Objects.requireNonNull(content);
    return new BundleConfiguration(() -> content);
  }

  public static BundleConfiguration ofFile(File file) {
    Objects.requireNonNull(file);
    return atPath(file.toPath());
  }

  public static BundleConfiguration atPath(Path path) {
    Objects.requireNonNull(path);
    return new BundleConfiguration(() -> Files.readString(path));
  }

  private final Callable<String> content;

  private BundleConfiguration(Callable<String> content) {
    this.content = content;
  }

  public Bundle read(Injector injector) throws IOException {
    try {
      return new Reading(readRoots(), injector).run();
    } catch (Exception failure) {
      throw MoreFiles.asIoException(failure);
    }
  }

  private Map<?, ?> readRoots() throws Exception {
    return new Yaml().loadAs(content.call(), Map.class);
  }

  private static final class Reading {
    private final Map<?, ?> topLevelProperties;
    private final Injector injector;
    private final Bundle.Builder config = Bundle.newBuilder();

    private Reading(Map<?, ?> topLevelProperties, Injector injector) {
      this.injector = injector;
      this.topLevelProperties = topLevelProperties;
    }

    public Bundle run() {
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
  }
}
