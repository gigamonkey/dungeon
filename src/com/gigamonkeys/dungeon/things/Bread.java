package com.gigamonkeys.dungeon.things;

import com.gigamonkeys.dungeon.*;

public class Bread extends Food {

  public Bread() {
    super("BREAD");
  }

  public String a() {
    return "some";
  }

  public String eat() {
    return "Ah, delicious. Could use some mayonnaise though.";
  }
}
