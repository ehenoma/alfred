package net.manukagames.alfred.schema.generation;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import net.manukagames.alfred.language.Language;
import net.manukagames.alfred.schema.Message;
import net.manukagames.alfred.schema.Schema;

public final class SoloAudienceFile extends AbstractAudienceFile {
  public static TypeName createTypeName(Schema schema) {
    return ClassName.get(schema.packageName(), NAME);
  }

  public static SoloAudienceFile fromSchema(Schema schema) {
    Objects.requireNonNull(schema);
    return new SoloAudienceFile(schema);
  }

  private static final String NAME = "SoloAudience";

  private final TypeName recipientType;
  private final TypeName recipientUtilType;
  private final TypeName messageBundleType;

  private SoloAudienceFile(Schema schema) {
    super(schema);
    this.recipientType = schema.framework().createRecipientTypeName();
    this.messageBundleType = BundleInterfaceFile.createTypeName(schema);
    this.recipientUtilType = RecipientUtilFile.createTypeName(schema);
  }

  @Override
  public ClassName name() {
    return ClassName.get(schema.packageName(), NAME);
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
    return ClassName.get(schema.packageName(), "Bundles");
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
      .addStatement("String message_ = this.bundle.$L($L)", name, arguments)
      .addStatement("$T.send(this.recipient, message_)", recipientUtilType)
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
      ParameterSpec.builder(recipientType, "recipient_").build(),
      ParameterSpec.builder(createBundlesType(), "bundles_").build()
    );
    return MethodSpec.constructorBuilder()
      .addModifiers(Modifier.PROTECTED)
      .addParameters(parameters)
      .addCode(createConstructorCode())
      .build();
  }

  private CodeBlock createConstructorCode() {
    var code = CodeBlock.builder();
    code.addStatement("$T language_ = $T.lookupLanguage(recipient_, bundles_.languages())",
      Language.class, recipientUtilType);
    code.addStatement("this.bundle = bundles_.forLanguage(language_)");
    code.addStatement("this.recipient = recipient_");
    return code.build();
  }
}