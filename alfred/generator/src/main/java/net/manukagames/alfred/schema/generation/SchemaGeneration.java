package net.manukagames.alfred.schema.generation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;

import net.manukagames.alfred.generation.GeneratedFile;
import net.manukagames.alfred.generation.Generation;
import net.manukagames.alfred.generation.RecipientSupport;
import net.manukagames.alfred.schema.Schema;

public final class SchemaGeneration {
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
    var targetDirectory = createTargetDirectory();
    var generation = createGeneration(targetDirectory);
    var generatedFiles = listGeneratedFiles(generation);
    writeFiles(targetDirectory, generatedFiles);
  }

  private void writeFiles(Path targetDirectory, Iterable<GeneratedFile> files) {
    try {
      ensureTargetDirectoryExists(targetDirectory);
      for (var file : files) {
        writeToTarget(targetDirectory, file);
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

  private void writeToTarget(Path targetDirectory, GeneratedFile file) throws IOException {
    var filePath = targetDirectory.resolve(file.name());
    Files.deleteIfExists(filePath);
    Files.writeString(filePath, file.asModel().toString(),
      StandardOpenOption.CREATE, StandardOpenOption.WRITE);
  }

  private Collection<GeneratedFile> listGeneratedFiles(Generation generation) {
    return List.of(
      MessageBundleFile.create(generation),
      AudienceFile.create(generation),
      SoloAudienceFile.create(generation),
      GroupAudienceFile.create(generation),
      MessageBundlesFile.create(generation),
      EmptyMessageBundleFile.create(generation),
      RecipientUtilFile.create(generation)
    );
  }

  private Generation createGeneration(Path targetDirectory) {
    return Generation.newBuilder()
      .withSchema(schema)
      .withRecipientSupport(findRecipientSupport())
      .withTargetDirectory(targetDirectory)
      .create();
  }

  private Path createTargetDirectory() {
    var packageTree = schema.packageName().replace(".", File.separator);
    return outputDirectory.resolve(packageTree);
  }

  private RecipientSupport findRecipientSupport() {
    var className = schema.recipient().supportClass();
    try {
      var resolvedClass = Class.forName(className);
      return createSupportFromType(resolvedClass);
    } catch (ClassNotFoundException invalidName) {
      throw new IllegalStateException("could not find recipientSupportClass", invalidName);
    }
  }

  private RecipientSupport createSupportFromType(Class<?> type) {
    var instance = injector.getInstance(type);
    if (!(instance instanceof RecipientSupport)) {
      var error = String.format(
        "configured recipientSupportClass %s is not of type RecipientSupport",
        type.getSimpleName()
      );
      throw new IllegalStateException(error);
    }
    return (RecipientSupport) instance;
  }

  public static SchemaGeneration of(Schema schema, Path outputDirectory) {
    return new SchemaGeneration(
      Guice.createInjector(),
      schema,
      outputDirectory
    );
  }
}
