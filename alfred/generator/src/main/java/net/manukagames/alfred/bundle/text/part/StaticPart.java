package net.manukagames.alfred.bundle.text.part;

import java.util.Objects;

import com.squareup.javapoet.CodeBlock;

import net.manukagames.alfred.generation.Framework;

public final class StaticPart implements Part {
  public static StaticPart of(String text) {
    Objects.requireNonNull(text);
    return new StaticPart(text);
  }

  private final String text;

  private StaticPart(String text) {
    this.text = text;
  }

  @Override
  public void emit(CodeBlock.Builder block, Framework framework) {
    block.add(".append($S)\n", text);
  }
}