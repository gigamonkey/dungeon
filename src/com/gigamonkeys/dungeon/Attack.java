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
   * being attacked. Should call applyAttack on the target if the attack
   * succeeds but doesn't have to use the resulting string. (Though probably
   * should.)
   */
  public String result(Target thing);

  //////////////////////////////////////////////////////////////////////////////
  // Some convenience implementations of Attack

  /**
   * Simple attack whose result is purely determined by the target, usually as a
   * function of the amount of damage.
   */
  public static record Simple(String description, int damage) implements Attack {
    public String result(Target target) {
      return target.applyAttack(this);
    }
  }

  /**
   * An attack that doesn't do any damage. (The default attack for a generic Thing.)
   */
  public static record Useless(String description) implements Attack {
    public int damage() {
      return 0;
    }
    public String result(Target thing) {
      return "doing zero damage";
    }
  }

  /**
   * An attack whose result is produced by combining the result of resultFn and
   * the result of applying the attack to the target.
   */
  public static record Full(String description, int damage, Function<Target, String> resultFn) implements Attack {
    public String result(Target t) {
      return resultFn.apply(t) + " " + t.applyAttack(this);
    }
  }
}
