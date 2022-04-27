package com.gigamonkeys.dungeon;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A location where a Thing can be. Every Thing must be in one location at a
 * time.
 */
public interface Location {
  /**
   * Add thing to this location with the where descriptor.
   */
  public void placeThing(Thing thing, String where);

  /**
   * Remove thing from this location.
   */
  public void removeThing(Thing thing);

  /**
   * Get the named thing from this location.
   */
  public Optional<Thing> thing(String name);

  /**
   * Things directly in this location.
   */
  public Collection<PlacedThing> things();

  /**
   * All things accessible from this location.
   */
  public Stream<PlacedThing> allThings();

  /**
   * Can the player take the Thing from this location.
   */
  public boolean canTake(Thing thing);
}
