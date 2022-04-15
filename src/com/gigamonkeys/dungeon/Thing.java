package com.gigamonkeys.dungeon;

import java.util.*;

public abstract class Thing implements Describable, Verbable {

  public String description() {
    return "A THING";
  }

  public abstract String verb(String verb);

  public abstract Set<String> verbs();

  public abstract String name();
}
