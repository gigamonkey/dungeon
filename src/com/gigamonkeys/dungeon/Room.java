package com.gigamonkeys.dungeon;

import java.util.*;

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
    StringBuilder sb = new StringBuilder(description);
    if (!doors.isEmpty()) {
      sb.append(" ");
      describeDoors(sb);
    }
    return sb.toString();
  }

  public Door getDoor(Direction d) {
    return doors.get(d);
  }

  public List<Thing> things() {
    return things;
  }

  private void describeDoors(StringBuilder sb) {
    for (var d : Direction.class.getEnumConstants()) {
      if (doors.containsKey(d)) {
        sb.append("To the " + d + " there is an " + doors.get(d) + ".");
      }
    }
  }
}
