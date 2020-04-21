package net.manukagames.alfred.language;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

final class LanguageTableTest {
  private final LanguageTable testTable = LanguageTable.newBuilder()
    .addLanguage(Locale.ENGLISH)
    .addLanguage(Locale.GERMAN)
    .addLanguageWithAlias(Locale.ITALIAN, "italian")
    .addLanguage(Locale.FRENCH)
    .addLanguage(Locale.TAIWAN)
    .create();

  @Test
  void testEmptyTable() {
    LanguageTable table = LanguageTable.empty();
    assertDoesNotExist(table.findById(0));
  }

  @Test
  void testGapDoesNotExist() {
    LanguageTable table = LanguageTable.of(
      Language.fromLocale(0, Locale.ENGLISH),
      Language.fromLocale(2, Locale.GERMAN)
    );
    assertExistsWithLocale(table.findById(0), Locale.ENGLISH);
    assertExistsWithLocale(table.findById(2), Locale.GERMAN);
    assertDoesNotExist(table.findById(1));
  }

  @Test
  void testFindById() {
    assertExistsWithLocale(testTable.findById(0), Locale.ENGLISH);
    assertExistsWithLocale(testTable.findById(1), Locale.GERMAN);
    assertExistsWithLocale(testTable.findById(4), Locale.TAIWAN);
    assertDoesNotExist(testTable.findById(-1));
    assertDoesNotExist(testTable.findById(5));
  }

  @Test
  void testFindByIdOrFallback() {
    Language fallback = Language.fromLocale(10, Locale.TRADITIONAL_CHINESE);
    Assertions.assertEquals(languageWithId(0), testTable.findByIdOrFallback(0, fallback));
    Assertions.assertEquals(languageWithId(1), testTable.findByIdOrFallback(1, fallback));
    Assertions.assertEquals(fallback, testTable.findByIdOrFallback(-1, fallback));
    Assertions.assertEquals(fallback, testTable.findByIdOrFallback(5, fallback));
  }

  private static Language languageWithId(int id) {
    return Language.fromLocale(id, Locale.ENGLISH);
  }

  @Test
  void testRegister() {
    LanguageTable.Builder builder = LanguageTable.newBuilder();
    for (int expectedId = 0; expectedId < 10; expectedId++) {
      Language language = builder.register(Locale.ENGLISH);
      Assertions.assertEquals(languageWithId(expectedId), language);
      Assertions.assertEquals(Locale.ENGLISH, language.asLocale());
    }
  }

  @Test
  void testFindByName() {
    Optional<Language> english = testTable.findByName(Locale.ENGLISH.toLanguageTag());
    assertExistsWithLocale(english, Locale.ENGLISH);
    Optional<Language> italian = testTable.findByName("italian");
    assertExistsWithLocale(italian, Locale.ITALIAN);
    assertDoesNotExist(testTable.findByName("russian"));
  }

  @Test
  void testFindByNameOrFallback() {
    Language fallback = Language.fromLocale(10, Locale.CHINESE);
    Language english = testTable.findByNameOrFallback(
      Locale.ENGLISH.toLanguageTag(),
      fallback
    );
    Assertions.assertEquals(english, languageWithId(0));
    Language russian = testTable.findByNameOrFallback("russian", fallback);
    Assertions.assertEquals(russian, languageWithId(10));
  }

  private static void assertExistsWithLocale(
    Optional<Language> optional,
    Locale locale
  ) {
    Predicate<Language> filter = language -> language.asLocale().equals(locale);
    Optional<Language> filtered = optional.filter(filter);
    Assertions.assertTrue(filtered.isPresent(), "the value does not exist");
  }

  private static void assertDoesNotExist(Optional<Language> language) {
    Assertions.assertFalse(language.isPresent(), "the value should not exist");
  }
}