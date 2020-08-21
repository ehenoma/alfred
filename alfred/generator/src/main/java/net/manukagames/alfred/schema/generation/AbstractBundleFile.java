package net.manukagames.alfred.schema.generation;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import net.manukagames.alfred.generation.AbstractCachedGeneratedFile;
import net.manukagames.alfred.schema.Message;
import net.manukagames.alfred.schema.Schema;

public abstract class AbstractBundleFile extends AbstractCachedGeneratedFile {
  private final TypeName recipientType;
  protected final Schema schema;

  protected AbstractBundleFile(Schema schema) {
    this.schema = schema;
    this.recipientType = schema.framework().createRecipientTypeName();
  }

  protected Collection<MethodSpec> createFormatMethods() {
    return schema.listMessages().stream()
      .map(this::createFormatMethod)
      .collect(Collectors.toList());
  }

  protected abstract MethodSpec createFormatMethod(Message message);

  protected MethodSpec.Builder createFormatterSignature(Message message) {
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
