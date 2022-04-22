package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Text.wrap;

import java.util.ArrayList;
import java.util.function.Function;

public record Command(String verb, String help, Function<String[], String> action) {
  public String run(String[] args, Player p) {
    try {
      var desc = new ArrayList<String>();
      desc.add(action.apply(args));
      p.room().describeAttacks(desc, p);
      return wrap(String.join(" ", desc), 60);
    } catch (SpecialCommandOutput output) {
      // Can't decideif this is a kludge or elegant.
      return output.text();
    }
  }

  public static Command unknown(String verb) {
    return new Command(verb, "", args -> "Don't know how to " + verb);
  }
}
