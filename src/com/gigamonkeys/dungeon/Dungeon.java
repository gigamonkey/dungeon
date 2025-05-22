package com.gigamonkeys.dungeon;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The main class for the game.
 */
public class Dungeon {

  private static final Pattern wordPattern = Pattern.compile("\\W*(\\w+)\\W*");

  private final Player player;
  private final BufferedReader in;
  private final PrintStream out;
  private final Map<String, Command> commands = new HashMap<>();

  private boolean gameOver = false;

  Dungeon(Player player, InputStream in, OutputStream out) {
    this.player = player;
    this.in = new BufferedReader(new InputStreamReader(in));
    this.out = new PrintStream(out);
    registerCommands(player);
  }

  private void loop(boolean printCommands) throws IOException {
    say(player.room().description());

    while (!gameOver) {
      out.print("> ");
      var line = in.readLine();
      if (line == null) break;
      if (printCommands) out.println(line);
      var tokens = tokenize(line.toLowerCase());
      if (tokens.length > 0) {
        say(doCommand(tokens, player));
        if (!player.alive()) {
          say("Ooops. You're dead. Game over.");
          gameOver = true;
        }
      }
    }
  }

  private String[] tokenize(String line) {
    return wordPattern.matcher(line).results().map(r -> r.group(1)).toList().toArray(new String[0]);
  }

  private void say(String s) {
    out.println("\n" + s + "\n");
  }

  public String doCommand(String[] tokens, Player player) {
    return Optional
      .ofNullable(commands.get(tokens[0]))
      .map(c -> c.run(tokens, player))
      .orElse("Don't know how to " + tokens[0] + ".");
  }

  private void registerCommand(Command command) {
    commands.put(command.verb(), command);
  }

  private void registerCommands(Player player) {
    registerCommand(new Command.Turn("ask", "Ask a question.", player::talk));
    registerCommand(new Command.Turn("attack", "Attack a monster with a weapon.", player::attack));
    registerCommand(new Command.Turn("close", "Close something.", player::close));
    registerCommand(new Command.Turn("drop", "Drop an item you are carrying.", player::drop));
    registerCommand(new Command.Turn("eat", "Eat an item you are holding or in the room.", player::eat));
    registerCommand(new Command.Turn("go", "Go in a direction (NORTH, SOUTH, EAST, or WEST).", player::go));
    registerCommand(new Command.Turn("look", "Look at the room your are in again.", player::look));
    registerCommand(new Command.Turn("open", "Open something.", player::open));
    registerCommand(new Command.Turn("put", "Put an object somewhere.", player::put));
    registerCommand(new Command.Turn("say", "Say something.", player::talk));
    registerCommand(new Command.Turn("take", "Take an item from the room.", player::take));
    registerCommand(new Command.NoTurn("help", "Get help on commands.", this::help));
    registerCommand(new Command.NoTurn("inventory", "List the items you are holding.", player::inventory));
    registerCommand(new Command.NoTurn("quit", "Quit the game", this::quit));
  }

  private String quit() {
    gameOver = true;
    return "Okay, bye!";
  }

  private String help() {
    var w = commands.values().stream().mapToInt(c -> c.verb().length()).max().getAsInt();

    var docs = commands
      .values()
      .stream()
      .sorted((a, b) -> a.verb().compareTo(b.verb()))
      .map(c -> {
        var padding = IntStream
          .range(0, (w + 2) - c.verb().length())
          .mapToObj(i -> " ")
          .collect(Collectors.joining(""));
        return "  " + c.verb() + padding + c.help();
      })
      .toList();

    return "I understand the following commands:\n\n" + String.join("\n", docs);
  }

  public static void main(String[] args) {
    try {
      var in = args.length > 0 ? new FileInputStream(args[0]) : System.in;
      var player = new Player(new Maze().build(), 20);
      new Dungeon(player, in, System.out).loop(args.length > 0);
    } catch (IOException ioe) {
      System.out.println("Yikes. Problem reading command: " + ioe);
    }
  }
}
