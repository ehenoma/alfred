package net.manukagames.alfred.bundle.text.part;

import java.util.Objects;

import com.squareup.javapoet.CodeBlock;

import net.manukagames.alfred.generation.Framework;

public final class FieldAccessPart implements Part {
  public static FieldAccessPart of(String variable, String field) {
    Objects.requireNonNull(variable);
    Objects.requireNonNull(field);
    return new FieldAccessPart(variable, field);
  }

  private final String field;
  private final String contextVariable;

  private FieldAccessPart(String contextVariable, String field) {
    this.contextVariable = contextVariable;
    this.field = field;
  }

  @Override
  public void emit(CodeBlock.Builder block, Framework framework) {
    var access = framework.translateFieldAccess(contextVariable, field);
    block.add(".append($L)\n", access);
  }
}
