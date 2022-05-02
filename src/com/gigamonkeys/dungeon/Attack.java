package com.gigamonkeys.dungeon;

import java.util.function.Function;

public interface Attack {
  /**
   * Something that can be the target of an attack. Currently the Player and Things.
   */
  public static interface Target {
    public String applyAttack(Attack attack);

    public String who();
  }

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
  public String result(Target thing);

  public static record Simple(String description, int damage) implements Attack {
    public String result(Target target) {
      return target.applyAttack(this);
    }
  }

  public static record Useless(String description) implements Attack {
    public int damage() {
      return 0;
    }
    public String result(Target thing) {
      return "doing zero damage";
    }
  }

  public static record Full(String description, int damage, Function<Target, String> resultFn) implements Attack {
    public String result(Target t) {
      return resultFn.apply(t) + " " + t.applyAttack(this);
    }
  }

}
