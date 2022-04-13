package com.gigamonkeys.dungeon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.regex.Pattern;

public class Dungeon {

  // Command loop:
  //
  // - [move|go|walk] direction
  // - verb object -- objects are the object in the room and the player's inventory. object must support the given verb.
  // - verb object WITH thing
  // - PUT object IN object
  // - LOOK
  //
  // Objects must be present and support the given verb. If needs a WITH clause and one not provided, ask for it.
  //

  private static final Pattern WS = Pattern.compile("\\s+");

  private boolean gameOver = false;
  private Room room;
  private Player player;

  private void commandLoop(InputStream in, PrintStream out) {
    try {
      var r = new BufferedReader(new InputStreamReader(in));
      while (!gameOver()) {
        var command = parse(r.readLine());
        out.println(command.result());
      }
    } catch (IOException ioe) {
      out.println("Problem reading command: " + ioe);
    }
  }

  private Command parse(String line) {
    return new Command.NotUnderstood(Arrays.toString(WS.split(line)));
  }

  public boolean gameOver() {
    return gameOver;
  }

  public static void main(String[] args) {
    new Dungeon().commandLoop(System.in, System.out);
  }
}
