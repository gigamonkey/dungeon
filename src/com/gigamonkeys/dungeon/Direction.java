package com.gigamonkeys.dungeon;

import java.util.Map;
import java.util.Optional;

/**
 * The directions the player can move. Also supports one-letter
 * abbreviations.
 */
public enum Direction {
  NORTH,
  EAST,
  SOUTH,
  WEST;

  private static Map<String, String> abbrevs = Map.of("N", "NORTH", "E", "EAST", "S", "SOUTH", "W", "WEST");

  public Direction opposite() {
    return Direction.class.getEnumConstants()[(ordinal() + 2) % 4];
  }

  public static Optional<Direction> fromString(String name) {
    try {
      if (abbrevs.containsKey(name.toUpperCase())) {
        name = abbrevs.get(name.toUpperCase());
      }
      return Optional.of(Direction.valueOf(name.toUpperCase()));
    } catch (IllegalArgumentException iae) {
      return Optional.empty();
    }
  }
}
