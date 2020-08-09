package net.manukagames.alfred.gradle

import org.gradle.api.Project

final class AlfredExtension {
  static AlfredExtension create(Project project) {
    return project.extensions.create(NAME, AlfredExtension)
  }

  static final String NAME = 'alfred'

  File schemaFile
}