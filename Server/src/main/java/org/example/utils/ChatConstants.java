package org.example.utils;

public final class ChatConstants {
    public static final int MAX_MESSAGE_LENGTH = 1000;
    public static final int MAX_CLIENT_NAME_LENGTH = 20;
    public static final int MAX_CONNECTIONS = 10;
    public static final int RATE_LIMIT_CONNECTIONS = 5;
    public static final long RATE_LIMIT_DURATION_MS = 10000;
    public static final int MAX_HISTORY_LENGTH = 100;
    public static final String HOSTNAME = "0.0.0.0";
    public static final int AUTHENTICATION_TIMEOUT = 30000;

    private ChatConstants() {
    }
}