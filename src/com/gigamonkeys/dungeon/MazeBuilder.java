package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Direction.*;

import java.util.*;

class MazeBuilder {

  private Map<String, Room> rooms = new HashMap<>();
  private Map<String, Thing> things = new HashMap<>();

  public MazeBuilder room(String name, String description) {
    var r = new Room(description);
    rooms.put(name, r);
    return this;
  }

  public Room room(String name) {
    return rooms.get(name);
  }

  public ThingBuilder thing(String name) {
    return new ThingBuilder(name) {
      public Thing thing() {
        var t = super.thing();
        things.put(name, t);
        return t;
      }
    };
  }

  public MazeBuilder place(String child, String place, String parent) {
    location(parent).placeThing(things.get(child), place);
    return this;
  }

  public MazeBuilder toNorthOf(String room1, String room2, String doorDescription) {
    return connect(room1, room2, doorDescription, NORTH);
  }

  public MazeBuilder toEastOf(String room1, String room2, String doorDescription) {
    return connect(room1, room2, doorDescription, EAST);
  }

  public MazeBuilder toSouthOf(String room1, String room2, String doorDescription) {
    return connect(room1, room2, doorDescription, SOUTH);
  }

  public MazeBuilder toWestOf(String room1, String room2, String doorDescription) {
    return connect(room1, room2, doorDescription, WEST);
  }

  public MazeBuilder connect(String room1, String room2, String doorDescription, Direction d) {
    var r1 = rooms.get(room1);
    var r2 = rooms.get(room2);
    if (r1 == null) throw new Error("No room " + room1);
    if (r2 == null) throw new Error("No room " + room2);

    rooms.get(room1).connect(doorDescription, rooms.get(room2), d);
    return this;
  }

  private Location location(String name) {
    var r = rooms.get(name);
    if (r != null) return r;
    return things.get(name);
  }
}
