package net.manukagames.alfred.bundle;

import java.util.Objects;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import net.manukagames.alfred.schema.Message;
import net.manukagames.alfred.schema.Schema;
import net.manukagames.alfred.schema.generation.AbstractBundleFile;
import net.manukagames.alfred.schema.generation.BundleInterfaceFile;
import net.manukagames.alfred.bundle.text.Scanning;

public final class BundleImplementationFile extends AbstractBundleFile {
  public static BundleImplementationFile create(Bundle bundle, Schema schema) {
    Objects.requireNonNull(bundle);
    Objects.requireNonNull(schema);
    var name = createClassName(bundle, schema);
    return new BundleImplementationFile(name, bundle, schema);
  }

  private static ClassName createClassName(Bundle bundle, Schema schema) {
    var localeName = bundle.locale().getDisplayName().replace(" ", "_");
    var name = "GeneratedMessageBundle_" + localeName;
    return ClassName.get(schema.packageName(), name);
  }

  private final Bundle bundle;
  private final Schema schema;
  private final ClassName name;

  protected BundleImplementationFile(ClassName name, Bundle bundle, Schema schema) {
    super(schema);
    this.name = name;
    this.bundle = bundle;
    this.schema = schema;
  }

  @Override
  public ClassName name() {
    return name;
  }

  @Override
  protected TypeSpec createType() {
    return TypeSpec.classBuilder(name())
      .addSuperinterface(BundleInterfaceFile.createTypeName(schema))
      .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
      .addMethods(createFormatMethods())
      .build();
  }

  @Override
  protected MethodSpec createFormatMethod(Message specification) {
    var message = findMessageOrDefault(specification);
    var text = Scanning.of(message).run();
    return createFormatterSignature(specification)
      .addCode(text.toCode(schema.framework()))
      .build();
  }

  private String findMessageOrDefault(Message specification) {
    return bundle.loadMessage(specification.name()).orElse("");
  }
}
