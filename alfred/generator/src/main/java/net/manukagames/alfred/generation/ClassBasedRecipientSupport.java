package net.manukagames.alfred.generation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

public abstract class ClassBasedRecipientSupport implements Framework {
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
  public TypeName createRecipientTypeName() {
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
      return "get" + capitalize(name) + "()";
    }

    private static String capitalize(String value) {
      return switch (value.length()) {
        case 0 -> "";
        case 1 -> value.toUpperCase();
        default -> capitalizeNonEmpty(value);
      };
    }

    private static String capitalizeNonEmpty(String value) {
      var tail = value.substring(1);
      var head = Character.toUpperCase(value.charAt(0));
      return head + tail;
    }

    public static GetAccessorStrategy create() {
      return new GetAccessorStrategy();
    }
  }
}
