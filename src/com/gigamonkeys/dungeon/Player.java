package com.gigamonkeys.dungeon;

import java.util.*;

public class Player implements Verbable {

  private final List<Thing> things = new ArrayList<>();
  private final Set<String> verbs = Set.of("INVENTORY");

  private Room room;

  public List<Thing> things() {
    return things;
  }

  public String name() {
    return "PLAYER";
  }

  public Set<String> verbs() {
    return verbs;
  }

  public Room room() {
    return room;
  }

  public void goThrough(Door door) {
    room = door.from(room);
  }

  public String verb(String verb) {
    // TODO: list things.
    return "You have " + things.size() + " things in your bag.";
  }

  public String observe() {
    return "Can't see anything!"; // FIXME: implement
  }
}
