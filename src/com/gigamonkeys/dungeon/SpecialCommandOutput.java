package com.gigamonkeys.dungeon;

public class SpecialCommandOutput extends RuntimeException {

  private final String text;

  public SpecialCommandOutput(String text) {
    this.text = text;
  }

  public String text() {
    return text;
  }
}
