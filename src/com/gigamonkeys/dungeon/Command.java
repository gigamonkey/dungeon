package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Text.wrap;

import java.util.ArrayList;
import java.util.function.Function;

public record Command(String verb, String help, Function<String[], String> action) {
  public String run(String[] args, Player p) {
    try {
      var desc = new ArrayList<String>();
      int origHitPoints = p.hitPoints();

      desc.add(action.apply(args));

      desc.addAll(
        p.room().allThings().map(PlacedThing::thing).flatMap(t -> t.onTurn(p)).map(a -> a.doAction(p)).toList()
      );

      if (p.hitPoints() < origHitPoints) {
        desc.add(p.describeDamage(origHitPoints - p.hitPoints()));
      }

      return wrap(String.join(" ", desc), 60);
    } catch (SpecialCommandOutput output) {
      // Can't decide if this is a kludge or elegant.
      return output.text();
    }
  }

  public static Command unknown(String verb) {
    return new Command(verb, "", args -> "Don't know how to " + verb);
  }
}
