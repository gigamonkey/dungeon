package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Location.PlacedThing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Methods that can serve as the parsers needed by Command objects.
 */
public record CommandParser(Player player, Dungeon dungeon, Map<String, Command> commands) {
  Action help(String[] args) {
    var w = commands.values().stream().mapToInt(c -> c.verb().length()).max().getAsInt();

    var docs = commands
      .values()
      .stream()
      .sorted((a, b) -> a.verb().compareTo(b.verb()))
      .map(c -> {
        var padding = IntStream
          .range(0, (w + 2) - c.verb().length())
          .mapToObj(i -> " ")
          .collect(Collectors.joining(""));
        return "  " + c.verb() + padding + c.help();
      })
      .toList();

    return Action.noWrap("I understand the following commands:\n\n" + String.join("\n", docs));
  }

  Action quit(String[] args) {
    // Doesn't need a real Action because it's the end of the road.
    return () -> {
      dungeon.endGame();
      return "Okay. Bye!";
    };
  }

  Action go(String[] args) {
    return arg(args, 1).map(this::goNamed).orElse(Action.none("Go where?"));
  }

  Action take(String[] args) {
    return arg(args, 1)
      .flatMap(n -> listOfThings(args, 1))
      .map(ts -> Action.take(player, ts))
      .orElse(Action.none("Take what?"));
  }

  Action drop(String[] args) {
    return arg(args, 1).map(this::dropNamed).orElse(Action.none("Drop what?"));
  }

  Action look(String[] args) {
    return Action.look(player);
  }

  Action inventory(String[] args) {
    return Action.none(player.inventory());
  }

  Action eat(String[] args) {
    return arg(args, 1).map(this::eatNamed).orElse(Action.none("Eat what?"));
  }

  Action attack(String[] args) {
    var i = 1;

    var target = args.length == 3 && args[i].equals("with") ? onlyMonster() : arg(args, i++).flatMap(player::roomThing);

    final var idx = i;

    return target.map(t -> attackTarget(t, args, idx)).orElse(Action.none("Attack what?"));
  }

  ////////////////////////////////////////////////////////////////////
  // Helper methods.

  private Optional<String> arg(String[] args, int idx) {
    return Optional.of(idx).filter(i -> i < args.length).map(i -> args[i]);
  }

  private Optional<Direction> direction(String name) {
    return Direction.fromString(name);
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
        thing.allThings().map(PlacedThing::thing).forEach(things::add);
      }
    }
    return Optional.of(things);
  }

  private Action goNamed(String name) {
    return direction(name).map(this::goDirection).orElse(Action.none("Don't understand direction " + name + "."));
  }

  private Action dropNamed(String name) {
    return player.thing(name).map(t -> Action.drop(player, t)).orElse(Action.none("No " + name + " to drop!"));
  }

  private Action goDirection(Direction d) {
    return player
      .room()
      .getDoor(d)
      .map(door -> Action.go(player, door))
      .orElse(Action.none("No door to the " + d + "."));
  }

  private Action eatNamed(String name) {
    return player
      .anyThing(name)
      .map(food -> Action.eat(player, food))
      .orElse(Action.none("No " + name + " here to eat."));
  }

  private Action attackTarget(Thing target, String[] args, int i) {
    return arg(args, i)
      .flatMap(n -> expect("with", n))
      .map(e -> attackTargetWithWeapon(target, args, i + 1))
      .orElse(Action.none("Don't understand 'ATTACK' with no 'WITH'."));
  }

  private Action attackTargetWithWeapon(Thing target, String[] args, int i) {
    return arg(args, i)
      .flatMap(player::anyThing)
      .map(w -> Action.playerAttack(target, w))
      .orElse(Action.none("Attack with what?"));
  }
}
