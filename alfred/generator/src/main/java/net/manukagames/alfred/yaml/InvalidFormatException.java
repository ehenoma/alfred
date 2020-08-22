package net.manukagames.alfred.yaml;

import java.util.Objects;

public final class InvalidFormatException extends RuntimeException {
  static InvalidFormatException withMessage(String message) {
    Objects.requireNonNull(message);
    return new InvalidFormatException(message);
  }

  private InvalidFormatException(String message) {
    super(message);
  }
}
