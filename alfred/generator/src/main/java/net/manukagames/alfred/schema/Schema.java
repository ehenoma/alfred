package net.manukagames.alfred.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Schema {
  private final String packageName;
  private final Recipient recipient;
  private final Collection<Message> messages;

  private Schema(
    String packageName,
    Recipient recipient,
    Collection<Message> messages
  ) {
    this.packageName = packageName;
    this.recipient = recipient;
    this.messages = messages;
  }

  public Collection<Message> listMessages() {
    return messages;
  }

  public String packageName() {
    return packageName;
  }

  public Recipient recipient() {
    return recipient;
  }

  @Override
  public String toString() {
    var messagesFormatted = messages.stream()
      .map(Objects::toString)
      .collect(Collectors.joining(", "));
    return String.format(
      "Schema{package: %s, recipient: %s, messages: [%s]}",
      packageName,
      recipient,
      messagesFormatted
    );
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private String packageName;
    private Recipient recipient;
    private Collection<Message> messages = new ArrayList<>();

    private Builder() {}

    public Builder withPackageName(String name) {
      this.packageName = name;
      return this;
    }

    public Builder withMessages(Collection<Message> messages) {
      Objects.requireNonNull(messages);
      messages.forEach(Objects::requireNonNull);
      this.messages = new ArrayList<>(messages);
      return this;
    }

    public Builder addMessage(Message message) {
      Objects.requireNonNull(message);
      messages.add(message);
      return this;
    }

    public Builder withRecipient(Recipient recipient) {
      this.recipient = recipient;
      return this;
    }

    public Schema create() {
      Objects.requireNonNull(packageName);
      Objects.requireNonNull(recipient);
      return new Schema(packageName, recipient, List.copyOf(messages));
    }
  }
}
