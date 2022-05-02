package com.gigamonkeys.dungeon;

import java.util.*;
import java.util.Optional;
import java.util.function.Function;

/**
 * Infrastructure for parsing commands from strings as tokens.
 */
public record CommandParser(Player player) {
  /**
   * Usual entry point, generates a Parse of the given element of args if it
   * exists and an error parse otherwise.
   */
  public static Parse<String, String[]> arg(String[] args, int idx) {
    return idx < args.length ? new Parse<>(args[idx], args, null) : new Parse<>(null, args, null);
  }

  /**
   * Combine consecutive tokens into a single argument.
   */
  public static Parse<String, String[]> args(String[] args, int start, int end) {
    var s = String.join(" ", Arrays.asList(Arrays.copyOfRange(args, start, end)));
    return !s.equals("") ? new Parse<>(s, args, null) : new Parse<>(null, args, null);
  }

  /**
   * Interface for command arg parsing. Needed since we need to throw an
   * exception.
   */
  @FunctionalInterface
  public static interface Parser {
    public Action parse(String[] args) throws BadCommandException;
  }

  /**
   * Interface for finally converting to an action which is where the
   * BadCommandException is actually thrown. Needed for the case where we chain
   * multiple Parse<> objects via lambdas.
   */
  @FunctionalInterface
  public static interface ToAction<T> {
    public Action actionify(T value) throws BadCommandException;
  }

  public static class BadCommandException extends Exception {

    BadCommandException(String message) {
      super(message);
    }
  }

  public static class Parse<T, U> {

    private final T value;
    private final U previous;
    private final String error;

    public Parse(T value, U previous, String error) {
      this.value = value;
      this.previous = previous;
      this.error = error;
    }

    public Action toAction(ToAction<T> fn) throws BadCommandException {
      if (value != null) {
        return fn.actionify(value);
      } else {
        throw new BadCommandException(error);
      }
    }

    public <X> Parse<X, T> maybe(Function<T, Optional<X>> fn) {
      return value != null
        ? fn.apply(value).map(x -> new Parse<>(x, value, null)).orElse(new Parse<>(null, value, null))
        : new Parse<>(null, null, error);
    }

    public Parse<T, T> expect(T expected) {
      return maybe(v -> Optional.ofNullable(expected.equals(v) ? v : null));
    }

    public Parse<T, U> or(String error) {
      return (value != null || this.error != null) ? this : new Parse<>(null, previous, error);
    }

    public Parse<T, U> or(Function<U, String> error) {
      return (value != null || this.error != null) ? this : new Parse<>(null, previous, error.apply(previous));
    }

    public String toString() {
      return "value: " + value + "; previous: " + previous + "; error: " + error;
    }
  }
}
