package com.gigamonkeys.dungeon;

import java.util.*;

public interface Verbable {
  public Set<String> verbs();

  public String verb(String verb);

  public String name();
}
