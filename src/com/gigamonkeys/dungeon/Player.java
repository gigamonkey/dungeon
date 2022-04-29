package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Text.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represent the player.
 */
public class Player implements Location {

  private final Map<String, PlacedThing> inventory = new HashMap<>();
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

  public Map<String, PlacedThing> locationMap() {
    return inventory;
  }

  //
  //////////////////////////////////////////////////////////////////////////////

  public Optional<Thing> roomThing(String name) {
    return room.thing(name);
  }

  public Optional<Thing> anyThing(String name) {
    return thing(name).or(() -> roomThing(name));
  }

  public void go(Door door) {
    room = door.from(room);
  }

  public Room room() {
    return room;
  }

  public void drop(Thing t) {
    room.drop(t);
  }

  public String inventory() {
    if (things().isEmpty()) {
      return "You've got nothing!";
    } else {
      var items = things().stream().map(t -> a(t.description())).toList();
      return new StringBuilder("You have ").append(commify(items)).append(".").toString();
    }
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
