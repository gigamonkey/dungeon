package com.gigamonkeys.dungeon;

public record Command(String verb, String help, Action action) {
  public static interface Action {
    public String run(String[] args);
  }

  public String run(String[] args) {
    return action.run(args);
  }
}
