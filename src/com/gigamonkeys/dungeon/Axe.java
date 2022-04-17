package com.gigamonkeys.dungeon;

public class Axe extends Weapon {

  public Axe(int damage) {
    super("AXE", 2);
  }

  public String eat() {
    return "Axes are not good for eating. Now your teeth hurt and you are no less hungry.";
  }
}
