package com.gigamonkeys.dungeon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;

public class Dungeon {

  private boolean gameOver = false;
  private Room room;
  private Player player;
  private CommandParser parser = new CommandParser();

  private void loop(InputStream in, PrintStream out) {
    try {
      var r = new BufferedReader(new InputStreamReader(in));
      while (!gameOver()) {
        // Tell the player what they see.
        out.println(player.observe());

        // Get the command and run it. The command may generate some
        // output that will be output before the next observation.
        out.println(getCommand(r).run(player));
      }
    } catch (IOException ioe) {
      out.println("Yikes. Problem reading command: " + ioe);
    }
  }

  public boolean gameOver() {
    return gameOver;
  }

  public static void main(String[] args) {
    new Dungeon().loop(System.in, System.out);
  }

  private Command getCommand(BufferedReader r) throws IOException {
    // This should perhaps loop to build a full command, e.g. figure
    // out what thing
    return parser.parse(r.readLine(), room, player);
  }
}
