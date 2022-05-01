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

    public Action toAction(Function<T, Action> fn) {
      return value != null ? fn.apply(value) : Action.none(error);
    }

    public <X> Parse<X, T> maybe(Function<T, Optional<X>> fn) {
      return value != null
        ? fn.apply(value).map(x -> new Parse<>(x, value, null)).orElse(new Parse<>(null, value, null))
        : new Parse<>(null, null, error);
    }

    public Parse<T, T> expect(T expected) {
      return maybe(v -> Optional.ofNullable(expected.equals(v) ? v : null));
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
    var name = arg(args, 1).or("Drop what?");
    var thing = name.maybe(n -> player.thing(n)).or(n -> "No " + n + " to drop!");
    return thing.toAction(t -> Action.drop(player, t));
  }

  Action eat(String[] args) {
    var name = arg(args, 1).or("Eat what?");
    var thing = name.maybe(player::anyThing).or(n -> "No " + n + " here to eat.");
    return thing.toAction(food -> Action.eat(player, food));
  }

  Action go(String[] args) {
    var name = arg(args, 1).or("Go where?");
    var dir = name.maybe(Direction::fromString).or(n -> "Don't understand direction " + n + ".");
    var door = dir.maybe(player.room()::door).or(d -> "No door to the " + d + ".");
    return door.toAction(d -> Action.go(player, d));
  }

  Action look(String[] args) {
    return Action.look(player);
  }

  Action attack(String[] args) {
    var targetName = arg(args, 1).or("Attack what? And with what?");
    var with = arg(args, args.length - 2).expect("with").or("Don't understand ATTACK with no WITH.");
    var weaponName = arg(args, args.length - 1).or("Attack with what?");
    var target = targetName.maybe(this::implicitMonster).or(n -> "No " + n + " here to attack.");
    var weapon = weaponName.maybe(player::anyThing).or(n -> "No " + n + " here to attack with!");
    return with.toAction(e -> weapon.toAction(w -> target.toAction(t -> Action.attack(t, w))));
  }

  Action take(String[] args) {
    return listOfThings(args, 1).or("Take what?").toAction(ts -> Action.take(player, ts));
  }

  ////////////////////////////////////////////////////////////////////
  // Helper methods.

  private Parse<String, String[]> arg(String[] args, int idx) {
    return idx < args.length ? new Parse<>(args[idx], args, null) : new Parse<>(null, args, null);
  }

  private Optional<Thing> implicitMonster(String name) {
    return name.equals("with") ? player.room().onlyMonster() : player.roomThing(name);
  }

  private Parse<List<Thing>, String[]> listOfThings(String[] args, int start) {
    var things = new ArrayList<Thing>();
    for (var i = start; i < args.length; i++) {
      var maybe = player.roomThing(args[i]);
      if (!maybe.isPresent()) {
        if (!args[i].equals("and")) {
          return new Parse<>(null, args, null);
        }
      } else {
        var thing = maybe.get();
        things.add(thing);
        thing.allThings().forEach(things::add);
      }
    }
    return new Parse<>(things, args, null);
  }
}
