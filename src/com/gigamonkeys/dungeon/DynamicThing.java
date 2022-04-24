package com.gigamonkeys.dungeon;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * An implementation of the Thing interface that can be put together
 * out of individual dynamic parts. Designed to be constructed with
 * ThingBuilder.
 */
public class DynamicThing extends AbstractThing {

  static record Dynamic(
    BiFunction<Thing, Integer, String> attackWith,
    BiFunction<Thing, Thing, String> weaponizeAgainst,
    Function<Thing, Integer> damage,
    Function<Thing, String> description,
    Function<Thing, String> describeAlive,
    Function<Thing, String> describeDead,
    Function<Thing, String> eat,
    Function<Thing, String> eatIfEdible,
    Function<Thing, String> eatIfInedible,
    Predicate<Thing> isEdible,
    Predicate<Thing> isMonster,
    Predicate<Thing> isPortable,
    BiFunction<Thing, Player, Stream<Action>> onEnter,
    BiFunction<Thing, Player, Stream<Action>> onTurn
  ) {}

  private final Dynamic dynamic;

  public DynamicThing(String name, int hitPoints, Dynamic dynamic) {
    super(name, hitPoints);
    this.dynamic = dynamic;
  }

  ////////////////////////////////////////////////////////////////////
  // Dynamic bits.

  @Override
  public String describeAlive() {
    return dynamic.describeAlive().apply(this);
  }

  @Override
  public String describeDead() {
    return dynamic.describeDead().apply(this);
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
  public Stream<Action> onEnter(Player p) {
    return dynamic.onEnter.apply(this, p);
  }

  @Override
  public Stream<Action> onTurn(Player p) {
    return dynamic.onTurn.apply(this, p);
  }

  //////////////////////////////////////////////////////////////////////////////
  // For protocol methods we have to do this kind of kludgy thing. I can't
  // figure out a value we can set on dynamic that will invoke the default
  // implementation properly.

  @Override
  public String eat() {
    if (dynamic.eat() == null) {
      return super.eat();
    } else {
      return dynamic.eat().apply(this);
    }
  }

  @Override
  public String description() {
    if (dynamic.description() == null) {
      return super.description();
    } else {
      return dynamic.description().apply(this);
    }
  }
}
