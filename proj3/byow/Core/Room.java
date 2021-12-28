package byow.Core;

import byow.TileEngine.TETile;

import java.io.Serializable;
import java.util.ArrayList;

public class Room implements Serializable {

    private int leftX;
    private int bottomY;
    private int width;
    private int height;
    private TETile[][] world;
    private ArrayList<Coordinate> coordinateCoords;

    public Room(int x, int y, int width, int height, TETile[][] world) {
        leftX = x;
        bottomY = y;
        this.width = width;
        this.height = height;
        this.coordinateCoords = new ArrayList<Coordinate>();
        this.world = world;
    }

    public class Node {
        private double distance;
        private Coordinate thisCoord;
        private Coordinate otherCoord;

        private Node(double distance, Coordinate posA, Coordinate posB) {
            this.distance = distance;
            this.thisCoord = posA;
            this.otherCoord = posB;
        }

        public double getDist() {
            return this.distance;
        }

        public Coordinate getThisCoord() {
            return this.thisCoord;
        }

        public Coordinate getOtherCoord() {
            return this.otherCoord;
        }
    }

    public int getLeftWall() {
        return leftX;
    }

    public int getRightWall() {
        return leftX + width;
    }

    public int getBottomWall() {
        return bottomY;
    }

    public int getTopWall() {
        return bottomY + height;
    }

    public void setLeftX(int x) {
        leftX = x;
    }

    public void setBottomY(int y) {
        bottomY = y;
    }


    public ArrayList<Coordinate> getWallcoords() {
        return coordinateCoords;
    }

    public boolean isOverlapping(Room other) {
        //code from
        //https://stackoverflow.com/
        //questions/23302698/java-check-if-two-rectangles-overlap-at-any-point
        int leftx = Math.max(getLeftWall(), other.getLeftWall());
        int rightX = Math.min(getRightWall(), other.getRightWall());
        int botY = Math.max(getBottomWall(), other.getBottomWall());
        int topY = Math.min(getTopWall(), other.getTopWall());
        return rightX >= leftx && topY >= botY;
    }

    public ArrayList<Node> findClosestSide(Room other) {
        ArrayList<Node> returnDist = new ArrayList();
        //Node contains distance, other Xpos, other Ypos
        //relative to this rooms wall tiles
        //the reasoning behind this NODE subclass is because it's
        //probably useful to return the closest coordinate wall tile along with the distance
        ArrayList<Coordinate> otherCoordinateCoords = other.getWallcoords();
        for (int i = 0; i < this.coordinateCoords.size(); i++) {
            for (int j = 0; j < otherCoordinateCoords.size(); j++) {
                Coordinate thisCoord = this.coordinateCoords.get(i);
                Coordinate otherCoord = otherCoordinateCoords.get(j);
                int xDif = thisCoord.getX() - otherCoord.getX();
                int yDif = thisCoord.getY() - otherCoord.getY();
                if (this.isCorner(thisCoord) || other.isCorner(otherCoord)) {
                    continue;
                }
                double distance = Math.sqrt(Math.pow(xDif, 2) + Math.pow(yDif, 2));
                Node kv = new Node(distance, thisCoord, otherCoord);
                returnDist.add(kv);
            }
        }
        return returnDist;
    }

    public boolean isCorner(Coordinate xy) {
//        if((this.leftX == xy.getX() || this.leftX + this.width == xy.getX())
//                && (this.bottomY == xy.getY() || this.bottomY + this.height == xy.getY())){
//            return true;
//        }
//        return false;

        int x = xy.getX();
        int y = xy.getY();
        if ((x == 0 || x == Engine.WIDTH - 1) && (y == 0 || y == Engine.HEIGHT - 1)) {
            return true;
        }
        if (x == 0 || x == Engine.WIDTH - 1) {
            return !(coordinateCoords.contains(new Coordinate(x, y - 1))
                    && coordinateCoords.contains(new Coordinate(x, y + 1)));
        } else if (y == 0 || y == Engine.HEIGHT - 1) {
            return !(coordinateCoords.contains(new Coordinate(x - 1, y))
                    && coordinateCoords.contains(new Coordinate(x + 1, y)));
        } else {
            return !((coordinateCoords.contains(new Coordinate(x - 1, y))
                    && coordinateCoords.contains(new Coordinate(x + 1, y)))
                    || (coordinateCoords.contains(new Coordinate(x, y - 1))
                    && coordinateCoords.contains(new Coordinate(x, y + 1))));
        }
    }
}
