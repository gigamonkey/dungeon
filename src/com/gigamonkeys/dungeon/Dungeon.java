package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Direction.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.regex.Pattern;

public class Dungeon {

  private static final Pattern WS = Pattern.compile("\\s+");

  private final Player player;
  private boolean gameOver = false;

  Dungeon(Player player) {
    this.player = player;
  }

  private void loop(InputStream in, PrintStream out) {
    try {
      var r = new BufferedReader(new InputStreamReader(in));

      out.println(player.look());

      while (!gameOver()) {
        String[] command = parse(r.readLine());
        out.println(doCommand(command));
      }
    } catch (IOException ioe) {
      out.println("Yikes. Problem reading command: " + ioe);
    }
  }

  public String[] parse(String line) {
    return WS.split(line.toUpperCase());
  }

  public boolean gameOver() {
    return gameOver;
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
    new Dungeon(p).loop(System.in, System.out);
  }
}
