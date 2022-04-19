package com.gigamonkeys.dungeon;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A concrete implementation of Location, to be used via composition.
 */
public class Things implements Location {

  private final Map<String, PlacedThing> things = new HashMap<>();

  public void placeThing(Thing thing, String where) {
    things.put(thing.name(), new PlacedThing(thing, where));
    thing.setLocation(this);
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
