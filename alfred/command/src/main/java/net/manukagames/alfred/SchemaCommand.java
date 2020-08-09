package net.manukagames.alfred;

import picocli.CommandLine;

@CommandLine.Command(
  name = "schema",
  mixinStandardHelpOptions = true,
  subcommands = {
    VerifySchemaCommand.class,
    BuildBundleCommand.class
  }
)
public final class SchemaCommand {}