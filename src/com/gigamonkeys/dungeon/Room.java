package com.gigamonkeys.dungeon;

import java.util.*;
import java.util.function.*;

public class Room {

  private final String description;
  private final Map<Direction, Door> doors = new HashMap<Direction, Door>();
  private final List<Thing> things = new ArrayList<>();

  public Room(String description) {
    this.description = description;
  }

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
    List<String> desc = new ArrayList<>();
    desc.add("You are in");
    desc.add(description + ".");
    describeThings(desc, t -> !t.isMonster());
    describeThings(desc, t -> t.isMonster());
    describeDoors(desc);
    return String.join(" ", desc);
  }

  public Door getDoor(Direction d) {
    return doors.get(d);
  }

  public Optional<Thing> thing(String name) {
    return things.stream().filter(t -> t.name().equals(name)).findAny();
  }

  public void addThing(Thing t) {
    things.add(t);
    t.setRoom(this);
  }

  public void removeThing(Thing t) {
    things.remove(t);
  }

  public void drop(Thing t) {
    things.add(t);
  }

  private void describeThings(List<String> desc, Predicate<Thing> p) {
    things
      .stream()
      .filter(p)
      .forEach(t -> {
        desc.add(t.where() + " is " + t.a() + " " + t.description() + ".");
      });
  }

  private void describeDoors(List<String> desc) {
    for (var d : Direction.class.getEnumConstants()) {
      if (doors.containsKey(d)) {
        desc.add("To the " + d + " there is " + doors.get(d).description() + ".");
      }
    }
  }
}
