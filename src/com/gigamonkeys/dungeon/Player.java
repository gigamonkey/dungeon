package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Location.PlacedThing;
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

  private final Location.Helper inventory = new Location.Helper(this);
  private Room room;
  private int hitPoints;

  public Player(int hitPoints) {
    this.hitPoints = hitPoints;
  }

  public void setStart(Room room) {
    this.room = room;
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

  public boolean canTake(Thing thing) {
    return true;
  }

  //
  //////////////////////////////////////////////////////////////////////////////

  public Optional<Thing> roomThing(String name) {
    return room.thing(name);
  }

  public Optional<Thing> anyThing(String name) {
    return thing(name).or(() -> roomThing(name));
  }

  public String go(Door door) {
    room = door.from(room);
    return room.description();
  }

  public Room room() {
    return room;
  }

  public String take(Thing t) {
    if (t.isPortable()) {
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
      if (t.canBeTaken()) {
        inventory.placeThing(t, "in your stuff");
        taken.add(t.description());
      } else {
        notTaken.add(t.name());
      }
    }
    var desc = new ArrayList<String>();
    if (!taken.isEmpty()) {
      desc.add("Okay, took " + commify(taken) + ".");
    }
    if (!notTaken.isEmpty()) {
      desc.add("Can't take " + commify(notTaken) + ".");
    }
    return String.join(" ", desc);
  }

  public String drop(Thing t) {
    room.drop(t);
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

  public int hitPoints() {
    return hitPoints;
  }

  public void takeDamage(int amount) {
    hitPoints -= amount;
  }

  public String describeDamage(int amount) {
    var status = hitPoints > 0 ? "You're down to " + hitPoints + "." : "You feel consciousness slipping away.";
    return "You take " + amount + plural(" hit point", amount) + " of damage. " + status;
  }

  public boolean alive() {
    return hitPoints > 0;
  }
}
