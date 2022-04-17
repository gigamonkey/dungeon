package com.gigamonkeys.dungeon;

import java.util.*;

public class Player {

  private final List<Thing> things = new ArrayList<>();

  private Room room;

  public Player(Room startingRoom) {
    this.room = startingRoom;
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

  public Room room() {
    return room;
  }

  public String take(Thing t) {
    if (t.portable()) {
      room.take(t);
      things.add(t);
      return "You put the " + t.name() + " in your bag.";
    } else {
      return "You can't take " + t.a() + " " + t.name() + "!";
    }
  }

  public String drop(Thing t) {
    room.drop(t);
    things.remove(t);
    return "You drop the " + t.name();
  }

  public String look() {
    return room.description();
  }

  public String inventory() {
    return String.join(" ", things.stream().map(x -> x.description()).toList());
  }

  public String eat(Thing t) {
    return t.beEatenBy(this);
  }

  public Optional<Thing> thing(String name) {
    return things.stream().filter(t -> t.name().equals(name)).findAny();
  }

  public String listen() {
    return "Can't hear anything!"; // FIXME: implement
  }
}
