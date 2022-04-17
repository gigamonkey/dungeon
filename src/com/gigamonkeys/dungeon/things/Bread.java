package com.gigamonkeys.dungeon.things;

import com.gigamonkeys.dungeon.*;

public class Bread extends Thing {

  public Bread() {
    super("BREAD");
  }

  public String beEatenBy(Player p) {
    return "Ah, delicious. Could use some mayonnaise though.";
  }
}
