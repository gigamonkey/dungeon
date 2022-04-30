package com.gigamonkeys.dungeon;

import java.util.function.*;

/**
 * A command that just produces output and does not count as a turn.
 */
public record MetaCommand(String verb, String help, Supplier<String> fn) implements Command {
  /**
   * Run the command.
   */
  public String run(String[] args, Player p) {
    return fn.get();
  }
}
