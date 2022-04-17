package com.gigamonkeys.dungeon;

public class Axe extends Thing {

  public Axe() {
    super("AXE");
  }

  public String eat() {
    return "Axes are not good for eating. Now your teeth hurt and you are no less hungry.";
  }

  public int damage() {
    return 2;
  }
}
