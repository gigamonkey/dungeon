package com.gigamonkeys.dungeon;

public abstract class Thing {

  private final String name;

  public Thing(String name) {
    this.name = name;
  }

  public String name() {
    return name;
  }

  public String description() {
    return name;
  }

  public abstract String beEatenBy(Player p);
}
