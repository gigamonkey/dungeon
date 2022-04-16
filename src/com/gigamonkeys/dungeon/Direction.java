package com.gigamonkeys.dungeon;

public enum Direction {
  NORTH,
  EAST,
  SOUTH,
  WEST;

  public Direction opposite() {
    return Direction.class.getEnumConstants()[(ordinal() + 2) % 4];
  }
}
