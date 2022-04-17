package com.gigamonkeys.dungeon;

public abstract class Monster extends Thing {

  private int hitPoints;

  Monster(String name, int hitPoints) {
    super(name);
    this.hitPoints = hitPoints;
  }

  public boolean alive() {
    return hitPoints > 0;
  }

  public void damage(int damage) {
    this.hitPoints -= damage;
  }
}
