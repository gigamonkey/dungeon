package com.gigamonkeys.dungeon;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Room implements Describable {

  private final String description;
  private final Point position;
  private final List<Door> doors = new ArrayList<>();
  private final List<Thing> things = new ArrayList<>();
  private final List<NPC> npcs = new ArrayList<>();

  public Room(String description, Point position) {
    this.description = description;
    this.position = position;
  }

  public String description() {
    StringBuilder sb = new StringBuilder();
    sb.append(description);
    describeAll(sb, things);
    describeAll(sb, npcs);
    describeAll(sb, doors);
    return sb.toString();
  }

  public void enter(Player player, UI ui) {
    ui.describe(this);
  }

  public void enter(NPC npc) {
    npc.exitCurrentRoom();
    npc.enter(this);
    npcs.add(npc);
  }

  private void describeAll(StringBuilder sb, Collection<? extends Describable> ds) {
    for (Describable d : ds) {
      sb.append(d.description());
    }
  }

  void addDoor(Door door) {
    doors.add(door);
  }
}
