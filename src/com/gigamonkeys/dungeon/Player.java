package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Text.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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

  public Stream<PlacedThing> allThings() {
    return inventory.allThings();
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
      t.location().removeThing(t);
      inventory.placeThing(t, "in your stuff");
      return "Okay. You have " + a(t.description());
    } else {
      return "You can't take the " + t.name() + "!";
    }
  }

  public String takeThings(List<Thing> things) {
    var taken = new ArrayList<String>();
    var notTaken = new ArrayList<String>();
    for (var t : things) {
      if (t.isPortable()) {
        t.location().removeThing(t);
        inventory.placeThing(t, "in your stuff");
        taken.add(t.description());
      } else {
        notTaken.add(t.name());
      }
    }
    var desc = new StringBuilder("Okay. Took " + commify(taken) + ".");
    if (!notTaken.isEmpty()) {
      desc.append(" Can't take " + commify(notTaken) + ".");
    }
    return desc.toString();
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
    if (inventory.things().isEmpty()) {
      return "You've got nothing!";
    } else {
      var items = inventory.things().stream().map(t -> a(t.thing().description())).toList();
      return new StringBuilder("You have ").append(commify(items)).append(".").toString();
    }
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
    var status = hitPoints > 0 ? "You're down to " + hitPoints : "You feel consciousness slipping away.";
    return "You take " + amount + " hit point" + s + " of damage. " + status;
  }

  public boolean alive() {
    return hitPoints > 0;
  }
}
