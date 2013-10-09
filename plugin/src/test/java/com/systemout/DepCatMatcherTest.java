package com.systemout;

import org.junit.Test;

import static org.junit.Assert.*;

public class DepCatMatcherTest {

  private DepCatMatcher matcher = new DepCatMatcher();

  @Test
  public void testBasic() {
    assertTrue(matcher.matches("com.systemout:depcat", "com.systemout:depcat"));
    assertTrue(matcher.matches("com.systemout:dep*", "com.systemout:depcat"));
    assertTrue(matcher.matches("com.systemout:*", "com.systemout:depcat"));
    assertTrue(matcher.matches("com.*:depcat", "com.systemout:depcat"));
    assertTrue(matcher.matches("com.*:*", "com.systemout:depcat"));
    assertTrue(matcher.matches("*:depcat", "com.systemout:depcat"));
    assertTrue(matcher.matches("*:*", "com.systemout:depcat"));
  }

  @Test
  public void testNegativeScenarios() {
    assertFalse(matcher.matches("com.systemout.*:depcat", "com.systemout:depcat"));
    assertFalse(matcher.matches("com.systemout.*:*", "com.systemout:depcat"));
    assertFalse(matcher.matches("*:foocat", "com.systemout:depcat"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBadSyntax() {
    matcher.matches("com.systemout.*", "com.systemout:depcat");
  }
}
