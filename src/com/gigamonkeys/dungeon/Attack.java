package com.gigamonkeys.dungeon;

public interface Attack {
  /**
   * Get the description of the attack, e.g. "You swing your axe and connect!"
   */
  public String description();

  /**
   * Damage done by this attack. (Technically, we should probably move this into
   * the applyAttack method on the thing being attacked.)
   */
  public int damage();

  /**
   * Get the description of the result which may be determined by the thing
   * being attacked.
   */
  public String result(Thing thing);

  public static record Simple(String description, int damage) implements Attack {
    public String result(Thing thing) {
      return thing.applyAttack(this);
    }
  }

  public static record Useless(String description) implements Attack {
    public int damage() {
      return 0;
    }
    public String result(Thing thing) {
      return "doing zero damage";
    }
  }
}
