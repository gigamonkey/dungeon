package com.gigamonkeys.dungeon;

interface Command {
  public String result();

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
