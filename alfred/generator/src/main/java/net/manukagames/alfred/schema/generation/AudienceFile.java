package net.manukagames.alfred.schema.generation;

import java.util.Objects;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import net.manukagames.alfred.generation.Generation;
import net.manukagames.alfred.schema.Message;

import javax.lang.model.element.Modifier;

public final class AudienceFile extends AbstractAudienceFile {
  private static final String NAME = "Audience";

  private AudienceFile(Generation generation) {
    super(NAME, generation);
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

  public static AudienceFile create(Generation generation) {
    Objects.requireNonNull(generation);
    return new AudienceFile(generation);
  }
}