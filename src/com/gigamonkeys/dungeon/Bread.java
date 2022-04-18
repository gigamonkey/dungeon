package com.gigamonkeys.dungeon;

/**
 * Specific class representing Bread. This could easily be replaced
 * with a DynamicThing.
 */
public class Bread extends BaseThing {

  public Bread() {
    super("BREAD", 0, 0);
  }

  public String description() {
    return "loaf of bread";
  }

  public boolean isEdible() {
    return true;
  }

  public String eat() {
    return "Ah, delicious. Could use some mayonnaise though.";
  }
}
