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
    BiFunction<Thing, Attack, String> applyAttack,
    Function<Thing, Integer> damage,
    Function<Thing, Attack> attack,
    Function<Thing, String> description,
    Function<Thing, String> describeAlive,
    Function<Thing, String> describeDead,
    Function<Thing, String> eat,
    Predicate<Thing> isEdible,
    Predicate<Thing> isMonster,
    Predicate<Thing> isPortable,
    BiFunction<Thing, Action.Go, Stream<Action>> onEnter,
    BiFunction<Thing, Action.Take, Stream<Action>> onTake,
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
  public String eat() {
    return dynamic.eat().apply(this);
  }

  @Override
  public String applyAttack(Attack attack) {
    return dynamic.applyAttack().apply(this, attack);
  }

  @Override
  public int damage() {
    return dynamic.damage().apply(this);
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
  public Stream<Action> onTurn(Player p) {
    return dynamic.onTurn.apply(this, p);
  }

  //////////////////////////////////////////////////////////////////////////////
  // For protocol methods we have to do this kind of kludgy thing. I can't
  // figure out a value we can set on dynamic that will invoke the default
  // implementation properly.

  @Override
  public String description() {
    if (dynamic.description() == null) {
      return super.description();
    } else {
      return dynamic.description().apply(this);
    }
  }
}
