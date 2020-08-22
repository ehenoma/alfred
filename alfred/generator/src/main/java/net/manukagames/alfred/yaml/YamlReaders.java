package net.manukagames.alfred.yaml;

import java.util.Map;

public final class YamlReaders {
  private YamlReaders() {}

  public static String convertToStringOrEmpty(Object value) {
    return value == null ? "" : value.toString();
  }

  public static <ValueT> ValueT require(
    String name,
    Map<?, ?> properties
  ) {
    var value = properties.get(name);
    if (value == null) {
      var error = String.format("required property %s is missing", name);
      throw InvalidFormatException.withMessage(error);
    }
    return tryToCast(name, value);
  }

  @SuppressWarnings("unchecked")
  public static <ValueT> ValueT tryToCast(String name, Object value) {
    try {
      return (ValueT) value;
    } catch (ClassCastException invalidCast) {
      var typeName = value.getClass().getSimpleName();
      var error = String.format(
        "property %s has an invalid type: %s", name, typeName
      );
      throw InvalidFormatException.withMessage(error);
    }
  }
}
