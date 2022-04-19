package com.gigamonkeys.dungeon;

import java.util.List;

class Text {

  public static String wrap(String text, int width) {
    var sb = new StringBuilder();
    int col = 0;
    for (var t : text.split("\\s+")) {
      if (col + t.length() > width) {
        sb.append("\n");
        col = 0;
      }
      sb.append(t);
      sb.append(" ");
      col += t.length();
    }
    return sb.toString();
  }

  public static String a(String thing) {
    var a = "AEIOUY".indexOf(Character.toUpperCase(thing.charAt(0))) != -1 ? "an " : "a ";
    return a + thing;
  }

  public static String commify(List<String> items) {
    return switch (items.size()) {
      case 0 -> "";
      case 1 -> items.get(0);
      case 2 -> items.get(0) + " and " + items.get(1);
      default -> String.join(", ", items.subList(0, items.size() - 1)) + ", and " + items.get(items.size() - 1);
    };
  }
}
