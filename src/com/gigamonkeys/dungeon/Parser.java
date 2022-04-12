package com.gigamonkeys.dungeon;

import java.util.*;

interface Parser<T> {
  public Parse<T> parse(String text, int position);

  static record Parse<T>(String text, int position, boolean ok, T result) {}

  static Parser<String> token() {
    return new Parser<>() {
      public Parse<String> parse(String text, int position) {
        var pos = position;
        while (pos < text.length() && text.charAt(pos) != ' ') {
          pos++;
        }
        if (pos > position) {
          return succeed(text, pos, text.substring(position, pos));
        } else {
          return fail(text, pos);
        }
      }
    };
  }

  static Parser<String> literal(String literal) {
    return new Parser<>() {
      public Parse<String> parse(String text, int position) {
        var end = position + literal.length();
        if (text.startsWith(literal, position) && (end == text.length() || text.charAt(end) == ' ')) {
          return succeed(text, end, literal);
        } else {
          return fail(text, position);
        }
      }
    };
  }


  static <U> Parser<List<U>> star(Parser<U> parser) {
    return new Parser<>() {
      public Parse<List<U>> parse(String text, int position) {
        List<U> items = new ArrayList<>();
        int newPosition = position;
        while (true) {
          Parse<U> p = parser.parse(text, newPosition);
          if (p.ok()) {
            items.add(p.result);
            newPosition = p.position();
          } else {
            return succeed(text, position, items);
          }
        }
      }
    };
  }

  static <U> Parser<List<U>> plus(Parser<U> parser) {
    return new Parser<>() {
      public Parse<List<U>> parse(String text, int position) {
        if (parser.parse(text, position).ok()) {
          List<U> items = new ArrayList<>();
          while (true) {
            Parse<U> p = parser.parse(text, position);
            if (p.ok()) {
              items.add(p.result);
            } else {
              return succeed(text, position, items);
            }
          }
        } else {
          return fail(text, position);
        }
      }
    };
  }

  @SafeVarargs
  static <U> Parser<U> or(Parser<U>... parsers) {
    return new Parser<>() {
      public Parse<U> parse(String text, int position) {
        for (var parser : parsers) {
          var parse = parser.parse(text, position);
          if (parse.ok()) {
            return parse;
          }
        }
        return fail(text, position);
      }
    };
  }

  static <U> Parse<U> fail(String text, int position) {
    return new Parse<U>(text, position, false, null);
  }

  static <U> Parse<U> succeed(String text, int position, U result) {
    return new Parse<>(text, position, true, result);
  }
}
