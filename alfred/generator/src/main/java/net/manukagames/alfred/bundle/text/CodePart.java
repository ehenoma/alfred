package net.manukagames.alfred.bundle.text;

import com.squareup.javapoet.CodeBlock;
import net.manukagames.alfred.bundle.BundleGeneration;

final class CodePart implements Part {
  private final String code;

  CodePart(String code) {
    this.code = code;
  }

  @Override
  public void emit(CodeBlock.Builder block, BundleGeneration generation) {
    block.add(".append($L)\n", code);
  }
}
