package com.gigamonkeys.dungeon.commands;

import com.gigamonkeys.dungeon.*;

record Move(Direction direction) implements Command {
  public String run(Player player) {
    var door = player.room().getDoor(direction);
    if (door == null) {
      return "Can't move " + direction;
    } else {
      player.goThrough(door);
      return player.room().description();
    }
  }

  public String result() {
    return "Huh.";
  }
}
