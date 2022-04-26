package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Text.*;

import java.util.stream.*;

public record PlacedThing(Thing thing, String where) {
  public Stream<String> describe() {
    return Stream.of(where + " is " + a(thing.description()) + ".", thing.describeThings());
  }
}
