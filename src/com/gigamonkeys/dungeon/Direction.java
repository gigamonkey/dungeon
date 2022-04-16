package com.gigamonkeys.dungeon;

import java.util.Optional;

public enum Direction {
  NORTH,
  EAST,
  SOUTH,
  WEST;

  public Direction opposite() {
    return Direction.class.getEnumConstants()[(ordinal() + 2) % 4];
  }

  public static Optional<Direction> fromString(String name) {
    try {
      return Optional.of(Direction.valueOf(name.toUpperCase()));
    } catch (IllegalArgumentException iae) {
      return Optional.empty();
    }
  }
}
