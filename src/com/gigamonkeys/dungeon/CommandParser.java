package com.gigamonkeys.dungeon;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Methods that can serve as the parsers needed by Command objects.
 */
public record CommandParser(Player player) {
  private static class Parse<T, U> {

    private final T value;
    private final U previous;
    private final String error;

    public Parse(T value, U previous, String error) {
      this.value = value;
      this.previous = previous;
      this.error = error;
    }

    public Action getAction(Function<T, Action> fn) {
      return value != null ? fn.apply(value) : Action.none(error);
    }

    public <X> Parse<X, T> map(Function<T, X> fn) {
      return value != null ? new Parse<>(fn.apply(value), value, null) : new Parse<>(null, value, error);
    }

    public <X> Parse<X, T> flatMap(Function<T, Optional<X>> fn) {
      return value != null
        ? fn.apply(value).map(x -> new Parse<>(x, value, null)).orElse(new Parse<>(null, value, null))
        : new Parse<>(null, value, error);
    }

    public Parse<T, U> or(String error) {
      return (value != null || this.error != null) ? this : new Parse<>(null, previous, error);
    }

    public Parse<T, U> or(Function<U, String> error) {
      return (value != null || this.error != null) ? this : new Parse<>(null, previous, error.apply(previous));
    }

    public String toString() {
      return "value: " + value + "; previous: " + previous + "; error: " + error;
    }
  }

  Action drop(String[] args) {
    var name = argParse(args, 1).or("Drop what?");
    var thing = name.flatMap(n -> player.thing(n)).or(n -> "No " + n + " to drop!");
    return thing.getAction(t -> Action.drop(player, t));
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

  Action attack(String[] args) {
    var target = args.length == 3 && args[1].equals("with") ? onlyMonster() : arg(args, 1).flatMap(player::roomThing);

    Function<Thing, Action> attackTargetWithWeapon = t ->
      arg(args, args.length == 3 ? 2 : 3)
        .flatMap(player::anyThing)
        .map(w -> Action.attack(t, w))
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

  private Parse<String, String[]> argParse(String[] args, int idx) {
    return idx < args.length ? new Parse<>(args[idx], args, null) : new Parse<>(null, args, null);
  }

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
