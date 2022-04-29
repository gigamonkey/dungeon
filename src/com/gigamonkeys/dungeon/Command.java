package com.gigamonkeys.dungeon;

public interface Command {
  /**
   * Run the command.
   */
  public String run(String[] args, Player p);

  public String verb();

  public String help();

  static class SpecialOutput extends RuntimeException {

    final String text;

    public SpecialOutput(String text) {
      this.text = text;
    }
  }
}
