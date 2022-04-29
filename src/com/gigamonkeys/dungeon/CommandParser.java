package com.gigamonkeys.dungeon;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Methods that can serve as the parsers needed by Command objects.
 */
public record CommandParser(Player player) {
  Action drop(String[] args) {
    Function<String, Action> dropNamed = name ->
      player.thing(name).map(t -> Action.drop(player, t)).orElse(Action.none("No " + name + " to drop!"));

    return arg(args, 1).map(dropNamed).orElse(Action.none("Drop what?"));
  }

  Action eat(String[] args) {
    Function<String, Action> eatNamed = name ->
      player.anyThing(name).map(food -> Action.eat(player, food)).orElse(Action.none("No " + name + " here to eat."));

    return arg(args, 1).map(eatNamed).orElse(Action.none("Eat what?"));
  }

  Action go(String[] args) {
    Function<Direction, Action> forDirection = d ->
      door(d).map(door -> Action.go(player, door)).orElse(Action.none("No door to the " + d + "."));

    Function<String, Action> forName = name ->
      direction(name).map(forDirection).orElse(Action.none("Don't understand direction " + name + "."));

    return arg(args, 1).map(forName).orElse(Action.none("Go where?"));
  }

  Action look(String[] args) {
    return Action.look(player);
  }

  Action playerAttack(String[] args) {
    var target = args.length == 3 && args[1].equals("with") ? onlyMonster() : arg(args, 1).flatMap(player::roomThing);

    Function<Thing, Action> attackTargetWithWeapon = t ->
      arg(args, args.length == 3 ? 2 : 3)
        .flatMap(player::anyThing)
        .map(w -> Action.playerAttack(t, w))
        .orElse(Action.none("Attack with what?"));

    Function<Thing, Action> attackWith = t ->
      arg(args, args.length == 3 ? 1 : 2)
        .flatMap(n -> expect("with", n))
        .map(e -> attackTargetWithWeapon.apply(t))
        .orElse(Action.none("Don't understand 'ATTACK' with no 'WITH'."));

    return target.map(attackWith).orElse(Action.none("Attack what?"));
  }

  Action take(String[] args) {
    return arg(args, 1)
      .flatMap(n -> listOfThings(args, 1))
      .map(ts -> Action.take(player, ts))
      .orElse(Action.none("Take what?"));
  }

  ////////////////////////////////////////////////////////////////////
  // Helper methods.

  private Optional<String> arg(String[] args, int idx) {
    return Optional.of(idx).filter(i -> i < args.length).map(i -> args[i]);
  }

  private Optional<Direction> direction(String name) {
    return Direction.fromString(name);
  }

  private Optional<Door> door(Direction d) {
    return player.room().door(d);
  }

  private Optional<String> expect(String expected, String s) {
    return Optional.of(s).filter(expected::equals);
  }

  private Optional<Thing> onlyMonster() {
    return player.room().onlyMonster();
  }

  private Optional<List<Thing>> listOfThings(String[] args, int start) {
    var things = new ArrayList<Thing>();
    for (var i = start; i < args.length; i++) {
      var maybe = player.roomThing(args[i]);
      if (!maybe.isPresent()) {
        if (!args[i].equals("and")) {
          return Optional.empty();
        }
      } else {
        var thing = maybe.get();
        things.add(thing);
        thing.allThings().forEach(things::add);
      }
    }
    return Optional.of(things);
  }
}
