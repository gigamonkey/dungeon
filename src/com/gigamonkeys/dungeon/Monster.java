package com.gigamonkeys.dungeon;

public class Monster {

  private final String name;
  private final String where;
  private final String description;
  private Room room;

  Monster(String name, String where, String description) {
    this.name = name;
    this.where = where;
    this.description = description;
  }

  public String name() {
    return name;
  }

  public String where() {
    return where;
  }

  public String description() {
    return description;
  }

  public Room room() {
    return room;
  }

  public String a() {
    return "AEIOUH".indexOf(name.charAt(0)) != -1 ? "an" : "a";
  }
}
