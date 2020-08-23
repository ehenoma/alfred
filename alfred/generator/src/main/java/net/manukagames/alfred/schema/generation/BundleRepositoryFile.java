package net.manukagames.alfred.schema.generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import net.manukagames.alfred.language.Language;
import net.manukagames.alfred.language.LanguageTable;
import net.manukagames.alfred.schema.Schema;

public final class BundleRepositoryFile extends AbstractCachedGeneratedFile {
  public static TypeName createTypeName(Schema schema) {
    return ClassName.get(schema.packageName(), NAME);
  }

  public static BundleRepositoryFile fromSchema(Schema schema) {
    Objects.requireNonNull(schema);
    return new BundleRepositoryFile(schema, false);
  }

  public static BundleRepositoryFile fromSchemaWithAudience(Schema schema) {
    Objects.requireNonNull(schema);
    return new BundleRepositoryFile(schema, true);
  }

  private final Schema schema;
  private final TypeName groupAudienceType;
  private final TypeName soloAudienceType;
  private final TypeName bundleArrayType;
  private final TypeName bundleType;
  private final boolean generateAudience;

  private static final String NAME = "Bundles";

  private BundleRepositoryFile(Schema schema, boolean generateAudience) {
    this.schema = schema;
    this.groupAudienceType = GroupAudienceFile.createTypeName(schema);
    this.soloAudienceType = SoloAudienceFile.createTypeName(schema);
    this.bundleType = BundleInterfaceFile.createTypeName(schema);
    this.bundleArrayType = ArrayTypeName.of(bundleType);
    this.generateAudience = generateAudience;
  }

  @Override
  public ClassName name() {
    return ClassName.get(schema.packageName(), NAME);
  }

  @Override
  protected TypeSpec createType() {
    return TypeSpec.classBuilder(NAME)
      .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
      .addFields(createFields())
      .addMethod(createConstructor())
      .addMethods(createMethods())
      .addMethod(createBuilderFactory())
      .addType(MessageBundlesBuilderType.createType(schema))
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
    var languagesParameter = ParameterSpec.builder(LANGUAGES_TYPE, "languages_").build();
    var bundlesParameter = ParameterSpec.builder(bundleArrayType, "bundles_").build();
    return MethodSpec.constructorBuilder()
      .addModifiers(Modifier.PRIVATE)
      .addParameter(bundlesParameter)
      .addParameter(languagesParameter)
      .addStatement("this.bundles = bundles_")
      .addStatement("this.languages = languages_")
      .build();
  }

  private Collection<MethodSpec> createMethods() {
    return generateAudience ? createAllMethods() : createRequiredMethods();
  }

  private Collection<MethodSpec> createAllMethods() {
    return Stream.concat(
      createRequiredMethods().stream(),
      createAudienceMethods().stream()
    ).collect(Collectors.toList());
  }

  private Collection<MethodSpec> createRequiredMethods() {
    return List.of(
      createFindMethod(),
      createFindByRecipientMethod(),
      createLanguageCounter(),
      createLanguageTableAccessor()
    );
  }

  private Collection<MethodSpec> createAudienceMethods() {
    var recipientType = schema.framework().createRecipientTypeName();
    var audienceType = createAudienceTypeName();
    return List.of(
      createSoloAudienceFactory(recipientType, audienceType),
      createVariadicGroupAudienceFactory(recipientType, audienceType),
      createGroupAudienceFactory(recipientType, audienceType)
    );
  }

  private TypeName createAudienceTypeName() {
    return ClassName.get(schema.packageName(), "Audience");
  }

  private MethodSpec createSoloAudienceFactory(
    TypeName recipientType,
    TypeName audience
  ) {
    var parameter = ParameterSpec.builder(recipientType, "recipient_").build();
    return createAudienceFactoryBuilder(parameter, audience)
      .addStatement("$T.requireNonNull(recipient_)", Objects.class)
      .addStatement("return new $T(recipient_, this)", soloAudienceType)
      .build();
  }

  private MethodSpec createVariadicGroupAudienceFactory(
    TypeName recipientType,
    TypeName audience
  ) {
    var parameterType = ArrayTypeName.of(recipientType);
    var parameter = ParameterSpec.builder(parameterType, "recipients_").build();
    return createAudienceFactoryBuilder(parameter, audience)
      .varargs()
      .addStatement("$T.requireNonNull(recipients_)", Objects.class)
      .addStatement("return new $T($T.asList(recipients_), this)", groupAudienceType, Arrays.class)
      .build();
  }

  private static final ClassName COLLECTION_NAME =
    ClassName.get(Collection.class);

  private MethodSpec createGroupAudienceFactory(
    TypeName recipientType,
    TypeName audienceType
  ) {
    var parameterType = ParameterizedTypeName.get(COLLECTION_NAME, recipientType);
    var parameter = ParameterSpec.builder(parameterType, "recipients_").build();
    return createAudienceFactoryBuilder(parameter, audienceType)
      .addStatement("$T.requireNonNull(recipients_)", Objects.class)
      .addStatement("return new $T(new $T<>(recipients_), this)", groupAudienceType, ArrayList.class)
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
    var parameter = ParameterSpec.builder(ClassName.get(Language.class), "language_").build();
    return MethodSpec.methodBuilder("forLanguage")
      .addParameter(parameter)
      .addModifiers(Modifier.PUBLIC)
      .returns(bundleType)
      .addStatement("return this.bundles[language_.id()]")
      .build();
  }

  private MethodSpec createFindByRecipientMethod() {
    var recipientType = schema.framework().createRecipientTypeName();
    var utilType = RecipientUtilFile.createTypeName(schema);
    var parameter = ParameterSpec.builder(recipientType, "recipient_").build();
    return MethodSpec.methodBuilder("forRecipient")
      .addModifiers(Modifier.PUBLIC)
      .addParameter(parameter)
      .returns(bundleType)
      .addStatement("$T language_ = $T.lookupLanguage(recipient_, this.languages)",
        Language.class, utilType)
      .addStatement("return forLanguage(language_)")
      .build();
  }

  private MethodSpec createLanguageCounter() {
    return MethodSpec.methodBuilder("countSupportedLanguages")
      .returns(TypeName.INT)
      .addModifiers(Modifier.PUBLIC)
      .addStatement("return this.bundles.length")
      .build();
  }

  private MethodSpec createLanguageTableAccessor() {
    return MethodSpec.methodBuilder("languages")
      .returns(LANGUAGES_TYPE)
      .addModifiers(Modifier.PUBLIC)
      .addStatement("return this.languages")
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
