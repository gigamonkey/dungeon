package com.gigamonkeys.dungeon;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Utilities for generating text.
 */
class Text {

  public static final int WRAP = 72;

  public static String a(String thing) {
    var a = "aeiouy".indexOf(Character.toLowerCase(thing.charAt(0))) != -1 ? "an " : "a ";
    return a + thing;
  }

  public static String plural(String s, int n) {
    if (n == 1) {
      return s;
    } else {
      return switch (s.charAt(s.length() - 1)) {
        case 'y' -> s.substring(0, s.length() - 1) + "ies";
        case 'x' -> s.substring(0, s.length() - 1) + "es";
        default -> s + "s";
      };
    }
  }

  public static String isAre(int n) {
    return n == 1 ? " is " : " are ";
  }

  public static String capitalize(String s) {
    return s.substring(0, 1).toUpperCase() + s.substring(1);
  }

  public static String numberOf(int n, String s) {
    return n + " " + plural(s, n);
  }

  public static String commify(List<String> items) {
    return commify(items, ",");
  }

  public static String commify(List<String> items, String comma) {
    return switch (items.size()) {
      case 0 -> "";
      case 1 -> items.get(0);
      case 2 -> items.get(0) + " and " + items.get(1);
      default -> String.join(comma + " ", items.subList(0, items.size() - 1)) + ", and " + items.get(items.size() - 1);
    };
  }

  public static class Wrapped {

    private List<String> items = new ArrayList<>();

    public Wrapped add(Stream<String> ss) {
      ss.forEach(items::add);
      return this;
    }

    public Wrapped add(String s) {
      items.add(s);
      return this;
    }

    public String toString() {
      var sb = new StringBuilder();
      int col = 0;
      for (var text : items) {
        for (var t : text.split("\\s+")) {
          if (col + t.length() > WRAP) {
            sb.append("\n");
            col = 0;
          }
          sb.append(t);
          sb.append(" ");
          col += t.length() + 1;
        }
      }
      return sb.toString().strip();
    }
  }
}
