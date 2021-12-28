package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;
//import org.junit.Test;

//import java.lang.reflect.Array;
import java.awt.*;
import java.io.Serializable;
//import java.util.Locale;
import java.util.Random;
import java.util.ArrayList;
import java.io.File;

public class Engine implements Serializable {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    public static final File CWD = new File(System.getProperty("user.dir"));
    //do we want multiple save states? OR just do one final file
    private ArrayList<Room> rooms = new ArrayList<Room>();
    private TETile[][] world;
    private Random rand;
    private Player player;
    private boolean gameover = false;
    private Font origFont = new Font("Monaco", Font.BOLD, 14);
    private ArrayList<Coordinate> floor;
    private ArrayList<Light> lights;
    private Flowers flowers;
    private int numSteps;
    private int currPoints;
    private int maxScore;
    private boolean won = false;
    private boolean isAutograder = false;
    private boolean quit = false;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */

    public void interactWithKeyboard() {
        isAutograder = false;
        StdDraw.setCanvasSize(WIDTH * 14, HEIGHT * 14);
        //SOMEHOW FIX CANVAS SIZE
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.white);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        menu();
        //this.rand = new Random(seed);
    }

    public void showPictureMenu() {
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.LIGHT_GRAY);
        StdDraw.filledRectangle(WIDTH / 2, HEIGHT * 5 / 10, WIDTH / 10, HEIGHT / 17 + .4);
        StdDraw.filledRectangle(WIDTH / 2, HEIGHT * 4 / 10, WIDTH / 10, HEIGHT / 17 + .3);
        StdDraw.filledRectangle(WIDTH / 2, HEIGHT * 3 / 10, WIDTH / 10, HEIGHT / 17 + .2);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(WIDTH / 2, HEIGHT * 4 / 5, "CS61B: THE GAME");
        StdDraw.setPenColor(Color.BLUE);
        StdDraw.text(WIDTH / 2, HEIGHT * 5 / 10, "NEW GAME (N)");
        StdDraw.text(WIDTH / 2, HEIGHT * 4 / 10, "LOAD GAME (L)");
        StdDraw.text(WIDTH / 2, HEIGHT * 3 / 10, "QUIT (Q)");
        StdDraw.show();
    }

    public void menu() {
        showPictureMenu();
        getNextMenuKey();
        while (!gameover) {
            double x = StdDraw.mouseX();
            double y = StdDraw.mouseY();
            if (StdDraw.hasNextKeyTyped()) {
                String key = Character.toString(StdDraw.nextKeyTyped()).toLowerCase();
                performAction(key);
            }
            displayTile(x, y);
        }

        if (won) {
            displayWinScreen();
        } else if (!quit) {
            displayLossScreen();
        }

    }

    public void displayLossScreen() {
        StdDraw.disableDoubleBuffering();
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.LIGHT_GRAY);
        Font fontc = new Font("Monaco", Font.ITALIC, 40);
        StdDraw.setFont(fontc);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 5, "YOU LOST -_-;");
        StdDraw.show();
    }

    public void displayWinScreen() {
        StdDraw.disableDoubleBuffering();
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.RED);
        StdDraw.filledRectangle(WIDTH / 8, HEIGHT / 2 + 5, WIDTH / 8, HEIGHT / 2 + 5);
        StdDraw.show();
        StdDraw.pause(500);
        StdDraw.setPenColor(Color.orange);
        StdDraw.filledRectangle(3 * WIDTH / 8, HEIGHT / 2 + 5, WIDTH / 8, HEIGHT / 2 + 5);
        StdDraw.show();
        StdDraw.pause(500);
        StdDraw.setPenColor(Color.yellow);
        StdDraw.filledRectangle(5 * WIDTH / 8, HEIGHT / 2 + 5, WIDTH / 8, HEIGHT / 2 + 5);
        StdDraw.show();
        StdDraw.pause(500);
        StdDraw.setPenColor(Color.green);
        StdDraw.filledRectangle(7 * WIDTH / 8, HEIGHT / 2 + 5, WIDTH / 8, HEIGHT / 2 + 5);
        StdDraw.show();
        StdDraw.pause(500);
        StdDraw.setPenColor(Color.BLACK);
        Font fontb = new Font("Monaco", Font.BOLD, 40);
        StdDraw.setFont(fontb);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "YOU WON ^_^");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 3, "YOUR Score: " + currPoints + " / " + maxScore);
        StdDraw.enableDoubleBuffering();
    }

    public void performAction(String key) {
        switch (key.toLowerCase()) {
            case "w":
                //uppers
                movePlayer(player.getX(), player.getY() + 1);
                break;
            case "a":
                //lefters
                movePlayer(player.getX() - 1, player.getY());
                break;
            case "s":
                //downers
                movePlayer(player.getX(), player.getY() - 1);
                break;
            case "d":
                //righters
                movePlayer(player.getX() + 1, player.getY());
                break;
            case ":q":
                saveWorld();
                break;
            case ":":
                //quitters
                while (true) {
                    if (StdDraw.hasNextKeyTyped()) {
                        String nextKey = Character.toString(StdDraw.nextKeyTyped()).toLowerCase();
                        if (nextKey.equalsIgnoreCase("Q")) {
                            saveWorld();
                            quit = true;
                            System.exit(0);
                            break;
                            //some how break every function
                        } else {
                            break;
                        }
                    }
                }
                break;
            case "c":
                //lighters
                interactClosestLight();
                break;
            default:
                break;
        }

    }

    public void displayTile(double x, double y) {
        StdDraw.setPenColor(Color.WHITE);
        int xPos = (int) Math.floor(x);
        int yPos = (int) Math.floor(y);
        StdDraw.text(WIDTH / 2, HEIGHT + 3, "Steps Remaining: " + numSteps);
        StdDraw.text(WIDTH - 10, HEIGHT + 3, "Points: " + currPoints);
        if (!outOfBounds(xPos, yPos)) {
            TETile color = world[xPos][yPos];
            Font newFont = new Font("Monaco", Font.BOLD, 25);
            StdDraw.setFont(newFont);
            if (color.equals(Tileset.WALL)) {
                StdDraw.text(4, HEIGHT + 3, "WALL");
            } else if (color.equals(Tileset.FLOOR)) {
                StdDraw.text(4, HEIGHT + 3, "FLOOR");
            } else if (color.equals(Tileset.NOTHING)) {
                StdDraw.text(4, HEIGHT + 3, "NOTHING");
            } else if (color.equals(Tileset.AVATAR)) {
                StdDraw.text(4, HEIGHT + 3, "AVATAR");
            } else if (color.equals(Tileset.LIGHT)) {
                StdDraw.text(7, HEIGHT + 3, "LIGHT SOURCE");
            } else if (color.equals(Tileset.MOUNTAIN)) {
                StdDraw.text(7, HEIGHT + 3, "MOUNTAIN");
            }
        }
        StdDraw.show(75);
        StdDraw.setFont(origFont);
        ter.renderFrame(world);
    }

    public void getNextMenuKey() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                String typed = Character.toString(StdDraw.nextKeyTyped());
                if (typed.equalsIgnoreCase("n")) {
                    String s = seedMenu();
                    fromSeedCreateWorld(parseSeed(s));
                    return;
                } else if (typed.equalsIgnoreCase("l")) {
                    loadWorld();
                    return;
                } else if (typed.equalsIgnoreCase("q")) {
                    saveWorld();
                    System.exit(0);
                    return;
                }
            } else {
                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();
                showMouseStatus(x, y);
                if (StdDraw.isMousePressed()) {
                    if ((x >= 31.9 && x <= 47.93)) {
                        if (y >= 13.4 && y <= 16.5) {
                            String s = seedMenu();
                            fromSeedCreateWorld(parseSeed(s));
                            return;
                        } else if (y >= 10.78 && y <= 13.28) {
                            loadWorld();
                            return;
                        } else if (y >= 7.92 && y <= 10.23) {
                            saveWorld();
                            System.exit(0);
                            return;
                        }
                    }
                }
            }
        }
    }

    public void fromSeedCreateWorld(long seed) {
        rand = new Random(seed);
        world = new TETile[WIDTH][HEIGHT];
        floor = new ArrayList<>();
        lights = new ArrayList<>();
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
        generateAllRooms();
        sortRooms();
        connectAllRooms();
        placePlayer();
        numSteps = 5 * floor.size() / 8;
        placeFlowers();
        for (Coordinate light : lights) {
            lightsOn(light);
        }
        if (!isAutograder) {
            ter.initialize(WIDTH, HEIGHT);
            ter.renderFrame(world);
        }

    }

    public void placeFlowers() {
        int numFlowers = lights.size();
        maxScore = 1000 * numFlowers;
        flowers = new Flowers(numSteps, this);
        for (int i = 0; i < numFlowers; i++) {
            int randomPos = rand.nextInt(floor.size());
            Coordinate pos = floor.get(randomPos);
            flowers.addFlower(pos);
            world[pos.getX()][pos.getY()] = Tileset.FLOWER;
            floor.remove(pos);
        }
    }


    public long parseSeed(String input) {
        long seed = 0;
        try {
            while (input.length() > 0) {
                seed = seed * 10 + Integer.parseInt(input.substring(0, 1));
                input = input.substring(1);
            }
        } catch (NumberFormatException e) {
            input = input.substring(1);
        }
        return seed;
    }

    public String seedMenu() {
        String s = "";
        StdDraw.clear(Color.GRAY);
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.text(WIDTH / 2, HEIGHT - 10, "ENTER SEED");
        StdDraw.show();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (Character.toString(c).equalsIgnoreCase("S") && s.length() > 0) {
                    return s;
                } else if (Character.getNumericValue(c) >= 0 && Character.getNumericValue(c) <= 9) {
                    s = s + c;
                    displaySeed(s);
                }
            }
        }
    }

    public void displaySeed(String s) {
        StdDraw.clear(Color.GRAY);
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.text(WIDTH / 2, HEIGHT - 10, "ENTER SEED");
        StdDraw.text(WIDTH / 2, HEIGHT / 2, s);
        StdDraw.show();
    }

    public void showMouseStatus(double x, double y) {
        StdDraw.clear();
        showPictureMenu();
        if ((x >= 31.9 && x <= 47.93)) {
            if (y >= 13.4 && y <= 16.5) {
                StdDraw.text(10, HEIGHT - 5, "START NEW GAME");
            } else if (y >= 10.78 && y <= 13.28) {
                StdDraw.text(10, HEIGHT - 5, "LOAD OLD GAME");
            } else if (y >= 7.92 && y <= 10.23) {
                StdDraw.text(10, HEIGHT - 5, "QUIT");
            }
        }
        StdDraw.show(75);
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @return the 2D TETile[][] representing the state of the world
     */

    public TETile[][] interactWithInputString(String input) {
        // Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        isAutograder = true;
        long seed = 0;
        if (input.substring(0, 1).equalsIgnoreCase("N")) {
            input = input.substring(1);
            try {
                while (input.length() > 0) {
                    seed = seed * 10 + Integer.parseInt(input.substring(0, 1));
                    input = input.substring(1);
                }
            } catch (NumberFormatException e) {
                input = input.substring(1);
            }
            fromSeedCreateWorld(seed);
            inputToAction(input);
        } else if (input.substring(0, 1).equalsIgnoreCase("L")) {
            loadWorld();
            input = input.substring(1);
            inputToAction(input);
        } else if (input.substring(0, 1).equalsIgnoreCase("Q")) {
            return world;
        }
        return world;
    }

    public void inputToAction(String input) {
        while (input.length() > 0) {
            if (input.substring(0, 1).equalsIgnoreCase(":")) {
                if (input.substring(1).length() > 0 && input.substring(1, 2)
                        .equalsIgnoreCase("q")) {
                    performAction(input.substring(0, 2));
                    input = "";
                } else {
                    input = input.substring(1);
                }
            } else {
                performAction(input.substring(0, 1));
                input = input.substring(1);
            }
        }

    }

    public void placePlayer() { //place player avatar on a floor tile
        int randomPos = rand.nextInt(floor.size());
        Coordinate pos = floor.get(randomPos);
        floor.remove(pos);
        player = new Player(pos.getX(), pos.getY(), Tileset.FLOOR);
        world[pos.getX()][pos.getY()] = Tileset.AVATAR;
    }

    public void movePlayer(int x, int y) {
        if (world[x][y].equals(Tileset.WALL)) {
            return;
        }
        if (numSteps == 0 && flowers.numFlowers() > 0) {
            gameover = true;
            return;
        }
        world[player.getX()][player.getY()] = player.getTileRemoved();
        TETile color = world[x][y];
        if (color.equals(Tileset.FLOWER)) {
            flowers.rmFlower(new Coordinate(x, y));
            currPoints += flowers.getPointValue();
            player.changePosition(x, y, new TETile(Tileset.FLOOR, color.getBackgroundColor(), 0));
            world[x][y] = new TETile(Tileset.AVATAR, color.getBackgroundColor(), 0);
            numSteps--;

        } else if (color.equals((Tileset.LIGHT))
                && world[x][y].getBackgroundColor().equals(Color.BLUE)) {
            ArrayList<Light> lightsb = keepOnLight();
            Light currLight = new Light(x, y, true);
            lightsb.remove(currLight);
            int r = rand.nextInt(lightsb.size());
            Light xy = lightsb.get(r);
            player.changePosition(xy.getX(), xy.getY(), Tileset.LIGHT);
            world[xy.getX()][xy.getY()] = new TETile(Tileset.AVATAR, color.getBackgroundColor(), 0);
            int factor = numSteps * 15 / 100;
            flowers.decay(factor);
            numSteps = numSteps - factor;
        } else if (color.equals(Tileset.MOUNTAIN)) {
            player.changePosition(x, y, color);
            world[x][y] = new TETile(Tileset.AVATAR, color.getBackgroundColor(), 0);
            numSteps = Math.max(0, numSteps - 3);
        } else {
            player.changePosition(x, y, color);
            world[x][y] = new TETile(Tileset.AVATAR, color.getBackgroundColor(), 0);
            numSteps--;
        }
        flowers.decay(1);
        if (flowers.numFlowers() == 0) {
            gameover = true;
            won = true;
        }
        if (!isAutograder) {
            ter.renderFrame(world);
        }
    }

    public ArrayList<Light> keepOnLight() {
        ArrayList<Light> returnLst = new ArrayList<>();
        for (Light i : lights) {
            if (i.getonOff()) {
                returnLst.add(i);
            }
        }
        return returnLst;
    }

    public void sortRooms() {
        ArrayList<Room> lst = new ArrayList<Room>();
        while (rooms.size() > 0) {
            int min = WIDTH;
            Room smallestRoom = rooms.get(0);
            for (Room r : rooms) {
                if (r.getLeftWall() < min) {
                    min = r.getLeftWall();
                    smallestRoom = r;
                }
            }
            rooms.remove(smallestRoom);
            lst.add(smallestRoom);
        }
        rooms = lst;
    }

    public void lightsOn(Coordinate xy) {
        int colorFactor = 255 / 3;
        int blue = 255;
        Light l = getLit(xy);
        l.switchOn();
        for (int i = -3; i < 4; i++) {
            for (int j = -3; j < 4; j++) {
                int tileX = xy.getX() + i;
                int tileY = xy.getY() + j;
                if (outOfBounds(tileX, tileY) || world[tileX][tileY].equals(Tileset.WALL)
                        || world[tileX][tileY].equals(Tileset.NOTHING)) {
                    continue;
                }
                int currRing = Math.max(Math.abs(i), Math.abs(j));
                world[tileX][tileY] = new TETile(world[tileX][tileY],
                        new Color(0, 0, blue - currRing * colorFactor), 0);
                if (player.getX() == tileX && player.getY() == tileY) {
                    player.setTileRemoved(new TETile(player.getTileRemoved(),
                            new Color(0, 0, blue - currRing * colorFactor), 0));
                }
            }
        }
    }

    public Light getLit(Coordinate xy) {
        for (Light i : lights) {
            if (xy.equals(i)) {
                return i;
            }
        }
        return null;
    }

    public void lightsOff(Coordinate xy) {
        Light l = getLit(xy);
        l.switchOff();
        for (int i = -3; i < 4; i++) {
            for (int j = -3; j < 4; j++) {
                int tileX = xy.getX() + i;
                int tileY = xy.getY() + j;
                if (outOfBounds(tileX, tileY) || world[tileX][tileY].equals(Tileset.WALL)
                        || world[tileX][tileY].equals(Tileset.NOTHING)) {
                    continue;
                }
                world[tileX][tileY] = new TETile(world[tileX][tileY], Color.black, 0);
                if (player.getX() == tileX && player.getY() == tileY) {
                    player.setTileRemoved(new TETile(player.getTileRemoved(),
                            Color.black, 0));
                }
            }
        }
    }

    public void interactClosestLight() {
        Coordinate closestLight = lights.get(0);
        double currDist = Math.sqrt(Math.pow(player.getX() - closestLight.getX(), 2)
                + Math.pow(player.getY() - closestLight.getY(), 2));
        for (Coordinate light : lights) {
            int xdif = player.getX() - light.getX();
            int ydif = player.getY() - light.getY();
            double dist = Math.sqrt(Math.pow(xdif, 2) + Math.pow(ydif, 2));
            if (dist < currDist) {
                closestLight = light;
                currDist = dist;
            }
        }
        if (world[closestLight.getX()][closestLight.getY()]
                .getBackgroundColor().equals(Color.black)) {
            lightsOn(closestLight);
        } else {
            lightsOff(closestLight);
        }
    }

    public void generateAllRooms() {
        int numRooms = rand.nextInt(50) + 75 /*arbitrary number??*/;
        for (int i = 0; i < numRooms; i++) {
            int randWidth = rand.nextInt(11) + 3; //random number FEEL FREE TO CHANGE
            int randHeight = rand.nextInt(11) + 3;
            int randX = rand.nextInt(WIDTH - randWidth);
            int randY = rand.nextInt(HEIGHT - randHeight);
            generateRoom(randX, randY, randWidth, randHeight);
        }
    }

    public void generateRoom(int X, int Y, int randW, int randH) {
        if (X - randW < 0 || X + randW >= WIDTH || Y - randH < 0 || Y + randH >= HEIGHT) {
            return;
        }
        Room newRoom = new Room(X, Y, randW, randH, world);
        for (Room room : rooms) {
            if (newRoom.isOverlapping(room)) { //overlapping
                return;
            }
        }
        int lightX = rand.nextInt(randW - 2) + X + 1;
        int lightY = rand.nextInt(randH - 2) + Y + 1;
        for (int i = X; i <= X + randW; i++) {
            for (int j = Y; j <= Y + randH; j++) {
                if (i == X || j == Y || i == X + randW || j == Y + randH) { //walls
                    world[i][j] = Tileset.WALL;
                    newRoom.getWallcoords().add(new Coordinate(i, j));
                } else {
                    if (i == lightX && j == lightY) {
                        world[i][j] = Tileset.LIGHT;
                        Light ij = new Light(i, j, true);
                        //SOME EDITS HERE
                        lights.add(ij);

                    } else {
                        world[i][j] = Tileset.FLOOR;
                        Coordinate ij = new Coordinate(i, j);
                        floor.add(ij);
                    }
                }
            }
        }
        rooms.add(newRoom);
    }

    public void connectAllRooms() {
        //preliminary not done yet
        while (rooms.size() > 1) {
            Room r2 = findClosestWall();
            connectClosestRoom(rooms.get(0), r2);
        }
    }

    public void connectClosestRoom(Room r1, Room r2) {
        ArrayList<Room.Node> nodeDist = r1.findClosestSide(r2);
        ArrayList<Room.Node> ordered = new ArrayList<Room.Node>();

        while (nodeDist.size() > 0) {
            double min = WIDTH;
            Room.Node minNode = nodeDist.get(0);
            for (int i = 0; i < nodeDist.size(); i++) {
                Room.Node node = nodeDist.get(i);
                if (node.getDist() < min) {
                    min = node.getDist();
                    minNode = node;
                }
            }
            nodeDist.remove(minNode);
            ordered.add(minNode);
        }
        ArrayList<Room.Node> minDists = new ArrayList<Room.Node>();
        minDists.add(ordered.get(0));
        for (Room.Node i : ordered) {
            if (i.getDist() == minDists.get(0).getDist()) {
                minDists.add(i);
            }
        }
        generateHallway(minDists.get(rand.nextInt(minDists.size())));

    }

    public void generateHallway(Room.Node distanceNode) {
        int randPath = rand.nextInt(2);
        switch (randPath) {
            case 0: //vertical first
                generateHallway(distanceNode.getThisCoord(), distanceNode.getOtherCoord(),
                        true);
                break;
            case 1: //horizontal first
                generateHallway(distanceNode.getThisCoord(), distanceNode.getOtherCoord(),
                        false);
                break;
            default:
                break;
        }
    }

    public Room findClosestWall() {
        Room returnRoom = rooms.get(0);
        double min = WIDTH;
        for (int i = 1; i < rooms.size(); i++) {
            Room thisRoom = rooms.get(0);
            ArrayList<Room.Node> nodeDist = thisRoom.findClosestSide(rooms.get(i));
            for (Room.Node j : nodeDist) {
                if (j.getDist() < min) {
                    min = j.getDist();
                    returnRoom = rooms.get(i);
                }
            }
        }
        return returnRoom;
    }

    public Room findRoom(Coordinate coord) {
        for (Room r : rooms) {
            ArrayList<Coordinate> coordinateCoords = r.getWallcoords();
            for (Coordinate w : coordinateCoords) {
                if (w.getX() == coord.getX() && w.getY() == coord.getY()) {
                    return r;
                }
            }
        }
        return null;
    }

    public void generateHallway(Coordinate startRoom, Coordinate endRoom, boolean vertFirst) {
        ArrayList<Coordinate> stagedForRemovalCoordinate = new ArrayList<Coordinate>();
        ArrayList<Coordinate> stagedForAdditionCoordinate = new ArrayList<Coordinate>();
        Room r1 = findRoom(startRoom);
        Room r2 = findRoom(endRoom);
        TETile hallwayTerrain = Tileset.FLOOR;
        if (rand.nextInt(11) < 3) {
            hallwayTerrain = Tileset.MOUNTAIN;
        }
        int increment = 1;
        if (vertFirst) {
            int yCoord = startRoom.getY();
            if (startRoom.getY() > endRoom.getY()) {
                increment = -1;
            }
            while (yCoord != endRoom.getY() + increment) {
                if (yCoord == endRoom.getY()) {
                    break;
                }
                if (collectWall(startRoom.getX(), yCoord)) {
                    Coordinate xy = new Coordinate(startRoom.getX(), yCoord);
                    stagedForRemovalCoordinate.add(xy);
                }
                world[startRoom.getX()][yCoord] = hallwayTerrain;
                addtoFloor(hallwayTerrain, startRoom.getX(), yCoord);
                surroundFloor(startRoom.getX(), yCoord, stagedForAdditionCoordinate);
                yCoord += increment;
            }
            increment = 1;
            if (startRoom.getX() > endRoom.getX()) {
                increment = -1;
            }
            int xCoord = startRoom.getX();
            while (xCoord != endRoom.getX() + increment) {
                if (collectWall(xCoord, yCoord)) {
                    Coordinate xy = new Coordinate(xCoord, yCoord);
                    stagedForRemovalCoordinate.add(xy);
                }
                world[xCoord][yCoord] = hallwayTerrain;
                addtoFloor(hallwayTerrain, xCoord, yCoord);
                surroundFloor(xCoord, yCoord, stagedForAdditionCoordinate);
                xCoord += increment;
            }
        } else {
            int xCoord = startRoom.getX();
            if (startRoom.getX() > endRoom.getX()) {
                increment = -1;
            }
            while (xCoord != endRoom.getX() + increment) {
                if (xCoord == endRoom.getX()) {
                    break;
                }
                if (collectWall(xCoord, startRoom.getY())) {
                    Coordinate xy = new Coordinate(xCoord, startRoom.getY());
                    stagedForRemovalCoordinate.add(xy);
                }
                world[xCoord][startRoom.getY()] = hallwayTerrain;
                addtoFloor(hallwayTerrain, xCoord, startRoom.getY());
                surroundFloor(xCoord, startRoom.getY(), stagedForAdditionCoordinate);
                xCoord += increment;
            }
            increment = 1;
            if (startRoom.getY() > endRoom.getY()) {
                increment = -1;
            }
            int yCoord = startRoom.getY();
            while (yCoord != endRoom.getY() + increment) {
                if (collectWall(xCoord, yCoord)) {
                    Coordinate xy = new Coordinate(xCoord, yCoord);
                    stagedForRemovalCoordinate.add(xy);
                }
                world[xCoord][yCoord] = hallwayTerrain;
                addtoFloor(hallwayTerrain, xCoord, yCoord);
                surroundFloor(xCoord, yCoord, stagedForAdditionCoordinate);
                yCoord += increment;
            }
        }
        concatenateWalls(stagedForRemovalCoordinate, stagedForAdditionCoordinate, r1, r2);
    }

    private void addtoFloor(TETile tile, int x, int y) {
        if (tile.equals(Tileset.FLOOR)) {
            floor.add(new Coordinate(x, y));
        }

    }

    public void concatenateWalls(ArrayList<Coordinate> remove, ArrayList<Coordinate> add,
                                 Room r1, Room r2) {
        ArrayList<Coordinate> newCoordinates = new ArrayList<Coordinate>();
        newCoordinates.addAll(r1.getWallcoords());
        newCoordinates.addAll(r2.getWallcoords());
        newCoordinates.addAll(add);
        newCoordinates.removeAll(remove);
        Room mergedRoom = new Room(0, 0, 0, 0, world);
        mergedRoom.getWallcoords().addAll(newCoordinates);
        rooms.remove(r1);
        rooms.remove(r2);
        rooms.add(0, mergedRoom);
    }

    public boolean collectWall(int x, int y) {
        if (world[x][y].equals(Tileset.WALL)) {
            return true;
        }
        return false;
    }

    public void surroundFloor(int x, int y, ArrayList<Coordinate> lst) {
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (outOfBounds(i, j)) {
                    continue;
                }
                if (world[i][j].equals(Tileset.NOTHING)) {
                    world[i][j] = Tileset.WALL;
                    Coordinate ij = new Coordinate(i, j);
                    lst.add(ij);
                }
            }
        }
    }

    public boolean outOfBounds(int x, int y) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
            return true;
        }
        return false;
    }

    public void saveWorld() { //DO THIS LATER
        File saveFile = Utils.join(CWD, "save.txt");
        Utils.writeContents(saveFile, Utils.serialize(this));
    }

    public void loadWorld() {
        Engine oldWorld = Utils.readObject(Utils.join(CWD, "save.txt"), Engine.class);
        this.world = oldWorld.getWorld();
        this.player = oldWorld.getPlayer();
        this.floor = oldWorld.getFloor();
        this.rand = oldWorld.getRand();
        this.lights = oldWorld.getLights();
        this.flowers = oldWorld.getFlowers();
        this.numSteps = oldWorld.getNumSteps();
        this.currPoints = oldWorld.getCurrPoints();
        this.maxScore = oldWorld.getMaxScore();
        this.isAutograder = oldWorld.isAutograder();
        if (!isAutograder) {
            ter.initialize(WIDTH, HEIGHT);
        }
    }

    public Flowers getFlowers() {
        return flowers;
    }

    public TETile[][] getWorld() {
        return world;
    }

    public Player getPlayer() {
        return player;
    }


    public ArrayList<Coordinate> getFloor() {
        return floor;
    }

    public Random getRand() {
        return rand;
    }

    public TERenderer getTer() {
        return ter;
    }

    public ArrayList<Light> getLights() {
        return lights;
    }

    public int getNumSteps() {
        return numSteps;
    }


    public int getCurrPoints() {
        return currPoints;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public boolean isAutograder() {
        return isAutograder;
    }
}
