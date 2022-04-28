package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Text.*;

import java.util.*;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public class Things {

  static class BaseThing implements Thing, ActualLocation {

    private final Items things = new Items(this);

    private final String name;
    private final String description;
    private final boolean isPortable;
    private final boolean isMonster;

    private int hitPoints;
    private Optional<Location> location = Optional.empty();

    BaseThing(String name, String description, boolean isPortable, boolean isMonster, int hitPoints) {
      this.name = name.toUpperCase();
      this.description = description;
      this.hitPoints = hitPoints;
      this.isPortable = isPortable;
      this.isMonster = isMonster;
    }

    @Override
    public String name() {
      return name;
    }

    @Override
    public String description() {
      return description;
    }

    @Override
    public String describeThings() {
      var desc = new ArrayList<String>();
      things()
        .stream()
        .map(pt -> pt.where() + " the " + name() + " is " + a(pt.thing().description()) + ".")
        .forEach(desc::add);

      things().stream().map(PlacedThing::thing).map(Thing::describeThings).forEach(desc::add);

      return String.join(" ", desc);
    }

    @Override
    public String eat() {
      return "Yuck. You can't eat " + a(description()) + ".";
    }

    @Override
    public boolean isPortable() {
      return isPortable;
    }

    @Override
    public boolean isMonster() {
      return isMonster;
    }

    @Override
    public int hitPoints() {
      return hitPoints;
    }

    @Override
    public void takeDamage(int damage) {
      hitPoints -= damage;
    }

    @Override
    public Attack attack() {
      return new Attack.Useless(a(description()) + " is not an effective weapon.");
    }

    ////////////////////////////////////////////////////////////////////////////
    // Movement

    @Override
    public final Optional<Location> location() {
      return location;
    }

    @Override
    public final void moveTo(Location location) {
      this.location = Optional.of(location);
    }

    @Override
    public final void clearLocation() {
      location = Optional.empty();
    }

    //////////////////////////////////////////////////////////////////////////////
    // Location implementation -- things can contain things.

    @Override
    public void placeThing(Thing thing, String where) {
      things.placeThing(thing, where);
    }

    @Override
    public void removeThing(Thing thing) {
      things.removeThing(thing);
    }

    @Override
    public Optional<Thing> thing(String name) {
      return things.thing(name);
    }

    @Override
    public Collection<PlacedThing> things() {
      return things.things();
    }

    @Override
    public Stream<PlacedThing> allThings() {
      return things.allThings();
    }

    @Override
    public boolean canTake(Thing thing) {
      return !alive();
    }

    //
    //////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////
    // Action events

    @Override
    public Stream<Action> onTurn(Action.Turn a) {
      return Stream.empty();
    }

    @Override
    public Stream<Action> onPlayerAttack(Action.PlayerAttack a) {
      return Stream.empty();
    }

    @Override
    public Stream<Action> onEnter(Action.Go a) {
      return Stream.empty();
    }

    @Override
    public Stream<Action> onTake(Action.Take a) {
      return Stream.empty();
    }

    @Override
    public Stream<Action> onDrop(Action.Drop a) {
      return Stream.empty();
    }

    @Override
    public Stream<Action> onLook(Action.Look a) {
      return Stream.empty();
    }

    @Override
    public Stream<Action> onEat(Action.Eat a) {
      return Stream.empty();
    }

    @Override
    public Stream<Action> onSay(Action.Say a) {
      return Stream.empty();
    }
  }

  static class Monster extends BaseThing {

    private final String deadDescription;

    Monster(String name, String liveDescription, String deadDescription, int hitPoints, boolean isPortable) {
      super(name, liveDescription, isPortable, true, hitPoints);
      this.deadDescription = deadDescription;
    }

    public String description() {
      return alive() ? super.description() : deadDescription;
    }
  }

  static class Furniture extends BaseThing {

    Furniture(String name, String description) {
      super(name, description, false, false, 0);
    }
  }

  static class Weapon extends BaseThing {

    private final Attack attack;

    Weapon(String name, String description, Attack attack) {
      super(name, description, true, false, 0);
      this.attack = attack;
    }

    public Attack attack() {
      return attack;
    }
  }

  static class Food extends BaseThing {

    private final String eat;

    Food(String name, String description, String eat) {
      super(name, description, true, false, 0);
      this.eat = eat;
    }

    public String eat() {
      return destroy(eat);
    }
  }
}
