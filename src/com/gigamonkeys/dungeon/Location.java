package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Text.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A location where a Thing can be. Every Thing must be in one location at a
 * time. Removing a Thing from its Location without putting it somewhere else
 * effectively destroys it.
 */
public interface Location {
  /**
   * Combine a Thing with the name of the place in the location, e.g. "on",
   * "under", "near the door", etc.
   */
  public static record PlacedThing(Thing thing, String where) {
    public Stream<String> describe() {
      return Stream.of(capitalize(where) + " is " + a(thing.description()) + ".", thing.describeThings());
    }
  }

  public static interface Named extends Location {
    public String name();
  }

  /**
   * Provide the map in which we manage the things. All other methods in this
   * interface have a default implementation.
   */
  public Map<String, PlacedThing> locationMap();

  /**
   * The valid places for this location. Default implementation is okay but can
   * definitely be overridden.
   */
  public default List<String> places() {
    return List.of("on");
  }

  /**
   * Can the player take the Thing from this location. This method can
   * reasonably be overridden to provide different behavior.
   */
  public default boolean canTake(Thing thing) {
    return true;
  }

  //////////////////////////////////////////////////////////////////////////////
  // Default implementations that almost certainly do not need to be overridden.

  /**
   * Get place if it exists.
   */
  public default Optional<String> place(String name) {
    return Optional.ofNullable(places().contains(name) ? name : null);
  }

  /**
   * Add thing to this location with the where descriptor.
   */
  public default void placeThing(Thing thing, String where) {
    thing.location().ifPresent(l -> l.removeThing(thing));
    locationMap().put(thing.name(), new PlacedThing(thing, where));
    thing.moveTo(this);
  }

  /**
   * Remove thing from this location.
   */
  public default void removeThing(Thing thing) {
    locationMap().remove(thing.name());
    thing.clearLocation();
  }

  /**
   * Get the named thing from this location.
   */
  public default Optional<Thing> thing(String name) {
    if (locationMap().containsKey(name)) {
      return Optional.of(locationMap().get(name).thing());
    } else {
      for (var pt : placedThings()) {
        var maybe = pt.thing().thing(name);
        if (maybe.isPresent()) {
          return maybe;
        }
      }
      return Optional.empty();
    }
  }

  /**
   * Things directly in this location.
   */
  public default Collection<Thing> things() {
    return placedThings().stream().map(PlacedThing::thing).toList();
  }

  /**
   * All things accessible from this location.
   */
  public default Stream<Thing> allThings() {
    return allPlacedThings().map(PlacedThing::thing);
  }

  /**
   * Placed things directly in this location.
   */
  public default Collection<PlacedThing> placedThings() {
    return locationMap().values();
  }

  /**
   * All placed things accessible from this location.
   */
  public default Stream<PlacedThing> allPlacedThings() {
    return locationMap().values().stream().flatMap(pt -> Stream.concat(Stream.of(pt), pt.thing().allPlacedThings()));
  }

  public default Map<String, List<Thing>> groupByPlace() {
    var m = new HashMap<String, List<Thing>>();
    placedThings().forEach(pt -> m.computeIfAbsent(pt.where(), k -> new ArrayList<>()).add(pt.thing()));
    return m;
  }
}
