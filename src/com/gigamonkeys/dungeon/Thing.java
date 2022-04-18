package com.gigamonkeys.dungeon;

public interface Thing {

  /**
   * The thing's name. How it must be referred to in commands.
   */
  public String name();

  /**
   * Return 'a' or 'an'.
   */
  public String a();

  /**
   * Set the room the thing is in.
   */
  public void setRoom(Room room);

  /**
   * Get the room the thing is in.
   */
  public Room room();

  /**
   * Where is the thing located in the room.
   */
  public String where();

  /**
   * Longer description of the thing used to refer to in in sentences.
   */
  public String description();

  /**
   * Is the thing alive.
   */
  public boolean alive();

  /**
   * Take damage.
   */
  public void takeDamage(int damage);

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

  public int damage();

  public boolean isMonster();

  public Attack attackPlayer();
}
