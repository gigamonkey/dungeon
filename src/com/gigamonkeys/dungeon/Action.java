package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Attack.Target;
import static com.gigamonkeys.dungeon.Text.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * The actions that can happen. Most command will cause an action which will
 * then cause zero or more reactions (which can then theoretically cause yet
 * more reactions.)
 */
public interface Action {
  /**
   * The description of the action happening plus the immediate result. e.g.
   * "You swing your axe and hit, leaving a nasty gash in the Blobbyblob."
   */
  public String description();

  /**
   * Get the reactions to this Action from the given thing. This method should
   * just dispatch to the appropriate on<Action> method in the Thing interface
   * so the Thing can generate its own reactions. I.e. this is the first hop in
   * a double dispatch.
   */
  public Stream<Action> reactions(Thing t);

  //////////////////////////////////////////////////////////////////////////////
  // Concrete actions classes.

  public static record Attack(Target target, Thing weapon) implements Action {
    public String description() {
      var attack = weapon.attack();
      return attack.description() + " " + attack.result(target);
    }

    public Stream<Action> reactions(Thing t) {
      return t.onAttack(this);
    }
  }

  public static record Close(Thing thing) implements Action {
    public String description() {
      return thing.close();
    }

    public Stream<Action> reactions(Thing t) {
      return t.onClose(this);
    }
  }

  public static record Drop(Player player, Thing thing) implements Action {
    public String description() {
      return player.drop(thing);
    }

    public Stream<Action> reactions(Thing t) {
      return t.onDrop(this);
    }
  }

  public static record Eat(Thing food) implements Action {
    public String description() {
      return food.eat();
    }
    public Stream<Action> reactions(Thing t) {
      return t.onEat(this);
    }
  }

  public static record Go(Player player, Door door) implements Action {
    public String description() {
      return player.go(door);
    }

    public Stream<Action> reactions(Thing t) {
      return t.onEnter(this);
    }
  }

  public static record Look(Player player) implements Action {
    public String description() {
      return player.room().description();
    }
    public Stream<Action> reactions(Thing t) {
      return t.onLook(this);
    }
  }

  public static record Move(Thing thing, Location location, String place) implements Action {
    public String description() {
      return thing.moveTo(location, place);
    }

    public Stream<Action> reactions(Thing t) {
      return t.onMove(this);
    }
  }

  public static record Open(Thing thing) implements Action {
    public String description() {
      return thing.open();
    }

    public Stream<Action> reactions(Thing t) {
      return t.onOpen(this);
    }
  }

  public static record Put(Thing thing, Thing location, String place) implements Action {
    public String description() {
      location.placeThing(thing, place);
      return "You put the " + thing.name() + " " + place + " the " + location.name() + ".";
    }
    public Stream<Action> reactions(Thing t) {
      return t.onPut(this);
    }
  }

  public static record Say(Thing speaker, String what) implements Action {
    public String description() {
      return "'" + what + "' says the " + speaker.name() + ".";
    }

    public Stream<Action> reactions(Thing t) {
      return t.onSay(this);
    }
  }

  public static record Take(Player p, List<Thing> things) implements Action {
    public String description() {
      var taken = new ArrayList<String>();
      var notTaken = new ArrayList<String>();
      for (var t : things) {
        if (t.canBeTaken()) {
          p.placeThing(t, "in your stuff");
          taken.add(t.description());
        } else {
          notTaken.add(t.name());
        }
      }
      var desc = new ArrayList<String>();
      if (!taken.isEmpty()) {
        desc.add("Okay, took the " + commify(taken) + ".");
      }
      if (!notTaken.isEmpty()) {
        desc.add("Can't take the " + commify(notTaken) + ".");
      }
      return String.join(" ", desc);
    }

    public Stream<Action> reactions(Thing t) {
      return t.onTake(this);
    }

    public boolean taking(Optional<Thing> t) {
      return t.map(things::contains).orElse(false);
    }
  }

  public static record Turn(Player player) implements Action {
    public String description() {
      return null; // This is a pseudo action so no description.
    }

    public Stream<Action> reactions(Thing t) {
      return t.onTurn(this);
    }
  }
}
