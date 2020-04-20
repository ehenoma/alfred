package net.manukagames.alfred.bundle;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

final class CombinedMessagePreprocessor implements MessagePreprocessor {
  private final Collection<MessagePreprocessor> preprocessors;

  private CombinedMessagePreprocessor(
    Collection<MessagePreprocessor> preprocessors
  ) {
    this.preprocessors = preprocessors;
  }

  @Override
  public String process(String message) {
    for (var preprocessor : preprocessors) {
      message = preprocessor.process(message);
    }
    return message;
  }

  public static CombinedMessagePreprocessor of(
    Collection<MessagePreprocessor> preprocessors
  ) {
    Objects.requireNonNull(preprocessors);
    return new CombinedMessagePreprocessor(List.copyOf(preprocessors));
  }
}
