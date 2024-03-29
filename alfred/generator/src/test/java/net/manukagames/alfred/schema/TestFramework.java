package net.manukagames.alfred.schema;

import com.google.inject.Inject;
import com.squareup.javapoet.*;

import net.manukagames.alfred.generation.FixedRecipientFramework;

import java.util.Locale;

public final class TestFramework extends FixedRecipientFramework {
  @Inject
  private TestFramework() {
    super(String.class, GetAccessorStrategy.create());
  }

  @Override
  public CodeBlock createLanguageLookupCode(
    String recipientParameter,
    String languagesParameter
  ) {
    return CodeBlock.builder()
      .addStatement("return $L.findByName($T.ENGLISH.toLanguageTag()).orElseThrow()",
        languagesParameter, Locale.class)
      .build();
  }

  @Override
  public CodeBlock createSendCode(String recipientParameter, String messageParameter) {
    return CodeBlock.builder()
      .addStatement(
        "$T.out.printf(\"%s <- \\\"%s\\\"%n\", $L, $L)",
        System.class,
        recipientParameter,
        messageParameter
      ).build();
  }
}
