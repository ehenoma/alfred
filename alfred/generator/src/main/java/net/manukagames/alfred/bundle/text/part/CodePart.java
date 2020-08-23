package net.manukagames.alfred.bundle.text.part;

import java.util.Objects;

import com.squareup.javapoet.CodeBlock;

import net.manukagames.alfred.generation.Framework;

public final class CodePart implements Part {
  public static CodePart of(String code) {
    Objects.requireNonNull(code);
    return new CodePart(code);
  }

  private final String code;

  private CodePart(String code) {
    this.code = code;
  }

  @Override
  public void emit(CodeBlock.Builder block, Framework framework) {
    block.add(".append($L)\n", code);
  }
}
