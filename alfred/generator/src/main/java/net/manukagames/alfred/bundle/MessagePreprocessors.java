package net.manukagames.alfred.bundle;

import java.util.Collection;
import java.util.List;

import com.google.errorprone.annotations.Var;

public final class MessagePreprocessors {
  private MessagePreprocessors() {}

  public static MessagePreprocessor none() {
    return (key, message) -> message;
  }

  public static MessagePreprocessor combine(MessagePreprocessor... preprocessors) {
    return new Composite(List.of(preprocessors));
  }

  public static MessagePreprocessor combine(Collection<? extends MessagePreprocessor> preprocessors) {
    return new Composite(preprocessors);
  }

  static final class Composite implements MessagePreprocessor {
    private final Collection<? extends MessagePreprocessor> preprocessors;

    private Composite(Collection<? extends MessagePreprocessor> preprocessors) {
      this.preprocessors = preprocessors;
    }

    @Override
    public String process(String key, @Var String message) {
      for (var preprocessor : preprocessors) {
        message = preprocessor.process(key, message);
      }
      return message;
    }
  }
}
