package net.manukagames.alfred.generation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;

public final class OutputPath {
  public static OutputPath fromFile(File file) {
    Objects.requireNonNull(file);
    return fromPath(file.toPath());
  }

  public static OutputPath fromPath(Path path) {
    Objects.requireNonNull(path);
    return new OutputPath(path);
  }

  private final Path path;

  private OutputPath(Path path) {
    this.path = path;
  }

  public void write(GeneratedFile file) throws IOException {
    var path = resolveDirectoryOfFile(file);
    ensureDirectoryExists(path);
    file.writeToDirectory(path);
  }

  private Path resolveDirectoryOfFile(GeneratedFile file) {
    var packageName = file.name().packageName();
    var packagePath = packageName.replace(".", File.separator);
    return path.resolve(packagePath);
  }

  private void ensureDirectoryExists(Path path) throws IOException {
    if (Files.notExists(path)) {
      Files.createDirectories(path);
    }
  }

  public void write(Collection<GeneratedFile> files) throws IOException {
    for (var file : files) {
      write(file);
    }
  }
}