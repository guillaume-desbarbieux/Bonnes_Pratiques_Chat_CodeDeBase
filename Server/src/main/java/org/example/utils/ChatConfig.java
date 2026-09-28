package org.example.utils;

public class ChatConfig {

  private final String serverHost;
  private final int serverPort;
  private final int maxMessageLength;
  private final int maxClientNameLength;
  private final int maxConnections;
  private final int maxHistoryLength;
  private final int rateLimitConnections;
  private final long rateLimitDurationMs;
  private final int authenticationTimeout;

  public ChatConfig(AppConfig config) {
    this.serverHost = config.getString("server.host");
    this.serverPort = config.getInt("server.port");
    this.maxMessageLength = config.getInt("chat.max-message-length");
    this.maxClientNameLength = config.getInt("chat.max-client-name-length");
    this.maxConnections = config.getInt("chat.max-connections");
    this.maxHistoryLength = config.getInt("chat.max-history-length");
    this.rateLimitConnections = config.getInt("rate-limit.max-connections");
    this.rateLimitDurationMs = config.getLong("rate-limit.duration-ms");
    this.authenticationTimeout = config.getInt("chat.authentication-timeout");
  }

  public String getServerHost() {
    return serverHost;
  }

  public int getServerPort() {
    return serverPort;
  }

  public int getMaxMessageLength() {
    return maxMessageLength;
  }

  public int getMaxClientNameLength() {
    return maxClientNameLength;
  }

  public int getMaxConnections() {
    return maxConnections;
  }

  public int getMaxHistoryLength() {
    return maxHistoryLength;
  }

  public int getRateLimitConnections() {
    return rateLimitConnections;
  }

  public long getRateLimitDurationMs() {
    return rateLimitDurationMs;
  }

  public int getAuthenticationTimeout() {
    return authenticationTimeout;
  }
}
