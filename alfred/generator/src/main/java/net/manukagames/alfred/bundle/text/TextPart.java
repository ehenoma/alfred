package net.manukagames.alfred.bundle.text;

import com.squareup.javapoet.CodeBlock;
import net.manukagames.alfred.bundle.BundleGeneration;

final class TextPart implements Part {
  private final String text;

  TextPart(String text) {
    this.text = text;
  }

  @Override
  public void emit(CodeBlock.Builder block, BundleGeneration generation) {
    block.add(".append($S)\n", text);
  }
}
