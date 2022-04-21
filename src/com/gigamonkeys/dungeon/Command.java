package com.gigamonkeys.dungeon;

import java.util.function.Function;

public record Command(String verb, String help, Function<String[], String> action) {

  public String run(String[] args) {
    return action.apply(args);
  }
}
