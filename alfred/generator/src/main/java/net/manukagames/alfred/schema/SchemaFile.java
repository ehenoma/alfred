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

import org.yaml.snakeyaml.Yaml;

public final class SchemaFile {
  private final Path path;

  private SchemaFile(Path path) {
    this.path = path;
  }

  public Schema read() throws IOException {
    return read(new Yaml());
  }

  public Schema read(Yaml parser) throws IOException{
    var topLevelProperties = parseTopLevel(parser);
    var reading = new Reading(topLevelProperties);
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

  public static SchemaFile of(File file) {
    Objects.requireNonNull(file);
    return new SchemaFile(file.toPath());
  }

  public static SchemaFile at(Path path) {
    Objects.requireNonNull(path);
    return new SchemaFile(path);
  }

  public static final class InvalidFormatException extends RuntimeException {
    private InvalidFormatException(String message) {
      super(message);
    }

    static InvalidFormatException withMessage(String message) {
      Objects.requireNonNull(message);
      return new InvalidFormatException(message);
    }
  }

  static final class Reading {
    private final Schema.Builder builder;
    private final Map<?, ?> topLevelProperties;

    Reading(Map<?, ?> topLevelProperties) {
      this.topLevelProperties = topLevelProperties;
      this.builder = Schema.newBuilder();
    }

    public Schema read() {
      builder.withPackageName(YamlReaders.require("packageName", topLevelProperties));
      Map<?, ?> messagesProperties = YamlReaders.require("messages", topLevelProperties);
      builder.withMessages(readMessages(messagesProperties));
      Map<?, ?> recipientProperties = YamlReaders.require("recipient", topLevelProperties);
      builder.withRecipient(readRecipient(recipientProperties));
      return builder.create();
    }

    private Recipient readRecipient(Map<?, ?> properties) {
      String type = YamlReaders.require("type", properties);
      String supportClass = YamlReaders.require("supportClass", properties);
      return Recipient.create(type, supportClass);
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

    private Message readMessage(String name, Map<?, ?> properties) {
      var message = Message.newBuilder();
      message.withName(name);
      var foundDescription = properties.get("description");
      message.withDescription(YamlReaders.convertToStringOrEmpty(foundDescription));
      message.withContext(maybeReadContext(properties));
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
  }
}
