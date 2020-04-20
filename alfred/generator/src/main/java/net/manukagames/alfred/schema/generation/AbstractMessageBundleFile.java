package net.manukagames.alfred.schema.generation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import net.manukagames.alfred.generation.AbstractCachedGeneratedFile;
import net.manukagames.alfred.generation.Generation;
import net.manukagames.alfred.schema.Message;

import javax.lang.model.element.Modifier;
import java.util.Collection;
import java.util.stream.Collectors;

public abstract class AbstractMessageBundleFile extends AbstractCachedGeneratedFile {
  private final TypeName recipientType;

  protected AbstractMessageBundleFile(String name, Generation generation) {
    super(name, generation);
    this.recipientType = generation.recipientSupport().createTypeName();
  }

  protected Collection<MethodSpec> createFactoryMethods() {
    var messages = generation.schema().listMessages();
    return messages.stream()
      .map(this::createFactoryMethod)
      .collect(Collectors.toList());
  }

  protected abstract MethodSpec createFactoryMethod(Message message);

  protected MethodSpec.Builder createFactoryMethodBuilder(Message message) {
    return MethodSpec.methodBuilder(message.createFactoryMethodName())
      .addModifiers(Modifier.PUBLIC)
      .returns(ClassName.get(String.class))
      .addParameters(translateContext(message));
  }

  private Collection<ParameterSpec> translateContext(Message message) {
    return message.context().stream()
      .map(this::createParameter)
      .collect(Collectors.toList());
  }

  protected ParameterSpec createParameter(Message.Variable variable) {
    var type = variable.hasRecipientType()
      ? recipientType
      : ClassName.bestGuess(variable.type());
    return ParameterSpec.builder(type, variable.name()).build();
  }
}
