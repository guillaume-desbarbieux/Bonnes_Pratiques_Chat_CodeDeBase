package org.example.utils;

public class ChatConfig {

    private final String serverHost;
    private final int serverPort;
    private final int maxMessageLength;
    private final int maxClientNameLength;

    public ChatConfig(AppConfig config) {
        this.serverHost = config.getString("server.host");
        this.serverPort = config.getInt("server.port");
        this.maxMessageLength = config.getInt("chat.max-message-length");
        this.maxClientNameLength = config.getInt("chat.max-client-name-length");
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

}
