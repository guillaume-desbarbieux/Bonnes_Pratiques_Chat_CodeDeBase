package org.example.model;

public class Message {
  private final String content;
  private final String sender;

  public Message(String sender, String content) {
    this.sender = sender;
    this.content = content;
  }

  public String getContent() {
    return content;
  }

  public String getSender() {
    return sender;
  }

  @Override
  public String toString() {
    return sender + ": " + content;
  }
}
