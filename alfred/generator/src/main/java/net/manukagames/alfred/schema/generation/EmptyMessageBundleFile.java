package net.manukagames.alfred.schema.generation;

import java.util.Objects;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import net.manukagames.alfred.schema.Message;
import net.manukagames.alfred.schema.Schema;

final class EmptyMessageBundleFile extends AbstractBundleFile {
  public static TypeName createTypeName(Schema schema) {
    return ClassName.get(schema.packageName(), NAME);
  }

  public static EmptyMessageBundleFile fromSchema(Schema schema) {
    Objects.requireNonNull(schema);
    return new EmptyMessageBundleFile(schema);
  }

  private static final String NAME = "EmptyBundle";

  private EmptyMessageBundleFile(Schema schema) {
    super(schema);
  }

  @Override
  public ClassName name() {
    return ClassName.get(schema.packageName(), NAME);
  }

  @Override
  protected TypeSpec createType() {
    return TypeSpec.classBuilder(NAME)
      .addSuperinterface(ClassName.get(schema.packageName(), "Bundle"))
      .addModifiers(Modifier.FINAL)
      .addMethods(createFormatMethods())
      .build();
  }

  @Override
  protected MethodSpec createFormatMethod(Message message) {
    return createFormatterSignature(message)
      .addStatement("return $S", "")
      .build();
  }
}
