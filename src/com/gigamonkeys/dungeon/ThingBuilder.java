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
  private Function<Thing, String> eat = null;
  private Function<Thing, String> eatIfEdible = t -> "Yum";
  private Function<Thing, String> eatIfInedible = t -> "Yuck. You can't eat " + t.a() + " " + t.description() + ".";
  private Predicate<Thing> isEdible = t -> false;
  private Predicate<Thing> isMonster = t -> false;
  private Predicate<Thing> isPortable = t -> !t.isMonster();

  ThingBuilder initialHitPoints(Supplier<Integer> initialHitPoints) {
    this.initialHitPoints = initialHitPoints;
    return this;
  }

  ThingBuilder initialHitPoints(int initialHitPoints) {
    return initialHitPoints(() -> initialHitPoints);
  }

  ThingBuilder attackPlayer(Function<Thing, Attack> attackPlayer) {
    this.attackPlayer = attackPlayer;
    return this;
  }

  ThingBuilder attackPlayer(Attack attackPlayer) {
    return attackPlayer(t -> attackPlayer);
  }

  ThingBuilder attackWith(BiFunction<Thing, Integer, String> attackWith) {
    this.attackWith = attackWith;
    return this;
  }

  ThingBuilder attackWith(String attackWith) {
    return attackWith((t, i) -> attackWith);
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

  ThingBuilder weaponizeAgainst(BiFunction<Thing, Thing, String> weaponizeAgainst) {
    this.weaponizeAgainst = weaponizeAgainst;
    return this;
  }

  ThingBuilder weaponizeAgainst(String weaponizeAgainst) {
    return weaponizeAgainst((t1, t2) -> weaponizeAgainst);
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
      eatIfEdible,
      eatIfInedible,
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
