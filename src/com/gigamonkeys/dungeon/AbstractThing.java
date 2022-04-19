package com.gigamonkeys.dungeon;

/**
 * An abstract implementation of Thing with the bits that shouldn't change.
 */
public abstract class AbstractThing implements Thing {

  private final String name;
  private int hitPoints;
  private Location location = null;

  public AbstractThing(String name, int hitPoints) {
    this.name = name.toUpperCase();
    this.hitPoints = hitPoints;
  }

  @Override
  public final String name() {
    return name;
  }

  @Override
  public final Location location() {
    return location;
  }

  @Override
  public final void setLocation(Location location) {
    this.location = location;
  }

  @Override
  public final void clearLocation() {
    location = null;
  }

  @Override
  public final void takeDamage(int damage) {
    hitPoints -= damage;
  }

  @Override
  public final int hitPoints() {
    return hitPoints;
  }
}
