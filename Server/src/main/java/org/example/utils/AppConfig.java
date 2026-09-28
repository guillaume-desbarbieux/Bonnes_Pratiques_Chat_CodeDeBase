package org.example.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

  private final Properties properties = new Properties();

  public AppConfig() throws IOException {
    try (InputStream input =
        getClass().getClassLoader().getResourceAsStream("application.properties")) {

      if (input == null) {
        throw new IOException("application.properties not found");
      }

      properties.load(input);
    }
  }

  public String getString(String key) {
    return properties.getProperty(key);
  }

  public int getInt(String key) {
    return Integer.parseInt(properties.getProperty(key));
  }

  public long getLong(String key) {
    return Long.parseLong(properties.getProperty(key));
  }
}
