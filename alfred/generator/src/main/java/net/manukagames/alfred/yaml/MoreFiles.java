package net.manukagames.alfred.yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class MoreFiles {
  private MoreFiles() {
  }

  public static IOException asIoException(Exception exception) {
    return exception instanceof IOException
      ? (IOException) exception
      : new IOException(exception);
  }

  private static final OpenOption[] NEW_OPEN_OPTIONS = {
    StandardOpenOption.CREATE,
    StandardOpenOption.WRITE
  };

  public static void writeToNewFile(Path path, String content) throws IOException {
    Files.deleteIfExists(path);
    Files.writeString(path, content, NEW_OPEN_OPTIONS);
  }
}