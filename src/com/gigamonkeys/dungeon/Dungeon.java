package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Direction.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.*;
import java.util.regex.Pattern;

public class Dungeon {

  private static interface Command {
    String run(String[] args);
  }

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
  }

  private void loop() {
    try {
      out.println(player.look());

      while (!gameOver) {
        out.println(doCommand(in.readLine().toUpperCase()));
      }
    } catch (IOException ioe) {
      out.println("Yikes. Problem reading command: " + ioe);
    }
  }

  public String doCommand(String line) {
    String[] tokens = WS.split(line);
    var c = commands.getOrDefault(tokens[0], args -> "Don't know how to " + args[0]);
    return c.run(tokens);
  }

  public String[] parse(String line) {
    return WS.split(line.toUpperCase());
  }

  public String doCommand(String[] command) {
    return player.go(EAST);
  }

  public static void main(String[] args) {
    System.out.println(NORTH + " -> " + NORTH.opposite());

    Room r = new Room("The first room");
    Room r2 = new Room("Second room");
    r.connect("Oaken door", r2, EAST);
    Player p = new Player(r);
    new Dungeon(p, System.in, System.out).loop();
  }
}
