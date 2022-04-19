package com.gigamonkeys.dungeon;

import java.util.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * One room in the dungeon. Rooms are connected to other Rooms by
 * Doors. They also contain Things which can be weapons, food,
 * monsters, etc.
 */
public class Room implements Location {

  private final String description;
  private final Map<Direction, Door> doors = new HashMap<Direction, Door>();
  private final Things things = new Things();

  public Room(String description) {
    this.description = description;
  }

  //////////////////////////////////////////////////////////////////////////////
  // Location implementation

  public void placeThing(Thing thing, String where) {
    things.placeThing(thing, where);
  }

  public void removeThing(Thing thing) {
    things.removeThing(thing);
  }

  public Optional<Thing> thing(String name) {
    return things.thing(name);
  }

  public Collection<PlacedThing> things() {
    return things.things();
  }

  //
  //////////////////////////////////////////////////////////////////////////////

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

  public String enter(Player player) {
    List<String> desc = new ArrayList<>();
    desc.add(description());
    return String.join(" ", desc);
  }

  public String description() {
    List<String> desc = new ArrayList<>();
    desc.add("You are in");
    desc.add(description + ".");
    describeThings(desc, t -> !t.isMonster());
    describeThings(desc, t -> t.isMonster());
    describeDoors(desc);
    return String.join(" ", desc);
  }

  public Door getDoor(Direction d) {
    return doors.get(d);
  }

  public void drop(Thing t) {
    placeThing(t, "on floor");
  }

  public Optional<Thing> onlyMonster() {
    return Optional.of(monsters().toList()).filter(m -> m.size() == 1).map(m -> m.get(0));
  }

  private Stream<Thing> monsters() {
    return things().stream().map(pt -> pt.thing()).filter(t -> t.isMonster());
  }

  private void describeThings(List<String> desc, Predicate<Thing> p) {
    things()
      .stream()
      .filter(pt -> p.test(pt.thing()))
      .forEach(pt -> {
        desc.add(pt.where() + " is " + pt.thing().a() + " " + pt.thing().description() + ".");
      });
  }

  private void describeDoors(List<String> desc) {
    for (var d : Direction.class.getEnumConstants()) {
      if (doors.containsKey(d)) {
        desc.add("To the " + d + " there is " + doors.get(d).description() + ".");
      }
    }
  }

  public void describeAttacks(List<String> desc, Player player) {
    var totalDamage = 0;
    for (var m : monsters().toList()) {
      var a = m.attackPlayer();
      totalDamage += a.damage();
      desc.add(a.description());
    }
    if (totalDamage > 0) {
      desc.add(player.loseHitPoints(totalDamage));
    }
  }
}
