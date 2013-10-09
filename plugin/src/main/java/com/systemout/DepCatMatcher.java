package com.systemout;

public class DepCatMatcher {

  public boolean matches(String pattern, String subject) {
    if (pattern.indexOf('*') == -1) {
      return pattern.equals(subject);
    }

    String[] patternTokens = pattern.split(":");
    String[] subjectTokens = subject.split(":");

    if (patternTokens.length != subjectTokens.length) {
      throw new IllegalArgumentException("Pattern and subject must contain equal amount of colons (" + pattern + " vs " + subject + ")");
    }

    if (patternTokens.length > 1) {
      for (int i = 0; i < patternTokens.length; i++) {
        if (!matches(patternTokens[i], subjectTokens[i])) {
          return false;
        }
      }
    }

    return subject.startsWith(pattern.substring(0, pattern.indexOf('*')));
  }
}
