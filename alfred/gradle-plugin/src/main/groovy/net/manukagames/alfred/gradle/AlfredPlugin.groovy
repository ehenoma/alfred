package net.manukagames.alfred.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

final class AlfredPlugin implements Plugin<Project> {
  @Override
  void apply(Project project) {
    def extension = AlfredExtension.create(project)
    configureBuildSchemaTask(project)
  }

  private static void configureBuildSchemaTask(Project project) {
    project.task('buildSchema', type: BuildSchemaTask) {
      group = AlfredPlugin.simpleName
      description = 'Generate sources for a schema definition'
    }
  }
}
