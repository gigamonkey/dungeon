package com.gigamonkeys.dungeon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * The main class for the game.
 */
public class Dungeon {

  private static final Pattern wordPattern = Pattern.compile("\\W*(\\w+)\\W*");

  private final BufferedReader in;
  private final PrintStream out;

  private final Map<String, Command> commands = new HashMap<>();

  private boolean gameOver = false;

  Dungeon(InputStream in, PrintStream out) {
    this.in = new BufferedReader(new InputStreamReader(in));
    this.out = out;
  }

  public void endGame() {
    gameOver = true;
  }

  private void loop(Player player) throws IOException {
    registerCommands(player);
    player.setStart(buildMaze());

    say(player.room().description());
    out.print("> ");
    while (!gameOver) {
      var cmd = in.readLine().toUpperCase();
      say(doCommand(cmd, player));
      if (!player.alive()) {
        say("Ooops. You're dead. Game over.");
        gameOver = true;
      } else {
        out.print("> ");
      }
    }
  }

  private void say(String s) {
    out.println();
    out.println(s.toUpperCase());
    out.println();
  }

  public String doCommand(String line, Player player) {
    var tokens = wordPattern.matcher(line).results().map(r -> r.group(1)).toList().toArray(new String[0]);
    if (tokens.length > 0) {
      return Optional
        .ofNullable(commands.get(tokens[0]))
        .map(c -> c.run(tokens, player))
        .orElse("Don't know how to " + tokens[0] + ".");
    } else {
      // FIXME: This results in too many blank lines being printed. Input loop
      // needs refactoring.
      return "";
    }
  }

  private void registerCommand(Command command) {
    commands.put(command.verb(), command);
  }

  private void registerCommands(Player player) {
    commands.clear();
    var parser = new CommandParser(player, this, commands);
    registerCommand(new Command("ATTACK", "Attack a monster with a weapon.", parser::attack));
    registerCommand(new Command("DROP", "Drop an item you are carrying.", parser::drop));
    registerCommand(new Command("EAT", "Eat an item you are holding or in the room.", parser::eat));
    registerCommand(new Command("GO", "Go in a direction (NORTH, SOUTH, EAST, or WEST).", parser::go));
    registerCommand(new Command("HELP", "Get help on commands.", parser::help));
    registerCommand(new Command("INVENTORY", "List the items you are holding.", parser::inventory));
    registerCommand(new Command("LOOK", "Look at the room your are in again.", parser::look));
    registerCommand(new Command("QUIT", "Quit the game", parser::quit));
    registerCommand(new Command("TAKE", "Take an item from the room.", parser::take));
  }

  private Room buildMaze() {
    var maze = new MazeBuilder();

    maze
      .room("entry", "a dusty entryway to a castle")
      .room("kitchen", "what appears to be a kitchen")
      .room("blobbybloblair", "the lair of a horrible creature")
      .room("dining", "a grand dining room with a crystal chandelier and tapestries on the walls");

    maze
      .toEastOf("entry", "kitchen", "oaken door")
      .toSouthOf("entry", "blobbybloblair", "dank tunnel")
      .toEastOf("kitchen", "dining", "swinging door");

    maze.thing("pedestal").description("stone pedestal").isPortable(false).thing();

    maze.thing("table").description("wooden table").isPortable(false).thing();

    maze.thing("tray").description("tv tray").isPortable(false).thing();

    maze
      .thing("ring")
      .description("ring of great power")
      .attack(
        new Attack.Full(
          "A sphere of light emanates from the ring",
          1000,
          (t -> " blasting the " + t.name() + " to smithereens.")
        )
      )
      .thing();

    maze
      .thing("axe")
      .eat("Axes are not good for eating. Now your teeth hurt and you are no less hungry.")
      .attack(new Attack.Simple("You swing your axe and connect!", 2))
      .thing();

    maze
      .thing("sword")
      .description("broadsword with a rusty iron hilt")
      .eat("What are you, a sword swallower?! You can't eat a sword.")
      .attack(new Attack.Simple("Oof, this sword is heavy but you manage to swing it.", 5))
      .thing();

    maze
      .thing("bread")
      .description("loaf of bread")
      .eat(t -> t.destroy("Ah, delicious. Could use some mayonnaise though."))
      .thing();

    maze
      .thing("sandwich")
      .description("ham and cheese sandwich")
      .eat(t -> t.destroy("Mmmm, tasty. But I think you got a spot of mustard on your tunic."))
      .thing();

    maze
      .thing("blobbyblob")
      .isMonster(true)
      .initialHitPoints(7)
      .description(t ->
        t.alive()
          ? t.name() + ", a gelatenous mass with too many eyes and an odor of jello casserole gone bad"
          : t.hitPoints() > -100
            ? "dead " + t.name() + " decaying into puddle of goo"
            : "a spattering of blobbyblob bits"
      )
      .eat(t ->
        t.alive()
          ? "Are you out of your mind?! This is a live and jiggling " + t.name()
          : t.destroy(
            t.hitPoints() < -100
              ? "The " +
              t.name() +
              " is blasted all over the room. There is nothing to eat unless you have a squeege and a straw."
              : "Ugh. This is worse than the worst jello casserole you have ever tasted. But it does slightly sate your hunger."
          )
      )
      .onTurn((t, a) ->
        streamIf(
          t.alive(),
          new Action.Attack(3, "The " + t.name() + " extrudes a blobby arm and smashes at you!", a.player())
        )
      )
      .thing();

    maze
      .thing("pirate")
      .isMonster(true)
      .initialHitPoints(10)
      .description(t -> t.alive() ? "pirate with a wooden leg and an eye patch" : "dead pirate with his eye patch askew"
      )
      .onEnter((t, a) -> streamIf(t.alive(), Action.say(t, "Arr, matey!")))
      .onTake((t, a) ->
        streamIf(
          t.alive() && t.thing("PARROT").map(p -> a.things().contains(p)).orElse(false),
          Action.say(t, "Oi, ye swarthy dog! Hands off me parrot!")
        )
      )
      .thing();

    maze
      .thing("parrot")
      .isMonster(true)
      .isPortable(true)
      .initialHitPoints(5)
      .description(t -> t.alive() ? "green and blue parrot with a tiny eye patch" : "dead parrot")
      .thing();

    maze
      .place("ring", "on", "pedestal")
      .place("parrot", "on the right shoulder of", "pirate")
      .place("pirate", "in the middle of the room", "dining")
      .place("table", "against the wall", "kitchen")
      .place("bread", "on", "table")
      .place("axe", "on floor", "blobbybloblair")
      .place("blobbyblob", "across from you", "blobbybloblair")
      .place("pedestal", "in the center of the room", "entry")
      .place("tray", "by the door", "entry")
      .place("sandwich", "on", "tray")
      .place("sword", "propped against a wall", "dining");

    return maze.room("entry");
  }

  private static <T> Stream<T> streamIf(boolean test, T v) {
    return Stream.ofNullable(test ? v : null);
  }

  public static void main(String[] args) {
    try {
      new Dungeon(System.in, System.out).loop(new Player(20));
    } catch (IOException ioe) {
      System.out.println("Yikes. Problem reading command: " + ioe);
    }
  }
}
