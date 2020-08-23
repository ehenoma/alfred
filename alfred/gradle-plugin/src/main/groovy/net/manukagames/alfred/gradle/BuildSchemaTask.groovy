package net.manukagames.alfred.gradle

import net.manukagames.alfred.generation.OutputPath
import net.manukagames.alfred.schema.SchemaConfiguration
import net.manukagames.alfred.schema.generation.SchemaGeneration

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

final class BuildSchemaTask extends DefaultTask {
  @TaskAction
  void run() {
    def extension = project.extensions.findByType(AlfredExtension)
    generateSources(extension)
  }

  private void generateSources(AlfredExtension extension) {
    def schema = SchemaConfiguration.ofFile(extension.schemaFile).read()
    def outputPath = selectOutputPath()
    def generation = SchemaGeneration.of(schema, outputPath)
    generation.run()
  }

  private def selectOutputPath() {
    def directory = project.buildDir.toPath().resolve("generated").resolve("alfred")
    return OutputPath.fromPath(directory)
  }
}
