package com.gigamonkeys.dungeon;

public class Blobbyblob extends Thing {

  private int hitPoints;

  Blobbyblob(int hitPoints) {
    super("Blobbyblob");
    this.hitPoints = hitPoints;
  }

  public String where() {
    return "across from you is";
  }

  public String description() {
    if (alive()) {
      return "a BlobbyBlob, a gelatenous mass with too many eyes and an odor of jello casserole gone bad";
    } else {
      return "a dead BlobbyBlob decaying into puddle of goo";
    }
  }

  public String beEatenBy(Player p) {
    if (alive()) {
      return "Are you out of your mind?! This is a live and jiggling BlobbyBlob!";
    } else {
      room().removeMonster(this);
      return "Ugh. This is worse than the worst jello casserole you have ever tasted. But it slightly sates your hunger.";
    }
  }

  public String attackWith(Thing weapon) {
    hitPoints -= weapon.damage();
    if (alive()) {
      return "The " + name() + " is wounded but still alive. And now it's mad.";
    } else {
      return "The " + name() + " is dead. Murderer.";
    }
  }

  public boolean alive() {
    return hitPoints > 0;
  }
}
