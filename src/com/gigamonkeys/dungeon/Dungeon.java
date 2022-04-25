package com.gigamonkeys.dungeon;

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
import java.util.stream.*;
import java.util.stream.Collectors;

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
    registerCommand(new Command("DROP", "Drop an item you are carrying.", this::drop));
    registerCommand(new Command("EAT", "Eat an item you are holding or in the room.", this::eat));
    registerCommand(new Command("GO", "Go in a direction (NORTH, SOUTH, EAST, or WEST).", this::go));
    registerCommand(new Command("HELP", "Get help on commands.", this::help));
    registerCommand(new Command("INVENTORY", "List the items you are holding.", this::inventory));
    registerCommand(new Command("LOOK", "Look at the room your are in again.", this::look));
    registerCommand(new Command("QUIT", "Quit the game", this::quit));
    registerCommand(new Command("TAKE", "Take an item from the room.", this::take));
  }

  public void registerCommand(Command command) {
    commands.put(command.verb(), command);
  }

  ////////////////////////////////////////////////////////////////////
  // Commands

  Action help(String[] args) {
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

    return Action.noWrap("I understand the following commands:\n\n" + String.join("\n", docs));
  }

  Action quit(String[] args) {
    return new Action() {
      public String description() {
        endGame();
        return "Okay. Bye!";
      }
    };
  }

  Action go(String[] args) {
    return arg(args, 1)
      .map(d -> direction(d).map(dir -> Action.go(player, dir)).orElse(Action.none("Don't understand direction " + d)))
      .orElse(Action.none("Go where?"));
  }

  Action take(String[] args) {
    return arg(args, 1)
      .flatMap(n -> listOfThings(args, 1))
      .map(ts -> Action.take(player, ts))
      .orElse(Action.none("Take what?"));
  }

  Action drop(String[] args) {
    return arg(args, 1)
      .map(name -> player.thing(name).map(t -> Action.drop(player, t)).orElse(Action.none("No " + name + " to drop!")))
      .orElse(Action.none("Drop what?"));
  }

  Action look(String[] args) {
    return Action.look(player);
  }

  Action inventory(String[] args) {
    return Action.none(player.inventory());
  }

  Action eat(String[] args) {
    return arg(args, 1)
      .map(name ->
        player.anyThing(name).map(t -> Action.eat(player, t)).orElse(Action.none("No " + name + " here to eat."))
      )
      .orElse(Action.none("Eat what?"));
  }

  Action attack(String[] args) {
    var i = 1;

    var target = (args.length == 3 && args[i].equals("WITH"))
      ? onlyMonster()
      : arg(args, i++).flatMap(n -> player.roomThing(n));

    var with = arg(args, i++).flatMap(n -> expect("WITH", n));
    var weapon = arg(args, i++).flatMap(n -> player.anyThing(n));

    return target
      .map(t ->
        with
          .map(e -> weapon.map(w -> Action.attack(t, w)).orElse(Action.none("Attack with what?")))
          .orElse(Action.none("Don't understand 'ATTACK' with no 'WITH'."))
      )
      .orElse(Action.none("Attack what?"));
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

  public void endGame() {
    gameOver = true;
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
    out.println(s.toUpperCase());
    out.println();
  }

  public String doCommand(String line) {
    var tokens = wordPattern.matcher(line).results().map(r -> r.group(1)).toList().toArray(new String[0]);
    if (tokens.length > 0) {
      var c = commands.getOrDefault(tokens[0], Command.unknown(tokens[0]));
      return c.run(tokens, player);
    } else {
      // FIXME: This results in too many blank lines being printed. Input loop
      // needs refactoring.
      return "";
    }
  }

  public static Room buildMaze() {
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
      .damage(1000)
      .attack(new Attack.Simple("A sphere of light emanates from the ring blasting the thing to smithereens.", 1000))
      .thing();

    maze
      .thing("axe")
      .damage(2)
      .eatIfInedible("Axes are not good for eating. Now your teeth hurt and you are no less hungry.")
      .attack(new Attack.Simple("You swing your axe and connect!", 2))
      .thing();

    maze
      .thing("sword")
      .description("broadsword with a rusty iron hilt")
      .damage(5)
      .attack(new Attack.Simple("Oof, this sword is heavy but you manage to swing it.", 5))
      .thing();

    maze
      .thing("bread")
      .description("loaf of bread")
      .isEdible(true)
      .eatIfEdible("Ah, delicious. Could use some mayonnaise though.")
      .thing();

    maze
      .thing("sandwich")
      .description("ham and cheese sandwich")
      .isEdible(true)
      .eatIfEdible("Mmmm, tasty. But I think you got a spot of mustard on your tunic.")
      .thing();

    maze
      .thing("blobbyblob")
      .isMonster(true)
      .initialHitPoints(7)
      .damage(3)
      .describeAlive(t -> t.name() + ", a gelatenous mass with too many eyes and an odor of jello casserole gone bad")
      .describeDead(t -> "dead " + t.name() + " decaying into puddle of goo")
      .isEdible(t -> !t.alive())
      .eatIfEdible(Dungeon::blobbyBlobEatIfEdible)
      .eatIfInedible("Are you out of your mind?! This is a live and jiggling BlobbyBlob!")
      .onTurn(Dungeon::blobbyBlobAttack)
      .thing();

    maze
      .thing("pirate")
      .isMonster(true)
      .initialHitPoints(10)
      .damage(2)
      .describeAlive("pirate with a wooden leg and and an eye patch")
      .describeDead("dead pirate with his eye patch askew")
      .onEnter(Dungeon::pirateGreeting)
      .thing();

    maze
      .thing("parrot")
      .isMonster(true)
      .initialHitPoints(5)
      .damage(1)
      .describeAlive("green and blue parrot with a tiny eye patch")
      .describeDead("dead parrot")
      .thing();

    maze
      .place("pedestal", "ring", "on")
      .place("pirate", "parrot", "on the right shoulder of")
      .place("dining", "pirate", "in the middle of the room")
      .place("kitchen", "table", "against the wall")
      .place("table", "bread", "on")
      .place("blobbybloblair", "axe", "on floor")
      .place("blobbybloblair", "blobbyblob", "across from you")
      .place("entry", "pedestal", "in the center of the room")
      .place("entry", "tray", "by the door")
      .place("tray", "sandwich", "on")
      .place("dining", "sword", "propped against a wall");

    return maze.room("entry");
  }

  private static String blobbyBlobEatIfEdible(Thing t) {
    if (t.hitPoints() < -100) {
      return (
        "The " +
        t.name() +
        " is blasted all over the room. " +
        "There is nothing to eat unless you have a squeege and a straw."
      );
    } else {
      return (
        "Ugh. This is worse than the worst jello casserole you have ever tasted. " +
        "But it does slightly sate your hunger."
      );
    }
  }

  private static Stream<Action> blobbyBlobAttack(Thing t, Player p) {
    System.out.println("in blobbyBlobAttack");
    if (t.alive()) {
      return Stream.of(
        new Action.Attack(t.damage(), "The " + t.name() + " extrudes a blobby arm and smashes at you!", p)
      );
    } else {
      return Stream.empty();
    }
  }

  private static Stream<Action> pirateGreeting(Thing t, Action.Go action) {
    if (t.alive()) {
      return Stream.of(Action.none("'Arr, matey!' says the " + t.name()));
    } else {
      return Stream.empty();
    }
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
