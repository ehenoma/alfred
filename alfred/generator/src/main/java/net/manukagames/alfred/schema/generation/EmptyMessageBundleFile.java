package net.manukagames.alfred.schema.generation;

import java.util.Objects;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import net.manukagames.alfred.generation.Generation;
import net.manukagames.alfred.schema.Message;

final class EmptyMessageBundleFile extends AbstractMessageBundleFile {
  public static EmptyMessageBundleFile create(Generation generation) {
    Objects.requireNonNull(generation);
    return new EmptyMessageBundleFile(generation);
  }

  private static final String NAME = "EmptyMessageBundle";

  private EmptyMessageBundleFile(Generation generation) {
    super(NAME, generation);
  }

  @Override
  protected TypeSpec createType() {
    return TypeSpec.classBuilder(NAME)
      .addSuperinterface(ClassName.get("", "MessageBundle"))
      .addModifiers(Modifier.FINAL)
      .addMethods(createFactoryMethods())
      .build();
  }

  @Override
  protected MethodSpec createFactoryMethod(Message message) {
    return createFactoryMethodBuilder(message)
      .addStatement("return $S", "")
      .build();
  }

  public static TypeName createTypeName(Generation generation) {
    return ClassName.get(generation.schema().packageName(), NAME);
  }
}
