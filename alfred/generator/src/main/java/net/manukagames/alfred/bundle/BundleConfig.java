package net.manukagames.alfred.bundle;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class BundleConfig {
  private final Locale locale;
  private final Collection<String> preprocessors;
  private final Map<String, String> messages;

  private BundleConfig(
    Locale locale,
    Map<String, String> messages,
    Collection<String> preprocessors
  ) {
    this.locale = locale;
    this.messages = messages;
    this.preprocessors = preprocessors;
  }

  public Locale locale() {
    return locale;
  }

  public Collection<String> listPreprocessors() {
    return preprocessors;
  }

  public Optional<String> findMessage(String key) {
    Objects.requireNonNull(key);
    return Optional.ofNullable(messages.get(key));
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private final Map<String, String> messages = new HashMap<>();
    private final Collection<String> preprocessors = new ArrayList<>();
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
    public Builder addPreprocessor(String preprocessorClass) {
      Objects.requireNonNull(preprocessorClass);
      preprocessors.add(preprocessorClass);
      return this;
    }

    public BundleConfig create() {
      Objects.requireNonNull(locale);
      return new BundleConfig(
        locale,
        Map.copyOf(messages),
        List.copyOf(preprocessors)
      );
    }
  }
}
