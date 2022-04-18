package com.gigamonkeys.dungeon;

public interface Thing {
  public String a();

  public void setRoom(Room room);

  public Room room();

  public String name();

  public String where();

  public String description();

  public boolean alive();

  public void takeDamage(int damage);

  public boolean isPortable();

  public boolean isEdible();

  public String eat();

  public String attackWith(int damage);

  public String weaponizeAgainst(Thing monster);

  public int damage();

  public boolean isMonster();

  public Attack attackPlayer();
}
