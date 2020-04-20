package net.manukagames.alfred.generation;

import com.squareup.javapoet.JavaFile;

public interface GeneratedFile {
  String name();

  JavaFile asModel();
}