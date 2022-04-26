package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Text.wrap;

import java.util.function.*;
import java.util.stream.*;

public record Command(String verb, String help, Function<String[], Action> parser) {
  /**
   * Run the command.
   */
  public String run(String[] args, Player p) {
    try {
      var action = parser.apply(args);
      var all = Stream.concat(results(action, p), Stream.concat(forTurn(p), playerStateChange(p)));
      return wrap(all.collect(Collectors.joining(" ")), 60);
    } catch (SpecialOutput output) {
      // Can't decide if this is a kludge or elegant.
      return output.text;
    }
  }

  /**
   * Get the description of executing this action and of all the resulting
   * reactions, recursively.
   */
  private Stream<String> results(Action action, Player player) {
    // N.B. Need to wrap player in stream to avoid defer getting the current
    // room so that we get the room after the action has been described (with
    // it's possible side effect of changing the player's room)
    var things = Stream.of(player).flatMap(p -> p.room().allThings().map(PlacedThing::thing));
    var reactions = things.flatMap(t -> action.event(t));
    return Stream.concat(Stream.of(action.description()), reactions.flatMap(a -> results(a, player)));
  }

  /**
   * Get the descriptions of any actions taking by things in the room from the
   * onTurn event.
   */
  private Stream<String> forTurn(Player p) {
    var things = p.room().allThings().map(PlacedThing::thing);
    var reactions = things.flatMap(t -> t.onTurn(p));
    return reactions.flatMap(a -> results(a, p));
  }

  /**
   * Get the decription of any changes to the player's state.
   */
  private Stream<String> playerStateChange(Player p) {
    // At the moment, the only relevant state is the player's hit point count.
    // If we add other state that can change, would need to thread it through
    // here.
    return Stream
      .of(p.hitPoints())
      .flatMap(orig -> p.hitPoints() < orig ? Stream.of(p.describeDamage(orig - p.hitPoints())) : Stream.empty());
  }

  static class SpecialOutput extends RuntimeException {

    final String text;

    public SpecialOutput(String text) {
      this.text = text;
    }
  }
}
