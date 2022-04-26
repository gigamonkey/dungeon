package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Text.*;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A builder for DynamicThings. (See https://en.wikipedia.org/wiki/Builder_pattern)
 */
public class ThingBuilder {

  private final String name;

  // N.B. initialHitPoints is only invoked when building the Thing
  // whereas the rest of the functions are installed in the
  // DynamicThing.Dynamic
  private Supplier<Integer> initialHitPoints = () -> 0;

  private BiFunction<Thing, Attack, String> applyAttack = ThingBuilder::defaultApplyAttack;
  private Function<Thing, Attack> attack = ThingBuilder::defaultAttack;
  private Function<Thing, Integer> damage = t -> 0;
  private Function<Thing, String> description = null; // protocol method kludge.
  private Function<Thing, String> describeAlive = t -> t.name();
  private Function<Thing, String> describeDead = t -> t.name();
  private Function<Thing, String> eat = null; // protocol method kludge.
  private Function<Thing, String> eatIfEdible = t -> "Yum";
  private Function<Thing, String> eatIfInedible = t -> "Yuck. You can't eat " + a(t.description()) + ".";
  private Predicate<Thing> isEdible = t -> false;
  private Predicate<Thing> isMonster = t -> false;
  private Predicate<Thing> isPortable = t -> !t.isMonster();
  private BiFunction<Thing, Action.Go, Stream<Action>> onEnter = (t, a) -> Stream.empty();
  private BiFunction<Thing, Action.Take, Stream<Action>> onTake = (t, a) -> Stream.empty();
  private BiFunction<Thing, Player, Stream<Action>> onTurn = (t, p) -> Stream.empty();

  public ThingBuilder(String name) {
    this.name = name;
  }

  ThingBuilder initialHitPoints(Supplier<Integer> initialHitPoints) {
    this.initialHitPoints = initialHitPoints;
    return this;
  }

  ThingBuilder initialHitPoints(int initialHitPoints) {
    return initialHitPoints(() -> initialHitPoints);
  }

  ThingBuilder applyAttack(BiFunction<Thing, Attack, String> applyAttack) {
    this.applyAttack = applyAttack;
    return this;
  }

  ThingBuilder applyAttack(String applyAttack) {
    return applyAttack((t, a) -> applyAttack);
  }

  ThingBuilder attack(Function<Thing, Attack> attack) {
    this.attack = attack;
    return this;
  }

  ThingBuilder attack(Attack attack) {
    return attack(t -> attack);
  }

  ThingBuilder damage(Function<Thing, Integer> damage) {
    this.damage = damage;
    return this;
  }

  ThingBuilder damage(int damage) {
    return damage(t -> damage);
  }

  ThingBuilder description(Function<Thing, String> description) {
    this.description = description;
    return this;
  }

  ThingBuilder description(String description) {
    return description(t -> description);
  }

  ThingBuilder describeAlive(Function<Thing, String> describeAlive) {
    this.describeAlive = describeAlive;
    return this;
  }

  ThingBuilder describeAlive(String describeAlive) {
    return describeAlive(t -> describeAlive);
  }

  ThingBuilder describeDead(Function<Thing, String> describeDead) {
    this.describeDead = describeDead;
    return this;
  }

  ThingBuilder describeDead(String describeDead) {
    return describeDead(t -> describeDead);
  }

  ThingBuilder eat(Function<Thing, String> eat) {
    this.eat = eat;
    return this;
  }

  ThingBuilder eat(String eat) {
    return eat(t -> eat);
  }

  ThingBuilder eatIfEdible(Function<Thing, String> eatIfEdible) {
    this.eatIfEdible = eatIfEdible;
    return this;
  }

  ThingBuilder eatIfEdible(String eatIfEdible) {
    return eatIfEdible(t -> eatIfEdible);
  }

  ThingBuilder eatIfInedible(Function<Thing, String> eatIfInedible) {
    this.eatIfInedible = eatIfInedible;
    return this;
  }

  ThingBuilder eatIfInedible(String eatIfInedible) {
    return eatIfInedible(t -> eatIfInedible);
  }

  ThingBuilder isEdible(Predicate<Thing> isEdible) {
    this.isEdible = isEdible;
    return this;
  }

  ThingBuilder isEdible(boolean isEdible) {
    return isEdible(t -> isEdible);
  }

  ThingBuilder isMonster(Predicate<Thing> isMonster) {
    this.isMonster = isMonster;
    return this;
  }

  ThingBuilder isMonster(boolean isMonster) {
    return isMonster(t -> isMonster);
  }

  ThingBuilder isPortable(Predicate<Thing> isPortable) {
    this.isPortable = isPortable;
    return this;
  }

  ThingBuilder isPortable(boolean isPortable) {
    return isPortable(t -> isPortable);
  }

  ThingBuilder onEnter(BiFunction<Thing, Action.Go, Stream<Action>> onEnter) {
    this.onEnter = onEnter;
    return this;
  }

  ThingBuilder onEnter(Stream<Action> onEnter) {
    return onEnter((t, p) -> onEnter);
  }

  ThingBuilder onTake(BiFunction<Thing, Action.Take, Stream<Action>> onTake) {
    this.onTake = onTake;
    return this;
  }

  ThingBuilder onTake(Stream<Action> onTake) {
    return onTake((t, p) -> onTake);
  }

  ThingBuilder onTurn(BiFunction<Thing, Player, Stream<Action>> onTurn) {
    this.onTurn = onTurn;
    return this;
  }

  ThingBuilder onTurn(Stream<Action> onTurn) {
    return onTurn((t, p) -> onTurn);
  }

  public Thing thing() {
    return new DynamicThing(
      name,
      initialHitPoints.get(),
      new DynamicThing.Dynamic(
        applyAttack,
        damage,
        attack,
        description,
        describeAlive,
        describeDead,
        eat,
        eatIfEdible,
        eatIfInedible,
        isEdible,
        isMonster,
        isPortable,
        onEnter,
        onTake,
        onTurn
      )
    );
  }

  ////////////////////////////////////////////////////////////////////
  // Default implementation of more complex methods.

  private static String defaultApplyAttack(Thing t, Attack attack) {
    if (t.isMonster()) {
      t.takeDamage(attack.damage());
      var s =
        "After " +
        attack.damage() +
        " points of damage, the " +
        t.name() +
        " is " +
        (t.alive() ? "wounded but still alive. And now it's mad." : "dead. Good job, murderer.");
      return s;
    } else {
      return "I don't know why you're attacking an innocent " + t.name() + ".";
    }
  }

  private static Attack defaultAttack(Thing weapon) {
    if (weapon.damage() == 0) {
      return new Attack.Useless(a(weapon.description()) + " is not an effective weapon.");
    } else {
      return new Attack.Simple("You attack with the " + weapon.name() + ".", weapon.damage());
    }
  }
}
