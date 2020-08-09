package net.manukagames.alfred.bundle.text;

import com.squareup.javapoet.CodeBlock;
import net.manukagames.alfred.bundle.BundleGeneration;

final class FieldAccessPart implements Part {
  private final String field;
  private final String contextVariable;

  FieldAccessPart(String contextVariable, String field) {
    this.contextVariable = contextVariable;
    this.field = field;
  }

  @Override
  public void emit(CodeBlock.Builder block, BundleGeneration generation) {
    var recipients = generation.recipientSupport();
    var access = recipients.translateFieldAccess(contextVariable, field);
    block.add(".append($L)\n", access);
  }
}
