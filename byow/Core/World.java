package byow.Core;
import byow.TileEngine.*;
import java.util.*;

public class World {
    TERenderer renderer = new TERenderer();
    TETile[][] worldDimensions;

    private final int HEIGHT;
    private final int WIDTH;
    private final Random RANDOM;
    private boolean avatarPlaced;


    public World(int w, int h, long s) {
        HEIGHT = h;
        WIDTH = w;
        RANDOM = new Random(s);
        avatarPlaced = false;
    }


    public TETile[][] getWorldDimensions() {
//        renderer.initialize(WIDTH, HEIGHT);
        worldDimensions = new TETile[WIDTH][HEIGHT];
        establishRenderer(worldDimensions, Tileset.NOTHING);

        for (int k = 0; k < Math.max(WIDTH, HEIGHT); k += 1) {
            int x = RandomUtils.uniform(RANDOM, WIDTH);
            int y = RandomUtils.uniform(RANDOM, HEIGHT);
            XandYCoordinates start = new XandYCoordinates(x, y);
            randomCreateRoom(worldDimensions, start, RANDOM);
        }

        for (int j = 0; j < 2 * Math.max(WIDTH, HEIGHT); j += 1) {
            int x = RandomUtils.uniform(RANDOM, WIDTH);
            int y = RandomUtils.uniform(RANDOM, HEIGHT);
            XandYCoordinates startHall = new XandYCoordinates(x, y);
            randomlyCreateAHallway(worldDimensions, startHall, RANDOM);
        }

        wallRemover(worldDimensions);
        randomlyCreateExit(worldDimensions, RANDOM);
//        renderer.renderFrame(worldDimensions);
        return worldDimensions;
    }

    private void establishRenderer(TETile[][] realm, TETile teTile) {
        for (int p = 0; p < WIDTH; p += 1) {
            for (int q = 0; q < HEIGHT; q += 1) {
                realm[p][q] = teTile;
            }
        }
//
    }

    private void randomCreateRoom(TETile[][] realm, XandYCoordinates beginning, Random R) {
        int widthOfRoom = RandomUtils.uniform(R, 5, 10);
        int heightOfRoom = RandomUtils.uniform(R, 5, 10);

        if (roomCanBePlaced(realm, beginning, widthOfRoom, heightOfRoom)) {

            // this is how you draw the walls of a room
            for (int j = beginning.x; j < beginning.x + widthOfRoom; j += 1) {
                realm[j][beginning.y] = Tileset.WALL;
                realm[j][beginning.y + heightOfRoom - 1] = Tileset.WALL;
            }

            for (int k = beginning.y; k < beginning.y + heightOfRoom; k += 1) {
                realm[beginning.x][k] = Tileset.WALL;
                realm[beginning.x + widthOfRoom - 1][k] = Tileset.WALL;
            }

            // the floors of the room that was drawn above
            for (int p = beginning.x + 1; p < beginning.x + widthOfRoom - 1; p += 1) {
                for (int q = beginning.y + 1;
                     q < beginning.y + heightOfRoom - 1; q += 1) {
                    realm[p][q] = Tileset.FLOOR;
                }
            }
//            if (avatarPlaced == false) {
//                int x = RandomUtils.uniform(R, beginning.x + 1, beginning.x + widthOfRoom - 1);
//                int y = RandomUtils.uniform(R, beginning.y + 1, beginning.y + heightOfRoom - 1);
//
//            }

        }

    }

    private boolean roomCanBePlaced(TETile[][] realm, XandYCoordinates xy,
                                    int widthOfRoom, int heightOfRoom) {
        if (xy.x + widthOfRoom >= WIDTH || xy.y + heightOfRoom >= HEIGHT) {
            return false;
        }

        for (int k = xy.x; k < xy.x + widthOfRoom; k += 1) {
//            System.out.println(k);
            for (int q = xy.y; q < xy.y + heightOfRoom; q += 1) {
//                System.out.println(q);
                if (!Tileset.NOTHING.equals(realm[k][q])) {
                    return false;
                }
            }
        }
        return true;
    }

