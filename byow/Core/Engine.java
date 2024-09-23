package byow.Core;

import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;
import byow.TileEngine.Tileset;
import byow.TileEngine.*;

import java.awt.*;
import java.io.*;
import java.util.Random;


public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private TETile[][] creatMap = new TETile[WIDTH][HEIGHT];
    private Random R;
    private XandYCoordinates playerPos;
    private long SEEDCOMMAND;
    private boolean gameSave = false;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT);
        createTitleScreen();
        World makeWorld = new World(WIDTH, HEIGHT, SEEDCOMMAND);
        creatMap = makeWorld.getWorldDimensions();
        playerPos = makeWorld.createAvatar(creatMap, R);
        ter.renderFrame(creatMap);
        gameControl();
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
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        if (input.toUpperCase().contains("N") && input.toUpperCase().contains("S")) {
            int beginSeed = input.toUpperCase().indexOf("N") + 1;
            int endSeed = input.toUpperCase().indexOf("S");
            if (input.substring(beginSeed, endSeed).length() > 0) {
                SEEDCOMMAND = Long.parseLong(input.substring(beginSeed, endSeed));
            } else {
                throw new IllegalArgumentException("Numbers between N and S only");
            }
            World makeWorld = new World(WIDTH, HEIGHT, SEEDCOMMAND);
            return makeWorld.getWorldDimensions();
        }
        TETile[][] grid = new TETile[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                grid[i][j] = Tileset.NOTHING;
            }
        }
