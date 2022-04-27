package com.gigamonkeys.dungeon;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

class ThingDescriber {

  private static record Describer(Predicate<Thing> p, Function<Thing, String> fn) {}

  List<Describer> describers = new ArrayList<>();

  public ThingDescriber add(Predicate<Thing> p, Function<Thing, String> fn) {
    describers.add(new Describer(p, fn));
    return this;
  }

  public Function<Thing, String> describer() {
    return t -> sample(applicable(t)).fn.apply(t);
  }

  private Stream<Describer> applicable(Thing t) {
    return describers.stream().filter(d -> d.p.test(t));
  }

  private static <T> T sample(Stream<T> stream) {
    var sampler = new BinaryOperator<T>() {
      int n = 1;

      public T apply(T t1, T t2) {
        return ((int) (Math.random() * n++) < 1) ? t2 : t1;
      }
    };

    return stream.reduce(null, sampler);
  }
}
