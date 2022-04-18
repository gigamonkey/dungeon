package com.gigamonkeys.dungeon;

public abstract class Thing {

  // Name is how the thing must be referred to in commands. AXE, BREAD, etc.
  private final String name;
  private final int damage;

  private int hitPoints;
  private Room room;

  public Thing(String name, int damage, int hitPoints) {
    this.name = name.toUpperCase();
    this.damage = damage;
    this.hitPoints = hitPoints;
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

  public boolean alive() {
    return hitPoints > 0;
  }

  public void takeDamage(int damage) {
    hitPoints -= damage;
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
      return a() + " " + description() + " is not an effective weapon. You do zero damage.";
    } else {
      return monster.attackWith(this.damage());
    }
  }

  public int damage() {
    return damage;
  }

  public boolean isMonster() {
    return false;
  }

  public Attack attackPlayer() {
    return Attack.EMPTY;
  }
}
