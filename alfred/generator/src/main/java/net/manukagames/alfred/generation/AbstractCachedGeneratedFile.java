package net.manukagames.alfred.generation;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

public abstract class AbstractCachedGeneratedFile implements GeneratedFile {
  private JavaFile model;

  @Override
  public final JavaFile asModel() {
    if (model == null) {
      model = generate();
    }
    return model;
  }

  private JavaFile generate() {
    return JavaFile.builder(name().packageName(), createType()).build();
  }

  protected abstract TypeSpec createType();

  protected MethodSpec createPrivateConstructor() {
    return MethodSpec.constructorBuilder()
      .addModifiers(Modifier.PRIVATE)
      .build();
  }
}
