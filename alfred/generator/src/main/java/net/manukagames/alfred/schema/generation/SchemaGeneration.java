package net.manukagames.alfred.schema.generation;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import net.manukagames.alfred.generation.GeneratedFile;
import net.manukagames.alfred.generation.OutputPath;
import net.manukagames.alfred.schema.Schema;

public final class SchemaGeneration {
  public static SchemaGeneration of(Schema schema, OutputPath path) {
    return new SchemaGeneration(schema, path);
  }

  private final Schema schema;
  private final OutputPath outputPath;

  private SchemaGeneration(Schema schema, OutputPath outputPath) {
    this.schema = schema;
    this.outputPath = outputPath;
  }

  public void run() throws IOException {
    var generatedFiles = listGeneratedFiles(schema);
    outputPath.write(generatedFiles);
  }

  private Collection<GeneratedFile> listGeneratedFiles(Schema schema) {
    return List.of(
      BundleInterfaceFile.fromSchema(schema),
      AudienceInterfaceFile.fromSchema(schema),
      SoloAudienceFile.fromSchema(schema),
      GroupAudienceFile.fromSchema(schema),
      BundleRepositoryFile.fromSchema(schema),
      EmptyMessageBundleFile.fromSchema(schema),
      RecipientUtilFile.fromSchema(schema)
    );
  }
}
