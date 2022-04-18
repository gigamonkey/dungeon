package com.gigamonkeys.dungeon;

public class Bread extends Food {

  public Bread() {
    super("BREAD");
  }

  public String description() {
    return "loaf of bread";
  }

  public String eat() {
    return "Ah, delicious. Could use some mayonnaise though.";
  }
}
