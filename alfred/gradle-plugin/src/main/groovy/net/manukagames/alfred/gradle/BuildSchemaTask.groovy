package net.manukagames.alfred.gradle

import net.manukagames.alfred.schema.Schema
import net.manukagames.alfred.schema.SchemaFile
import net.manukagames.alfred.schema.generation.SchemaGeneration
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

final class BuildSchemaTask extends DefaultTask {

  @TaskAction
  void run() {
    def extension = project.extensions.findByType(PolyglotExtension)
    generateSources(extension)
  }

  private static void generateSources(PolyglotExtension extension) {
    def schema = readSchema(extension.schemaFile)
    def generation = SchemaGeneration.of(schema)
    generation.run()
  }

  private static Schema readSchema(File file) {
    return SchemaFile.of(file).read()
  }
}
