package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Text.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Represent the player.
 */
public class Player implements Location, Target {

  private final Map<String, PlacedThing> inventory = new HashMap<>();
  private Room room;
  private int hitPoints;

  public Player(Room start, int hitPoints) {
    this.room = start;
    this.hitPoints = hitPoints;
  }

  //////////////////////////////////////////////////////////////////////////////
  // Location implementation

  public Map<String, PlacedThing> locationMap() {
    return inventory;
  }

  //
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Tracking and describing state changes.

  public static record State(int hitPoints) {}

  public State state() {
    return new State(hitPoints);
  }

  public Stream<String> stateChanges(State original) {
    int damage = original.hitPoints - hitPoints;
    return Stream.ofNullable(damage > 0 ? describeDamage(damage) : null);
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
      return new Text.Wrapped(72)
        .add("You have")
        .add(commify(items) + ".")
        .toString();
    }
  }

  public int hitPoints() {
    return hitPoints;
  }

  public String applyAttack(Attack attack) {
    int damage = attack.damage();
    hitPoints -= damage;
    return "You take " + damage + " " + plural("hit point", damage) + " of damage.";
  }

  public String who() {
    return "you";
  }

  public String describeDamage(int amount) {
    var status = hitPoints > 0 ? "You're down to " + hitPoints + "." : "You feel consciousness slipping away.";
    return "You take " + amount + plural(" hit point", amount) + " of damage. " + status;
  }

  public boolean alive() {
    return hitPoints > 0;
  }
}
