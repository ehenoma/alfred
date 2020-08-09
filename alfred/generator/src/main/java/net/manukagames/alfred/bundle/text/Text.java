package net.manukagames.alfred.bundle.text;

import com.squareup.javapoet.CodeBlock;
import net.manukagames.alfred.bundle.BundleGeneration;

import java.util.Objects;

public interface Text {
  void emit(CodeBlock.Builder block, BundleGeneration generation);

  static Text parse(String text) {
    Objects.requireNonNull(text);
    return new Scanning(text).scan();
  }
}