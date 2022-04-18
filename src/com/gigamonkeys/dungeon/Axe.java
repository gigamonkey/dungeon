package com.gigamonkeys.dungeon;

public class Axe extends BaseThing {

  public Axe(int damage) {
    super("AXE", 2, 0);
  }

  public String eat() {
    return "Axes are not good for eating. Now your teeth hurt and you are no less hungry.";
  }

  public String weaponizeAgainst(Thing m) {
    return "You swing your axe and connect! " + super.weaponizeAgainst(m);
  }
}
