package com.gigamonkeys.dungeon;

public abstract class Food extends Thing {

  public Food(String name) {
    super(name);
  }

  public boolean isEdible() {
    return true;
  }
}
