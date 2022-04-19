package com.gigamonkeys.dungeon;

/**
 * The interface for all the things in the Dungeon other than the
 * Player, Rooms, and Doors. We don't distinguish between different
 * kinds of things by class because ultimately it seems better to
 * actually have to deal with the player applying any verb to any
 * thing.
 */
public interface Thing {
  /**
   * The thing's name. How it must be referred to in commands.
   */
  public String name();

  /**
   * Set the thing's location.
   */
  public void setLocation(Location location);

  /**
   * Get the thing's location.
   */
  public Location location();

  /**
   * Clear the thing's location. (Such as when the thing is eaten or otherwise destroyed.)
   */
  public void clearLocation();

  /**
   * Longer description of the thing used to refer to in in sentences.
   */
  public String description();

  /**
   * Take damage.
   */
  public void takeDamage(int damage);

  /**
   * How many points does the Thing have left.
   */
  public int hitPoints();

  /**
   * Can the thing be taken by the player.
   */
  public boolean isPortable();

  /**
   * Can the thing be eaten by the player.
   */
  public boolean isEdible();

  /**
   * Describe eating the thing.
   */
  public String eat();

  /**
   * Describe an attack on the thing with the given amount of damage.
   */
  public String attackWith(int damage);

  /**
   * Use the thing to attack another thing and return the description.
   */
  public String weaponizeAgainst(Thing monster);

  /**
   * The damage this thing does when used as a weapon.
   */
  public int damage();

  /**
   * Is the thing a monster.
   */
  public boolean isMonster();

  //////////////////////////////////////////////////////////////////////////////
  // Some default implementations. Probably don't need to override these.

  /**
   * Generate an attack against the player.
   */
  public default Attack attackPlayer() {
    return Attack.EMPTY;
  }

  /**
   * Return 'a' or 'an'.
   */
  public default String a() {
    return "AEIOUH".indexOf(name().charAt(0)) != -1 ? "an" : "a";
  }

  /**
   * Is the thing alive?
   */
  public default boolean alive() {
    return hitPoints() > 0;
  }
}
