package com.gigamonkeys.dungeon;

public class Things {

  static class Monster extends Thing {

    private final String deadDescription;

    Monster(String name, String liveDescription, String deadDescription, int hitPoints, boolean isPortable) {
      super(name, liveDescription, isPortable, true, hitPoints);
      this.deadDescription = deadDescription;
    }

    public String description() {
      return alive() ? super.description() : deadDescription;
    }
  }

  static class Furniture extends Thing {

    Furniture(String name, String description) {
      super(name, description, false, false, 0);
    }
  }

  static class Weapon extends Thing {

    private final Attack attack;

    Weapon(String name, String description, Attack attack) {
      super(name, description, true, false, 0);
      this.attack = attack;
    }

    public Attack attack() {
      return attack;
    }
  }

  static class Food extends Thing {

    private final String eat;

    Food(String name, String description, String eat) {
      super(name, description, true, false, 0);
      this.eat = eat;
    }

    public String eat() {
      return destroy(eat);
    }
  }
}
