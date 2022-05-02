package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.CommandParser.*;

import java.util.function.*;

/**
 * The things that can happen when the user types something.
 */
public interface Command {
  /**
   * The verb that invokes this command.
   */
  public String verb();

  /**
   * Help string for this command.
   */
  public String help();

  /**
   * Run the command.
   */
  public String run(String[] args, Player p);

  /**
   * A command that produces an Action which can produce reactions and counts as a
   * turn. The combined output from the reaction and reactions is all jammed
   * together and wrapped as a paragraph.
   */
  public static record Turn(String verb, String help, Parser parser) implements Command {
    /**
     * Run the command.
     */
    public String run(String[] args, Player p) {
      try {
        var action = parser.parse(args);

        // Get this before running any actions since they could change it.
        var startingState = p.state();

        var text = new Text.Wrapped();
        text.add(action.description());
        addReactions(text, action, p);
        addReactions(text, new Action.Turn(p), p);
        text.add(p.stateChanges(startingState));
        return text.toString();
      } catch (BadCommandException bce) {
        return bce.getMessage();
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

      var reactions = player.room().allThings().flatMap(action::reactions).toList();
      text.add(reactions.stream().map(Action::description));
      for (var a : reactions) {
        addReactions(text, a, player);
      }
    }
  }

  /**
   * A command that just produces output and does not count as a turn.
   */
  public static record NoTurn(String verb, String help, Supplier<String> fn) implements Command {
    /**
     * Run the command.
     */
    public String run(String[] args, Player p) {
      return fn.get();
    }
  }
}
