package com.gigamonkeys.dungeon;

import java.util.*;

public class Room {

  private final String description;
  private final Map<Direction, Door> doors = new HashMap<Direction, Door>();
  private final List<Thing> things = new ArrayList<>();

  public Room(String description) {
    this.description = description;
  }

  public String description() {
    StringBuilder sb = new StringBuilder(description);
    if (!doors.isEmpty()) {
      sb.append("\n");
      var ds = new ArrayList<>(doors.keySet());
      sb.append(
        switch (ds.size()) {
          case 1 -> "There is a door to the " + ds.get(0);
          case 2 -> "There are doors to the " + ds.get(0) + " and " + ds.get(1);
          case 3 -> "There are doors to the " + ds.get(0) + ", " + ds.get(1) + " and " + ds.get(2);
          default -> "There are doors in every direction.";
        }
      );
    }
    return sb.toString();
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

  public Door getDoor(Direction d) {
    return doors.get(d);
  }

  public List<Thing> things() {
    return things;
  }
}
