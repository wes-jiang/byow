package byow.Core;

import byow.TileEngine.TETile;
//import byow.TileEngine.Tileset;

import java.io.Serializable;

public class Player implements Serializable {
    private int playerHealth;
    private int x;
    private int y;
    private TETile tileRemoved;

    public Player(int xPos, int yPos, TETile tile) {
        this.x = xPos;
        this.y = yPos;
        this.tileRemoved = tile;
    }

    public void changePosition(int xPos, int yPos, TETile color) {
        this.x = xPos;
        this.y = yPos;
        this.tileRemoved = color;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public TETile getTileRemoved() {
        return this.tileRemoved;
    }

    public void setTileRemoved(TETile tileRemoved) {
        this.tileRemoved = tileRemoved;
    }
}
