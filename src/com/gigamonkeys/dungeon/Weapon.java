package com.gigamonkeys.dungeon;

public abstract class Weapon extends Thing {

  private final int damage;

  public Weapon(String name, int damage) {
    super(name);
    this.damage = damage;
  }

  public int damage() {
    return damage;
  }
}
