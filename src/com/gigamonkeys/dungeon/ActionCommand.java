package com.gigamonkeys.dungeon;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A command that produces an Action which can produce reactions and counts as a
 * turn. The combined output from the reaction and reactions is all jammed
 * together and wrapped as a paragraph.
 */
public record ActionCommand(String verb, String help, Function<String[], Action> parser) implements Command {
  /**
   * Run the command.
   */
  public String run(String[] args, Player p) {
    var action = parser.apply(args);

    // Get this before running any actions since they could change it.
    var startingState = p.state();

    var text = new Text.Wrapped(72);
    text.add(action.description());
    addReactions(text, action, p);
    addReactions(text, Action.turn(p), p);
    text.add(p.stateChanges(startingState));
    return text.toString();
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

    var reactions = player.room().allThings().flatMap(action::reactions).toList();
    text.add(reactions.stream().map(Action::description));
    for (var a : reactions) {
      addReactions(text, a, player);
    }
  }
}
