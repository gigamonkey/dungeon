package com.gigamonkeys.dungeon;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A builder for DynamicThings. (See https://en.wikipedia.org/wiki/Builder_pattern)
 */
public class ThingBuilder {

  // N.B. initialHitPoints is only invoked when building the Thing
  // whereas the rest of the functions are installed in the
  // DynamicThing.
  private Supplier<Integer> initialHitPoints = () -> 0;

  private BiFunction<Thing, Integer, String> attackWith = ThingBuilder::defaultAttackWith;
  private BiFunction<Thing, Thing, String> weaponizeAgainst = ThingBuilder::defaultWeaponizeAgainst;
  private Function<Thing, Attack> attackPlayer = t -> Attack.EMPTY;
  private Function<Thing, Integer> damage = t -> 0;
  private Function<Thing, String> description = t -> t.name();
  private Function<Thing, String> eat = t -> t.isEdible() ? "Yum." : "Ouch, you hurt your teeth.";
  private Predicate<Thing> isEdible = t -> false;
  private Predicate<Thing> isMonster = t -> false;
  private Predicate<Thing> isPortable = t -> !t.isMonster();

  ThingBuilder initialHitPoints(Supplier<Integer> initialHitPoints) {
    this.initialHitPoints = initialHitPoints;
    return this;
  }

  ThingBuilder initialHitPoints(int initialHitPoints) {
    this.initialHitPoints = () -> initialHitPoints;
    return this;
  }

  ThingBuilder attackPlayer(Attack attackPlayer) {
    this.attackPlayer = t -> attackPlayer;
    return this;
  }

  ThingBuilder attackPlayer(Function<Thing, Attack> attackPlayer) {
    this.attackPlayer = attackPlayer;
    return this;
  }

  ThingBuilder attackWith(BiFunction<Thing, Integer, String> attackWith) {
    this.attackWith = attackWith;
    return this;
  }

  ThingBuilder attackWith(String attackWith) {
    this.attackWith = (t, i) -> attackWith;
    return this;
  }

  ThingBuilder damage(Function<Thing, Integer> damage) {
    this.damage = damage;
    return this;
  }

  ThingBuilder damage(int damage) {
    this.damage = t -> damage;
    return this;
  }

  ThingBuilder description(Function<Thing, String> description) {
    this.description = description;
    return this;
  }

  ThingBuilder description(String description) {
    this.description = t -> description;
    return this;
  }

  ThingBuilder eat(Function<Thing, String> eat) {
    this.eat = eat;
    return this;
  }

  ThingBuilder eat(String eat) {
    this.eat = t -> eat;
    return this;
  }

  ThingBuilder isEdible(Predicate<Thing> isEdible) {
    this.isEdible = isEdible;
    return this;
  }

  ThingBuilder isEdible(boolean isEdible) {
    this.isEdible = t -> isEdible;
    return this;
  }

  ThingBuilder isMonster(Predicate<Thing> isMonster) {
    this.isMonster = isMonster;
    return this;
  }

  ThingBuilder isMonster(boolean isMonster) {
    this.isMonster = t -> isMonster;
    return this;
  }

  ThingBuilder isPortable(Predicate<Thing> isPortable) {
    this.isPortable = isPortable;
    return this;
  }

  ThingBuilder isPortable(boolean isPortable) {
    this.isPortable = t -> isPortable;
    return this;
  }

  ThingBuilder weaponizeAgainst(BiFunction<Thing, Thing, String> weaponizeAgainst) {
    this.weaponizeAgainst = weaponizeAgainst;
    return this;
  }

  ThingBuilder weaponizeAgainst(String weaponizeAgainst) {
    this.weaponizeAgainst = (t1, t2) -> weaponizeAgainst;
    return this;
  }

  public Thing thing(String name) {
    return new DynamicThing(
      name,
      initialHitPoints.get(),
      attackWith,
      weaponizeAgainst,
      attackPlayer,
      damage,
      description,
      eat,
      isEdible,
      isMonster,
      isPortable
    );
  }

  ////////////////////////////////////////////////////////////////////
  // Default implementation of more complex methods.

  private static String defaultAttackWith(Thing t, int damage) {
    return "I don't know why you're attacking an innocent " + t.name() + ".";
  }

  private static String defaultWeaponizeAgainst(Thing t, Thing m) {
    if (t.damage() == 0) {
      return t.a() + " " + t.description() + " is not an effective weapon. You do zero damage.";
    } else {
      return m.attackWith(t.damage());
    }
  }
}
