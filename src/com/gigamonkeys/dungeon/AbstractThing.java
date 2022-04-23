package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Text.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * An abstract implementation of Thing with the bits that shouldn't change. At
 * the moment the only concrete implementation is DynamicThing so we could
 * collapse them.
 */
public abstract class AbstractThing implements Thing {

  private final Things things = new Things();
  private final String name;
  private int hitPoints;
  private Optional<Location> location = Optional.empty();

  public AbstractThing(String name, int hitPoints) {
    this.name = name.toUpperCase();
    this.hitPoints = hitPoints;
  }

  @Override
  public final String name() {
    return name;
  }

  @Override
  public final Optional<Location> location() {
    return location;
  }

  @Override
  public final void setLocation(Location location) {
    this.location = Optional.of(location);
  }

  @Override
  public final void clearLocation() {
    location = Optional.empty();
  }

  @Override
  public final void takeDamage(int damage) {
    hitPoints -= damage;
  }

  @Override
  public final int hitPoints() {
    return hitPoints;
  }

  @Override
  public String describeThings() {
    var desc = new ArrayList<String>();
    for (var pt : things()) {
      var t = pt.thing();
      desc.add(pt.where() + " the " + name() + " is " + a(t.description()) + ".");
    }
    for (var pt : things()) {
      desc.add(pt.thing().describeThings());
    }
    return String.join(" ", desc);
  }

  //////////////////////////////////////////////////////////////////////////////
  // Location implementation -- things can contain things.

  public void placeThing(Thing thing, String where) {
    things.placeThing(thing, where);
  }

  public void removeThing(Thing thing) {
    things.removeThing(thing);
  }

  public Optional<Thing> thing(String name) {
    return things.thing(name);
  }

  public Collection<PlacedThing> things() {
    return things.things();
  }

  public Stream<PlacedThing> allThings() {
    return things.allThings();
  }
  //
  //////////////////////////////////////////////////////////////////////////////

}
