package com.gigamonkeys.dungeon;

public abstract class Thing implements Describable {

  public String description() {
    return "A THING";
  }

  public abstract String verb(String verb);
}
