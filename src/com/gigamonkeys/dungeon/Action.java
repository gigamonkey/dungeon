package com.gigamonkeys.dungeon;

import java.util.List;
import java.util.stream.Stream;

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
  // Non event generating pseudo actions.

  public static Action none(String description) {
    return () -> description;
  }

  public static Action noWrap(String description) {
    return new Action() {
      public String description() {
        throw new Command.SpecialOutput(description);
      }
    };
  }

  //////////////////////////////////////////////////////////////////////////////
  // Static methods for creataing Actions.

  public static Action attack(Thing monster, Thing weapon) {
    return new PlayerAttack(monster, weapon);
  }

  public static Action look(Player p) {
    return new Look(p);
  }

  public static Action go(Player p, Door door) {
    return new Go(p, door);
  }

  public static Action eat(Player p, Thing t) {
    return new Eat(p, t);
  }

  public static Action take(Player p, List<Thing> things) {
    return new Take(p, things);
  }

  public static Action say(Thing who, String what) {
    return new Say(who, what);
  }

  public static Action drop(Player p, Thing thing) {
    return new Drop(p, thing);
  }

  //////////////////////////////////////////////////////////////////////////////
  // Concrete actions. All actions that generate events need to have a concrete
  // class rather than a lambda.

  public static record Look(Player player) implements Action {
    public String description() {
      return player.look();
    }
    public Stream<Action> event(Thing t) {
      return t.onLook(this);
    }
  }

  public static record Eat(Player player, Thing food) implements Action {
    public String description() {
      return player.eat(food);
    }
    public Stream<Action> event(Thing t) {
      return t.onEat(this);
    }
  }

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

  public static record Drop(Player player, Thing thing) implements Action {
    public String description() {
      return player.drop(thing);
    }

    public Stream<Action> event(Thing t) {
      return t.onDrop(this);
    }
  }

  public static record Say(Thing speaker, String what) implements Action {
    public String description() {
      return "'" + what + "' says the " + speaker.name() + ".";
    }

    public Stream<Action> event(Thing t) {
      return t.onSay(this);
    }
  }

  public static record Turn(Player player) implements Action {
    public String description() {
      return ""; // This is a pseudo action so no description.
    }

    public Stream<Action> event(Thing t) {
      return t.onTurn(this);
    }
  }
}
