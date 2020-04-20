package net.manukagames.alfred.schema.generation;

import com.squareup.javapoet.*;
import net.manukagames.alfred.generation.Generation;
import net.manukagames.alfred.language.Language;
import net.manukagames.alfred.language.LanguageTable;

import javax.lang.model.element.Modifier;
import java.util.*;

final class MessageBundlesBuilderType {
  private final Generation generation;
  private final TypeName bundleType;

  private MessageBundlesBuilderType(Generation generation) {
    this.generation = generation;
    this.bundleType = MessageBundleFile.createTypeName(generation);
  }

  public TypeSpec createType() {
    return TypeSpec.classBuilder("Builder")
      .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
      .addMethod(createPrivateConstructor())
      .addFields(createFields())
      .addMethods(createMethods())
      .build();
  }

  private MethodSpec createPrivateConstructor() {
    return MethodSpec.constructorBuilder()
      .addModifiers(Modifier.PRIVATE)
      .build();
  }

  private static final TypeName LANGUAGE_TYPE = ClassName.get(Language.class);

  private Collection<FieldSpec> createFields() {
    return List.of(
      createLanguageBundlesField(),
      createLanguageTableBuilderField(),
      createHighestIdField()
    );
  }

  private TypeName createLanguageBundlesTypeName() {
    return ParameterizedTypeName.get(
      ClassName.get(Map.class),
      LANGUAGE_TYPE,
      bundleType
    );
  }

  private FieldSpec createLanguageBundlesField() {
    return FieldSpec.builder(
      createLanguageBundlesTypeName(),
      "languageBundles"
    ).initializer("new $T<>()", HashMap.class)
      .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
      .build();
  }

  private FieldSpec createHighestIdField() {
    return FieldSpec.builder(TypeName.INT, "highestId")
      .addModifiers(Modifier.PRIVATE)
      .build();
  }

  private FieldSpec createLanguageTableBuilderField() {
    return FieldSpec.builder(
      ClassName.get(LanguageTable.Builder.class),
      "languages"
    ).initializer("$T.newBuilder()", LanguageTable.class)
      .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
      .build();
  }

  private Collection<MethodSpec> createMethods() {
    return List.of(
      createAddBundleMethod(),
      createPackMethod(),
      createBuildMethod()
    );
  }

  private static final TypeName THIS_RETURN_TYPE = ClassName.get("", "Builder");
  private static final TypeName LOCALE_TYPE = ClassName.get(Locale.class);

  private MethodSpec createAddBundleMethod() {
    var localeParameter = ParameterSpec.builder(LOCALE_TYPE, "locale").build();
    var bundleParameter = ParameterSpec.builder(bundleType, "bundle").build();
    return MethodSpec.methodBuilder("addBundle")
      .addModifiers(Modifier.PUBLIC)
      .addParameter(localeParameter)
      .addParameter(bundleParameter)
      .returns(THIS_RETURN_TYPE)
      .addStatement("$T.requireNonNull(locale)", Objects.class)
      .addStatement("$T.requireNonNull(bundle)", Objects.class)
      .addStatement("$T language = languages.register(locale)", LANGUAGE_TYPE)
      .beginControlFlow("if (language.id() > highestId)")
      .addStatement("highestId = language.id()")
      .endControlFlow()
      .addStatement("languageBundles.put(language, bundle)")
      .addStatement("return this")
      .build();
  }

  private MethodSpec createBuildMethod() {
    var bundlesType = MessageBundlesFile.createTypeName(generation);
    return MethodSpec.methodBuilder("create")
      .addModifiers(Modifier.PUBLIC)
      .returns(bundlesType)
      .addStatement("$T[] packed = pack()", bundleType)
      .addStatement("return new $T(packed, languages.create())", bundlesType)
      .build();
  }

  private MethodSpec createPackMethod() {
    var emptyBundleType = EmptyMessageBundleFile.createTypeName(generation);
    return MethodSpec.methodBuilder("pack")
      .addModifiers(Modifier.PRIVATE)
      .returns(ArrayTypeName.of(bundleType))
      .addStatement("$T[] packed = new $T[highestId + 1]", bundleType, bundleType)
      .addStatement("$T.fill(packed, new $T())", Arrays.class, emptyBundleType)
      .beginControlFlow("for ($T<$T, $T> entry : languageBundles.entrySet())",
        Map.Entry.class, Language.class, bundleType)
      .addStatement("packed[entry.getKey().id()] = entry.getValue()")
      .endControlFlow()
      .addStatement("return packed")
      .build();
  }

  public static TypeSpec createType(Generation generation) {
    Objects.requireNonNull(generation);
    return new MessageBundlesBuilderType(generation).createType();
  }
}
