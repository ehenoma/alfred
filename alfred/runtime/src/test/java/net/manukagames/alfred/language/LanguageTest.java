package net.manukagames.alfred.language;

import java.util.Locale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class LanguageTest {
  @Test
  void testNameOfLocaleWithId() {
    Language language = Language.fromLocale(10, Locale.ENGLISH);
    Assertions.assertEquals(Locale.ENGLISH.toLanguageTag(), language.name());
  }

  @Test
  void testNameOfNamedLanguage() {
    Language language = Language.create(10, "english", Locale.ENGLISH);
    Assertions.assertEquals( "english", language.name());
  }

  @Test
  void testLocaleEquality() {
    Language language = Language.fromLocale(0, Locale.ENGLISH);
    Assertions.assertEquals(Locale.ENGLISH, language.asLocale());
  }

  @Test
  void testEqualityWithOtherObject() {
    Language language = Language.fromLocale(0, Locale.ENGLISH);
    Assertions.assertNotEquals(new Object(), language);
    Assertions.assertNotEquals(Locale.ENGLISH, language);
  }

  @Test
  void testIdEquality() {
    Language language = Language.fromLocale(0, Locale.ENGLISH);
    Assertions.assertEquals(Language.fromLocale(0, Locale.ENGLISH), language);
    Assertions.assertEquals(Language.fromLocale(0, Locale.GERMAN), language);
    Assertions.assertNotEquals(Language.fromLocale(1, Locale.ENGLISH), language);
  }

  @Test
  void testIdHashCode() {
    Language language = Language.fromLocale(0, Locale.ENGLISH);
    Language languageWithSameId = Language.fromLocale(0, Locale.GERMAN);
    Language languageWithDifferentId = Language.fromLocale(1, Locale.ENGLISH);
    Assertions.assertEquals(languageWithSameId.hashCode(), language.hashCode());
    Assertions.assertNotEquals(languageWithDifferentId.hashCode(), language.hashCode());
  }

  @Test
  void testStringRepresentationOfLocaleWitHId() {
    Language localeWithId = Language.fromLocale(0, Locale.ENGLISH);
    Assertions.assertEquals(
      String.format("Language{id: 0, name: %s, locale: %s}",
        Locale.ENGLISH.toLanguageTag(),
        Locale.ENGLISH),
      localeWithId.toString()
    );
  }

  @Test
  void testStringRepresentationOfNamedLanguage() {
    Language named = Language.create(0, "english", Locale.ENGLISH);
    Assertions.assertEquals(
      String.format("Language{id: 0, name: english, locale: %s}", Locale.ENGLISH),
      named.toString()
    );
  }
}