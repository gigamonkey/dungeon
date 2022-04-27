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
    Function<Thing, Attack> attack,
    Function<Thing, String> description,
    Function<Thing, String> eat,
    Predicate<Thing> isMonster,
    Predicate<Thing> isPortable,
    BiFunction<Thing, Action.Go, Stream<Action>> onEnter,
    BiFunction<Thing, Action.Take, Stream<Action>> onTake,
    BiFunction<Thing, Action.Turn, Stream<Action>> onTurn
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
  public String eat() {
    return dynamic.eat().apply(this);
  }

  @Override
  public Attack attack() {
    return dynamic.attack().apply(this);
  }

  @Override
  public boolean isMonster() {
    return dynamic.isMonster().test(this);
  }

  @Override
  public Stream<Action> onEnter(Action.Go a) {
    return dynamic.onEnter.apply(this, a);
  }

  @Override
  public Stream<Action> onTake(Action.Take a) {
    return dynamic.onTake.apply(this, a);
  }

  @Override
  public Stream<Action> onTurn(Action.Turn a) {
    return dynamic.onTurn.apply(this, a);
  }
}
