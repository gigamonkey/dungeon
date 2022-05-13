package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.CommandParser.*;
import static com.gigamonkeys.dungeon.Text.*;

import com.gigamonkeys.dungeon.CommandParser.Parse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Represent the player.
 */
public class Player implements Location, Attack.Target {

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

  private String describeDamage(int amount) {
    var status = hitPoints > 0 ? "You're down to " + hitPoints + "." : "You feel consciousness slipping away.";
    return "You take " + amount + plural(" hit point", amount) + " of damage. " + status;
  }

  //////////////////////////////////////////////////////////////////////////////
  // Basic methods on Player

  public Room room() {
    return room;
  }

  public int hitPoints() {
    return hitPoints;
  }

  public boolean alive() {
    return hitPoints > 0;
  }

  //////////////////////////////////////////////////////////////////////////////
  // Some verbs

  public String go(Door door) {
    room = door.from(room);
    return room.description();
  }

  public String drop(Thing t) {
    room.drop(t);
    return "You drop the " + t.name() + ".";
  }

  public String inventory() {
    if (things().isEmpty()) {
      return "You've got nothing!";
    } else {
      var items = things().stream().map(t -> a(t.description())).toList();
      return new Text.Wrapped().add("You have").add(commify(items) + ".").toString();
    }
  }

  //////////////////////////////////////////////////////////////////////////////
  // Attack.Target implementation

  public String applyAttack(Attack attack) {
    int damage = attack.damage();
    hitPoints -= damage;
    return "";
  }

  public String who() {
    return "you";
  }

  //////////////////////////////////////////////////////////////////////////////
  // Command action parsers.

  Action attack(String[] args) throws BadCommandException {
    var i = 1;
    var target = args.length == 3
      ? implicit(room::onlyMonster).or("No monster here.")
      : anyThing(arg(args, i++).or("Attack what? And with what?"));
    var with = arg(args, i++).expect("with").or("Don't understand ATTACK with no WITH.");
    var weapon = anyThing(arg(args, i++).or("Attack with what?"));
    return with.toAction(e -> weapon.toAction(w -> target.toAction(t -> new Action.Attack(t, w))));
  }

  Action close(String[] args) throws BadCommandException {
    return simpleVerb(args, "close", Action.Close::new);
  }

  Action drop(String[] args) throws BadCommandException {
    return simpleVerb(args, "drop", t -> new Action.Drop(this, t));
  }

  Action eat(String[] args) throws BadCommandException {
    return simpleVerb(args, "eat", Action.Eat::new);
  }

  Action go(String[] args) throws BadCommandException {
    var name = arg(args, 1).or("Go where?");
    var dir = name.maybe(Direction::fromString).or(n -> "Don't understand direction " + n + ".");
    var door = dir.maybe(room()::door).or(d -> "No door to the " + d + ".");
    return door.toAction(d -> new Action.Go(this, d));
  }

  Action look(String[] args) throws BadCommandException {
    return new Action.Look(this);
  }

  Action open(String[] args) throws BadCommandException {
    return simpleVerb(args, "open", Action.Open::new);
  }

  Action put(String[] args) throws BadCommandException {
    var thing = anyThing(arg(args, 1).or("Put what? And where?"));
    var placeName = args(args, 2, args.length - 1).or("Where?");
    var location = anyThing(arg(args, args.length - 1).or("Need location."));

    return thing.toAction(t ->
      location.toAction(l ->
        placeName
          .maybe(l::place)
          .or(n -> "Can't put things " + n + " the " + l.name() + ".")
          .toAction(p -> new Action.Put(t, l, p))
      )
    );
  }

  Action take(String[] args) throws BadCommandException {
    return listOfThings(args, 1).or("Take what?").toAction(ts -> new Action.Take(this, ts));
  }

  //////////////////////////////////////////////////////////////////////////////
  // Helpers for action parsers

  private Action simpleVerb(String[] args, String verb, ToAction<Thing> factory) throws BadCommandException {
    return anyThing(arg(args, 1).or(capitalize(verb) + " what?")).toAction(factory);
  }

  private <T> Parse<Thing, String> anyThing(Parse<String, T> parse) {
    return parse.maybe(n -> thing(n).or(() -> room.thing(n))).or(n -> "No " + n + " here.");
  }

  private Parse<List<Thing>, String[]> listOfThings(String[] args, int start) {
    var things = new ArrayList<Thing>();
    for (var i = start; i < args.length; i++) {
      var maybe = room.thing(args[i]);
      if (!maybe.isPresent()) {
        if (!args[i].equals("and")) {
          return bad(args, "No " + args[i] + " here to take.");
        }
      } else {
        var thing = maybe.get();
        things.add(thing);
        thing.allThings().forEach(things::add);
      }
    }
    return things.isEmpty() ? bad(args, "Take what?") : good(things, args);
  }
}
