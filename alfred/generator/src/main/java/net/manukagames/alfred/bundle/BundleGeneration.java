package net.manukagames.alfred.bundle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.Immutable;
import com.google.inject.Guice;
import com.google.inject.Injector;

import net.manukagames.alfred.generation.Generation;
import net.manukagames.alfred.schema.Schema;

@Immutable
public final class BundleGeneration extends Generation {
  private final BundleConfig config;
  private final Injector injector;
  private final Path outputDirectory;
  private final Schema schema;
  private MessagePreprocessor preprocessors;

  private BundleGeneration(
    Generation base,
    BundleConfig config,
    Injector injector,
    Path outputDirectory,
    Schema schema
  ) {
    super(base.schema(), base.recipientSupport());
    this.config = config;
    this.injector = injector;
    this.outputDirectory = outputDirectory;
    this.schema = schema;
  }

  public BundleConfig bundleConfig() {
    return config;
  }

  public void generate() throws IOException {
    var targetDirectory = createTargetDirectory();
    ensureTargetDirectoryExists(targetDirectory);
    var file = MessageBundleImplementationFile.create(this);
    file.writeToDirectory(targetDirectory);
  }

  private void ensureTargetDirectoryExists(Path directory) throws IOException {
    if (Files.notExists(directory)) {
      Files.createDirectories(directory);
    }
  }

  private Path createTargetDirectory() {
    var packageTree = schema.packageName().replace(".", File.separator);
    return outputDirectory.resolve(packageTree);
  }

  String preprocess(String key, String message) {
    var preprocessor = createCombinedPreprocessor();
    return preprocessor.process(key, message);
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
      .map(this::loadPreprocessorClass)
      .map(injector::getInstance)
      .collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  private Class<? extends MessagePreprocessor> loadPreprocessorClass(String name) {
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

  public static Builder newBundleGenerationBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private BundleConfig config;
    private Injector injector;
    private Path outputDirectory;
    private Schema schema;
    private Generation base;

    private Builder() {}

    public Builder withBase(Generation base) {
      this.base = base;
      return this;
    }

    public Builder withConfig(BundleConfig config) {
      this.config = config;
      return this;
    }

    public Builder withInjector(Injector injector) {
      this.injector = injector;
      return this;
    }

    public Builder withOutputDirectory(Path directory) {
      this.outputDirectory = directory;
      return this;
    }

    public Builder withSchema(Schema schema) {
      this.schema = schema;
      return this;
    }

    public BundleGeneration create() {
      Preconditions.checkNotNull(config);
      Preconditions.checkNotNull(injector);
      Preconditions.checkNotNull(outputDirectory);
      Preconditions.checkNotNull(schema);
      return new BundleGeneration(base, config, injector, outputDirectory, schema);
    }
  }
}