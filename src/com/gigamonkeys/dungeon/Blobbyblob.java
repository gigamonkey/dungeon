package com.gigamonkeys.dungeon;

public class Blobbyblob extends Monster {

  Blobbyblob(int hitPoints) {
    super("Blobbyblob", hitPoints);
  }

  public String where() {
    return "across from you";
  }

  public String description() {
    if (alive()) {
      return (name() + ", a gelatenous mass with too many eyes and an odor of jello casserole gone bad");
    } else {
      return "dead " + name() + " decaying into puddle of goo";
    }
  }

  public String eat() {
    if (alive()) {
      return "Are you out of your mind?! This is a live and jiggling BlobbyBlob!";
    } else {
      return "Ugh. This is worse than the worst jello casserole you have ever tasted. But it does slightly sate your hunger.";
    }
  }
}
