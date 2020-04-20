package net.manukagames.alfred.schema;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

final class SchemaFileTest {
  @Test
  @Tag("integration")
  void testParsingExampleFile() {
    var exampleFile = TestSchemaFile.named("example_messages.yml");
    exampleFile.read();
  }
}