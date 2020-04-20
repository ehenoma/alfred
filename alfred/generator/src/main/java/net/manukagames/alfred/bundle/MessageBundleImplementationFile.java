package net.manukagames.alfred.bundle;

import java.util.Objects;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import net.manukagames.alfred.schema.Message;
import net.manukagames.alfred.schema.generation.AbstractMessageBundleFile;
import net.manukagames.alfred.schema.generation.MessageBundleFile;

public final class MessageBundleImplementationFile extends AbstractMessageBundleFile {
  private final BundleGeneration bundleGeneration;

  protected MessageBundleImplementationFile(
    String name,
    BundleGeneration generation
  ) {
    super(name, generation);
    this.bundleGeneration = generation;
  }

  @Override
  protected TypeSpec createType() {
    return TypeSpec.classBuilder(name)
      .addSuperinterface(MessageBundleFile.createTypeName(generation))
      .addModifiers(Modifier.FINAL)
      .addMethod(createPackagePrivateConstructor())
      .addMethods(createFactoryMethods())
      .build();
  }

  private MethodSpec createPackagePrivateConstructor() {
    return MethodSpec.constructorBuilder().build();
  }

  @Override
  protected MethodSpec createFactoryMethod(Message specification) {
    var config = bundleGeneration.bundleConfig();
    return config.findMessage(specification.name())
      .map(bundleGeneration::preprocess)
      .map(message -> createNonEmptyFactoryMethod(specification, message))
      .orElseGet(() -> createEmptyFactoryMethod(specification));
  }

  private MethodSpec createNonEmptyFactoryMethod(
    Message specification,
    String message
  ) {
    return createFactoryMethodBuilder(specification)
      .addStatement("return \"\"")
      .build();
  }

  private MethodSpec createEmptyFactoryMethod(Message specification) {
    return createFactoryMethodBuilder(specification)
      .addStatement("return \"\"")
      .build();
  }

  public static MessageBundleImplementationFile create(
    BundleGeneration generation
  ) {
    Objects.requireNonNull(generation);
    var name = createClassName(generation.bundleConfig());
    return new MessageBundleImplementationFile(name, generation);
  }

  private static String createClassName(BundleConfig config) {
    return "MessageBundle_" + config.locale().toLanguageTag();
  }
}
