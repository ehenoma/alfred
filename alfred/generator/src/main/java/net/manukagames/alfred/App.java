package net.manukagames.alfred;

import picocli.CommandLine;

public final class App {
  private App() {}

  public static void main(String[] options) {
    CommandLine commandLine = new CommandLine(new PolyglotCommand());
    int exitCode = commandLine.execute(options);
    System.exit(exitCode);
  }
}