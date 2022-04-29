package com.gigamonkeys.dungeon;

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
}
