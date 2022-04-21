package com.gigamonkeys.dungeon;

import java.util.stream.*;
import java.util.*;

class Text {

  interface Element {
    public void child();
    public void text(String text);
    public String toString();
  }

  static class Paragraph implements Element {

    private final List<Sentence> sentences = new ArrayList<>();
    private final int width;
    private Sentence current;

    Paragraph(int width) {
      this.width = width;
      child();
    }

    public void child() {
      if (current == null || !current.isEmpty()) {
        current = new Sentence();
        sentences.add(current);
      }
    }

    public void text(String s) {
      current.text(s);
    }

    public String toString() {
      var s = sentences.stream().map(e -> e.toString()).collect(Collectors.joining(" "));
      return wrap(s, width);
    }
  }

  static class Items implements Element {

    private final List<Item> items = new ArrayList<>();
    private Item current;

    Items() {
      child(); // open the first item.
    }

    public void child() {
      if (current == null || !current.isEmpty()) {
        current = new Item();
        items.add(current);
      }
    }

    public void text(String s) {
      current.text(s);
    }

    public String toString() {
      return items.stream().map(i -> i.toString()).collect(Collectors.joining("\n"));
    }
  }

  static class Sentence {

    protected final StringBuilder text = new StringBuilder();

    public void text(String s) {
      text.append(s);
    }

    public String toString() {
      return text.toString();
    }

    public boolean isEmpty() {
      return text.length() == 0;
    }
  }

  static class Item extends Sentence {
    public String toString() {
      return " - " + text.toString();
    }
  }


  private final List<Element> elements = new ArrayList<>();
  private final int width;

  private Optional<Element> current = Optional.empty();

  public Text(int width) {
    this.width = width;
  }

  public Text paragraph() {
    current = Optional.of(newParagraph());
    return this;
  }

  public Text paragraph(String s) {
    current = Optional.of(newParagraph());
    text(s);
    return this;
  }

  public Text list() {
    current = Optional.of(newList());
    return this;
  }

  public Text sentence() {
    ensureParagraph();
    current.get().child();
    return this;
  }

  public Text sentence(String s) {
    sentence();
    text(s);
    return this;
  }

  public Text item() {
    ensureList();
    current.get().child();
    return this;
  }

  public Text item(String s) {
    item();
    text(s);
    return this;
  }

  public Text text(String s) {
    if (current.isEmpty()) {
      System.out.println("Ensuring a paragraph.");
      ensureParagraph();
    }
    current.get().text(s);
    return this;
  }

  public String toString() {
      return elements.stream().map(i -> i.toString()).collect(Collectors.joining("\n\n"));
  }

  private Paragraph newParagraph() {
    var p = new Paragraph(width);
    elements.add(p);
    return p;
  }

  private Element newList() {
    var p = new Items();
    elements.add(p);
    return p;
  }

  private void ensureParagraph() {
    current = current
      .filter(e -> e instanceof Paragraph)
      .or(() -> Optional.of(newParagraph()));
  }

  private void ensureList() {
    current = current
      .filter(e -> e instanceof Items)
      .or(() -> Optional.of(newList()));
  }



  public static void main(String[] args) {
    var t = new Text(60);
    t.paragraph("This is the first paragraph.")
      .paragraph()
      .sentence().text("The quick brown fox jumps over the lazy dog.")
      .sentence().text("The quick brown fox jumps over the lazy dog.")
      .sentence().text("The quick brown fox jumps over the lazy dog.")
      .sentence().text("The quick brown fox jumps over the lazy dog.")
      .sentence().text("The quick brown fox jumps over the lazy dog.")
      .list()
      .item("Foo bar baz.")
      .item("Foo bar baz.").text(" some extra")
      .item("Foo bar baz.")
      .item("Foo bar baz.")
      .item("Foo bar baz.")
      .item("Foo bar baz.");
    System.out.println(t);

  }



  //////////////////////////////////////////////////////////////////////////////
  // Static helpers

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
}
