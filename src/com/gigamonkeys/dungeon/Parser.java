package com.gigamonkeys.dungeon;

import java.util.*;
import java.util.function.*;

interface Parser<T> {
  public Parse<T> parse(String text, int position);

  default <U> Parser<U> map(Function<T, U> fn) {
    return (text, position) -> {
      return this.parse(text, position).map(fn);
    };
  }

  static record Parse<T>(String text, int position, boolean ok, T result) {
    <U> Parse<U> map(Function<T, U> fn) {
      return new Parse<>(text(), position(), ok(), fn.apply(result()));
    }
  }

  static Parser<String> token() {
    return (text, position) -> {
      var pos = position;
      while (pos < text.length() && text.charAt(pos) != ' ') {
        pos++;
      }
      if (pos > position) {
        return succeed(text, pos, text.substring(position, pos));
      } else {
        return fail(text, pos);
      }
    };
  }

  static Parser<String> character(Character c) {
    return (text, position) -> {
      if (position < text.length() && text.charAt(position) == c) {
        return succeed(text, position + 1, String.valueOf(c));
      } else {
        return fail(text, position);
      }
    };
  }

  static Parser<Void> whitespace() {
    return plus(character(' ')).map(r -> null);
  }

  static Parser<String> literal(String literal) {
    return (text, position) -> {
      var end = position + literal.length();
      if (text.startsWith(literal, position) && (end == text.length() || text.charAt(end) == ' ')) {
        return succeed(text, end, literal);
      } else {
        return fail(text, position);
      }
    };
  }

  static <U> Parser<List<U>> star(Parser<U> parser) {
    return (text, position) -> {
      List<U> items = new ArrayList<>();
      int newPosition = position;
      while (true) {
        Parse<U> p = parser.parse(text, newPosition);
        if (p.ok()) {
          items.add(p.result);
          newPosition = p.position();
        } else {
          return succeed(text, newPosition, items);
        }
      }
    };
  }

  static <U> Parser<List<U>> plus(Parser<U> parser) {
    return (text, position) -> {
      if (parser.parse(text, position).ok()) {
        List<U> items = new ArrayList<>();
        int newPosition = position;
        while (true) {
          Parse<U> p = parser.parse(text, newPosition);
          if (p.ok()) {
            items.add(p.result);
            newPosition = p.position();
            System.out.println("Moved newPosition to " + newPosition);
          } else {
            return succeed(text, newPosition, items);
          }
        }
      } else {
        return fail(text, position);
      }
    };
  }

  @SafeVarargs
  static <U> Parser<U> or(Parser<U>... parsers) {
    return (text, position) -> {
      for (var parser : parsers) {
        var parse = parser.parse(text, position);
        if (parse.ok()) {
          return parse;
        }
      }
      return fail(text, position);
    };
  }

  @SafeVarargs
  static Parser<List<Object>> sequence(Parser... parsers) {
    return (text, position) -> {
      var result = new ArrayList<Object>();
      var newPosition = position;
      for (var parser : parsers) {
        var parse = parser.parse(text, newPosition);
        if (parse.ok()) {
          System.out.println("Read " + parse.result() + " ending at " + parse.position());
          result.add(parse.result());
          newPosition = parse.position();
        } else {
          System.out.println("Couldn't parse at " + newPosition);
          return fail(text, position);
        }
      }
      return succeed(text, position, result);
    };
  }

  static <U> Parse<U> fail(String text, int position) {
    return new Parse<U>(text, position, false, null);
  }

  static <U> Parse<U> succeed(String text, int position, U result) {
    return new Parse<>(text, position, true, result);
  }
}