//        else {
//            throw new IllegalArgumentException("String starts with N and ends with S");
//        }
        return grid;
    }

    private void createTitleScreen() {
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 65));
        StdDraw.text(40, HEIGHT * 3 / 4, "CS61B : THE GAME");
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 25));
        StdDraw.text(40, 15, "NewGame(N)");
        StdDraw.text(40, 12, "Load Game(L)");
        StdDraw.text(40, 9, "Quit(Q)");
        StdDraw.show();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char letter = StdDraw.nextKeyTyped();
                switch (letter) {
                    case 'N': case 'n': {
                        seedReciever();
                        return;
                    }
                    case 'L': case 'l': {
                        loadGame();
                        if (gameSave) {
                            gameControl();
                            break;
                        } else {
                            continue;
                        }
                    }
                    case 'Q': case 'q': {
                        System.exit(0);
                        break;
                    }
                    default: break;
                }
            }
        }
    }
    private void seedReciever() {
        StringBuilder sB = new StringBuilder();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char letter = StdDraw.nextKeyTyped();
                if (letter == 'S' || letter == 's') {
                    SEEDCOMMAND = Long.valueOf(sB.toString().substring(1));
                    R = new Random(SEEDCOMMAND);
                    return;
                }
                sB.append(letter);
                StdDraw.clear(Color.BLACK);
                StdDraw.setFont(new Font("Monaco", Font.BOLD, 65));
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.text(40, HEIGHT * 3 / 4, "CS61B: THE GAME");
                StdDraw.setFont(new Font("Monaco", Font.BOLD, 25));
                StdDraw.text(40, 15, "Entering Seed:");
                StdDraw.text(40, 12, sB.toString());
                StdDraw.show();
            }
        }
    }

    private void gameControl() {
//        ter.renderFrame(creatMap);
        while (true) {
            locationDescr();
            if (StdDraw.hasNextKeyTyped()) {
                char letter = StdDraw.nextKeyTyped();
                switch (letter) {
                    case 'W': case 'w': {
                        if (creatMap[playerPos.x][playerPos.y + 1].equals(Tileset.WALL)) {
                            break;
                        }
                        if (creatMap[playerPos.x][playerPos.y + 1].equals(Tileset.UNLOCKED_DOOR)) {
                            winning(); System.exit(0);
                        } else {
                            creatMap[playerPos.x][playerPos.y] = Tileset.FLOOR;
                            creatMap[playerPos.x][playerPos.y + 1] = Tileset.AVATAR;
                            playerPos.y += 1; ter.renderFrame(creatMap);
                        }
                        break;
                    }
                    case 'S': case 's': {
                        if (creatMap[playerPos.x][playerPos.y - 1].equals(Tileset.WALL)) {
                            break;
                        }
                        if (creatMap[playerPos.x][playerPos.y - 1].equals(Tileset.UNLOCKED_DOOR)) {
                            winning(); System.exit(0);
                        } else {
                            creatMap[playerPos.x][playerPos.y] = Tileset.FLOOR;
                            creatMap[playerPos.x][playerPos.y - 1] = Tileset.AVATAR;
                            playerPos.y -= 1; ter.renderFrame(creatMap);
                        }
                        break;
                    }
                    case 'A': case 'a': {
                        if (creatMap[playerPos.x - 1][playerPos.y].equals(Tileset.WALL)) {
                            break;
                        }
                        if (creatMap[playerPos.x - 1][playerPos.y].equals(Tileset.UNLOCKED_DOOR)) {
                            winning(); System.exit(0);
                        } else {
                            creatMap[playerPos.x][playerPos.y] = Tileset.FLOOR;
                            creatMap[playerPos.x - 1][playerPos.y] = Tileset.AVATAR;
                            playerPos.x -= 1;
                            ter.renderFrame(creatMap);
                        }
                        break;
                    }
                    case 'D': case 'd': {
                        if (creatMap[playerPos.x + 1][playerPos.y].equals(Tileset.WALL)) {
                            break;
                        }
                        if (creatMap[playerPos.x + 1][playerPos.y].equals(Tileset.UNLOCKED_DOOR)) {
                            winning();
                            System.exit(0);
                        } else {
                            creatMap[playerPos.x][playerPos.y] = Tileset.FLOOR;
                            creatMap[playerPos.x + 1][playerPos.y] = Tileset.AVATAR;
                            playerPos.x += 1;
                            ter.renderFrame(creatMap);
                        }
                        break;
                    }
                    case ':': {
                        while (true) {
                            if (StdDraw.hasNextKeyTyped()) {
                                char alph = StdDraw.nextKeyTyped();
                                if (alph == 'Q' || alph == 'q') {
                                    gameSave(); System.exit(0);
                                } else {
                                    break;
                                }
                            }
                        }
                        break;
                    }
                    default: break;
                }
            }
        }
    }

    private void locationDescr() {
        ter.renderFrame(creatMap);
        int mX = (int) StdDraw.mouseX();
        int mY = (int) StdDraw.mouseY();
        if (creatMap[mX][mY] != null) {
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.setFont(new Font("Monaco", Font.BOLD, 15));
            StdDraw.text(5, HEIGHT - 1, creatMap[mX][mY].description());
            StdDraw.show();
        }
    }
    private void winning() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 25));
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(40, 15, "You escaped!!!");
        StdDraw.show();
        StdDraw.pause(500);
    }

    private void gameSave() {
        File sG = new File("./savedGame.txt");
        try {
            if (!sG.exists()) {
                sG.createNewFile();
            }
            FileOutputStream fileOS = new FileOutputStream(sG);
            ObjectOutputStream objrctOS = new ObjectOutputStream(fileOS);
            objrctOS.writeObject(this.playerPos);
            objrctOS.writeObject(this.SEEDCOMMAND);
            objrctOS.close();
            fileOS.close();
        } catch (FileNotFoundException item) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException item) {
            System.out.println(item);
            System.exit(0);
        }
    }
    private void loadGame() {
        File sG = new File("./savedGame.txt");
        if (sG.exists()) {
            try {
                FileInputStream fileIS = new FileInputStream(sG);
                ObjectInputStream objectIS = new ObjectInputStream(fileIS);
                playerPos = (XandYCoordinates) objectIS.readObject();
                SEEDCOMMAND = (Long) objectIS.readObject();
                World makeWorld = new World(WIDTH, HEIGHT, SEEDCOMMAND);
                creatMap = makeWorld.getWorldDimensions();
                creatMap[playerPos.x][playerPos.y] = Tileset.AVATAR;
                gameSave = true;
                objectIS.close();
                fileIS.close();
            } catch (FileNotFoundException item) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException item) {
                System.out.println(item);
                System.exit(0);
            } catch (ClassNotFoundException item) {
                System.out.println("class not found");
                System.exit(0);
            }
        }
    }

}

