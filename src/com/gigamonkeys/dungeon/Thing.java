package com.gigamonkeys.dungeon;

import java.util.*;

public abstract class Thing {

  static enum Kind {
    WEAPON,
    MEDICINE,
    FOOD,
  }

  public final Kind kind;

  public Thing(Kind kind) {
    this.kind = kind;
  }

  public abstract String description();

  public void take(Player p) {
    p.addThing(this);
  }
}
