package net.manukagames.alfred.schema.generation;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.manukagames.alfred.generation.GeneratedFile;
import net.manukagames.alfred.generation.OutputPath;
import net.manukagames.alfred.schema.Schema;

public final class SchemaGeneration {
  public enum Option {
    GENERATE_AUDIENCE
  }

  public static SchemaGeneration of(
    Schema schema,
    OutputPath path,
    Option... options
  ) {
    boolean generateAudience = isOptionSet(options, Option.GENERATE_AUDIENCE);
    return new SchemaGeneration(schema, path, generateAudience);
  }

  private static boolean isOptionSet(Option[] options, Option option) {
    for (var entry : options) {
      if (entry.equals(option)) {
        return true;
      }
    }
    return false;
  }

  private final Schema schema;
  private final OutputPath outputPath;
  private final boolean generateAudience;

  private SchemaGeneration(
    Schema schema,
    OutputPath outputPath,
    boolean generateAudience
  ) {
    this.schema = schema;
    this.outputPath = outputPath;
    this.generateAudience = generateAudience;
  }

  public void run() throws IOException {
    var generatedFiles = listGeneratedFiles(schema);
    outputPath.write(generatedFiles);
  }

  private Collection<GeneratedFile> listGeneratedFiles(Schema schema) {
    return generateAudience
      ? listAllFiles(schema)
      : listRequiredFiles(schema);
  }

  private Collection<GeneratedFile> listAllFiles(Schema schema) {
    return Stream.concat(
      listRequiredFiles(schema).stream(),
      listGeneratedAudienceFiles(schema).stream()
    ).collect(Collectors.toList());
  }

  private Collection<GeneratedFile> listRequiredFiles(Schema schema) {
    return List.of(
      BundleInterfaceFile.fromSchema(schema),
      EmptyMessageBundleFile.fromSchema(schema),
      RecipientUtilFile.fromSchema(schema),
      createBundleRepositoryFile(schema)
    );
  }

  private BundleRepositoryFile createBundleRepositoryFile(Schema schema) {
    return generateAudience
      ? BundleRepositoryFile.fromSchemaWithAudience(schema)
      : BundleRepositoryFile.fromSchema(schema);
  }

  private Collection<GeneratedFile> listGeneratedAudienceFiles(Schema schema) {
    return List.of(
      AudienceInterfaceFile.fromSchema(schema),
      SoloAudienceFile.fromSchema(schema),
      GroupAudienceFile.fromSchema(schema)
    );
  }
}
