package com.gigamonkeys.dungeon;

import com.gigamonkeys.dungeon.Location.PlacedThing;
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
      var text = new Text.Wrapped(72);

      text.add(action.description());
      addReactions(text, action, p);
      addReactions(text, Action.turn(p), p);
      text.add(playerStateChange(p, origHitPoints));
      return text.toString();
    } catch (SpecialOutput output) {
      // Can't decide if this is a kludge or elegant. The idea here is that
      // certain actions want to both control the formatting of the output and
      // to short circuit the normal recursive text generaation so they can
      // throw SpecialOutput from their description method. Probably better
      // would be to have special commands rather than special actions. Maybe
      // later.
      return output.text;
    }
  }

  /**
   * Get the description of executing this action and of all the resulting
   * reactions, recursively.
   */
  private void addReactions(Text.Wrapped text, Action action, Player player) {
    // Adds the reactions in batches because getting the description of each
    // action may potentially change the state of the world in ways that lead to
    // ConcurrentModificationExceptions from the stream if we try to do
    // everything in one big lazy stream.

    var reactions = player.room().allThings().flatMap(action::event).toList();
    text.add(reactions.stream().map(Action::description));
    for (var a : reactions) {
      addReactions(text, a, player);
    }
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
