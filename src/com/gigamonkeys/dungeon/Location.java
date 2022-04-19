package com.gigamonkeys.dungeon;

import java.util.Optional;

/**
 * A location where a Thing can be. Every Thing must be in one location at a time.
 */
public interface Location {

  /**
   * Add thing to this location with the where descriptor.
   */
  public void addThing(Thing thing, String where);


  /**
   * Remove thing from this location.
   */
  public void removeThing(Thing thing);


  /**
   * Move thing within this location.
   */
  public void move(Thing thing, String where);


  /**
   * Get the named thing from this location.
   */
  public Optional<Thing> thing(String name);

}
