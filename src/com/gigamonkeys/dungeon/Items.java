package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Location.PlacedThing;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A concrete implementation of Location, to be used via composition. This class
 * manages the data structure for managing the things and places but note that
 * canTake delegates back to the ActualLocation since this class doesn't know.
 */
public class Items implements Location {

  public static interface ActualLocation {
    public boolean canTake(Thing thing);
  }

  private final Map<String, PlacedThing> things = new HashMap<>();
  private final ActualLocation actual;

  public Items(ActualLocation actual) {
    this.actual = actual;
  }

  public void placeThing(Thing thing, String where) {
    thing.location().ifPresent(l -> l.removeThing(thing));
    things.put(thing.name(), new PlacedThing(thing, where));
    thing.moveTo(this);
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

  public boolean canTake(Thing thing) {
    return actual.canTake(thing);
  }
}
