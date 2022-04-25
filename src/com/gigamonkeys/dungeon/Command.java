package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Text.wrap;

import java.util.function.*;
import java.util.stream.*;

public record Command(String verb, String help, Function<String[], Action> parser) {
  public static Command unknown(String verb) {
    return new Command(verb, "", args -> new Action.NoAction("Don't know how to " + verb));
  }

  public String run(String[] args, Player p) {
    try {
      var action = parser.apply(args);
      var all = Stream.concat(results(action, p), playerStateChange(p));
      return wrap(all.collect(Collectors.joining(" ")), 60);
    } catch (SpecialCommandOutput output) {
      // Can't decide if this is a kludge or elegant.
      return output.text();
    }
  }

  public Stream<String> results(Action action, Player p) {
    var things = p.room().allThings().map(PlacedThing::thing);
    var reactions = things.flatMap(t -> action.event(t));
    return Stream.concat(Stream.of(action.description()), reactions.flatMap(a -> results(a, p)));
  }

  public Stream<String> playerStateChange(Player p) {
    return Stream
      .of(p.hitPoints())
      .flatMap(orig -> p.hitPoints() < orig ? Stream.of(p.describeDamage(orig - p.hitPoints())) : Stream.empty());
  }
}