    private void randomlyCreateAHallway(TETile[][] realm, XandYCoordinates beginning, Random R) {
        while (!(Tileset.WALL.equals(realm[beginning.x][beginning.y]))
                || beginning.x < 3 || beginning.y < 3 || beginning.x > WIDTH - 3
                || beginning.y > HEIGHT - 3) {
            int randPoint1 = RandomUtils.uniform(R, WIDTH);
            int randPoint2 = RandomUtils.uniform(R, HEIGHT);
            beginning = new XandYCoordinates(randPoint1, randPoint2);
            if (beginning.x < 3 || beginning.y < 3 || beginning.x > WIDTH - 3
                    || beginning.y > HEIGHT - 3) {
                continue;
            }
            if (!hallwayCanBePlaced(realm, beginning)) {
                continue;
            }
        }
        DirectionOfHall d = directionOfHall(realm, beginning);
        switch (d) {
            case RISE: {
                while (beginning.y < HEIGHT - 2
                        && !tethered(realm, beginning, DirectionOfHall.RISE)) {
                    realm[beginning.x - 1][beginning.y] = Tileset.WALL;
                    realm[beginning.x + 1][beginning.y] = Tileset.WALL;
                    realm[beginning.x][beginning.y] = Tileset.FLOOR;
                    beginning.y += 1;
                }
                realm[beginning.x - 1][beginning.y] = Tileset.WALL;
                realm[beginning.x + 1][beginning.y] = Tileset.WALL;
                realm[beginning.x][beginning.y] = Tileset.WALL;
                break;
            }

            case FALL: {
                while (beginning.y > 1
                        && !tethered(realm, beginning, DirectionOfHall.FALL)) {
                    realm[beginning.x - 1][beginning.y] = Tileset.WALL;
                    realm[beginning.x + 1][beginning.y] = Tileset.WALL;
                    realm[beginning.x][beginning.y] = Tileset.FLOOR;
                    beginning.y -= 1;
                }
                realm[beginning.x - 1][beginning.y] = Tileset.WALL;
                realm[beginning.x + 1][beginning.y] = Tileset.WALL;
                realm[beginning.x][beginning.y] = Tileset.WALL;
                break;
            }

            case LEFT_TURN: {
                while (beginning.x > 1
                        && !tethered(realm, beginning, DirectionOfHall.LEFT_TURN)) {
                    realm[beginning.x][beginning.y - 1] = Tileset.WALL;
                    realm[beginning.x][beginning.y + 1] = Tileset.WALL;
                    realm[beginning.x][beginning.y] = Tileset.FLOOR;
                    beginning.x -= 1;
                }
                realm[beginning.x][beginning.y + 1] = Tileset.WALL;
                realm[beginning.x][beginning.y - 1] = Tileset.WALL;
                realm[beginning.x][beginning.y] = Tileset.WALL;
                break;
            }

            case RIGHT_TURN: {
                while (beginning.x < WIDTH - 2
                        && !tethered(realm, beginning, DirectionOfHall.RIGHT_TURN)) {
                    realm[beginning.x][beginning.y - 1] = Tileset.WALL;
                    realm[beginning.x][beginning.y + 1] = Tileset.WALL;
                    realm[beginning.x][beginning.y] = Tileset.FLOOR;
                    beginning.x += 1;
                }
                realm[beginning.x][beginning.y + 1] = Tileset.WALL;
                realm[beginning.x][beginning.y - 1] = Tileset.WALL;
                realm[beginning.x][beginning.y] = Tileset.WALL;
                break;
            }
            default: break;
        }
    }

    private boolean hallwayCanBePlaced(TETile[][] realm, XandYCoordinates xy) {
//        boolean bool1 = Tileset.FLOOR.equals(realm[xy.x + 1][xy.y]);
//        boolean bool2 = Tileset.FLOOR.equals(realm[xy.x - 1][xy.y]);
//        boolean bool3 = Tileset.FLOOR.equals(realm[xy.x][xy.y + 1]);
//        boolean bool4 = Tileset.FLOOR.equals(realm[xy.x][xy.y - 1]);

        return Tileset.FLOOR.equals(realm[xy.x + 1][xy.y])
                || Tileset.FLOOR.equals(realm[xy.x - 1][xy.y])
                || Tileset.FLOOR.equals(realm[xy.x][xy.y + 1])
                || Tileset.FLOOR.equals(realm[xy.x][xy.y - 1]);
    }

    private DirectionOfHall directionOfHall(TETile[][] realm, XandYCoordinates xy) {
        DirectionOfHall d = DirectionOfHall.NULL;
        boolean check1 = Tileset.FLOOR.equals(realm[xy.x][xy.y - 1]);
        boolean check2 = Tileset.FLOOR.equals(realm[xy.x][xy.y + 1]);
        boolean check3 = Tileset.FLOOR.equals(realm[xy.x + 1][xy.y]);
        boolean check4 = Tileset.FLOOR.equals(realm[xy.x - 1][xy.y]);
        if (check1) {
            d = DirectionOfHall.RISE;
        }
        if (check2) {
            d = DirectionOfHall.FALL;
        }
        if (check3) {
            d = DirectionOfHall.LEFT_TURN;
        }
        if (check4) {
            d = DirectionOfHall.RIGHT_TURN;
        }
        return d;
    }

