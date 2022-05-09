package com.gigamonkeys.dungeon;

import static com.gigamonkeys.dungeon.Direction.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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

  Dungeon(InputStream in, OutputStream out) {
    this.in = new BufferedReader(new InputStreamReader(in));
    this.out = new PrintStream(out);
  }

  private void loop(boolean printCommands) throws IOException {
    var player = new Player(buildMaze(), 20);
    registerCommands(player);

    say(player.room().description());
    while (!gameOver) {
      out.print("> ");
      var line = in.readLine();
      if (line == null) break;
      if (printCommands) out.println(line);
      var tokens = parseLine(line.toLowerCase());
      if (tokens.length > 0) {
        say(doCommand(tokens, player));
        if (!player.alive()) {
          say("Ooops. You're dead. Game over.");
          gameOver = true;
        }
      }
    }
  }

  private String[] parseLine(String line) {
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
    commands.clear();
    registerCommand(new Command.Turn("attack", "Attack a monster with a weapon.", player::attack));
    registerCommand(new Command.Turn("close", "Close something.", player::close));
    registerCommand(new Command.Turn("drop", "Drop an item you are carrying.", player::drop));
    registerCommand(new Command.Turn("eat", "Eat an item you are holding or in the room.", player::eat));
    registerCommand(new Command.Turn("go", "Go in a direction (NORTH, SOUTH, EAST, or WEST).", player::go));
    registerCommand(new Command.Turn("look", "Look at the room your are in again.", player::look));
    registerCommand(new Command.Turn("open", "Open something.", player::open));
    registerCommand(new Command.Turn("put", "Put an object somewhere.", player::put));
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

  private Room buildMaze() {
    // Rooms
    var entry = new Room("a dusty entryway to a castle");
    var kitchen = new Room("what appears to be a kitchen");
    var blobbyblobLair = new Room("the lair of a horrible creature");
    var dining = new Room("a grand dining room with a crystal chandelier and tapestries on the walls");
    var storeroom = new Room("a storeroom");

    // Doors
    entry.connect("oaken door", kitchen, EAST);
    entry.connect("dank tunnel", blobbyblobLair, SOUTH);
    kitchen.connect("swinging door", dining, EAST);
    kitchen.connect("wooden door", storeroom, SOUTH);

    // Furniture
    var pedestal = new Thing.Furniture("pedestal", "stone pedestal");
    var table = new Thing.Furniture("table", "wooden table");
    var tray = new Thing.Furniture("tray", "TV tray");

    var treasureChest = new Thing.Furniture("chest", "wooden treasure chest") {
      boolean open = false;

      @Override
      public boolean isMonster() {
        var b = super.isMonster();
        return b;
      }

      @Override
      public String description() {
        return descriptor() + " " + super.description();
      }

      private String descriptor() {
        return open ? (things().isEmpty() ? "empty" : "open") : "closed";
      }

      @Override
      public String describeThings() {
        return open ? super.describeThings() : "";
      }

      public String open() {
        if (!open) {
          open = true;
          return "The chest lid opens with a creak. " + describeThings();
        } else {
          return "The " + name() + " is already open.";
        }
      }

      public String close() {
        if (open) {
          open = false;
          return "The chest snaps shut.";
        } else {
          return "The " + name() + " is already closed.";
        }
      }

      public List<String> places() {
        return List.of("inside");
      }
    };

    var jeweledDagger = new Thing.Weapon("dagger", "jeweled dagger", new Attack.Simple("Stabby, stab, stab.", 1)) {
      @Override
      public String describeThings() {
        return super.describeThings();
      }
    };

    // Things
    var ring = new Thing.Weapon(
      "ring",
      "ring of great power",
      new Attack.Full(
        "A sphere of light emanates from the ring",
        1000,
        (t -> " blasting " + t.who() + " to smithereens.")
      )
    );

    var axe = new Thing.Weapon("axe", "heavy dwarven axe", new Attack.Simple("You swing your axe and connect!", 2)) {
      public String eat() {
        return "Axes are not good for eating. Now your teeth hurt and you are no less hungry.";
      }
    };

    var sword = new Thing.Weapon(
      "sword",
      "broadsword with a rusty iron hilt",
      new Attack.Simple("Oof, this sword is heavy but you manage to swing it.", 5)
    ) {
      public String eat() {
        return "What are you, a sword swallower?! You can't eat a sword.";
      }
    };

    var bread = new Thing.Food("bread", "loaf of bread", "Ah, delicious. Could use some mayonnaise though.");

    var sandwich = new Thing.Food(
      "sandwich",
      "ham and cheese sandwich",
      "Mmmm, tasty. But I think you got a spot of mustard on your tunic."
    );

    // Monsters
    var blobbyblob = new Thing.Monster("blobbyblob", 7) {
      @Override
      public String description() {
        return alive()
          ? name() + ", a gelatenous mass with too many eyes and an odor of jello casserole gone bad"
          : hitPoints() > -100 ? "dead " + name() + " decaying into puddle of goo" : "a spattering of blobbyblob bits";
      }

      @Override
      public String eat() {
        return alive()
          ? "Are you out of your mind?! This is a live and jiggling " + name() + "."
          : destroy(
            hitPoints() < -100
              ? "The " +
              name() +
              " is blasted all over the room. There is nothing to eat unless you have a squeege and a straw."
              : "Ugh. This is worse than the worst jello casserole you have ever tasted. But it does slightly sate your hunger."
          );
      }

      @Override
      public Attack attack() {
        return new Attack.Simple("The blobbyblob extrudes a blobby arm and smashes at you!", 3);
      }

      @Override
      public Stream<Action> onTurn(Action.Turn a) {
        return streamIf(alive(), new Action.Attack(a.player(), this));
      }
    };

    var pirate = new Thing.Monster(
      "pirate",
      "pirate with a wooden leg and an eye patch",
      "dead pirate with his eye patch askew",
      10,
      false
    ) {
      @Override
      public Stream<Action> onEnter(Action.Go a) {
        return streamIf(alive(), new Action.Say(this, "Arr, matey!"));
      }

      @Override
      public Stream<Action> onTake(Action.Take a) {
        return streamIf(
          alive() && a.taking(thing("parrot")),
          new Action.Say(this, "Oi, ye swarthy dog! Hands off me parrot!")
        );
      }
    };

    var parrot = new Thing.Monster("parrot", "green and blue parrot with a tiny eye patch", "dead parrot", 5, true) {
      @Override
      public Stream<Action> onDrop(Action.Drop a) {
        return a.thing().name().equals("bread") ? Stream.of(new Action.Move(this, a.thing(), "on")) : Stream.empty();
      }

      @Override
      public String moveTo(Location location, String place) {
        var s = super.moveTo(location, place);
        return location == bread ? "The " + name() + " flies down and starts eating the bread." : s;
      }
    };

    // Place things
    pedestal.placeThing(ring, "on");
    pirate.placeThing(parrot, "on the right shoulder of");
    dining.placeThing(pirate, "in the middle of the room");
    kitchen.placeThing(table, "against the wall");
    table.placeThing(bread, "on");
    blobbyblobLair.placeThing(axe, "on the floor");
    storeroom.placeThing(treasureChest, "against the wall");
    treasureChest.placeThing(jeweledDagger, "inside");
    blobbyblobLair.placeThing(blobbyblob, "across from you");
    entry.placeThing(pedestal, "in the center of the room");
    entry.placeThing(tray, "by the door");
    tray.placeThing(sandwich, "on");
    dining.placeThing(sword, "propped against a wall");

    return entry;
  }

  private static <T> Stream<T> streamIf(boolean test, T v) {
    return Stream.ofNullable(test ? v : null);
  }

  public static void main(String[] args) {
    try {
      var in = args.length > 0 ? new FileInputStream(args[0]) : System.in;
      new Dungeon(in, System.out).loop(args.length > 0);
    } catch (IOException ioe) {
      System.out.println("Yikes. Problem reading command: " + ioe);
    }
  }
}
