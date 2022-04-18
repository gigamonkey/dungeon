package com.gigamonkeys.dungeon;

/**
 * Represent an attack from a monster on the Player. Possibly should
 * use it from player attacking the monster too.
 */
public record Attack(int damage, String description) {
  public static final Attack EMPTY = new Attack(0, "");
}
