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

  public void takeDamage(int damage) {
    this.hitPoints -= damage;
  }

  public boolean isMonster() {
    return true;
  }

  public boolean isEdible() {
    return !alive();
  }

  public String attackWith(int damage) {
    takeDamage(damage);
    if (alive()) {
      return "The " + name() + " is wounded but still alive. And now it's mad.";
    } else {
      return "The " + name() + " is dead. Murderer.";
    }
  }
}
