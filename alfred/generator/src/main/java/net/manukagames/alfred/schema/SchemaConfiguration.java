package net.manukagames.alfred.schema;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.inject.Injector;
import net.manukagames.alfred.generation.Framework;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nullable;

public final class SchemaConfiguration {
  public static SchemaConfiguration of(File file) {
    Objects.requireNonNull(file);
    return new SchemaConfiguration(file.toPath());
  }

  public static SchemaConfiguration at(Path path) {
    Objects.requireNonNull(path);
    return new SchemaConfiguration(path);
  }

  private final Path path;

  private SchemaConfiguration(Path path) {
    this.path = path;
  }

  public Schema read(Injector injector) throws IOException {
    return read(injector, new Yaml());
  }

  public Schema read(Injector injector, Yaml parser) throws IOException{
    var topLevelProperties = parseTopLevel(parser);
    var reading = new Reading(topLevelProperties, injector);
    try {
      return reading.read();
    } catch (InvalidFormatException invalidFormat) {
      throw new IOException(invalidFormat);
    }
  }

  private Map<?, ?> parseTopLevel(Yaml parser) throws IOException {
    try (var input = Files.newBufferedReader(path)) {
      return parser.load(input);
    }
  }

  public static final class InvalidFormatException extends RuntimeException {
    static InvalidFormatException withMessage(String message) {
      Objects.requireNonNull(message);
      return new InvalidFormatException(message);
    }

    private InvalidFormatException(String message) {
      super(message);
    }
  }

  static final class Reading {
    private final Schema.Builder builder;
    private final Injector injector;
    private final Map<?, ?> topLevelProperties;

    Reading(Map<?, ?> topLevelProperties, Injector injector) {
      this.topLevelProperties = topLevelProperties;
      this.injector = injector;
      this.builder = Schema.newBuilder();
    }

    public Schema read() {
      builder.withPackageName(YamlReaders.require("packageName", topLevelProperties));
      Map<?, ?> messagesProperties = YamlReaders.require("messages", topLevelProperties);
      builder.withMessages(readMessages(messagesProperties));
      Map<?, ?> frameworkProperties = YamlReaders.require("framework", topLevelProperties);
      builder.withFramework(readFramework(frameworkProperties));
      return builder.create();
    }

    private Framework readFramework(Map<?, ?> properties) {
      String supportClass = YamlReaders.require("supportClass", properties);
      return loadFramework(supportClass);
    }

    private Collection<Message> readMessages(Map<?, ?> properties) {
      var messages = new ArrayList<Message>(properties.size());
      for (var entry : properties.entrySet()) {
        Map<?, ?> messageProperties = YamlReaders.tryToCast("message", entry.getValue());
        var name = entry.getKey().toString();
        messages.add(readMessage(name, messageProperties));
      }
      return messages;
    }

    private Message readMessage(String name, @Nullable Map<?, ?> properties) {
      var message = Message.newBuilder();
      message.withName(name);
      if (properties != null) {
        var foundDescription = properties.get("description");
        message.withDescription(YamlReaders.convertToStringOrEmpty(foundDescription));
        message.withContext(maybeReadContext(properties));
      }
      return message.create();
    }

    private Collection<Message.Variable> maybeReadContext(Map<?, ?> messageProperties) {
      var context = messageProperties.get("context");
      if (context == null) {
        return List.of();
      }
      Map<?, ?> contextProperties = YamlReaders.tryToCast("context", context);
      return readContext(contextProperties);
    }

    private Collection<Message.Variable> readContext(Map<?, ?> properties) {
      var context = new ArrayList<Message.Variable>();
      for (var property : properties.entrySet()) {
        var name = property.getKey().toString();
        var type = property.getValue().toString();
        context.add(Message.Variable.create(name, type));
      }
      return context;
    }

    private Framework loadFramework(String typeName) {
      try {
        var resolvedClass = Class.forName(typeName);
        return createFrameworkFromType(resolvedClass);
      } catch (ClassNotFoundException invalidName) {
        throw new IllegalStateException("could not find framework type", invalidName);
      }
    }

    private Framework createFrameworkFromType(Class<?> type) {
      var instance = injector.getInstance(type);
      if (!(instance instanceof Framework)) {
        var error = String.format("type %s is not a Framework", type.getSimpleName());
        throw new IllegalStateException(error);
      }
      return (Framework) instance;
    }
  }
}
