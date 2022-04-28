package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Text.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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

  public static record PlacedThing(Thing thing, String where) {
    public Stream<String> describe() {
      return Stream.of(where + " is " + a(thing.description()) + ".", thing.describeThings());
    }
  }

  /**
   * A concrete implementation of the Location mechanics, to be used via
   * composition.
   */
  public static class Helper {

    private final Map<String, PlacedThing> things = new HashMap<>();
    private final Location actual;

    public Helper(Location actual) {
      this.actual = actual;
    }

    public void placeThing(Thing thing, String where) {
      thing.location().ifPresent(l -> l.removeThing(thing));
      things.put(thing.name(), new PlacedThing(thing, where));
      thing.moveTo(actual);
    }

    public void removeThing(Thing thing) {
      things.remove(thing.name());
      thing.clearLocation();
    }

    public Optional<Thing> thing(String name) {
      if (things.containsKey(name)) {
        return Optional.of(things.get(name).thing());
      } else {
        for (var pt : things()) {
          var maybe = pt.thing().thing(name);
          if (maybe.isPresent()) {
            return maybe;
          }
        }
        return Optional.empty();
      }
    }

    public Collection<PlacedThing> things() {
      return things.values();
    }

    public Stream<PlacedThing> allThings() {
      return things.values().stream().flatMap(pt -> Stream.concat(Stream.of(pt), pt.thing().allThings()));
    }
  }
}
