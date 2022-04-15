package com.gigamonkeys.dungeon;

import java.util.*;
import java.util.Arrays;
import java.util.regex.Pattern;

// Form of commands:
//
// - GO direction
// - EXAMINE object (supported by all objects)
// - verb object
// - verb object WITH thing
//
// Objects must be present and support the given verb. If needs a WITH clause and one not provided, ask for it.
//
// Commands can also be partial. E.g. just a verb. Command loop should
// then ask for an object and parse the next answer (excepting certain
// commands like INVENTORY) as the name of the object and then execute
// the full command. Likewise for verbs that require a WITH clause,
// e.g. ATTACK MONSTER. If there is only one object present supporting
// a verb (e.g. EAT) it can be implicit.
//

public interface Command {
  public default String run(Player player) {
    return result();
  }

  /**
   * Return the text to display.
   */
  public String result();

  /**
   * Is the command ready to execute or does it need more information.
   */
  public default boolean isComplete() {
    return true;
  }

  /**
   * Prompt for information needed to complete the command.
   */
  public default String promptForRest() {
    return "";
  }

  /**
   * Provide the info needed to complete the command.
   */
  public default void finish(String rest) {}

  public static record JustVerb(String verb) implements Command {
    public String result() {
      return "Verbing: " + verb;
    }
  }

  public static record PickObject(String verb, Thing[] things) implements Command {
    public String result() {
      return "Need to pick object from " + Arrays.toString(things);
    }
  }

  public static record VerbObject(String verb, Thing object) implements Command {
    public String result() {
      return "Verb: " + verb + "; object: " + object.name();
    }
  }

  public static record VerbObjectWith(String verb, String object, String with) implements Command {
    public String result() {
      return "Verb: " + verb + "; object: " + object + "; with: " + with;
    }
  }

  public static record NotUnderstood(String line) implements Command {
    public String result() {
      return "Don't understand " + line;
    }
  }

  public static record VerbObjectCommand(String verb, Thing object) implements Command {
    public String result() {
      return object.verb(verb);
    }
  }

  public static record UnknownObject(String object) implements Command {
    public String result() {
      return "There is no " + object + "here.";
    }
  }

  public static record InapplicableVerb(String verb, String object) implements Command {
    public String result() {
      return "Don't know how to " + verb + " " + object;
    }
  }

  public static record UnknownVerb(String verb) implements Command {
    public String result() {
      return "Don't know how to " + verb;
    }
  }
}
