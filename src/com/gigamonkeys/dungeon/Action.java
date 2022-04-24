package com.gigamonkeys.dungeon;

/**
 * An action that can occur in a turn, e.g. a monster attacks a player.
 */
public interface Action {
  public default String doAction(Player p) {
    perform(p);
    return description();
  }

  /**
   * Perform the action, producing whatever effect on the state of the world.
   */
  public void perform(Player p);

  /**
   * Describe the action.
   */
  public String description();
}
