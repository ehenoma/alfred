package net.manukagames.alfred.schema.generation;

import java.util.Objects;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import net.manukagames.alfred.generation.AbstractCachedGeneratedFile;
import net.manukagames.alfred.language.Language;
import net.manukagames.alfred.language.LanguageTable;
import net.manukagames.alfred.schema.Schema;

public final class RecipientUtilFile extends AbstractCachedGeneratedFile {
  static TypeName createTypeName(Schema schema) {
    return ClassName.get(schema.packageName(), NAME);
  }

  public static RecipientUtilFile fromSchema(Schema schema) {
    Objects.requireNonNull(schema);
    return new RecipientUtilFile(schema);
  }

  private static final String NAME = "Recipients";

  private final Schema schema;
  private final ParameterSpec recipientParameter;

  private RecipientUtilFile(Schema schema) {
    var recipientType = schema.framework().createRecipientTypeName();
    this.recipientParameter = ParameterSpec.builder(recipientType, "recipient").build();
    this.schema = schema;
  }

  @Override
  public ClassName name() {
    return ClassName.get(schema.packageName(), NAME);
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
    var code = schema.framework().createSendCode("recipient", "message");
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
    var code = schema.framework().createLanguageLookupCode("recipient", "languages");
    return MethodSpec.methodBuilder("lookupLanguage")
      .returns(TypeName.get(Language.class))
      .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
      .addParameter(recipientParameter)
      .addParameter(LANGUAGES_PARAMETER)
      .addCode(code)
      .build();
  }
}
