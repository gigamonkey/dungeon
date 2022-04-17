package com.gigamonkeys.dungeon;

public abstract class Thing {

  private final String name;

  public Thing(String name) {
    this.name = name.toUpperCase();
  }

  public String a() {
    return "AEIOUH".indexOf(name.charAt(0)) != -1 ? "an" : "a";
  }

  public String name() {
    return name;
  }

  public String description() {
    return name;
  }

  public abstract String beEatenBy(Player p);

  public String attackWith(Thing weapon) {
    return "I don't know why you're attacking an innocent " + name() + ".";
  }

  public int damage() {
    return 0;
  }
}
