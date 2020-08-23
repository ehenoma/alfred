package net.manukagames.alfred.schema.generation;

import java.util.Objects;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import net.manukagames.alfred.schema.Message;
import net.manukagames.alfred.schema.Schema;

public final class AudienceInterfaceFile extends AbstractAudienceFile {
  public static AudienceInterfaceFile fromSchema(Schema schema) {
    Objects.requireNonNull(schema);
    return new AudienceInterfaceFile(schema);
  }

  private AudienceInterfaceFile(Schema schema) {
    super(schema);
  }

  private static final String NAME = "Audience";

  @Override
  public ClassName name() {
    return ClassName.get(schema.packageName(), NAME);
  }

  @Override
  protected TypeSpec createType() {
    return TypeSpec.interfaceBuilder(NAME)
      .addMethods(createSendMethods())
      .build();
  }

  @Override
  protected MethodSpec createSendMethod(Message message) {
    return createSendMethodBuilder(message)
    .addModifiers(Modifier.ABSTRACT)
    .build();
  }
}
