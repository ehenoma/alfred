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
import java.util.concurrent.Callable;

import javax.annotation.Nullable;

import com.google.inject.Injector;

import org.yaml.snakeyaml.Yaml;

import net.manukagames.alfred.generation.Framework;
import net.manukagames.alfred.yaml.MoreFiles;
import net.manukagames.alfred.yaml.YamlReaders;

public final class SchemaConfiguration {
  public static SchemaConfiguration withContent(String content) {
    Objects.requireNonNull(content);
    return new SchemaConfiguration(() -> content);
  }

  public static SchemaConfiguration ofFile(File file) {
    Objects.requireNonNull(file);
    return atPath(file.toPath());
  }

  public static SchemaConfiguration atPath(Path path) {
    Objects.requireNonNull(path);
    return new SchemaConfiguration(() -> Files.readString(path));
  }

  private final Callable<String> content;

  private SchemaConfiguration(Callable<String> content) {
    this.content = content;
  }

  public Schema read(Injector injector) throws IOException {
    try {
      var topLevelProperties = parseTopLevel();
      return new Reading(topLevelProperties, injector).run();
    } catch (Exception failure) {
      throw MoreFiles.asIoException(failure);
    }
  }

  private Map<?, ?> parseTopLevel() throws Exception {
    return new Yaml().load(content.call());
  }

  private static final class Reading {
    private final Schema.Builder builder;
    private final Injector injector;
    private final Map<?, ?> topLevelProperties;

    private Reading(Map<?, ?> topLevelProperties, Injector injector) {
      this.topLevelProperties = topLevelProperties;
      this.injector = injector;
      this.builder = Schema.newBuilder();
    }

    public Schema run() {
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
