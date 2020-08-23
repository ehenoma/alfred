package net.manukagames.alfred.bundle.text;

import com.squareup.javapoet.CodeBlock;

import net.manukagames.alfred.generation.Framework;

public interface Text {
  CodeBlock toCode(Framework framework);
}