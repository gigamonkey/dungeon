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
    BiFunction<Thing, Action.Drop, Stream<Action>> onDrop,
    BiFunction<Thing, Action.Eat, Stream<Action>> onEat,
    BiFunction<Thing, Action.Go, Stream<Action>> onEnter,
    BiFunction<Thing, Action.Look, Stream<Action>> onLook,
    BiFunction<Thing, Action.PlayerAttack, Stream<Action>> onPlayerAttack,
    BiFunction<Thing, Action.Say, Stream<Action>> onSay,
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

  @Override
  public Stream<Action> onDrop(Action.Drop a) {
    return dynamic.onDrop.apply(this, a);
  }

  @Override
  public Stream<Action> onEat(Action.Eat a) {
    return dynamic.onEat.apply(this, a);
  }

  @Override
  public Stream<Action> onLook(Action.Look a) {
    return dynamic.onLook.apply(this, a);
  }

  @Override
  public Stream<Action> onPlayerAttack(Action.PlayerAttack a) {
    return dynamic.onPlayerAttack.apply(this, a);
  }

  @Override
  public Stream<Action> onSay(Action.Say a) {
    return dynamic.onSay.apply(this, a);
  }
}
