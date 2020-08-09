package net.manukagames.alfred.schema.generation;

import java.util.Objects;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import net.manukagames.alfred.generation.AbstractCachedGeneratedFile;
import net.manukagames.alfred.generation.Generation;
import net.manukagames.alfred.language.Language;
import net.manukagames.alfred.language.LanguageTable;

public final class RecipientUtilFile extends AbstractCachedGeneratedFile {
  static TypeName createTypeName(Generation generation) {
    return ClassName.get(generation.schema().packageName(), NAME);
  }

  public static RecipientUtilFile create(Generation generation) {
    Objects.requireNonNull(generation);
    return new RecipientUtilFile(generation);
  }

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
}
