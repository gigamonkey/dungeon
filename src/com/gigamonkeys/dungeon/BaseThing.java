package com.gigamonkeys.dungeon;

/**
 * A base implementation of Thing for non-DynamicThing things.
 */
public abstract class BaseThing implements Thing {

  // Name is how the thing must be referred to in commands. AXE, BREAD, etc.
  private final String name;
  private final int damage;

  private int hitPoints;
  private Location location;

  public BaseThing(String name, int damage, int hitPoints) {
    this.name = name.toUpperCase();
    this.damage = damage;
    this.hitPoints = hitPoints;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public Location location() {
    return location;
  }

  public void clearLocation() {
    location = null;
  }

  public String name() {
    return name;
  }

  public String description() {
    return name;
  }

  public void takeDamage(int damage) {
    hitPoints -= damage;
  }

  public int hitPoints() {
    return hitPoints;
  }

  public boolean isPortable() {
    return !isMonster();
  }

  public boolean isEdible() {
    return false;
  }

  public String eatIfEdible() {
    return "Yum";
  }

  public String eatIfInedible() {
    return "Yuck. You can't eat " + a() + " " + description() + ".";
  }

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
}
