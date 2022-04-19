package com.gigamonkeys.dungeon;

/**
 * Specific class representing a specific kind of montser. This could
 * pretty easily be replaced with a DynamicThing.
 */
public class Blobbyblob extends BaseThing {

  Blobbyblob(int hitPoints) {
    super("Blobbyblob", 2, hitPoints);
  }

  @Override
  public boolean isMonster() {
    return true;
  }

  @Override
  public boolean isEdible() {
    return !alive();
  }

  @Override
  public String attackWith(int damage) {
    takeDamage(damage);
    if (alive()) {
      return "The " + name() + " is wounded but still alive. And now it's mad.";
    } else {
      return "The " + name() + " is dead. Good job, murderer.";
    }
  }

  @Override
  public String description() {
    if (alive()) {
      return (name() + ", a gelatenous mass with too many eyes and an odor of jello casserole gone bad");
    } else {
      return "dead " + name() + " decaying into puddle of goo";
    }
  }

  @Override
  public String eatIfEdible() {
    if (hitPoints() < -100) {
      return (
        "The " +
        name() +
        " is blasted all over the room. There is nothing to eat unless you have a squeege and a straw."
      );
    } else {
      return "Ugh. This is worse than the worst jello casserole you have ever tasted. But it does slightly sate your hunger.";
    }
  }

  @Override
  public String eatIfInedible() {
    return "Are you out of your mind?! This is a live and jiggling BlobbyBlob!";
  }

  @Override
  public Attack attackPlayer() {
    if (alive()) {
      return new Attack(damage(), "The " + name() + " extrudes a blobby arm and smashes at you!");
    } else {
      return Attack.EMPTY;
    }
  }
}
