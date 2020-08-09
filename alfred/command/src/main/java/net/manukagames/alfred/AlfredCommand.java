package net.manukagames.alfred;

import picocli.CommandLine;

@CommandLine.Command(
  subcommands = SchemaCommand.class
)
public final class AlfredCommand { }