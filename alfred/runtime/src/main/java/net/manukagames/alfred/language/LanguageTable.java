package net.manukagames.alfred.language;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class LanguageTable {
  private final Map<String, Language> lookupTable;
  private final Language[] languages;

  private LanguageTable(
    Map<String, Language> lookupTable,
    Language[] languages
  ) {
    this.languages = languages;
    this.lookupTable = lookupTable;
  }

  public Optional<Language> findById(int id) {
    return id >= languages.length
      ? Optional.empty()
      : Optional.ofNullable(languages[id]);
  }

  public Language findByIdOrFallback(int id, Language fallback) {
    if (id >= languages.length) {
      return fallback;
    }
    Language language = languages[id];
    return language == null ? fallback : language;
  }

  public Optional<Language> findByName(String name) {
    return Optional.ofNullable(lookupTable.get(name));
  }

  public Language findByNameOrFallback(String name, Language fallback) {
    return lookupTable.getOrDefault(name, fallback);
  }

  public static LanguageTable of(Language... languages) {
    Objects.requireNonNull(languages);
    Map<String, Language> lookupTable = populateLookupTable(languages);
    return new LanguageTable(lookupTable, languages.clone());
  }

  private static Map<String, Language> populateLookupTable(Language[] languages) {
    Map<String, Language> table = new HashMap<>();
    for (Language language : languages) {
      table.put(language.name(), language);
    }
    return table;
  }

  private static final LanguageTable EMPTY = new LanguageTable(
    Collections.emptyMap(),
    new Language[0]
  );

  public static LanguageTable empty() {
    return EMPTY;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private Collection<Language> languages = new ArrayList<>();
    private int idSeed;

    private Builder() {}

    public Builder addLanguage(Locale locale) {
      Objects.requireNonNull(locale);
      int id = generateId();
      Language language = Language.fromLocale(id, locale);
      languages.add(language);
      return this;
    }

    public Builder addLanguageWithAlias(Locale locale, String alias) {
      Objects.requireNonNull(locale);
      Objects.requireNonNull(alias);
      int id = generateId();
      Language language = Language.create(id, alias, locale);
      languages.add(language);
      return this;
    }

    public Language register(Locale locale) {
      Objects.requireNonNull(locale);
      int id = generateId();
      Language language = Language.fromLocale(id, locale);
      languages.add(language);
      return language;
    }

    private int generateId() {
      return idSeed++;
    }

    private Language[] convertToArrayTable() {
      Language[] table = new Language[languages.size()];
      for (Language language : languages) {
        table[language.id()] = language;
      }
      return table;
    }

    public LanguageTable create() {
      Language[] table = convertToArrayTable();
      Map<String, Language> lookupTable = populateLookupTable(table);
      return new LanguageTable(lookupTable, table);
    }
  }
}
