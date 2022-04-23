package com.gigamonkeys.dungeon;

/**
 * Represent an attack from a monster on the Player. Possibly should
 * use it from player attacking the monster too.
 */
public record Attack(int damage, String description) implements Action {
  public static final Attack EMPTY = new Attack(0, "");

  public void perform(Player p) {
    p.takeDamage(damage());
  }
}
