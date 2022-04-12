package com.gigamonkeys.dungeon;

import java.util.*;

class Test {

  public static void main(String[] args) {
    var p = new Parser.Or<String>(List.of(new Parser.Literal("quux"), new Parser.Literal("foo")));
    System.out.println(p.parse("foo bar baz", 0));
    System.out.println(p.parse("foo", 0));
  }

}
