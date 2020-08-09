package net.manukagames.alfred.bundle.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class Scanning {
  private final Collection<Part> parts = new ArrayList<>();
  private final String input;
  private int offset;
  private int partBegin;

  Scanning(String input) {
    this.input = input;
  }

  public Text scan() {
    scanParts();
    if (parts.isEmpty()) {
      return PlainText.of(input);
    }
    maybeFinishTextPart();
    return new PlaceholderText(List.copyOf(parts));
  }

  private void scanParts() {
    while (!isComplete()) {
      advance();
    }
  }

  private static final int ESCAPED_DOLLAR_LENGTH = 2;

  private void advance() {
    char currentCharacter = input.charAt(offset);
    if (currentCharacter == '\\' && isLookingAtPlaceholder()) {
      offset += ESCAPED_DOLLAR_LENGTH;
      return;
    }
    if (currentCharacter == '$') {
      scanPlaceholder();
      startNewPart();
    } else {
      offset++;
    }
  }

  private void startNewPart() {
    partBegin = offset;
  }

  private void scanPlaceholder() {
    maybeFinishTextPart();
    offset++;
    if (input.charAt(offset) == '{') {
      offset++;
      scanCode();
    } else {
      scanSimplePlaceholder();
    }
  }

  private void scanCode() {
    startNewPart();
    while (!isComplete() && input.charAt(offset) != '}') {
      offset++;
    }
    if (!isComplete()) {
      String code = input.substring(partBegin, offset);
      parts.add(new CodePart(code));
      offset++;
    }
  }

  private void scanSimplePlaceholder() {
    String variable = scanIdentifier();
    if (isComplete()) {
      parts.add(new CodePart(variable));
      return;
    }
    scanVariableOrFieldAccess(variable);
  }

  private void scanVariableOrFieldAccess(String variable) {
    if (input.charAt(offset) == '.') {
      offset++;
      String field = scanIdentifier();
      parts.add(new FieldAccessPart(variable, field));
      return;
    }
    parts.add(new CodePart(variable));
  }

  private String scanIdentifier() {
    startNewPart();
    while (!isComplete() && Character.isJavaIdentifierPart(input.charAt(offset))) {
      offset++;
    }
    return input.substring(partBegin, offset);
  }

  private void maybeFinishTextPart() {
    if (partBegin != offset) {
      finishTextPart();
    }
  }

  private void finishTextPart() {
    String text = input.substring(partBegin, offset);
    parts.add(new TextPart(text));
  }

  private boolean isLookingAtPlaceholder() {
    if (offset + 1 >= input.length()) {
      return false;
    }
    return input.charAt(offset + 1) == '$';
  }

  private boolean isComplete() {
    return offset >= input.length();
  }
}
