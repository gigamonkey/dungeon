package com.gigamonkeys.dungeon;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represent the player.
 */
public class Player {

  private final List<Thing> things = new ArrayList<>();
  private Room room;
  private int hitPoints;

  public Player(Room startingRoom, int hitPoints) {
    this.room = startingRoom;
    this.hitPoints = hitPoints;
  }

  public String go(Direction d) {
    var door = room.getDoor(d);
    if (door == null) {
      return "No door to the " + d;
    } else {
      room = door.from(room);
      return room.enter(this);
    }
  }

  public Room room() {
    return room;
  }

  public String take(Thing t) {
    if (t.isPortable()) {
      room.removeThing(t);
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
    if (t.isEdible()) {
      things.remove(t);
      room.removeThing(t);
    }
    return t.eat();
  }

  public Optional<Thing> thing(String name) {
    return things.stream().filter(t -> t.name().equals(name)).findAny();
  }

  public Optional<Thing> roomThing(String name) {
    return room.thing(name);
  }

  public Optional<Thing> anyThing(String name) {
    return thing(name).or(() -> roomThing(name));
  }

  public String listen() {
    return "Can't hear anything!"; // FIXME: implement
  }

  public String loseHitPoints(int amount) {
    hitPoints -= amount;
    var s = amount != 1 ? "s" : "";
    return "You take " + amount + " hit point" + s + " of damage.";
  }
}
