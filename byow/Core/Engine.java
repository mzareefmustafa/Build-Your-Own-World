package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.TERenderer;
import edu.princeton.cs.introcs.StdDraw;
import byow.TileEngine.Tileset;

import java.awt.*;
import java.io.*;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();

    /* Map dimensions */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;

    /* Game state variables */
    private TETile[][] creatMap = new TETile[WIDTH][HEIGHT];
    private Random R;
    private XandYCoordinates playerPos;
    private long SEEDCOMMAND;
    private boolean gameSave = false;

    /**
     * Entry point for interacting with the keyboard. Displays the title screen, initializes the world,
     * and begins the game.
     */
    public void interactWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT);
        createTitleScreen();
        World makeWorld = new World(WIDTH, HEIGHT, SEEDCOMMAND);
        creatMap = makeWorld.getWorldDimensions();
        playerPos = makeWorld.createAvatar(creatMap, R);
        validateObjectivePoint(creatMap);
        ter.renderFrame(creatMap);
        gameControl();
    }

    private void validateObjectivePoint(TETile[][] map) {
        boolean validObjective = false;
        for (int x = 0; x < WIDTH && !validObjective; x++) {
            for (int y = 0; y < HEIGHT && !validObjective; y++) {
                if (map[x][y] == Tileset.UNLOCKED_DOOR) {
                    validObjective = true;
                }
            }
        }
        if (!validObjective) throw new IllegalStateException("Objective point generated outside the map!");
    }

    /**
     * Handles string-based input for autograding and testing.
     * @param input Input string.
     * @return The 2D TETile[][] representing the state of the world.
     */
    public TETile[][] interactWithInputString(String input) {
        if (input.toUpperCase().contains("N") && input.toUpperCase().contains("S")) {
            int beginSeed = input.toUpperCase().indexOf("N") + 1;
            int endSeed = input.toUpperCase().indexOf("S");
            if (input.substring(beginSeed, endSeed).length() > 0) {
                SEEDCOMMAND = Long.parseLong(input.substring(beginSeed, endSeed));
            } else {
                throw new IllegalArgumentException("Invalid seed: Numbers between N and S only.");
            }
            World makeWorld = new World(WIDTH, HEIGHT, SEEDCOMMAND);
            TETile[][] generatedMap = makeWorld.getWorldDimensions();
            validateObjectivePoint(generatedMap);
            return generatedMap;
        }
        throw new IllegalArgumentException("Input must start with N and end with S.");
    }

    private void createTitleScreen() {
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 65));
        StdDraw.text(40, HEIGHT * 3 / 4, "ESCAPE: THE GAME");
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 25));
        StdDraw.text(40, 15, "New Game (n)");
        StdDraw.text(40, 12, "Load Game (l)");
        StdDraw.text(40, 9, "Quit (q)");
        StdDraw.show();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char letter = StdDraw.nextKeyTyped();
                switch (Character.toUpperCase(letter)) {
                    case 'N': seedReciever(); return;
                    case 'L': loadGame(); if (gameSave) gameControl(); break;
                    case 'Q': System.exit(0); break;
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
                    SEEDCOMMAND = Long.parseLong(sB.toString().substring(1));
                    R = new Random(SEEDCOMMAND);
                    return;
                }
                sB.append(letter);
                StdDraw.clear(Color.BLACK);
                StdDraw.setFont(new Font("Monaco", Font.BOLD, 65));
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.text(40, HEIGHT * 3 / 4, "ESCAPE: THE GAME");
                StdDraw.setFont(new Font("Monaco", Font.BOLD, 25));
                StdDraw.text(40, 15, "Entering Seed:");
                StdDraw.text(40, 12, sB.toString());
                StdDraw.show();
            }
        }
    }

    private void gameControl() {
        while (true) {
            locationDescr();
            if (StdDraw.hasNextKeyTyped()) {
                char letter = StdDraw.nextKeyTyped();
                handlePlayerMove(letter);
            }
        }
    }

    private void handlePlayerMove(char move) {
        int dx = 0, dy = 0;
        switch (Character.toUpperCase(move)) {
            case 'W': dy = 1; break;
            case 'S': dy = -1; break;
            case 'A': dx = -1; break;
            case 'D': dx = 1; break;
            case ':':
                if (StdDraw.hasNextKeyTyped()) {
                    char nextChar = StdDraw.nextKeyTyped();
                    if (Character.toUpperCase(nextChar) == 'Q') {
                        gameSave();  // Save the game state
                        System.out.println("Game saved. Exiting...");
                        System.exit(0);  // Exit the game
                    }
                }
                return;
            default: return;
        }
        movePlayer(dx, dy);
    }

    private void movePlayer(int dx, int dy) {
        int newX = playerPos.x + dx;
        int newY = playerPos.y + dy;

        if (!creatMap[newX][newY].equals(Tileset.WALL)) {
            if (creatMap[newX][newY].equals(Tileset.UNLOCKED_DOOR)) {
                winning(); System.exit(0);
            }
            creatMap[playerPos.x][playerPos.y] = Tileset.FLOOR;
            creatMap[newX][newY] = Tileset.AVATAR;
            playerPos.x = newX;
            playerPos.y = newY;
            ter.renderFrame(creatMap);
        }
    }

    private void loadGame() {
        File saveFile = new File("./savedGame.txt");
        if (saveFile.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(saveFile))) {
                creatMap = (TETile[][]) in.readObject();
                playerPos = (XandYCoordinates) in.readObject();
                SEEDCOMMAND = (Long) in.readObject();
                R = new Random(SEEDCOMMAND);
                gameSave = true;
                ter.initialize(WIDTH, HEIGHT);
                ter.renderFrame(creatMap);
                System.out.println("Game successfully loaded.");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Failed to load game: " + e.getMessage());
            }
        } else {
            System.out.println("No saved game found.");
        }
    }

    private void locationDescr() {
        ter.renderFrame(creatMap);
        int mX = (int) StdDraw.mouseX();
        int mY = (int) StdDraw.mouseY();
        if (mX >= 0 && mX < WIDTH && mY >= 0 && mY < HEIGHT && creatMap[mX][mY] != null) {
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
        StdDraw.pause(2000);
    }

    private void gameSave() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("./savedGame.txt"))) {
            out.writeObject(creatMap);
            out.writeObject(playerPos);
            out.writeObject(SEEDCOMMAND);
            System.out.println("Game successfully saved.");
        } catch (IOException e) {
            System.out.println("Failed to save game: " + e.getMessage());
        }
    }
}
