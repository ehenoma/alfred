package net.manukagames.alfred.bundle.text;

import com.squareup.javapoet.CodeBlock;

import java.util.Collection;

import net.manukagames.alfred.bundle.BundleGeneration;

final class PlaceholderText implements Text {
  private final Collection<Part> parts;

  PlaceholderText(Collection<Part> parts) {
    this.parts = parts;
  }

  public void emit(CodeBlock.Builder block, BundleGeneration generation) {
    block.add("return new $T()\n", StringBuilder.class);
    block.indent();
    for (Part part : parts) {
      part.emit(block, generation);
    }
    block.addStatement(".toString()");
    block.unindent();
  }
}
