package com.gigamonkeys.dungeon;

interface Target {
  public String applyAttack(Attack attack);

  public String who();
}
