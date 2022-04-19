package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Direction.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * The main class for the game.
 */
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

    commands.put("ATTACK", this::attack);
    commands.put("DROP", this::drop);
    commands.put("EAT", this::eat);
    commands.put("GO", this::go);
    commands.put("INVENTORY", this::inventory);
    commands.put("LOOK", this::look);
    commands.put("QUIT", this::quit);
    commands.put("TAKE", this::take);
  }

  ////////////////////////////////////////////////////////////////////
  // Commands

  String quit(String[] args) {
    gameOver = true;
    return "Okay. Bye!";
  }

  String go(String[] args) {
    return arg(args, 1)
      .map(d -> direction(d).map(player::go).orElse("Don't understand direction " + d))
      .orElse("Go where?");
  }

  String take(String[] args) {
    return arg(args, 1)
      .map(name -> player.roomThing(name).map(player::take).orElse("There is no " + name + " here."))
      .orElse("Take what?");
  }

  String drop(String[] args) {
    return arg(args, 1)
      .map(name -> player.thing(name).map(player::drop).orElse("No " + name + " to drop!"))
      .orElse("Drop what?");
  }

  String look(String[] args) {
    return player.room().description();
  }

  String inventory(String[] args) {
    return player.inventory();
  }

  String eat(String[] args) {
    return arg(args, 1)
      .map(name -> player.anyThing(name).map(player::eat).orElse("No " + name + " here to eat."))
      .orElse("Eat what?");
  }

  String attack(String[] args) {
    var i = 1;

    var target = (args.length == 3 && args[i].equals("WITH"))
      ? onlyMonster()
      : arg(args, i++).flatMap(n -> player.roomThing(n));

    var with = arg(args, i++).flatMap(n -> expect("WITH", n));
    var weapon = arg(args, i++).flatMap(n -> player.anyThing(n));

    return target
      .map(t ->
        with
          .map(e -> weapon.map(w -> w.weaponizeAgainst(t)).orElse("Attack with what?"))
          .orElse("Don't understand 'ATTACK' with no 'WITH'.")
      )
      .orElse("Attack what?");
  }

  // End commands
  ////////////////////////////////////////////////////////////////////

  Optional<String> arg(String[] args, int idx) {
    return Optional.of(idx).filter(i -> i < args.length).map(i -> args[i]);
  }

  Optional<Direction> direction(String name) {
    return Direction.fromString(name);
  }

  Optional<String> expect(String expected, String s) {
    return Optional.of(s).filter(v -> expected.equals(v));
  }

  Optional<Thing> onlyMonster() {
    return player.room().onlyMonster();
  }

  private void loop() throws IOException {
    say(player.room().description());
    out.print("> ");
    while (!gameOver) {
      say(doCommand(in.readLine().toUpperCase()));
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
    out.println(wrap(s.toUpperCase(), 60));
    out.println();
  }

  private String wrap(String text, int width) {
    var sb = new StringBuilder();
    int col = 0;
    for (var t : text.split("\\s+")) {
      if (col + t.length() > width) {
        sb.append("\n");
        col = 0;
      }
      sb.append(t);
      sb.append(" ");
      col += t.length();
    }
    return sb.toString();
  }

  public String doCommand(String line) {
    String[] tokens = WS.split(line);
    var c = commands.getOrDefault(tokens[0], args -> "Don't know how to " + args[0]);
    var desc = new ArrayList<String>();
    desc.add(c.run(tokens));
    player.room().describeAttacks(desc, player);
    return String.join(" ", desc);
  }

  public static Room buildMaze() {
    var entry = new Room("a dusty entryway to a castle");
    var kitchen = new Room("what appears to be a kitchen");
    var blobbyblobLair = new Room("the lair of a horrible creature");
    var dining = new Room("a grand dining room with a crystal chandelier and tapestries on the walls");

    entry.connect("an oaken door", kitchen, EAST);
    entry.connect("a dank tunnel", blobbyblobLair, SOUTH);
    kitchen.connect("swinging door", dining, EAST);

    var ring = new ThingBuilder("RING")
      .description("ring of great power")
      .damage(1000)
      .weaponizeAgainst((t, m) ->
        "A blast of light emanates from the ring blasting the " +
        m.name() +
        " to smithereens. " +
        m.attackWith(t.damage())
      )
      .thing();

    var sandwich = new ThingBuilder("SANDWICH")
      .description("ham and cheese sandwich")
      .eat("Mmmm, tasty. But I think you got a spot of mustard on your tunic.")
      .thing();

    var sword = new ThingBuilder("SWORD")
      .description("broadsword with a rusty iron hilt")
      .damage(5)
      .weaponizeAgainst((t, m) -> "Oof. This sword is heavy to swing. But you connect. " + m.attackWith(t.damage()))
      .thing();

    kitchen.placeThing(new Bread(), "on a table");
    blobbyblobLair.placeThing(new Axe(2), "on floor");
    blobbyblobLair.placeThing(new Blobbyblob(3), "across from you");
    entry.placeThing(ring, "floating in mid air");
    entry.placeThing(sandwich, "on small tray by the door");
    dining.placeThing(sword, "propped against a wall");

    return entry;
  }

  public static void main(String[] args) {
    try {
      Player p = new Player(buildMaze(), 10);
      new Dungeon(p, System.in, System.out).loop();
    } catch (IOException ioe) {
      System.out.println("Yikes. Problem reading command: " + ioe);
    }
  }
}
