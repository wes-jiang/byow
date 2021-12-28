package byow.Core;

import byow.TileEngine.TETile;
//import byow.TileEngine.Tileset;

import java.io.Serializable;
//import java.util.Random;
import java.util.ArrayList;
import java.awt.Color;

public class Flowers implements Serializable {
    private int pointValue; //num points flower gives once collected
    private int currR; //red
    private int currG; //green
    private int currB; //blue
    private int numSteps;
    private double decayR;
    private double decayB;
    private double decayG;
    private double decayPts;
    private ArrayList<Coordinate> flowerList;
    private Engine eng;

    public Flowers(int nnumSteps, Engine e) {
        pointValue = 1000;
        currR = Color.MAGENTA.getRed();
        currG = Color.MAGENTA.getGreen();
        currB = Color.MAGENTA.getBlue();
        numSteps = nnumSteps;
        decayR = ((double) (Math.abs(currR - 149)) / nnumSteps);
        decayB = ((double) (Math.abs(69 - currB)) / nnumSteps);
        decayG = ((double) (Math.abs(currG - 53)) / nnumSteps);
        decayPts = 1000 / nnumSteps;
        flowerList = new ArrayList<>();
        eng = e;
    }
    //current R: 50, desired R: 30, numSteps: 5. DecayR = (50 - 30) / 5 = 4.

    public void decay(int factor) {
        currR = Math.max((int) Math.floor(currR - decayR * factor), 40);
        currG = Math.min((int) Math.ceil(currG + decayG * factor), 26);
        currB = Math.max((int) Math.floor(currB - decayB * factor), 13);
        pointValue = (int) Math.floor(pointValue - decayPts);
        for (Coordinate flower : flowerList) {
            eng.getWorld()[flower.getX()][flower.getY()] = new TETile(eng.getWorld()[flower.getX()]
                    [flower.getY()], new Color(currR, currG, currB));
        }
    }

    public void addFlower(Coordinate xy) {
        flowerList.add(xy);
    }

    public void rmFlower(Coordinate xy) {
        flowerList.remove(xy);
    }

    public int getPointValue() {
        return pointValue;
    }

    public int numFlowers() {
        return flowerList.size();
    }
}
