package net.manukagames.alfred.bundle.text;

import com.squareup.javapoet.CodeBlock;
import net.manukagames.alfred.bundle.BundleGeneration;

import java.util.Objects;

final class PlainText implements Text {
  public static PlainText of(String value) {
    Objects.requireNonNull(value);
    return new PlainText(value);
  }

  private final String text;

  private PlainText(String text) {
    this.text = text;
  }

  @Override
  public void emit(CodeBlock.Builder block, BundleGeneration generation) {
    block.addStatement("return $S", text);
  }
}