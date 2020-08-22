package net.manukagames.alfred.language;

import java.util.Locale;
import java.util.Objects;

public final class Language {
  public static Language fromLocale(int id, Locale locale) {
    Objects.requireNonNull(locale);
    return new Language(id, locale.toLanguageTag(), locale);
  }

  public static Language create(int id, String name, Locale locale) {
    Objects.requireNonNull(name);
    Objects.requireNonNull(locale);
    return new Language(id, name, locale);
  }

  private final int id;
  private final String name;
  private final Locale locale;

  private Language(int id, String name, Locale locale) {
    this.id = id;
    this.name = name;
    this.locale = locale;
  }

  public int id() {
    return id;
  }

  public String name() {
    return name;
  }

  public Locale asLocale() {
    return locale;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof Language)) {
      return false;
    }
    return ((Language) other).id == id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return String.format(
      "Language{id: %d, name: %s, locale: %s}",
      id,
      name,
      locale
    );
  }
}