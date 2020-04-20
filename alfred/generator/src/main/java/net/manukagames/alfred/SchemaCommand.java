package net.manukagames.alfred;

import picocli.CommandLine;

@CommandLine.Command(
  name = "schema",
  mixinStandardHelpOptions = true,
  subcommands = BuildSchemaCommand.class
)
public final class SchemaCommand {}