package net.manukagames.alfred.schema.generation;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import com.google.common.base.Preconditions;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import net.manukagames.alfred.generation.Generation;
import net.manukagames.alfred.language.Language;
import net.manukagames.alfred.schema.Message;

public final class SoloAudienceFile extends AbstractAudienceFile {
  private static final String NAME = "SoloAudience";

  private final TypeName recipientType;
  private final TypeName recipientUtilType;
  private final TypeName messageBundleType;

  private SoloAudienceFile(Generation generation) {
    super(NAME, generation);
    this.recipientType = generation.recipientSupport().createTypeName();
    this.messageBundleType = MessageBundleFile.createTypeName(generation);
    this.recipientUtilType = RecipientUtilFile.createTypeName(generation);
  }

  @Override
  protected TypeSpec createType() {
    return TypeSpec.classBuilder(NAME)
      .addModifiers(Modifier.FINAL)
      .addSuperinterface(ClassName.get("", "Audience"))
      .addFields(createFields())
      .addMethod(createConstructor())
      .addMethods(createSendMethods())
      .build();
  }

  private static final String RECIPIENT_FIELD_NAME = "recipient";

  private Collection<FieldSpec> createFields() {
    return List.of(createRecipientField(), createMessageBundleField());
  }

  private FieldSpec createRecipientField() {
    return FieldSpec.builder(recipientType, RECIPIENT_FIELD_NAME)
      .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
      .build();
  }

  private FieldSpec createMessageBundleField() {
    return FieldSpec.builder(messageBundleType, "bundle")
      .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
      .build();
  }

  private ClassName createBundlesType() {
    return ClassName.get(generation.schema().packageName(), "MessageBundles");
  }

  @Override
  protected MethodSpec createSendMethod(Message message) {
    return createSendMethodBuilder(message)
      .addAnnotation(Override.class)
      .addCode(createSendMethodCode(message))
      .build();
  }

  private CodeBlock createSendMethodCode(Message message) {
    var arguments = String.join(", ", listArgumentNames(message));
    var name = message.createFactoryMethodName();
    return CodeBlock.builder()
      .addStatement("String message_ = bundle.$L($L)", name, arguments)
      .addStatement("$T.send(recipient, message_)", recipientUtilType)
      .build();
  }

  private Collection<String> listArgumentNames(Message message) {
    return message.context().stream()
      .map(this::translateContextArgument)
      .collect(Collectors.toList());
  }

  private String translateContextArgument(Message.Variable variable) {
    return variable.hasRecipientType()
      ? RECIPIENT_FIELD_NAME
      : variable.name();
  }

  private MethodSpec createConstructor() {
    var parameters = List.of(
      ParameterSpec.builder(recipientType, "recipient").build(),
      ParameterSpec.builder(createBundlesType(), "bundles").build()
    );
    return MethodSpec.constructorBuilder()
      .addModifiers(Modifier.PROTECTED)
      .addParameters(parameters)
      .addCode(createConstructorCode())
      .build();
  }

  private CodeBlock createConstructorCode() {
    var code = CodeBlock.builder();
    code.addStatement("$T language = $T.lookupLanguage(recipient, bundles.languages())",
      Language.class, recipientUtilType);
    code.addStatement("this.bundle = bundles.find(language)");
    code.addStatement("this.recipient = recipient");
    return code.build();
  }

  public static TypeName createTypeName(Generation generation) {
    return ClassName.get(generation.schema().packageName(), NAME);
  }

  public static SoloAudienceFile create(Generation generation) {
    Preconditions.checkNotNull(generation);
    return new SoloAudienceFile(generation);
  }
}