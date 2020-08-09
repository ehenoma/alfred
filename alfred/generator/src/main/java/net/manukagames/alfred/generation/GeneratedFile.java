package net.manukagames.alfred.generation;

import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public interface GeneratedFile {
  String name();

  JavaFile asModel();

  default void writeToDirectory(Path directory) throws IOException {
    var filePath = directory.resolve(name());
    Files.deleteIfExists(filePath);
    Files.writeString(filePath, asModel().toString(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
  }
}