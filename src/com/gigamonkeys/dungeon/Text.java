package com.gigamonkeys.dungeon;

class Text {

  public static String a(String thing) {
    var a = "AEIOUY".indexOf(Character.toUpperCase(thing.charAt(0))) != -1 ? "an " : "a ";
    return a + thing;
  }
}
