package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Parser.*;

import java.util.*;

class Test {

  public static void main(String[] args) {
    Parser<String> p = or(literal("quux"), literal("foo"));
    Parser<List<Object>> p2 = sequence(literal("foo"), whitespace(), token());
    System.out.println(p.parse("foo bar baz", 0));
    System.out.println(p.parse("foo", 0));
    System.out.println(p2.parse("foo bar", 0));
    System.out.println(p2.parse("foo    bar", 0));
  }
}
