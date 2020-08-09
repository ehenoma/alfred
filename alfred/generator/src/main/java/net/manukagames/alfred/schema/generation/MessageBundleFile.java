package net.manukagames.alfred.schema.generation;

import java.util.Objects;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import net.manukagames.alfred.generation.Generation;
import net.manukagames.alfred.schema.Message;

public final class MessageBundleFile extends AbstractMessageBundleFile {
  public static TypeName createTypeName(Generation generation) {
    return ClassName.get(generation.schema().packageName(), NAME);
  }

  public static MessageBundleFile create(Generation generation) {
    Objects.requireNonNull(generation);
    return new MessageBundleFile(generation);
  }

  private static final String NAME = "MessageBundle";

  private MessageBundleFile(Generation generation) {
    super(NAME, generation);
  }

  @Override
  protected TypeSpec createType() {
    return TypeSpec.interfaceBuilder(NAME)
      .addModifiers(Modifier.PUBLIC)
      .addMethods(createFactoryMethods())
      .build();
  }

  @Override
  protected MethodSpec createFactoryMethod(Message message) {
    return createFactoryMethodBuilder(message)
      .addModifiers(Modifier.ABSTRACT)
      .build();
  }
}
