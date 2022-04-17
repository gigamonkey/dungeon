package com.gigamonkeys.dungeon;

public abstract class Thing {

  private final String name;
  private Room room;

  public Thing(String name) {
    this.name = name.toUpperCase();
  }

  public String a() {
    return "AEIOUH".indexOf(name.charAt(0)) != -1 ? "an" : "a";
  }

  public void setRoom(Room room) {
    this.room = room;
  }

  public Room room() {
    return room;
  }

  public String name() {
    return name;
  }

  public String where() {
    return "on the floor";
  }

  public String description() {
    return name;
  }

  public boolean portable() {
    return !isMonster();
  }

  public boolean isEdible() {
    return false;
  }

  public abstract String eat();

  public String attackWith(int damage) {
    return "I don't know why you're attacking an innocent " + name() + ".";
  }

  public String weaponizeAgainst(Thing monster) {
    if (damage() == 0) {
      return a() + " " + name() + " is not an effective weapon. You do zero damage.";
    } else {
      return monster.attackWith(this.damage());
    }
  }

  public int damage() {
    return 0;
  }

  public boolean isMonster() {
    return false;
  }
}
