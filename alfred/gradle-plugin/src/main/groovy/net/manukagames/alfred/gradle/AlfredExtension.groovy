package net.manukagames.alfred.gradle

import org.gradle.api.Project

final class AlfredExtension {
  static final String NAME = 'alfred'

  static AlfredExtension create(Project project) {
    return project.extensions.create(NAME, AlfredExtension)
  }

  File schemaFile
}