package com.gigamonkeys.dungeon;

public record Attack(int damage, String description) {

  public static final Attack EMPTY = new Attack(0, "");

}
