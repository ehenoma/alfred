package net.manukagames.alfred.bundle;

import java.io.IOException;
import java.util.Objects;

import net.manukagames.alfred.generation.OutputPath;
import net.manukagames.alfred.schema.Schema;

public final class BundleGeneration {
  private final OutputPath outputPath;
  private final Bundle bundle;
  private final Schema schema;

  private BundleGeneration(
    Bundle bundle,
    Schema schema,
    OutputPath outputPath
  ) {
    this.bundle = bundle;
    this.schema = schema;
    this.outputPath = outputPath;
  }

  public void run() throws IOException {
    var file = BundleImplementationFile.create(bundle, schema);
    outputPath.write(file);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private Schema schema;
    private Bundle bundle;
    private OutputPath outputPath;

    private Builder() {}

    public Builder withBundle(Bundle bundle) {
      this.bundle = bundle;
      return this;
    }

    public Builder withOutputPath(OutputPath path) {
      this.outputPath = path;
      return this;
    }

    public Builder withSchema(Schema schema) {
      this.schema = schema;
      return this;
    }

    public BundleGeneration create() {
      Objects.requireNonNull(bundle);
      Objects.requireNonNull(schema);
      Objects.requireNonNull(outputPath);
      return new BundleGeneration(bundle, schema, outputPath);
    }
  }
}