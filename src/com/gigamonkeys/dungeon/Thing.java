package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Text.*;

import java.util.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * The base class for all the things in the Dungeon other than the Player,
 * Rooms, and Doors. Different static inner classes exist as conveniences for
 * setting up Things with specific properties which can either be used directly
 * or as the parent for other classes, possibly anonymous classes, that want to
 * override a handful of other methods.
 */
public class Thing implements Location, Attack.Target {

  private final Map<String, PlacedThing> things = new HashMap<>();

  private final String name;
  private final String description;
  private final boolean isPortable;
  private final boolean isMonster;

  private int hitPoints = 0;
  private Optional<Location> location = Optional.empty();

  Thing(String name, String description, boolean isPortable, boolean isMonster, int hitPoints) {
    this.name = name;
    this.description = description;
    this.hitPoints = hitPoints;
    this.isPortable = isPortable;
    this.isMonster = isMonster;
  }

  public String name() {
    return name;
  }

  public String description() {
    return description;
  }

  public String describeThings() {
    var desc = new ArrayList<String>();

    var byPlace = groupByPlace();

    places()
      .stream()
      .flatMap(p -> {
        var things = byPlace.getOrDefault(p, List.of()).stream().map(t -> a(t.description())).toList();
        return things.isEmpty()
          ? Stream.empty()
          : Stream.of(capitalize(p) + " the " + name() + isAre(things.size()) + commify(things) + ".");
      })
      .forEach(desc::add);

    things().stream().map(Thing::describeThings).forEach(desc::add);

    return String.join(" ", desc);
  }

  public String close() {
    return "You can't close a " + name() + ".";
  }

  public String eat() {
    return "Yuck. You can't eat " + a(description()) + ".";
  }

  public String open() {
    return "You can't open a " + name() + ".";
  }

  public boolean isPortable() {
    return isPortable;
  }

  public boolean isMonster() {
    return isMonster;
  }

  public int hitPoints() {
    return hitPoints;
  }

  public Attack attack() {
    return new Attack.Useless(a(description()) + " is not an effective weapon.");
  }

  /**
   * Is the thing alive?
   */
  public boolean alive() {
    return hitPoints() > 0;
  }

  /**
   * Destroy the thing by unlinking it from any location and return a
   * description of the destruction.
   */
  public String destroy(String s) {
    location().ifPresent(l -> l.removeThing(this));
    return s;
  }

  /**
   * Apply an attack to this thing as a target.
   */
  public String applyAttack(Attack attack) {
    hitPoints -= attack.damage();

    if (hitPoints < -500) {
      return destroy("");
    }

    if (isMonster()) {
      return (
        "After " +
        attack.damage() +
        " points of damage, the " +
        name() +
        " is " +
        (alive() ? "wounded but still alive. And now it's mad." : "dead. Good job, murderer.")
      );
    } else {
      return "I don't know why you're attacking an innocent " + name() + ".";
    }
  }

  public String who() {
    return "the " + name;
  }

  /**
   * Can this thing be taken from it's current location. It needs to be both
   * inherently portable and the location needs to allow it to be taken.
   */
  public boolean canBeTaken() {
    return isPortable() && location().map(l -> l.canTake(this)).orElse(false);
  }

  ////////////////////////////////////////////////////////////////////////////
  // Movement

  public final Optional<Location> location() {
    return location;
  }

  public String moveTo(Location location, String place) {
    location.placeThing(this, place);
    return "The " + name() + " moves to " + place + " " + location + ".";
  }

  public final void setLocation(Location location) {
    this.location = Optional.of(location);
  }

  public final void clearLocation() {
    location = Optional.empty();
  }

  //////////////////////////////////////////////////////////////////////////////
  // Location implementation -- things can contain things.

  public Map<String, PlacedThing> locationMap() {
    return things;
  }

  public boolean canTake(Thing thing) {
    return !alive();
  }

  //////////////////////////////////////////////////////////////////////////////
  // Action events. When an action occurs all the things in the room are
  // notified via the appropriate one of these methods and can generate
  // reactions which are then processed as part of the turn.

  public Stream<Action> onAttack(Action.Attack a) {
    return Stream.empty();
  }

  public Stream<Action> onClose(Action.Close a) {
    return Stream.empty();
  }

  public Stream<Action> onDrop(Action.Drop a) {
    return Stream.empty();
  }

  public Stream<Action> onEat(Action.Eat a) {
    return Stream.empty();
  }

  public Stream<Action> onEnter(Action.Go a) {
    return Stream.empty();
  }

  public Stream<Action> onLook(Action.Look a) {
    return Stream.empty();
  }

  public Stream<Action> onMove(Action.Move a) {
    return Stream.empty();
  }

  public Stream<Action> onOpen(Action.Open a) {
    return Stream.empty();
  }

  public Stream<Action> onPut(Action.Put a) {
    return Stream.empty();
  }

  public Stream<Action> onSay(Action.Say a) {
    return Stream.empty();
  }

  public Stream<Action> onTake(Action.Take a) {
    return Stream.empty();
  }

  public Stream<Action> onTurn(Action.Turn a) {
    return Stream.empty();
  }

  //////////////////////////////////////////////////////////////////////////////
  // Static convenient classes for creating various kinds of Thing.

  static class Monster extends Thing {

    private final String deadDescription;

    Monster(String name, String liveDescription, String deadDescription, int hitPoints, boolean isPortable) {
      super(name, liveDescription, isPortable, true, hitPoints);
      this.deadDescription = deadDescription;
    }

    Monster(String name, int hitPoints, boolean isPortable) {
      this(name, null, null, hitPoints, isPortable);
    }

    Monster(String name, int hitPoints) {
      this(name, null, null, hitPoints, false);
    }

    public String description() {
      return alive() ? super.description() : deadDescription;
    }
  }

  static class Furniture extends Thing {

    Furniture(String name, String description) {
      super(name, description, false, false, 0);
    }
  }

  static class Weapon extends Thing {

    private final Attack attack;

    Weapon(String name, String description, Attack attack) {
      super(name, description, true, false, 0);
      this.attack = attack;
    }

    public Attack attack() {
      return attack;
    }
  }

  static class Food extends Thing {

    private final String eat;

    Food(String name, String description, String eat) {
      super(name, description, true, false, 0);
      this.eat = eat;
    }

    public String eat() {
      return destroy(eat);
    }
  }
}
