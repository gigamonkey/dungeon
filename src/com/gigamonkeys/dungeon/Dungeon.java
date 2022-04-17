package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Direction.*;

import com.gigamonkeys.dungeon.things.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class Dungeon {

  private static final Pattern WS = Pattern.compile("\\s+");

  private final Player player;
  private final BufferedReader in;
  private final PrintStream out;

  private final Map<String, Command> commands = new HashMap<>();

  private boolean gameOver = false;

  Dungeon(Player player, InputStream in, PrintStream out) {
    this.player = player;
    this.in = new BufferedReader(new InputStreamReader(in));
    this.out = out;

    commands.put("QUIT", this::quit);
    commands.put("GO", this::go);
    commands.put("TAKE", this::take);
    commands.put("DROP", this::drop);
    commands.put("LOOK", this::look);
    commands.put("INVENTORY", this::inventory);
    commands.put("EAT", this::eat);
    commands.put("ATTACK", this::attack);
  }

  ////////////////////////////////////////////////////////////////////
  // Commands

  private static interface Command {
    String run(String[] args);
  }

  String quit(String[] args) {
    gameOver = true;
    return "Okay. Bye!";
  }

  String go(String[] args) {
    return direction(args[1])
      .map(d -> player.go(d))
      .orElse("Don't understand direction " + args[1]);
  }

  String take(String[] args) {
    return player
      .room()
      .thing(args[1])
      .map(t -> player.take(t))
      .orElse("There is no " + args[1] + " here.");
  }

  String drop(String[] args) {
    return player.thing(args[1]).map(t -> player.drop(t)).orElse("No " + args[1] + " to drop!");
  }

  String look(String[] args) {
    return player.room().description();
  }

  String inventory(String[] args) {
    return player.inventory();
  }

  String eat(String[] args) {
    return thing(args[1]).map(t -> player.eat(t)).orElse("No " + args[1] + " here to eat.");
  }

  String attack(String[] args) {
    return switch (args.length) {
      case 1 -> "Attack what. And with what?";
      case 2, 3 -> "With what?";
      case 4 -> doAttack(args[1], args[3]);
      default -> "Huh?";
    };
  }

  private String doAttack(String target, String weapon) {
    return thing(target)
      .map(t ->
        thing(weapon)
          .map(w -> w.weaponizeAgainst(t))
          .orElse("No " + weapon + " here to attack with.")
      )
      .orElse("No " + target + " here to attack.");
  }

  // End commands
  ////////////////////////////////////////////////////////////////////

  Optional<Direction> direction(String name) {
    return Direction.fromString(name);
  }

  Optional<Thing> thing(String name) {
    return player.thing(name).or(() -> player.room().thing(name));
  }

  private void loop() throws IOException {
    say(player.room().description());
    while (!gameOver) {
      say(doCommand(in.readLine().toUpperCase()));
    }
  }

  private void say(String s) {
    out.println();
    out.println(new Description(s.toUpperCase(), 60).wrapped());
    out.println();
    out.print("> ");
  }

  public String doCommand(String line) {
    String[] tokens = WS.split(line);
    var c = commands.getOrDefault(tokens[0], args -> "Don't know how to " + args[0]);
    return c.run(tokens);
  }

  public static Room buildMaze() {
    Room entry = new Room("a dusty entryway to a castle.");
    Room kitchen = new Room("what appears to be a kitchen.");
    Room blobbyblobLair = new Room("the lair of a horrible creature.");
    Room diningRoom = new Room(
      "a grand dining room with a crystal chandelier and tapestries on the walls."
    );

    entry.connect("an oaken door", kitchen, EAST);
    entry.connect("a dank tunnel", blobbyblobLair, SOUTH);
    kitchen.connect("swinging door", diningRoom, EAST);

    kitchen.addThing(new Bread());
    blobbyblobLair.addThing(new Axe());
    blobbyblobLair.addThing(new Blobbyblob(3));

    return entry;
  }

  public static void main(String[] args) {
    try {
      Player p = new Player(buildMaze());
      new Dungeon(p, System.in, System.out).loop();
    } catch (IOException ioe) {
      System.out.println("Yikes. Problem reading command: " + ioe);
    }
  }
}
