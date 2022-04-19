package com.gigamonkeys.dungeon;

/**
 * A base implementation of Thing for non-DynamicThing things with reasonable
 * defaults.
 */
public class BaseThing extends AbstractThing {

  public BaseThing(String name, int hitPoints) {
    super(name, hitPoints);
  }

  @Override
  public String description() {
    return name();
  }

  @Override
  public boolean isPortable() {
    return !isMonster();
  }

  @Override
  public boolean isEdible() {
    return false;
  }

  @Override
  public boolean isMonster() {
    return false;
  }

  @Override
  public String eatIfEdible() {
    return "Yum";
  }

  @Override
  public String eatIfInedible() {
    return "Yuck. You can't eat " + a() + " " + description() + ".";
  }

  @Override
  public String attackWith(int damage) {
    return "I don't know why you're attacking an innocent " + name() + ".";
  }

  @Override
  public String weaponizeAgainst(Thing monster) {
    if (damage() == 0) {
      return a() + " " + description() + " is not an effective weapon. You do zero damage.";
    } else {
      return monster.attackWith(this.damage());
    }
  }

  @Override
  public int damage() {
    return 0;
  }
}
