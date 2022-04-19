package com.gigamonkeys.dungeon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * An abstract implementation of Thing with the bits that shouldn't change.
 */
public abstract class AbstractThing implements Thing {

  private final Things things = new Things();
  private final String name;
  private int hitPoints;
  private Location location = null;

  public AbstractThing(String name, int hitPoints) {
    this.name = name.toUpperCase();
    this.hitPoints = hitPoints;
  }

  @Override
  public final String name() {
    return name;
  }

  @Override
  public final Location location() {
    return location;
  }

  @Override
  public final void setLocation(Location location) {
    this.location = location;
  }

  @Override
  public final void clearLocation() {
    location = null;
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
      desc.add(pt.where() + " the " + description() + " is " + t.a() + " " + t.description() + ".");
    }
    for (var pt : things()) {
      desc.add(pt.thing().describeThings());
    }
    return String.join(" ", desc);
  }

  //////////////////////////////////////////////////////////////////////////////
  // Location implementation

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
  //
  //////////////////////////////////////////////////////////////////////////////

}
