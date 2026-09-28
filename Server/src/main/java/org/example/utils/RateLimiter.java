package org.example.utils;

public class RateLimiter {
  private final int maxAttempts;
  private final long durationMs;
  private int attempts;
  private long windowStart = System.currentTimeMillis();

  public RateLimiter(int maxAttempts, long durationMs) {
    this.maxAttempts = maxAttempts;
    this.durationMs = durationMs;
  }

  public synchronized boolean isLimited() {
    long now = System.currentTimeMillis();

    if (now - windowStart >= durationMs) {
      windowStart = now;
      attempts = 0;
    }

    attempts++;
    return attempts > maxAttempts;
  }
}
