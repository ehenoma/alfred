package net.manukagames.alfred.bundle.text;

import com.squareup.javapoet.CodeBlock;

import net.manukagames.alfred.generation.Framework;

final class PlainText implements Text {
  private final String text;

  PlainText(String text) {
    this.text = text;
  }

  @Override
  public CodeBlock toCode(final Framework framework) {
    return CodeBlock.builder()
    .addStatement("return $S", text)
    .build();
  }
}