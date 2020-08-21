package net.manukagames.alfred.schema.generation;

import java.util.Objects;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import net.manukagames.alfred.schema.Message;
import net.manukagames.alfred.schema.Schema;

public final class BundleInterfaceFile extends AbstractBundleFile {
  private static final String NAME = "Bundle";

  public static TypeName createTypeName(Schema schema) {
    return ClassName.get(schema.packageName(), NAME);
  }

  public static BundleInterfaceFile fromSchema(Schema schema) {
    Objects.requireNonNull(schema);
    return new BundleInterfaceFile(schema);
  }

  private BundleInterfaceFile(Schema schema) {
    super(schema);
  }

  @Override
  public ClassName name() {
    return ClassName.get(schema.packageName(), NAME);
  }

  @Override
  protected TypeSpec createType() {
    return TypeSpec.interfaceBuilder(NAME)
      .addModifiers(Modifier.PUBLIC)
      .addMethods(createFormatMethods())
      .build();
  }

  @Override
  protected MethodSpec createFormatMethod(Message message) {
    return createFormatterSignature(message)
      .addModifiers(Modifier.ABSTRACT)
      .build();
  }
}
