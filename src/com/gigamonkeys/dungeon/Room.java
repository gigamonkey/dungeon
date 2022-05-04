package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Text.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * One room in the dungeon. Rooms are connected to other Rooms by
 * Doors. They also contain Things which can be weapons, food,
 * monsters, etc.
 */
public class Room implements Location {

  private final String description;
  private final Map<Direction, Door> doors = new HashMap<Direction, Door>();
  private final Map<String, PlacedThing> things = new HashMap<>();

  public Room(String description) {
    this.description = description;
  }

  //////////////////////////////////////////////////////////////////////////////
  // Location implementation

  public Map<String, PlacedThing> locationMap() {
    return things;
  }

  //
  //////////////////////////////////////////////////////////////////////////////

  void connect(String doorDescription, Room other, Direction d) {
    if (doors.containsKey(d)) {
      throw new RuntimeException("Already have a door in direction " + d);
    }
    if (other.doors.containsKey(d.opposite())) {
      throw new RuntimeException("Other room already has a door in direction " + d.opposite());
    }

    var door = new Door(doorDescription, this, other);

    doors.put(d, door);
    other.doors.put(d.opposite(), door);
  }

  public String description() {
    return new Text.Wrapped()
      .add("You are in " + description + ".")
      .add(describeThings(t -> !t.isMonster()))
      .add(describeThings(t -> t.isMonster()))
      .add(describeDoors())
      .toString();
  }

  public Optional<Door> door(Direction d) {
    return Optional.ofNullable(doors.get(d));
  }

  public void drop(Thing t) {
    placeThing(t, "on the floor");
  }

  public Optional<Thing> onlyMonster() {
    return Optional.of(monsters().toList()).filter(m -> m.size() == 1).map(m -> m.get(0));
  }

  private Stream<Thing> monsters() {
    return allThings().filter(t -> t.isMonster());
  }

  private Stream<String> describeThings(Predicate<Thing> p) {
    var desc = new ArrayList<String>();

    var byPlace = groupByPlace();

    byPlace
      .keySet()
      .stream()
      .flatMap(place -> {
        var things = byPlace.getOrDefault(place, List.of()).stream().filter(p).map(t -> a(t.description())).toList();
        return things.isEmpty()
          ? Stream.empty()
          : Stream.of(capitalize(place) + isAre(things.size()) + commify(things) + ".");
      })
      .forEach(desc::add);

    things().stream().filter(p).map(Thing::describeThings).forEach(desc::add);

    return desc.stream();
  }

  private String describeDoors() {
    var ds = Arrays
      .stream(Direction.class.getEnumConstants())
      .filter(doors::containsKey)
      .map(d -> a(doors.get(d).description()) + " to the " + d.toString().toLowerCase())
      .toList();

    return "There is " + commify(ds) + ".";
  }
}
