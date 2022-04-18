package com.gigamonkeys.dungeon;

/**
 * A door connecting two rooms. For now the description is the same on
 * both sides. Might want to make that more flexible.
 */
public class Door {

  private final String description;
  private final Room a;
  private final Room b;

  public Door(String description, Room a, Room b) {
    this.description = description;
    this.a = a;
    this.b = b;
  }

  public Room from(Room r) {
    if (r == a) {
      return b;
    } else if (r == b) {
      return a;
    } else {
      throw new Error("Not connected room: " + r);
    }
  }

  public String description() {
    return description;
  }
}
