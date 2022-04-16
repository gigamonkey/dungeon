package com.gigamonkeys.dungeon;

public class Thing {

  static enum Kind {
    WEAPON,
    MEDICINE,
    FOOD,
  }

  private final Kind kind;
  private final String name;

  public Thing(Kind kind, String name) {
    this.kind = kind;
    this.name = name;
  }

  public Kind kind() {
    return kind;
  }

  public String name() {
    return name;
  }

  public String description() {
    return name;
  }
}
