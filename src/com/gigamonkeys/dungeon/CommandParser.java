package com.gigamonkeys.dungeon;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.*;

// Form of commands:
//
// - INVENTORY
// - LOOK
// - GO direction
// - EXAMINE object (supported by all objects)
// - verb object
// - verb object WITH thing
//
// Objects must be present and support the given verb. If needs a WITH clause and one not provided, ask for it.
//
// Commands can also be partial. E.g. just a verb. Command loop should
// then ask for an object and parse the next answer (excepting certain
// commands like INVENTORY) as the name of the object and then execute
// the full command. Likewise for verbs that require a WITH clause,
// e.g. ATTACK MONSTER. If there is only one object present supporting
// a verb (e.g. EAT) it can be implicit.
//

class CommandParser {

  private static final Pattern WS = Pattern.compile("\\s+");

  public Command parse(String line, Room room, Player player) {
    Map<String, Map<String, Verbable>> registry = verbRegistry(room, player);

    var tokens = WS.split(line.toUpperCase());

    return switch (tokens.length) {
      case 1 -> justVerb(tokens[0], registry);
      case 2 -> verbAndObject(tokens[0], tokens[1], registry);
      default -> new Command.NotUnderstood(line);
    };
  }

  private Map<String, Map<String, Verbable>> verbRegistry(Room room, Player player) {
    var m = new HashMap<String, Map<String, Verbable>>();
    registerThing(m, player);
    registerThings(m, room.things());
    registerThings(m, player.things());
    return m;
  }

  private void registerThings(Map<String, Map<String, Verbable>> m, Collection<Thing> things) {
    for (var thing : things) {
      registerThing(m, thing);
    }
  }

  private void registerThing(Map<String, Map<String, Verbable>> m, Verbable thing) {
    var name = thing.name();
    for (var verb : thing.verbs()) {
      m.computeIfAbsent(verb, k -> new HashMap<String, Verbable>()).put(name, thing);
    }
  }

  private Command justVerb(String verb, Map<String, Map<String, Verbable>> registry) {
    if (registry.containsKey(verb)) {
      var things = registry.get(verb).values().toArray(Thing[]::new);
      if (things.length == 1) {
        return new Command.VerbObject(verb, things[0]);
      } else {
        return new Command.PickObject(verb, things);
      }
    }
    return new Command.UnknownVerb(verb);
  }

  private Command verbAndObject(
    String verb,
    String thing,
    Map<String, Map<String, Verbable>> registry
  ) {
    if (registry.containsKey(verb)) {
      var things = registry.get(verb).values().toArray(Thing[]::new);
      if (things.length == 1) {
        return new Command.VerbObject(verb, things[0]);
      } else {
        return new Command.PickObject(verb, things);
      }
    } else {
      return new Command.UnknownVerb(verb);
    }
  }
}
