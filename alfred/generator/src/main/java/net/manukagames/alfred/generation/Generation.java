package net.manukagames.alfred.generation;

import net.manukagames.alfred.schema.Schema;

import java.nio.file.Path;
import java.util.Objects;

public class Generation {
  private final Schema schema;
  private final RecipientSupport recipientSupport;

  protected Generation(
    Schema schema,
    RecipientSupport recipientSupport
  ) {
    this.schema = schema;
    this.recipientSupport = recipientSupport;
  }

  public RecipientSupport recipientSupport() {
    return recipientSupport;
  }

  public Schema schema() {
    return schema;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private Schema schema;
    private RecipientSupport recipientSupport;
    private Path targetDirectory;

    private Builder() {}

    public Builder withSchema(Schema schema) {
      this.schema = schema;
      return this;
    }

    public Builder withRecipientSupport(RecipientSupport support) {
      this.recipientSupport = support;
      return this;
    }

    public Builder withTargetDirectory(Path targetDirectory) {
      this.targetDirectory = targetDirectory;
      return this;
    }

    public Generation create() {
      Objects.requireNonNull(schema);
      Objects.requireNonNull(recipientSupport);
      Objects.requireNonNull(targetDirectory);
      return new Generation(schema, recipientSupport);
    }
  }
}
