package net.manukagames.alfred.bundle.text;

import java.util.Collection;

import com.squareup.javapoet.CodeBlock;

import net.manukagames.alfred.bundle.text.part.Part;
import net.manukagames.alfred.generation.Framework;

final class PlaceholderText implements Text {
  private final Collection<Part> parts;

  PlaceholderText(Collection<Part> parts) {
    this.parts = parts;
  }

  public CodeBlock toCode(Framework framework) {
    var block = CodeBlock.builder();
    writeTo(block, framework);
    return block.build();
  }

  private void writeTo(CodeBlock.Builder block, Framework framework) {
    block.add("return new $T()\n", StringBuilder.class);
    block.indent();
    for (Part part : parts) {
      part.emit(block, framework);
    }
    block.addStatement(".toString()");
    block.unindent();
  }
}
