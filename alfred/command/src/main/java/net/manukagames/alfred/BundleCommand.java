package net.manukagames.alfred;

import picocli.CommandLine;

@CommandLine.Command(
  name = "schema",
  mixinStandardHelpOptions = true,
  subcommands = {
    BuildBundleCommand.class
  }
)
public class BundleCommand {
}
