package net.manukagames.alfred.generation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;

public interface GeneratedFile {
  ClassName name();
  JavaFile asModel();

  default void writeToDirectory(Path directory) throws IOException {
    var filePath = directory.resolve(name().toString());
    Files.deleteIfExists(filePath);
    Files.writeString(filePath, asModel().toString(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
  }
}