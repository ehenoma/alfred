package net.manukagames.alfred.schema;

import java.util.Objects;

public final class Recipient {
  public static Recipient create(String type, String supportClass) {
    Objects.requireNonNull(type);
    Objects.requireNonNull(supportClass);
    return new Recipient(type, supportClass);
  }

  public static final String MESSAGE_VARIABLE_TYPE_NAME = "$Recipient";

  private final String type;
  private final String supportClass;

  private Recipient(String type, String supportClass) {
    this.type = type;
    this.supportClass = supportClass;
  }

  public String type() {
    return type;
  }

  public String supportClass() {
    return supportClass;
  }

  @Override
  public String toString() {
    return String.format(
      "Recipient{type: %s, supportClass: %s}",
      type,
      supportClass
    );
  }
}