    private boolean tethered(TETile[][] realm, XandYCoordinates xy,
                             DirectionOfHall directionOfHall) {
        boolean tethered = false;
        boolean check1 = Tileset.FLOOR.equals(realm[xy.x - 1][xy.y]);
        boolean check2 = Tileset.FLOOR.equals(realm[xy.x + 1][xy.y]);
        boolean check3 = Tileset.FLOOR.equals(realm[xy.x][xy.y + 1]);
        boolean check4 = Tileset.FLOOR.equals(realm[xy.x][xy.y - 1]);
        switch (directionOfHall) {
            case RISE: {
                if (check1 || check2 || check3) {
                    tethered = true;
                }
                break;
            }
            case FALL: {
                if (check1 || check2 || check4) {
                    tethered = true;
                }
                break;
            }
            case LEFT_TURN: {
                if (check4 || check3 || check1) {
                    tethered = true;
                }
                break;
            }
            case RIGHT_TURN: {
                if (check4 || check3 || check2) {
                    tethered = true;
                }
                break;
            }
            default: {
                break;
            }
        }
        return tethered;
    }

    private void wallRemover(TETile[][] realm) {
        for (int j = 2; j < WIDTH - 2; j += 1) {
            for (int k = 2; k < HEIGHT - 2; k += 1) {
                XandYCoordinates mostRecent = new XandYCoordinates(j, k);
                if (removalNecessary(realm, mostRecent)) {
                    realm[mostRecent.x][mostRecent.y] = Tileset.FLOOR;
                }
            }
        }

        for (int j = 2; j < WIDTH - 2; j += 1) {
            for (int k = 2; k < HEIGHT - 2; k += 1) {
                XandYCoordinates mostRecent = new XandYCoordinates(j, k);
                if (openHallWall(realm, mostRecent)) {
                    realm[mostRecent.x][mostRecent.y] = Tileset.FLOOR;
                }
            }
        }

    }


    private boolean removalNecessary(TETile[][] realm, XandYCoordinates xy) {
        boolean realmCheck1 = realm[xy.x + 1][xy.y].equals(Tileset.FLOOR);
        boolean realmCheck2 = realm[xy.x - 1][xy.y].equals(Tileset.FLOOR);
        boolean realmCheck3 = realm[xy.x][xy.y - 1].equals(Tileset.WALL);
        boolean realmCheck4 = realm[xy.x][xy.y + 1].equals(Tileset.WALL);
        boolean realmCheck5 = realm[xy.x + 1][xy.y].equals(Tileset.WALL);
        boolean realmCheck6 = Tileset.WALL.equals(realm[xy.x - 1][xy.y]);
        boolean realmCheck7 = Tileset.FLOOR.equals(realm[xy.x][xy.y - 1]);
        boolean realmCheck8 = Tileset.FLOOR.equals(realm[xy.x][xy.y + 1]);

        return (realmCheck1 && realmCheck2 && realmCheck3 && realmCheck4)
                || (realmCheck5 && realmCheck6 && realmCheck7 && realmCheck8);

    }

    private boolean openHallWall(TETile[][] realm, XandYCoordinates xy) {
        boolean cond0 = realm[xy.x][xy.y].equals(Tileset.FLOOR);
        if (cond0) {
            return false;
        }
        int floorCount = 0;
        boolean cond1 = realm[xy.x + 1][xy.y].equals(Tileset.FLOOR);
        if (cond1) {
            floorCount += 1;
        }
        boolean cond2 = realm[xy.x - 1][xy.y].equals(Tileset.FLOOR);
        if (cond2) {
            floorCount += 1;
        }
        boolean cond3 = realm[xy.x][xy.y + 1].equals(Tileset.FLOOR);
        if (cond3) {
            floorCount += 1;
        }
        boolean cond4 = realm[xy.x][xy.y - 1].equals(Tileset.FLOOR);
        if (cond4) {
            floorCount += 1;
        }
        return floorCount >= 3;
    }

    private void randomlyCreateExit(TETile[][] realm, Random R) {
        int x = RandomUtils.uniform(R, 3, WIDTH - 3);
        int y = RandomUtils.uniform(R, 3, HEIGHT - 3);
        XandYCoordinates xy = new XandYCoordinates(x, y);
        if (!realm[xy.x][xy.y].equals(Tileset.NOTHING)) {
            x = RandomUtils.uniform(R, 3, WIDTH - 3);
            y = RandomUtils.uniform(R, 3, HEIGHT - 3);
            xy = new XandYCoordinates(x, y);
        }
        realm[xy.x][xy.y] = Tileset.UNLOCKED_DOOR;
    }

    public XandYCoordinates createAvatar(TETile[][] realm, Random R) {
        int x = RandomUtils.uniform(R, 3, WIDTH - 3);
        int y = RandomUtils.uniform(R, 3, HEIGHT - 3);
        XandYCoordinates xy = new XandYCoordinates(x, y);
        if (!realm[xy.x][xy.y].equals(Tileset.FLOOR)) {
            x = RandomUtils.uniform(R, 3, WIDTH - 3);
            y = RandomUtils.uniform(R, 3, HEIGHT - 3);
            xy = new XandYCoordinates(x, y);
        }
        realm[xy.x][xy.y] = Tileset.AVATAR;
        return xy;
    }


}

