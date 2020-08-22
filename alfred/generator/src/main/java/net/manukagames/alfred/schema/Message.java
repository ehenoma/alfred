package net.manukagames.alfred.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Message {
  private final String name;
  private final String description;
  private final Collection<Variable> context;

  private Message(
    String name,
    String description,
    Collection<Variable> context
  ) {
    this.name = name;
    this.description = description;
    this.context = context;
  }

  public String name() {
    return name;
  }

  public String createSendMethodName() {
    return String.format("send%s", capitalize(name));
  }

  public String createFactoryMethodName() {
    return String.format("create%s", capitalize(name));
  }

  private static String capitalize(String name) {
    if (name.length() <= 1) {
      return name.toUpperCase();
    }
    var tail = name.substring(1);
    var head = Character.toUpperCase(name.charAt(0));
    return head + tail;
  }

  public String description() {
    return description;
  }

  public Collection<Variable> context() {
    return context;
  }

  @Override
  public String toString() {
    var contextFormatted = context.stream()
      .map(Objects::toString)
      .collect(Collectors.joining(", "));
    return String.format(
      "Message{name: %s, description: '%s', context: [%s]}",
      name,
      description,
      contextFormatted
    );
  }

  public static final class Variable {
    private static final String RECIPIENT_TYPE_NAME = "$Recipient";

    public static Variable create(String name, String type) {
      Objects.requireNonNull(name);
      Objects.requireNonNull(type);
      return new Variable(name, type);
    }

    private final String name;
    private final String type;

    private Variable(String name, String type) {
      this.name = name;
      this.type = type;
    }

    public String name() {
      return name;
    }

    public String type() {
      return type;
    }

    public boolean hasRecipientType() {
      return type.equals(RECIPIENT_TYPE_NAME);
    }

    public boolean isIncludedInParameters() {
      return !hasRecipientType();
    }

    @Override
    public String toString() {
      return String.format("Variable{name: %s, type: %s}", name, type);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private String name;
    private String description = "";
    private Collection<Variable> context = new ArrayList<>();

    private Builder() {}

    public Builder withName(String name) {
      this.name = name;
      return this;
    }

    public Builder withDescription(String description) {
      this.description = description;
      return this;
    }

    public Builder withContext(Collection<Variable> context) {
      Objects.requireNonNull(context);
      context.forEach(Objects::requireNonNull);
      this.context = new ArrayList<>(context);
      return this;
    }

    public Builder addVariable(Variable variable) {
      Objects.requireNonNull(variable);
      context.add(variable);
      return this;
    }

    public Message create() {
      Objects.requireNonNull(name);
      Objects.requireNonNull(description);
      return new Message(name, description, List.copyOf(context));
    }
  }
}