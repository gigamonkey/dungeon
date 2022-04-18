package com.gigamonkeys.dungeon;

import java.util.function.*;

/**
 * An implementation of the Thing interface that can be put together
 * out of individual dynamic parts. Designed to be constructed with
 * ThingBuilder.
 */
public class DynamicThing implements Thing {

  private final String name;

  private int hitPoints;
  private Room room;

  private final BiFunction<Thing, Integer, String> attackWith;
  private final BiFunction<Thing, Thing, String> weaponizeAgainst;
  private final Function<Thing, Attack> attackPlayer;
  private final Function<Thing, Integer> damage;
  private final Function<Thing, String> description;
  private final Function<Thing, String> eat;
  private final Function<Thing, String> where;
  private final Predicate<Thing> isEdible;
  private final Predicate<Thing> isMonster;
  private final Predicate<Thing> isPortable;

  public DynamicThing(
    String name,
    int hitPoints,
    BiFunction<Thing, Integer, String> attackWith,
    BiFunction<Thing, Thing, String> weaponizeAgainst,
    Function<Thing, Attack> attackPlayer,
    Function<Thing, Integer> damage,
    Function<Thing, String> description,
    Function<Thing, String> eat,
    Function<Thing, String> where,
    Predicate<Thing> isEdible,
    Predicate<Thing> isMonster,
    Predicate<Thing> isPortable
  ) {
    this.name = name;
    this.hitPoints = hitPoints;
    this.description = description;
    this.damage = damage;
    this.where = where;
    this.isPortable = isPortable;
    this.isEdible = isEdible;
    this.eat = eat;
    this.weaponizeAgainst = weaponizeAgainst;
    this.attackWith = attackWith;
    this.isMonster = isMonster;
    this.attackPlayer = attackPlayer;
  }

  public String name() {
    return name;
  }

  public Room room() {
    return room;
  }

  public void setRoom(Room room) {
    this.room = room;
  }

  public String a() {
    return "AEIOUH".indexOf(name.charAt(0)) != -1 ? "an" : "a";
  }

  public boolean alive() {
    return hitPoints > 0;
  }

  public void takeDamage(int damage) {
    hitPoints -= damage;
  }

  public int hitPoints() {
    return hitPoints;
  }

  ////////////////////////////////////////////////////////////////////
  // Dynamic bits.

  public String description() {
    return description.apply(this);
  }

  public String where() {
    return where.apply(this);
  }

  public boolean isPortable() {
    return isPortable.test(this);
  }

  public boolean isEdible() {
    return isEdible.test(this);
  }

  public String eat() {
    return eat.apply(this);
  }

  public String attackWith(int damage) {
    return attackWith.apply(this, damage);
  }

  public String weaponizeAgainst(Thing monster) {
    return weaponizeAgainst.apply(this, monster);
  }

  public int damage() {
    return damage.apply(this);
  }

  public boolean isMonster() {
    return isMonster.test(this);
  }

  public Attack attackPlayer() {
    return attackPlayer.apply(this);
  }
}
