package com.gigamonkeys.dungeon;

import java.util.function.*;

public record MetaCommand(String verb, String help, Function<Player, String> fn) implements Command {
  /**
   * Run the command.
   */
  public String run(String[] args, Player p) {
    return fn.apply(p);
  }
}
