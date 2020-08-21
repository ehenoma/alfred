package net.manukagames.alfred.schema.generation;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import net.manukagames.alfred.language.Language;
import net.manukagames.alfred.schema.Message;
import net.manukagames.alfred.schema.Schema;

public final class GroupAudienceFile extends AbstractAudienceFile {
  public static GroupAudienceFile fromSchema(Schema schema) {
    Objects.requireNonNull(schema);
    return new GroupAudienceFile(schema);
  }

  public static ClassName createTypeName(Schema schema) {
    return ClassName.get(schema.packageName(), NAME);
  }

  private final TypeName recipientUtilType;
  private final TypeName bundleType;
  private final TypeName recipientType;
  private final TypeName bundlesType;
  private final TypeName iterableRecipientType;

  private static final String NAME = "GroupAudience";

  private GroupAudienceFile(Schema schema) {
    super(schema);
    this.recipientType = schema.framework().createRecipientTypeName();
    this.bundlesType = BundleRepositoryFile.createTypeName(schema);
    this.recipientUtilType = RecipientUtilFile.createTypeName(schema);
    this.bundleType = BundleInterfaceFile.createTypeName(schema);
    this.iterableRecipientType = ParameterizedTypeName.get(
      ClassName.get(Iterable.class),
      recipientType
    );
  }

  @Override
  public ClassName name() {
    return createTypeName(schema);
  }

  @Override
  protected TypeSpec createType() {
    return TypeSpec.classBuilder(NAME)
      .addSuperinterface(ClassName.get("", "Audience"))
      .addModifiers(Modifier.FINAL)
      .addField(createRecipientsField())
      .addField(createBundlesField())
      .addMethod(createConstructor())
      .addMethods(createSendMethods())
      .build();
  }

  private FieldSpec createRecipientsField() {
    return FieldSpec.builder(iterableRecipientType, "recipients")
      .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
      .build();
  }

  private FieldSpec createBundlesField() {
    return FieldSpec.builder(bundlesType, "bundles")
      .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
      .build();
  }

  private MethodSpec createConstructor() {
    return MethodSpec.constructorBuilder()
      .addParameter(ParameterSpec.builder(iterableRecipientType, "recipients").build())
      .addParameter(ParameterSpec.builder(bundlesType, "bundles").build())
      .addStatement("this.recipients = recipients")
      .addStatement("this.bundles = bundles")
      .build();
  }

  @Override
  protected MethodSpec createSendMethod(Message message) {
    return createSendMethodBuilder(message)
      .addCode(createSendMethodCode(message))
      .build();
  }

  private CodeBlock createSendMethodCode(Message message) {
    var factoryMethod = message.createFactoryMethodName();
    var arguments =  String.join(", ", listArgumentNames(message));
    return CodeBlock.builder()
      .addStatement("String[] cache_ = new String[this.bundles.countSupportedLanguages()]")
      .beginControlFlow("for ($T recipient_ : this.recipients)", recipientType)
      .addStatement("$T language_ = $T.lookupLanguage(recipient_, this.bundles.languages())",
        Language.class, recipientUtilType)
      .addStatement("int languageId_ = language_.id()")
      .addStatement("String cachedMessage_ = cache_[languageId_]")
      .beginControlFlow("if (cachedMessage_ != null)")
      .addStatement("$T.send(recipient_, cachedMessage_)", recipientUtilType)
      .addStatement("continue")
      .endControlFlow()
      .addStatement("$T bundle_ = this.bundles.find(language_)", bundleType)
      .addStatement("String message_ = bundle_.$L($L)", factoryMethod, arguments)
      .addStatement("cache_[languageId_] = message_")
      .addStatement("$T.send(recipient_, message_)", recipientUtilType)
      .endControlFlow()
      .build();
  }

  private Collection<String> listArgumentNames(Message message) {
    return message.context().stream()
      .map(this::translateContextArgument)
      .collect(Collectors.toList());
  }

  private static final String LOCAL_RECIPIENT_FIELD = "recipient_";

  private String translateContextArgument(Message.Variable variable) {
    return variable.hasRecipientType()
       ? LOCAL_RECIPIENT_FIELD
      : variable.name();
  }
}
