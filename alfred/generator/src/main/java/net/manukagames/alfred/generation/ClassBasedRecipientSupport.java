package net.manukagames.alfred.generation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

public abstract class ClassBasedRecipientSupport implements RecipientSupport {
  private final AccessorStrategy accessorStrategy;
  private final TypeName name;

  protected ClassBasedRecipientSupport(
    Class<?> recipientClass,
    AccessorStrategy accessorStrategy
  ) {
    this.accessorStrategy = accessorStrategy;
    this.name = ClassName.get(recipientClass);
  }

  @Override
  public TypeName createTypeName() {
    return name;
  }

  @Override
  public String translateFieldAccess(String recipientParameter, String fieldName) {
    var accessor = accessorStrategy.createForField(fieldName);
    return recipientParameter + "." + accessor;
  }

  public interface AccessorStrategy {
    String createForField(String name);
  }

  public static final class GetAccessorStrategy implements AccessorStrategy {
    private GetAccessorStrategy() {}

    @Override
    public String createForField(String name) {
      return "get" + capitalize(name);
    }

    private static String capitalize(String value) {
      if (value.length() <= 1) {
        return "";
      }
      var tail = value.substring(1);
      var head = Character.toUpperCase(value.charAt(0));
      return head + tail;
    }

    public static GetAccessorStrategy create() {
      return new GetAccessorStrategy();
    }
  }
}
