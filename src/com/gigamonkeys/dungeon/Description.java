package com.gigamonkeys.dungeon;

class Description {

  private final String text;
  private final int width;

  public Description(String text, int width) {
    this.text = text;
    this.width = width;
  }

  public String wrapped() {
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
}
