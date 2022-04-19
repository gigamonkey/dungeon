package com.gigamonkeys.dungeon;

import java.util.function.*;

/**
 * An implementation of the Thing interface that can be put together
 * out of individual dynamic parts. Designed to be constructed with
 * ThingBuilder.
 */
public class DynamicThing extends BaseThing {

  private final BiFunction<Thing, Integer, String> attackWith;
  private final BiFunction<Thing, Thing, String> weaponizeAgainst;
  private final Function<Thing, Attack> attackPlayer;
  private final Function<Thing, Integer> damage;
  private final Function<Thing, String> description;
  private final Function<Thing, String> eat;
  private final Function<Thing, String> eatIfEdible;
  private final Function<Thing, String> eatIfInedible;
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
    Function<Thing, String> eatIfEdible,
    Function<Thing, String> eatIfInedible,
    Predicate<Thing> isEdible,
    Predicate<Thing> isMonster,
    Predicate<Thing> isPortable
  ) {
    super(name, hitPoints);
    this.description = description;
    this.damage = damage;
    this.isPortable = isPortable;
    this.isEdible = isEdible;
    this.eat = eat;
    this.eatIfEdible = eatIfEdible;
    this.eatIfInedible = eatIfInedible;
    this.weaponizeAgainst = weaponizeAgainst;
    this.attackWith = attackWith;
    this.isMonster = isMonster;
    this.attackPlayer = attackPlayer;
  }

  ////////////////////////////////////////////////////////////////////
  // Dynamic bits.

  @Override
  public String description() {
    return description.apply(this);
  }

  @Override
  public boolean isPortable() {
    return isPortable.test(this);
  }

  @Override
  public boolean isEdible() {
    return isEdible.test(this);
  }

  @Override
  public String eat() {
    return eat.apply(this);
  }

  @Override
  public String eatIfEdible() {
    return eatIfEdible.apply(this);
  }

  @Override
  public String eatIfInedible() {
    return eatIfInedible.apply(this);
  }

  @Override
  public String attackWith(int damage) {
    return attackWith.apply(this, damage);
  }

  @Override
  public String weaponizeAgainst(Thing monster) {
    return weaponizeAgainst.apply(this, monster);
  }

  @Override
  public int damage() {
    return damage.apply(this);
  }

  @Override
  public boolean isMonster() {
    return isMonster.test(this);
  }

  @Override
  public Attack attackPlayer() {
    return attackPlayer.apply(this);
  }
}
