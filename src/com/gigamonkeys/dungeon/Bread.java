package com.gigamonkeys.dungeon;

/**
 * Specific class representing Bread. This could easily be replaced
 * with a DynamicThing.
 */
public class Bread extends BaseThing {

  public Bread() {
    super("BREAD", 0);
  }

  @Override
  public String description() {
    return "loaf of bread";
  }

  @Override
  public boolean isEdible() {
    return true;
  }

  @Override
  public String eatIfEdible() {
    return "Ah, delicious. Could use some mayonnaise though.";
  }
}
