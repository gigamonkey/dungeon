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
  // Concrete actions.

  public static record NoAction(String description) implements Action {}

  public static record Quit(Dungeon d) implements Action {
    public String description() {
      d.endGame();
      return "Okay. Bye!";
    }
  }

  public static record Go(Player p, Direction d) implements Action {
    public String description() {
      return p.go(d);
    }
  }

  public static record PlayerAttack(Thing monster, Thing weapon) implements Action {
    public String description() {
      var desc = new ArrayList<String>();
      var attack = weapon.attack();
      desc.add("You " + attack.description() + ".");
      desc.add(attack.result(monster) + ".");
      return String.join(" ", desc);
    }

    public Stream<Action> event(Thing t) {
      return t.onPlayerAttack(this);
    }
  }
}
