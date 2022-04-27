package com.gigamonkeys.dungeon;

import java.util.function.Function;
import java.util.stream.Stream;

public record Command(String verb, String help, Function<String[], Action> parser) {
  /**
   * Run the command.
   */
  public String run(String[] args, Player p) {
    try {
      var action = parser.apply(args);
      int origHitPoints = p.hitPoints();
      return new Text.Wrapped(60)
        .add(results(action, p))
        .add(forTurn(p))
        .add(playerStateChange(p, origHitPoints))
        .toString();
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
    // N.B. Need to wrap the player in the second stream to avoid defer getting
    // the current room so that we get the room after the action has been
    // described (with its possible side effect of changing the player's room)
    return Stream.concat(
      Stream.of(action.description()),
      Stream
        .of(player)
        .flatMap(p -> p.room().allThings().map(PlacedThing::thing))
        .flatMap(action::event)
        .flatMap(a -> results(a, player))
    );
  }

  /**
   * Get the descriptions of any actions taking by things in the room from the
   * onTurn event.
   */
  private Stream<String> forTurn(Player p) {
    var turn = new Action.Turn(p);
    return p.room().allThings().map(PlacedThing::thing).flatMap(t -> t.onTurn(turn)).flatMap(a -> results(a, p));
  }

  /**
   * Get the decription of any changes to the player's state.
   */
  private Stream<String> playerStateChange(Player p, int orig) {
    // At the moment, the only relevant state is the player's hit point count.
    // If we add other state that can change, would need to thread it through
    // here.
    return Stream.of(p.hitPoints()).filter(hp -> hp < orig).map(hp -> p.describeDamage(orig - hp));
  }

  static class SpecialOutput extends RuntimeException {

    final String text;

    public SpecialOutput(String text) {
      this.text = text;
    }
  }
}
