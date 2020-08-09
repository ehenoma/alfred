package net.manukagames.alfred.bundle.text;

import com.squareup.javapoet.CodeBlock;
import net.manukagames.alfred.bundle.BundleGeneration;

public interface Part {
  void emit(CodeBlock.Builder block, BundleGeneration generation);
}