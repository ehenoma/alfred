package net.manukagames.alfred.generation;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

public interface RecipientSupport {
  CodeBlock createLanguageLookupCode(
    String recipientParameter,
    String languagesParameter
  );

  CodeBlock createSendCode(
    String recipientParameter,
    String messageParameter
  );

  String translateFieldAccess(String recipientParameter, String fieldName);

  TypeName createTypeName();
}