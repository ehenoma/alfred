package net.manukagames.alfred.bundle.text.part;

import com.squareup.javapoet.CodeBlock;
import net.manukagames.alfred.generation.Framework;

public interface Part {
  void emit(CodeBlock.Builder block, Framework framework);
}
