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

  //////////////////////////////////////////////////////////////////////////////
  // Static methods for creataing Actions.

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

  public static Action go(Player p, Door door) {
    return new Go(p, door);
  }

  public static Action eat(Player p, Thing t) {
    return () -> p.eat(t);
  }

  public static Action take(Player p, List<Thing> things) {
    return new Take(p, things);
  }

  public static Action say(Thing who, String what) {
    return new Say(who, what);
  }

  public static Action drop(Player p, Thing thing) {
    return new Action() {
      public String description() {
        return p.drop(thing);
      }
    };
  }

  //////////////////////////////////////////////////////////////////////////////
  // Concrete actions. All actions that generate events need to have a concrete
  // class rather than a lambda.

  public static record PlayerAttack(Thing monster, Thing weapon) implements Action {
    public String description() {
      var attack = weapon.attack();
      return attack.description() + " " + attack.result(monster);
    }

    public Stream<Action> event(Thing t) {
      return t.onPlayerAttack(this);
    }
  }

  public static record Go(Player player, Door door) implements Action {
    public String description() {
      return player.go(door);
    }

    public Stream<Action> event(Thing t) {
      return t.onEnter(this);
    }
  }

  public static record Attack(int damage, String text, Player p) implements Action {
    public String description() {
      p.takeDamage(damage);
      return text;
    }
  }

  public static record Take(Player p, List<Thing> things) implements Action {
    public String description() {
      return p.takeThings(things);
    }

    public Stream<Action> event(Thing t) {
      return t.onTake(this);
    }
  }

  public static record Say(Thing who, String what) implements Action {
    public String description() {
      return "'" + what + "' says the " + who.name() + ".";
    }
  }
}
