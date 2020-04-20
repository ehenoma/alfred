package net.manukagames.alfred.gradle

import org.gradle.api.Project

final class PolyglotExtension {
  static final String NAME = 'alfred'

  File schemaFile


  static PolyglotExtension create(Project project) {
    return project.extensions.create(NAME, PolyglotExtension)
  }
}