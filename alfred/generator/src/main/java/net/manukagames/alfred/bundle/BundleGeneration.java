package net.manukagames.alfred.bundle;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.inject.Guice;
import com.google.inject.Injector;

import net.manukagames.alfred.generation.Generation;

public final class BundleGeneration extends Generation {
  private final BundleConfig config;
  private final Injector injector;
  private MessagePreprocessor preprocessors;

  private BundleGeneration(
    Generation base,
    BundleConfig config,
    Injector injector
  ) {
    super(base.schema(), base.recipientSupport());
    this.config = config;
    this.injector = injector;
  }

  public BundleConfig bundleConfig() {
    return config;
  }

  public String preprocess(String message) {
    var preprocessor = createCombinedPreprocessor();
    return preprocessor.process(message);
  }

  private MessagePreprocessor createCombinedPreprocessor() {
    if (preprocessors == null) {
      var children = createPreprocessors();
      preprocessors = CombinedMessagePreprocessor.of(children);
    }
    return preprocessors;
  }

  public Collection<MessagePreprocessor> createPreprocessors() {
    return config.listPreprocessors().stream()
      .map(this::findPreprocessorClass)
      .map(injector::getInstance)
      .collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  private Class<? extends MessagePreprocessor> findPreprocessorClass(String name) {
    try {
      return (Class<? extends MessagePreprocessor>) Class.forName(name);
    } catch (ClassNotFoundException invalidClass) {
      throw new RuntimeException("could not find preprocessor", invalidClass);
    } catch (ClassCastException invalidType) {
      var error = String.format("the preprocessor %s does not implement %s",
        name, MessagePreprocessor.class.getSimpleName());
      throw new RuntimeException(error, invalidType);
    }
  }

  public static BundleGeneration fromBase(Generation base, BundleConfig config) {
    Objects.requireNonNull(base);
    Objects.requireNonNull(config);
    return new BundleGeneration(base, config, Guice.createInjector());
  }
}
