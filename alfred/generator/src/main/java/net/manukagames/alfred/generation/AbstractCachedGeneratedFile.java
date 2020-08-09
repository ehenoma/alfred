package net.manukagames.alfred.generation;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

public abstract class AbstractCachedGeneratedFile implements GeneratedFile {
  protected final String name;
  protected final Generation generation;
  private JavaFile model;

  protected AbstractCachedGeneratedFile(
    String name,
    Generation generation
  ) {
    this.name = name;
    this.generation = generation;
  }

  @Override
  public String name() {
    return name + ".java";
  }

  @Override
  public JavaFile asModel() {
    if (model == null) {
      model = generate();
    }
    return model;
  }

  private static final String COMMENT = "This file was generated by Alfred";

  private JavaFile generate() {
    return JavaFile.builder(
      generation.schema().packageName(),
      createType()
    ).addFileComment(COMMENT)
     .build();
  }

  protected abstract TypeSpec createType();

  protected MethodSpec createPrivateConstructor() {
    return MethodSpec.constructorBuilder()
      .addModifiers(Modifier.PRIVATE)
      .build();
  }
}
