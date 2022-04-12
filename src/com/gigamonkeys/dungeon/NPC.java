package com.gigamonkeys.dungeon;

public class NPC implements Describable {

  private Room currentRoom;

  public NPC(Room currentRoom) {
    this.currentRoom = currentRoom;
  }

  public void exitCurrentRoom() {
    currentRoom = null;
  }

  public void enter(Room room) {
    this.currentRoom = room;
  }

  public String description() {
    return "A MONSTER";
  }
}
