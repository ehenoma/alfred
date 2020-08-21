package net.manukagames.alfred.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import net.manukagames.alfred.generation.Framework;

public final class Schema {
  private final String packageName;
  private final Framework framework;
  private final Collection<Message> messages;

  private Schema(
    String packageName,
    Framework framework,
    Collection<Message> messages
  ) {
    this.packageName = packageName;
    this.framework = framework;
    this.messages = messages;
  }

  public Collection<Message> listMessages() {
    return messages;
  }

  public Framework framework() {
    return framework;
  }

  public String packageName() {
    return packageName;
  }

  @Override
  public String toString() {
    var messagesFormatted = messages.stream()
      .map(Objects::toString)
      .collect(Collectors.joining(", "));
    return String.format(
      "Schema{package: %s, framework: %s, messages: [%s]}",
      packageName,
      framework,
      messagesFormatted
    );
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private String packageName;
    private Framework framework;
    private Collection<Message> messages = new ArrayList<>();

    private Builder() {}

    @CanIgnoreReturnValue
    public Builder withPackageName(String name) {
      this.packageName = name;
      return this;
    }

    @CanIgnoreReturnValue
    public Builder withMessages(Collection<Message> messages) {
      Objects.requireNonNull(messages);
      messages.forEach(Objects::requireNonNull);
      this.messages = new ArrayList<>(messages);
      return this;
    }

    @CanIgnoreReturnValue
    public Builder addMessage(Message message) {
      Objects.requireNonNull(message);
      messages.add(message);
      return this;
    }

    @CanIgnoreReturnValue
    public Builder withFramework(Framework framework) {
      this.framework = framework;
      return this;
    }

    public Schema create() {
      Objects.requireNonNull(packageName);
      Objects.requireNonNull(framework);
      return new Schema(packageName, framework, List.copyOf(messages));
    }
  }
}
