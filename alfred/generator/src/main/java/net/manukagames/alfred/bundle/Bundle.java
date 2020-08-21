package net.manukagames.alfred.bundle;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

public final class Bundle {
  private final Locale locale;
  private final MessagePreprocessor preprocessor;
  private final Map<String, String> messages;

  private Bundle(
    Locale locale,
    Map<String, String> messages,
    MessagePreprocessor preprocessor
  ) {
    this.locale = locale;
    this.messages = messages;
    this.preprocessor = preprocessor;
  }

  public Locale locale() {
    return locale;
  }

  public Optional<String> loadMessage(String key) {
    return loadRawMessage(key)
      .map(message -> preprocessor.process(key, message));
  }

  public Optional<String> loadRawMessage(String key) {
    Objects.requireNonNull(key);
    return Optional.ofNullable(messages.get(key));
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private final Map<String, String> messages = new HashMap<>();
    private MessagePreprocessor preprocessor = MessagePreprocessors.none();
    private Locale locale;

    private Builder() { }

    @CanIgnoreReturnValue
    public Builder withLocale(Locale locale) {
      this.locale = locale;
      return this;
    }

    @CanIgnoreReturnValue
    public Builder addMessage(String key, String value) {
      Objects.requireNonNull(key);
      Objects.requireNonNull(value);
      messages.put(key, value);
      return this;
    }

    @CanIgnoreReturnValue
    public Builder withPreprocessor(MessagePreprocessor preprocessor) {
      Objects.requireNonNull(preprocessor);
      this.preprocessor = preprocessor;
      return this;
    }

    public Bundle create() {
      Objects.requireNonNull(locale);
      return new Bundle(locale, Map.copyOf(messages), preprocessor);
    }
  }
}
