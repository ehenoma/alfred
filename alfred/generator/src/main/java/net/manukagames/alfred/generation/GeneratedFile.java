package net.manukagames.alfred.generation;

import java.io.IOException;
import java.nio.file.Path;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;

import net.manukagames.alfred.yaml.MoreFiles;

public interface GeneratedFile {
  ClassName name();
  JavaFile asModel();

  default void writeToDirectory(Path directory) throws IOException {
    var path = selectTargetPath(directory);
    var content = asModel().toString();
    MoreFiles.writeToNewFile(path, content);
  }

  private Path selectTargetPath(Path directory) {
    var name = String.format("%s.java", name().simpleName());
    return directory.resolve(name);
  }
}