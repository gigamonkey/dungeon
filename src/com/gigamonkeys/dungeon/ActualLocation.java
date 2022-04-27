package com.gigamonkeys.dungeon;

/**
 * The actual location, used for Things to delegate back to a Room or Thing.
 */
public interface ActualLocation {
  public boolean canTake(Thing thing);
}
