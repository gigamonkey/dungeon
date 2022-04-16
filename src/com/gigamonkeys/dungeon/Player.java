package com.gigamonkeys.dungeon;

import java.util.*;

public class Player {

  private final Map<Thing.Kind, List<Thing>> things = new HashMap<Thing.Kind, List<Thing>>();

  private Room room;

  public Player(Room startingRoom) {
    this.room = startingRoom;
  }

  void addThing(Thing t) {
    things.computeIfAbsent(t.kind, k -> new ArrayList<Thing>()).add(t);
  }

  public String go(Direction d) {
    var door = room.getDoor(d);
    if (door == null) {
      return "No door to the " + d;
    } else {
      room = door.from(room);
      return look();
    }
  }

  public String look() {
    return room.description();
  }

  public String listen() {
    return "Can't hear anything!"; // FIXME: implement
  }
}
