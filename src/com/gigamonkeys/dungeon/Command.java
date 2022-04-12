package com.gigamonkeys.dungeon;

interface Command {
  public String result();

  public static final record UnknownObject(String object) implements Command {
    public String result() {
      return "There is no " + object + "here.";
    }
  }

  public static final record InapplicableVerb(String verb, String object)
    implements Command {
    public String result() {
      return "Don't know how to " + verb + " " + object;
    }
  }

  public static final record UnknownVerb(String verb) implements Command {
    public String result() {
      return "Don't know how to " + verb;
    }
  }
}
