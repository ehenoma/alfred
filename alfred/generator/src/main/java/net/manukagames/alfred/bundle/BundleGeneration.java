package net.manukagames.alfred.bundle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.Immutable;

import net.manukagames.alfred.schema.Schema;

@Immutable
public final class BundleGeneration {
  private final Path outputDirectory;
  private final Bundle bundle;
  private final Schema schema;

  private BundleGeneration(
    Bundle bundle,
    Path outputDirectory,
    Schema schema
  ) {
    this.bundle = bundle;
    this.schema = schema;
    this.outputDirectory = outputDirectory;
  }
  public void generate() throws IOException {
    var targetDirectory = createTargetDirectory();
    ensureTargetDirectoryExists(targetDirectory);
    var file = BundleImplementationFile.create(bundle, schema);
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

  public static Builder newBundleGenerationBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private Path outputDirectory;
    private Schema schema;
    private Bundle config;

    private Builder() {}

    public Builder withConfig(Bundle config) {
      this.config = config;
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
      Preconditions.checkNotNull(outputDirectory);
      Preconditions.checkNotNull(schema);
      return new BundleGeneration(config, outputDirectory, schema);
    }
  }
}