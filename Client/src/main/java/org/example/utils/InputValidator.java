package org.example.utils;

public class InputValidator {
    private final ChatConfig config;

    public InputValidator(ChatConfig config) {
        this.config = config;
    }


    public boolean isValidClientName(String name) {
        return name != null
                && !name.isBlank()
                && name.length() <= config.getMaxClientNameLength();
    }

    public boolean isValidMessage(String message) {
        return message != null
                && message.length() <= config.getMaxMessageLength();
    }
}