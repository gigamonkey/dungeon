package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Text.*;

import java.util.*;
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
  private final Things things = new Things();

  public Room(String description) {
    this.description = description;
  }

  //////////////////////////////////////////////////////////////////////////////
  // Location implementation

  public void placeThing(Thing thing, String where) {
    things.placeThing(thing, where);
  }

  public void removeThing(Thing thing) {
    things.removeThing(thing);
  }

  public Optional<Thing> thing(String name) {
    return things.thing(name);
  }

  public Collection<PlacedThing> things() {
    return things.things();
  }

  public Stream<PlacedThing> allThings() {
    return things.allThings();
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
    return new Text.Wrapped(60)
      .add("You are in " + description + ".")
      .add(describeThings(t -> !t.isMonster()))
      .add(describeThings(t -> t.isMonster()))
      .add(describeDoors())
      .toString();
  }

  public Optional<Door> getDoor(Direction d) {
    return Optional.ofNullable(doors.get(d));
  }

  public void drop(Thing t) {
    placeThing(t, "on floor");
  }

  public Optional<Thing> onlyMonster() {
    return Optional.of(monsters().toList()).filter(m -> m.size() == 1).map(m -> m.get(0));
  }

  private Stream<Thing> monsters() {
    return allThings().map(pt -> pt.thing()).filter(t -> t.isMonster());
  }

  private Stream<String> describeThings(Predicate<Thing> p) {
    return things().stream().filter(pt -> p.test(pt.thing())).flatMap(PlacedThing::describe);
  }

  private String describeDoors() {
    var ds = Arrays
      .stream(Direction.class.getEnumConstants())
      .filter(doors::containsKey)
      .map(d -> a(doors.get(d).description()) + " to the " + d)
      .toList();

    return "There is " + commify(ds) + ".";
  }
}
