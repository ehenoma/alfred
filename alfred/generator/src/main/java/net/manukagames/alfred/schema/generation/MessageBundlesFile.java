package net.manukagames.alfred.schema.generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import net.manukagames.alfred.generation.AbstractCachedGeneratedFile;
import net.manukagames.alfred.generation.Generation;
import net.manukagames.alfred.language.Language;
import net.manukagames.alfred.language.LanguageTable;

public final class MessageBundlesFile extends AbstractCachedGeneratedFile {
  public static TypeName createTypeName(Generation generation) {
    return ClassName.get(generation.schema().packageName(), NAME);
  }

  public static MessageBundlesFile create(Generation generation) {
    Objects.requireNonNull(generation);
    return new MessageBundlesFile(generation);
  }

  private final TypeName groupAudienceType;
  private final TypeName soloAudienceType;
  private final TypeName bundleArrayType;
  private final TypeName bundleType;

  private static final String NAME = "MessageBundles";

  private MessageBundlesFile(Generation generation) {
    super(NAME, generation);
    this.groupAudienceType = GroupAudienceFile.createTypeName(generation);
    this.soloAudienceType = SoloAudienceFile.createTypeName(generation);
    this.bundleType = MessageBundleFile.createTypeName(generation);
    this.bundleArrayType = ArrayTypeName.of(bundleType);
  }

  @Override
  protected TypeSpec createType() {
    return TypeSpec.classBuilder(NAME)
      .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
      .addFields(createFields())
      .addMethod(createConstructor())
      .addMethods(createMethods())
      .addMethod(createBuilderFactory())
      .addType(MessageBundlesBuilderType.createType(generation))
      .build();
  }

  private Collection<FieldSpec> createFields() {
    return List.of(createBundlesField(), createLanguagesField());
  }

  private FieldSpec createBundlesField() {
    return FieldSpec.builder(bundleArrayType, "bundles")
      .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
      .build();
  }

  private static final TypeName LANGUAGES_TYPE = ClassName.get(LanguageTable.class);

  private FieldSpec createLanguagesField() {
    return FieldSpec.builder(LANGUAGES_TYPE, "languages")
      .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
      .build() ;
  }

  private MethodSpec createConstructor() {
    var languagesParameter = ParameterSpec.builder(LANGUAGES_TYPE, "languages").build();
    var bundlesParameter = ParameterSpec.builder(bundleArrayType, "bundles").build();
    return MethodSpec.constructorBuilder()
      .addModifiers(Modifier.PRIVATE)
      .addParameter(bundlesParameter)
      .addParameter(languagesParameter)
      .addStatement("this.bundles = bundles")
      .addStatement("this.languages = languages")
      .build();
  }

  private Collection<MethodSpec> createMethods() {
    var recipientType = generation.recipientSupport().createTypeName();
    var audienceType = createAudienceTypeName();
    return List.of(
      createSoloAudienceFactory(recipientType, audienceType),
      createVariadicGroupAudienceFactory(recipientType, audienceType),
      createGroupAudienceFactory(recipientType, audienceType),
      createFindMethod(),
      createFindByRecipientMethod(),
      createLanguageCounter(),
      createLanguageTableAccessor()
    );
  }

  private TypeName createAudienceTypeName() {
    return ClassName.get(generation.schema().packageName(), "Audience");
  }

  private MethodSpec createSoloAudienceFactory(
    TypeName recipientType,
    TypeName audience
  ) {
    var parameter = ParameterSpec.builder(recipientType, "recipient").build();
    return createAudienceFactoryBuilder(parameter, audience)
      .addStatement("$T.requireNonNull(recipient)", Objects.class)
      .addStatement("return new $T(recipient, this)", soloAudienceType)
      .build();
  }

  private MethodSpec createVariadicGroupAudienceFactory(
    TypeName recipientType,
    TypeName audience
  ) {
    var parameterType = ArrayTypeName.of(recipientType);
    var parameter = ParameterSpec.builder(parameterType, "recipients").build();
    return createAudienceFactoryBuilder(parameter, audience)
      .varargs()
      .addStatement("$T.requireNonNull(recipients)", Objects.class)
      .addStatement("return new $T($T.asList(recipients), this)", groupAudienceType, Arrays.class)
      .build();
  }

  private static final ClassName COLLECTION_NAME =
    ClassName.get(Collection.class);

  private MethodSpec createGroupAudienceFactory(
    TypeName recipientType,
    TypeName audienceType
  ) {
    var parameterType = ParameterizedTypeName.get(COLLECTION_NAME, recipientType);
    var parameter = ParameterSpec.builder(parameterType, "recipients").build();
    return createAudienceFactoryBuilder(parameter, audienceType)
      .addStatement("$T.requireNonNull(recipients)", Objects.class)
      .addStatement("return new $T(new $T<>(recipients), this)", groupAudienceType, ArrayList.class)
      .build();
  }

  private MethodSpec.Builder createAudienceFactoryBuilder(
    ParameterSpec parameter,
    TypeName audience
  ) {
    return MethodSpec.methodBuilder("createAudience")
      .addModifiers(Modifier.PUBLIC)
      .addParameter(parameter)
      .returns(audience);
  }

  private MethodSpec createFindMethod() {
    var parameter = ParameterSpec.builder(ClassName.get(Language.class), "language").build();
    return MethodSpec.methodBuilder("find")
      .addParameter(parameter)
      .returns(bundleType)
      .addStatement("return bundles[language.id()]")
      .build();
  }

  private MethodSpec createFindByRecipientMethod() {
    var recipientType = generation.recipientSupport().createTypeName();
    var utilType = RecipientUtilFile.createTypeName(generation);
    var parameter = ParameterSpec.builder(recipientType, "recipient").build();
    return MethodSpec.methodBuilder("forRecipient")
      .addModifiers(Modifier.PUBLIC)
      .addParameter(parameter)
      .returns(bundleType)
      .addStatement("$T language = $T.lookupLanguage(recipient)",
        Language.class, utilType)
      .addStatement("return find(language)")
      .build();
  }

  private MethodSpec createLanguageCounter() {
    return MethodSpec.methodBuilder("countSupportedLanguages")
      .returns(TypeName.INT)
      .addModifiers(Modifier.PUBLIC)
      .addStatement("return bundles.length")
      .build();
  }

  private MethodSpec createLanguageTableAccessor() {
    return MethodSpec.methodBuilder("languages")
      .returns(LANGUAGES_TYPE)
      .addModifiers(Modifier.PUBLIC)
      .addStatement("return languages")
      .build();
  }

  private MethodSpec createBuilderFactory() {
    return MethodSpec.methodBuilder("newBuilder")
      .returns(ClassName.get("", "Builder"))
      .addStatement("return new Builder()")
      .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
      .build();
  }
}
