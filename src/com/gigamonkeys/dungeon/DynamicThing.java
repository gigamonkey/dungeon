package com.gigamonkeys.dungeon;

import java.util.function.*;

/**
 * An implementation of the Thing interface that can be put together
 * out of individual dynamic parts. Designed to be constructed with
 * ThingBuilder.
 */
public class DynamicThing extends BaseThing {

  static record Dynamic(
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
  ) {}

  private final Dynamic dynamic;

  public DynamicThing(String name, int hitPoints, Dynamic dynamic) {
    super(name, hitPoints);
    this.dynamic = dynamic;
  }

  ////////////////////////////////////////////////////////////////////
  // Dynamic bits.

  @Override
  public String description() {
    return dynamic.description().apply(this);
  }

  @Override
  public boolean isPortable() {
    return dynamic.isPortable().test(this);
  }

  @Override
  public boolean isEdible() {
    return dynamic.isEdible().test(this);
  }

  @Override
  public String eat() {
    // This feels like a kludge but I can't figure out a value we can set on
    // dynamic that will invoke the default implementation properly.
    if (dynamic.eat() == null) {
      return super.eat();
    } else {
      return dynamic.eat().apply(this);
    }
  }

  @Override
  public String eatIfEdible() {
    return dynamic.eatIfEdible().apply(this);
  }

  @Override
  public String eatIfInedible() {
    return dynamic.eatIfInedible().apply(this);
  }

  @Override
  public String attackWith(int damage) {
    return dynamic.attackWith().apply(this, damage);
  }

  @Override
  public String weaponizeAgainst(Thing monster) {
    return dynamic.weaponizeAgainst().apply(this, monster);
  }

  @Override
  public int damage() {
    return dynamic.damage().apply(this);
  }

  @Override
  public boolean isMonster() {
    return dynamic.isMonster().test(this);
  }

  @Override
  public Attack attackPlayer() {
    return dynamic.attackPlayer().apply(this);
  }
}
