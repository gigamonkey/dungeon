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
  private Function<Thing, Attack> attack = t -> new Attack.Useless(a(t.description()) + " is not an effective weapon.");
  private Function<Thing, String> description = t -> t.name();
  private Function<Thing, String> eat = t -> "Yuck. You can't eat " + a(t.description()) + ".";
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

  ThingBuilder description(Function<Thing, String> description) {
    this.description = description;
    return this;
  }

  ThingBuilder description(String description) {
    return description(t -> description);
  }

  ThingBuilder eat(Function<Thing, String> eat) {
    this.eat = eat;
    return this;
  }

  ThingBuilder eat(String eat) {
    return eat(t -> eat);
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
        attack,
        description,
        eat,
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
      return (
        "After " +
        attack.damage() +
        " points of damage, the " +
        t.name() +
        " is " +
        (t.alive() ? "wounded but still alive. And now it's mad." : "dead. Good job, murderer.")
      );
    } else {
      return "I don't know why you're attacking an innocent " + t.name() + ".";
    }
  }
}
