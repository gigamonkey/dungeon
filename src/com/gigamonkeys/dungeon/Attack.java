package com.gigamonkeys.dungeon;

public interface Attack {
  /**
   * Get the description of the attack, e.g. "You swing your axe and hit."
   */
  public String description();

  /**
   * Get the description of the result (which is partly determined by the thing
   * being attacked.
   */
  public String result(Thing thing);

  public static record Simple(String description, int damage) implements Attack {
    public String result(Thing thing) {
      return thing.attackWith(damage());
    }
  }

  public static record Useless(String description) implements Attack {
    public String result(Thing thing) {
      return "doing zero damage";
    }
  }
}
