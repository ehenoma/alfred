package net.manukagames.alfred.gradle

import net.manukagames.alfred.schema.Schema
import net.manukagames.alfred.schema.SchemaFile
import net.manukagames.alfred.schema.generation.SchemaGeneration
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

final class BuildSchemaTask extends DefaultTask {
  @TaskAction
  void run() {
    def extension = project.extensions.findByType(AlfredExtension)
    generateSources(extension, project)
  }

  private static void generateSources(AlfredExtension extension, Project project) {
    def schema = readSchema(extension.schemaFile)
    def outputPath = project.buildDir.toPath().resolve("generated").resolve("alfred")
    def generation = SchemaGeneration.of(schema, outputPath)
    generation.run()
  }

  private static Schema readSchema(File file) {
    return SchemaFile.of(file).read()
  }
}
