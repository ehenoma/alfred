package net.manukagames.alfred.schema.generation;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.*;

import net.manukagames.alfred.generation.AbstractCachedGeneratedFile;
import net.manukagames.alfred.generation.Generation;
import net.manukagames.alfred.language.Language;
import net.manukagames.alfred.language.LanguageTable;

import java.util.Objects;

public final class RecipientUtilFile extends AbstractCachedGeneratedFile {
  private static final String NAME = "Recipients";

  private final ParameterSpec recipientParameter;

  private RecipientUtilFile(Generation generation) {
    super(NAME, generation);
    var recipientType = generation.recipientSupport().createTypeName();
    this.recipientParameter = ParameterSpec.builder(recipientType, "recipient").build();
  }

  @Override
  protected TypeSpec createType() {
    return TypeSpec.classBuilder(NAME)
      .addModifiers(Modifier.FINAL)
      .addMethod(createPrivateConstructor())
      .addMethod(createSendMethod())
      .addMethod(createLanguageLookupMethod())
      .build();
  }

  private static final ParameterSpec MESSAGE_PARAMETER = ParameterSpec.builder(
    ClassName.get(String.class),
    "message"
  ).build();

  private MethodSpec createSendMethod() {
    var code = generation.recipientSupport()
      .createSendCode("recipient", "message");
    return MethodSpec.methodBuilder("send")
      .addParameter(recipientParameter)
      .addParameter(MESSAGE_PARAMETER)
      .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
      .addCode(code)
      .build();
  }

  private static final ParameterSpec LANGUAGES_PARAMETER = ParameterSpec.builder(
    ClassName.get(LanguageTable.class), "languages"
  ).build();

  private MethodSpec createLanguageLookupMethod() {
    var code = generation.recipientSupport()
      .createLanguageLookupCode("recipient", "languages");
    return MethodSpec.methodBuilder("lookupLanguage")
      .returns(TypeName.get(Language.class))
      .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
      .addParameter(recipientParameter)
      .addParameter(LANGUAGES_PARAMETER)
      .addCode(code)
      .build();
  }

  static TypeName createTypeName(Generation generation) {
    return ClassName.get(generation.schema().packageName(), NAME);
  }

  public static RecipientUtilFile create(Generation generation) {
    Objects.requireNonNull(generation);
    return new RecipientUtilFile(generation);
  }
}
