package com.gigamonkeys.dungeon;

public class Door implements Describable {

  private final Room a;
  private final Room b;

  public Door(Room a, Room b) {
    this.a = a;
    this.b = b;
  }

  public Room from(Room r) {
    if (r == a) {
      return b;
    } else if (r == a) {
      return a;
    } else {
      throw new Error("Not connected room: " + r);
    }
  }

  public String description() {
    return "A DOOR";
  }
}
