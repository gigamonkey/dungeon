package com.gigamonkeys.dungeon;

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
    return arg(args, 1).map(this::goByArg).orElse(Action.none("Go where?"));
  }

  Action take(String[] args) {
    return arg(args, 1)
      .flatMap(n -> listOfThings(args, 1))
      .map(ts -> Action.take(player, ts))
      .orElse(Action.none("Take what?"));
  }

  Action drop(String[] args) {
    return arg(args, 1)
      .map(name -> player.thing(name).map(t -> Action.drop(player, t)).orElse(Action.none("No " + name + " to drop!")))
      .orElse(Action.none("Drop what?"));
  }

  Action look(String[] args) {
    return Action.look(player);
  }

  Action inventory(String[] args) {
    return Action.none(player.inventory());
  }

  Action eat(String[] args) {
    return arg(args, 1)
      .map(name ->
        player.anyThing(name).map(t -> Action.eat(player, t)).orElse(Action.none("No " + name + " here to eat."))
      )
      .orElse(Action.none("Eat what?"));
  }

  Action attack(String[] args) {
    var i = 1;

    var target = (args.length == 3 && args[i].equals("WITH"))
      ? onlyMonster()
      : arg(args, i++).flatMap(n -> player.roomThing(n));

    var with = arg(args, i++).flatMap(n -> expect("WITH", n));
    var weapon = arg(args, i++).flatMap(n -> player.anyThing(n));

    return target
      .map(t ->
        with
          .map(e -> weapon.map(w -> Action.attack(t, w)).orElse(Action.none("Attack with what?")))
          .orElse(Action.none("Don't understand 'ATTACK' with no 'WITH'."))
      )
      .orElse(Action.none("Attack what?"));
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
    return Optional.of(s).filter(v -> expected.equals(v));
  }

  private Optional<Thing> onlyMonster() {
    return player.room().onlyMonster();
  }

  private Optional<List<Thing>> listOfThings(String[] args, int start) {
    var things = new ArrayList<Thing>();
    for (var i = start; i < args.length; i++) {
      var maybe = player.roomThing(args[i]);
      if (!maybe.isPresent()) {
        if (!args[i].equals("AND")) {
          return Optional.empty();
        }
      } else {
        things.add(maybe.get());
      }
    }
    return Optional.of(things);
  }

  private Action goByArg(String arg) {
    return direction(arg).map(this::goByDirection).orElse(Action.none("Don't understand direction " + arg + "."));
  }

  private Action goByDirection(Direction d) {
    return player
      .room()
      .getDoor(d)
      .map(door -> Action.go(player, door))
      .orElse(Action.none("No door to the " + d + "."));
  }
}
