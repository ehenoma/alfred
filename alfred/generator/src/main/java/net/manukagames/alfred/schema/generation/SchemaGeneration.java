package net.manukagames.alfred.schema.generation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;

import net.manukagames.alfred.generation.GeneratedFile;
import net.manukagames.alfred.schema.Schema;

public final class SchemaGeneration {
  public static SchemaGeneration of(Schema schema, Path outputDirectory) {
    return new SchemaGeneration(
      Guice.createInjector(),
      schema,
      outputDirectory
    );
  }

  private final Schema schema;
  private final Injector injector;
  private final Path outputDirectory;

  SchemaGeneration(
    Injector injector,
    Schema schema,
    Path outputDirectory
  ) {
    this.schema = schema;
    this.injector = injector;
    this.outputDirectory = outputDirectory;
  }

  public void run() {
    var generatedFiles = listGeneratedFiles(schema);
    writeToTargetDirectory(generatedFiles);
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

  private void writeToTargetDirectory(Collection<GeneratedFile> files) {
    var targetDirectory = createTargetDirectory();
    writeFiles(targetDirectory, files);
  }

  private Path createTargetDirectory() {
    var packageTree = schema.packageName().replace(".", File.separator);
    return outputDirectory.resolve(packageTree);
  }

  private void writeFiles(Path targetDirectory, Iterable<GeneratedFile> files) {
    try {
      ensureTargetDirectoryExists(targetDirectory);
      for (var file : files) {
        file.writeToDirectory(targetDirectory);
      }
    } catch (IOException failedWrite) {
      throw new RuntimeException("could not write generated file", failedWrite);
    }
  }

  private void ensureTargetDirectoryExists(Path directory) throws IOException {
    if (Files.notExists(directory)) {
      Files.createDirectories(directory);
    }
  }
}
