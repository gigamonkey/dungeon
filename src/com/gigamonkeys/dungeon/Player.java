package com.gigamonkeys.dungeon;

import java.util.Collection;
import java.util.Optional;

/**
 * Represent the player.
 */
public class Player implements Location {

  private final Things inventory = new Things();
  private Room room;
  private int hitPoints;

  public Player(Room startingRoom, int hitPoints) {
    this.room = startingRoom;
    this.hitPoints = hitPoints;
  }

  //////////////////////////////////////////////////////////////////////////////
  // Location implementation

  public void placeThing(Thing thing, String where) {
    inventory.placeThing(thing, where);
  }

  public void removeThing(Thing thing) {
    inventory.removeThing(thing);
  }

  public Optional<Thing> thing(String name) {
    return inventory.thing(name);
  }

  public Collection<PlacedThing> things() {
    return inventory.things();
  }

  //
  //////////////////////////////////////////////////////////////////////////////

  public Optional<Thing> roomThing(String name) {
    return room.thing(name);
  }

  public Optional<Thing> anyThing(String name) {
    return thing(name).or(() -> roomThing(name));
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
      inventory.placeThing(t, "in your bag");
      return "You put the " + t.name() + " in your bag.";
    } else {
      return "You can't take " + t.a() + " " + t.name() + "!";
    }
  }

  public String drop(Thing t) {
    room.drop(t);
    inventory.removeThing(t);
    return "You drop the " + t.name();
  }

  public String look() {
    return room.description();
  }

  public String inventory() {
    var desc = new StringBuilder("You have:\n");
    for (var pt : inventory.things()) {
      Thing t = pt.thing();
      desc.append(" - " + t.a() + " " + t.description() + " " + pt.where() + "\n");
    }
    return desc.toString();
  }

  public String eat(Thing t) {
    return t.eat();
  }

  public String listen() {
    return "Can't hear anything!"; // FIXME: implement
  }

  public String loseHitPoints(int amount) {
    hitPoints -= amount;
    var s = amount != 1 ? "s" : "";
    return "You take " + amount + " hit point" + s + " of damage. You're down to " + hitPoints;
  }

  public boolean alive() {
    return hitPoints > 0;
  }
}
