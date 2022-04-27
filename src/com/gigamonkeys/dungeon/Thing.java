package com.gigamonkeys.dungeon;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * The interface for all the things in the Dungeon other than the
 * Player, Rooms, and Doors. We don't distinguish between different
 * kinds of things by class because ultimately it seems better to
 * actually have to deal with the player applying any verb to any
 * thing.
 */
public interface Thing extends Location {
  /**
   * The thing's name. How it must be referred to in commands.
   */
  public String name();

  /**
   * Set the thing's location.
   */
  public void moveTo(Location location);

  /**
   * Get the thing's location.
   */
  public Optional<Location> location();

  /**
   * Clear the thing's location. (Such as when the thing is eaten or otherwise destroyed.)
   */
  public void clearLocation();

  /**
   * Take damage.
   */
  public void takeDamage(int damage);

  /**
   * How many points does the Thing have left.
   */
  public int hitPoints();

  /**
   * Generate an attack when used as a weapon.
   */
  public Attack attack();

  //////////////////////////////////////////////////////////////////////////////
  // Descriptors -- string renderedings of the thing or consequences of taking
  // certain actions on the thing.

  /**
   * Describe eating the thing.
   */
  public String eat();

  /**
   * Describe the thing itself.
   */
  public String description();

  /**
   * Describe any things located on this thing.
   */
  public String describeThings();

  //////////////////////////////////////////////////////////////////////////////
  // Predicates

  /**
   * Can the thing be taken by the player?
   */
  public boolean isPortable();

  /**
   * Is the thing a monster?
   */
  public boolean isMonster();

  //////////////////////////////////////////////////////////////////////////////
  // Some default implementations. Probably don't need to override these.

  /**
   * Is the thing alive?
   */
  public default boolean alive() {
    return hitPoints() > 0;
  }

  /**
   * Destroy the thing by unlinking it from any location and return a
   * description of the destruction.
   */
  public default String destroy(String s) {
    location().ifPresent(l -> l.removeThing(this));
    return s;
  }

  /**
   * Apply an attack to this thing as a target.
   */
  public default String applyAttack(Attack attack) {
    if (isMonster()) {
      takeDamage(attack.damage());
      return (
        "After " +
        attack.damage() +
        " points of damage, the " +
        name() +
        " is " +
        (alive() ? "wounded but still alive. And now it's mad." : "dead. Good job, murderer.")
      );
    } else {
      return "I don't know why you're attacking an innocent " + name() + ".";
    }
  }

  /**
   * Can this thing be taken from it's current location. It needs to be both
   * inherently portable and the location needs to allow it to be taken.
   */
  public default boolean canBeTaken() {
    return isPortable() && location().map(l -> l.canTake(this)).orElse(false);
  }

  //////////////////////////////////////////////////////////////////////////////
  // Action events

  public Stream<Action> onTurn(Action.Turn a);

  public Stream<Action> onPlayerAttack(Action.PlayerAttack a);

  public Stream<Action> onEnter(Action.Go a);

  public Stream<Action> onTake(Action.Take a);

  public Stream<Action> onDrop(Action.Drop a);

  public Stream<Action> onLook(Action.Look a);

  public Stream<Action> onEat(Action.Eat a);

  public Stream<Action> onSay(Action.Say a);
}
