package com.gigamonkeys.dungeon;

import java.util.*;

public class Room {

  private final String description;
  private final Map<Direction, Door> doors = new HashMap<Direction, Door>();
  private final List<Thing> things = new ArrayList<>();
  private final List<Monster> monsters = new ArrayList<>();

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
    desc.add(description);
    describeThings(desc);
    describeMonsters(desc);
    describeDoors(desc);
    return String.join(" ", desc);
  }

  public Door getDoor(Direction d) {
    return doors.get(d);
  }

  public Optional<Thing> thing(String name) {
    return things.stream().filter(t -> t.name().equals(name)).findAny();
  }

  public Optional<Monster> monster(String name) {
    return monsters.stream().filter(t -> t.name().equals(name)).findAny();
  }

  public List<Thing> things() {
    return things;
  }

  public void addThing(Thing t) {
    things.add(t);
  }

  public void addMonster(Monster m) {
    monsters.add(m);
  }

  public void take(Thing t) {
    things.remove(t);
  }

  public void drop(Thing t) {
    things.add(t);
  }

  private void describeDoors(List<String> desc) {
    for (var d : Direction.class.getEnumConstants()) {
      if (doors.containsKey(d)) {
        desc.add("To the " + d + " there is " + doors.get(d).description() + ".");
      }
    }
  }

  private void describeThings(List<String> desc) {
    for (var t : things) {
      desc.add("On the floor is " + t.description() + ".");
    }
  }

  private void describeMonsters(List<String> desc) {
    for (var m : monsters) {
      desc.add(m.where());
      desc.add(m.a());
      desc.add(m.name() + ",");
      desc.add(m.description() + ".");
    }
  }
}
