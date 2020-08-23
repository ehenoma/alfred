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

public abstract class AbstractAudienceFile extends AbstractCachedGeneratedFile {
  protected final Schema schema;

  protected AbstractAudienceFile(Schema schema) {
    this.schema = schema;
  }

  protected abstract MethodSpec createSendMethod(Message message);

  protected Collection<MethodSpec> createSendMethods() {
    return schema.listMessages().stream()
      .map(this::createSendMethod)
      .collect(Collectors.toList());
  }

  protected MethodSpec.Builder createSendMethodBuilder(Message message) {
    var parameters = translateContext(message);
    return MethodSpec.methodBuilder(message.createSendMethodName())
      .addModifiers(Modifier.PUBLIC)
      .addParameters(parameters)
      .returns(TypeName.VOID);
  }

  private Collection<ParameterSpec> translateContext(Message message) {
    return message.context().stream()
      .filter(Message.Variable::isIncludedInParameters)
      .map(this::createParameter)
      .collect(Collectors.toList());
  }

  private ParameterSpec createParameter(Message.Variable variable) {
    var type = ClassName.bestGuess(variable.type());
    return ParameterSpec.builder(type, variable.name()).build();
  }
}
