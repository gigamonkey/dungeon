package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Direction.*;
import static com.gigamonkeys.dungeon.Text.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

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

  Dungeon(Player player, InputStream in, PrintStream out) {
    this.player = player;
    this.in = new BufferedReader(new InputStreamReader(in));
    this.out = out;

    registerCommand(new Command("ATTACK", "Attack a monster with a weapon.", this::attack));
    registerCommand(new Command("DROP", "Drop a named item you are carrying.", this::drop));
    registerCommand(new Command("EAT", "Eat an item you are holding or in the room.", this::eat));
    registerCommand(new Command("GO", "Go in a direction (NORTH, SOUTH, EAST, or WEST).", this::go));
    registerCommand(new Command("INVENTORY", "List the items you are holding.", this::inventory));
    registerCommand(new Command("LOOK", "Look at the room your are in again.", this::look));
    registerCommand(new Command("QUIT", "Quit the game", this::quit));
    registerCommand(new Command("TAKE", "Take an item from the room.", this::take));
    registerCommand(new Command("HELP", "Get help on commands.", this::help));
  }

  public void registerCommand(Command command) {
    commands.put(command.verb(), command);
  }

  ////////////////////////////////////////////////////////////////////
  // Commands

  String help(String[] args) {
    var desc = new ArrayList<String>();
    for (var c : commands.values()) {
      desc.add(c.verb() + " - " + c.help());
    }
    return String.join("\n", desc);
  }

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
    return arg(args, 1).flatMap(n -> listOfThings(args, 1)).map(player::takeThings).orElse("Take what?");
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

  Optional<List<Thing>> listOfThings(String[] args, int start) {
    var things = new ArrayList<Thing>();
    for (var i = start; i < args.length; i++) {
      var maybe = player.roomThing(args[i]);
      if (!maybe.isPresent()) {
        if (!args[i].equals("AND")) {
          return Optional.empty();
        }
      } else {
        things.add(maybe.get());
      }
    }
    return Optional.of(things);
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

  public String doCommand(String line) {
    var tokens = wordPattern.matcher(line).results().map(r -> r.group(1)).toList().toArray(new String[0]);
    var c = commands.getOrDefault(tokens[0], new Command(tokens[0], "", args -> "Don't know how to " + tokens[0]));
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

    entry.connect("oaken door", kitchen, EAST);
    entry.connect("dank tunnel", blobbyblobLair, SOUTH);
    kitchen.connect("swinging door", dining, EAST);

    var pedestal = new ThingBuilder("PEDESTAL").description("stone pedestal").isPortable(false).thing();

    var table = new ThingBuilder("TABLE").description("wooden table").isPortable(false).thing();

    var tvTray = new ThingBuilder("TRAY").description("tv tray").isPortable(false).thing();

    var ring = new ThingBuilder("RING")
      .description("ring of great power")
      .damage(1000)
      .weaponizeAgainst((t, m) ->
        "A sphere of light emanates from the ring blasting the " +
        m.name() +
        " to smithereens. " +
        m.attackWith(t.damage())
      )
      .thing();

    pedestal.placeThing(ring, "on");

    var axe = new ThingBuilder("AXE")
      .damage(2)
      .eatIfInedible("Axes are not good for eating. Now your teeth hurt and you are no less hungry.")
      .weaponizeAgainst((t, m) -> "You swing your axe and connect! " + m.attackWith(t.damage()))
      .thing();

    var sword = new ThingBuilder("SWORD")
      .description("broadsword with a rusty iron hilt")
      .damage(5)
      .weaponizeAgainst((t, m) -> "Oof, this sword is heavy to swing, but you connect. " + m.attackWith(t.damage()))
      .thing();

    var bread = new ThingBuilder("BREAD")
      .description("loaf of bread")
      .isEdible(true)
      .eatIfEdible("Ah, delicious. Could use some mayonnaise though.")
      .thing();

    var sandwich = new ThingBuilder("SANDWICH")
      .description("ham and cheese sandwich")
      .isEdible(true)
      .eatIfEdible("Mmmm, tasty. But I think you got a spot of mustard on your tunic.")
      .thing();

    var blobbyblob = new ThingBuilder("BLOBBYBLOB")
      .isMonster(true)
      .initialHitPoints(7)
      .damage(3)
      .describeAlive(t -> t.name() + ", a gelatenous mass with too many eyes and an odor of jello casserole gone bad")
      .describeDead(t -> "dead " + t.name() + " decaying into puddle of goo")
      .isEdible(t -> !t.alive())
      .eatIfEdible(t -> {
        if (t.hitPoints() < -100) {
          return (
            "The " +
            t.name() +
            " is blasted all over the room. There is nothing to eat unless you have a squeege and a straw."
          );
        } else {
          return "Ugh. This is worse than the worst jello casserole you have ever tasted. But it does slightly sate your hunger.";
        }
      })
      .eatIfInedible("Are you out of your mind?! This is a live and jiggling BlobbyBlob!")
      .attackPlayer(t -> {
        if (t.alive()) {
          return new Attack(t.damage(), "The " + t.name() + " extrudes a blobby arm and smashes at you!");
        } else {
          return Attack.EMPTY;
        }
      })
      .thing();

    var pirate = new ThingBuilder("PIRATE")
      .isMonster(true)
      .initialHitPoints(10)
      .damage(2)
      .describeAlive("pirate with a wooden leg and and an eye patch")
      .describeDead("dead pirate with his eye patch askew")
      .thing();

    var parrot = new ThingBuilder("PARROT")
      .isMonster(true)
      .initialHitPoints(5)
      .damage(1)
      .describeAlive("green and blue parrot with a tiny eye patch")
      .describeDead("dead parrot")
      .thing();

    pirate.placeThing(parrot, "on the right shoulder of");
    dining.placeThing(pirate, "in the middle of the room");

    kitchen.placeThing(table, "against the wall");
    table.placeThing(bread, "on");
    blobbyblobLair.placeThing(axe, "on floor");
    blobbyblobLair.placeThing(blobbyblob, "across from you");
    entry.placeThing(pedestal, "in the center of the room");
    entry.placeThing(tvTray, "by the door");
    tvTray.placeThing(sandwich, "on");
    dining.placeThing(sword, "propped against a wall");

    return entry;
  }

  public static void main(String[] args) {
    try {
      var p = new Player(buildMaze(), 20);
      new Dungeon(p, System.in, System.out).loop();
    } catch (IOException ioe) {
      System.out.println("Yikes. Problem reading command: " + ioe);
    }
  }
}
