package com.gigamonkeys.dungeon;

import com.gigamonkeys.dungeon.CommandParser.BadCommandException;
import com.gigamonkeys.dungeon.CommandParser.Parser;
import java.util.function.Supplier;

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
   * The main Command implementation, for commands that produce an Action which
   * can produce reactions and counts as a turn. The combined outputs from the
   * initial action and all the reactions plus a synthetic Turn action, and
   * finally any state changes reported by the Player are jammed together and
   * wrapped as a paragraph.
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
      //
      // N.B. this method does nothing to detect or prevent infinite loops or
      // sorcerer's apprentice situations so be careful about what reactions you
      // generate in the various Things.

      // Get the imediate reactions from all the things in the room.
      var reactions = player.room().allThings().flatMap(action::reactions).toList();

      // Add the descriptions of those reactions first.
      text.add(reactions.stream().map(Action::description));

      // Now get all the reactions to those actions, recursively.
      for (var a : reactions) {
        addReactions(text, a, player);
      }
    }
  }

  /**
   * A command that just produces output and does not count as a turn. The
   * Supplier of the output is responsible for formatting the output exactly as
   * it should appear.
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
