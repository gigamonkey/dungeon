package com.gigamonkeys.dungeon;

import java.util.*;
import java.util.stream.*;

/**
 * An action that can occur in a turn, e.g. a monster attacks a player.
 */
public interface Action {
  /**
   * The description of the action happening plus the immediate result. e.g.
   * "You swing your axe and hit, leaving a nasty gash in the Blobbyblob."
   */
  public String description();

  /**
   * Dispatch this action to the appropriate event handler on the Thing.
   */
  public default Stream<Action> event(Thing t) {
    return Stream.empty();
  }

  public static Action attack(Thing monster, Thing weapon) {
    return new PlayerAttack(monster, weapon);
  }

  public static Action none(String description) {
    return () -> description;
  }

  public static Action noWrap(String description) {
    return new Action() {
      public String description() {
        throw new SpecialCommandOutput(description);
      }
    };
  }

  public static Action look(Player p) {
    return () -> p.look();
  }

  public static Action go(Player p, Direction d) {
    return () -> p.go(d);
  }

  public static Action eat(Player p, Thing t) {
    return () -> p.eat(t);
  }

  public static Action take(Player p, List<Thing> things) {
    return () -> p.takeThings(things);
  }

  public static Action drop(Player p, Thing thing) {
    return new Action() {
      public String description() {
        return p.drop(thing);
      }
    };
  }

  //////////////////////////////////////////////////////////////////////////////
  // Concrete actions.

  // TODO: add event() method to all classes as appropriate.

  public static record PlayerAttack(Thing monster, Thing weapon) implements Action {
    public String description() {
      var attack = weapon.attack();
      return attack.description() + " " + attack.result(monster);
    }

    public Stream<Action> event(Thing t) {
      return t.onPlayerAttack(this);
    }
  }
}
